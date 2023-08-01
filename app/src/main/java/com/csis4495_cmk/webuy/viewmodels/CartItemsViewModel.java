package com.csis4495_cmk.webuy.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartItemsViewModel extends ViewModel {
    private MutableLiveData<Map<String, ArrayList<CartItem>>> sellerItemsMapLiveData = new MutableLiveData<>();
    private MutableLiveData<Map<String, Boolean>> sellerAllItemsCheckedMapLiveData = new MutableLiveData<>();
    private MutableLiveData<Map<CartItem, CartItemInfo>> cartItemsInfoMapLiveData = new MutableLiveData<>();

    public CartItemsViewModel() { //get data from firebase and set two maps
        Log.d("vm","vm constructor created");
        Map<String, ArrayList<CartItem>> sellerItemsMap = new HashMap<>();
        Map<String, Boolean> sellerAllItemsCheckedMap = new HashMap<>();
        Map<CartItem, CartItemsViewModel.CartItemInfo> cartItemsInfoMap = new HashMap<>();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference customerRef = firebaseDatabase.getReference("Customer").child(customerId);
        DatabaseReference groupRef = firebaseDatabase.getReference("Group");
        DatabaseReference productRef = firebaseDatabase.getReference("Product");
        Log.d("vm","vm here");
        customerRef.child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sellerSnapshot: snapshot.getChildren()) {
                    String sellerId = sellerSnapshot.getKey();
                    Log.d("vm",sellerId);
                    ArrayList<CartItem> sellerItems = new ArrayList<>();
                    final boolean[] allChecked = {true};
                    long totalCallbacks = sellerSnapshot.getChildrenCount();
                    final int[] callbacksCompleted = {0};

                    for (DataSnapshot cartItemSnapshot: sellerSnapshot.getChildren()) {
                        CartItem cartItem = cartItemSnapshot.getValue(CartItem.class);
                        if(cartItem == null) {
                            Log.d("vm", "null");
                        }
                        sellerItems.add(cartItem);
                        Log.d("vm!!!!", cartItemSnapshot.getKey());
                        Log.d("vm!!!!", cartItem.getAmount()+" amount");

                        //get cart info
                        String groupId = cartItem.getGroupId();
                        String productId = cartItem.getProductId();
                        String styleId = cartItem.getStyleId();
                        final CartItemInfo[] cartItemInfo = new CartItemInfo[1];
                        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String groupPicName = null;
                                final String[] groupImgUrl = {null};
                                String groupPrice = "CA$ N/A";
                                String styleName = "Style N/A";
                                String groupName = "Group N/A";
                                final int[] inventoryAmount = {9999};
                                final String[] productName = {"Product N/A"};
                                final int[] tax = {-1};
                                int groupType = -1;

                                Group group = snapshot.getValue(Group.class);
                                Long groupEndTimestamp;
                                try {
                                    groupEndTimestamp = group.getEndTimestamp();
                                } catch (Exception e) {
                                    groupEndTimestamp = null;
                                }

                                Integer groupStatus = null;

                                if (group != null) {
                                    groupStatus = group.getStatus();
                                    groupName = group.getGroupName();
                                    groupType = group.getGroupType();
                                    if (styleId == null) {
                                        styleName = null;
                                        groupPicName = group.getGroupImages().get(0);
                                        groupPrice = group.getGroupPrice();
                                        inventoryAmount[0] = group.getGroupQtyMap().get("p___"+productId);
                                    } else {
                                        for(DataSnapshot styleShot: snapshot.child("groupStyles").getChildren()){
                                            if (styleShot.child("styleId").getValue(String.class).equals(styleId)) {
                                                groupPicName = styleShot.child("stylePicName").getValue(String.class);
                                                groupPrice = "CA$ " + styleShot.child("stylePrice").getValue(Double.class);
                                                styleName = styleShot.child("styleName").getValue(String.class);
                                                inventoryAmount[0] = group.getGroupQtyMap().get("s___"+styleId);
                                            }
                                        }
                                    }
                                    //product
                                    productRef.child(productId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Product product = snapshot.getValue(Product.class);
                                            if (product != null) {
                                                productName[0] = product.getProductName();
                                                tax[0] = product.getTax();
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    //imgUrl
                                    StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage").child(productId);
                                    int finalGroupType = groupType;
                                    String finalStyleName = styleName;
                                    String finalGroupPrice = groupPrice;
                                    String finalGroupName = groupName;
                                    Integer finalGroupStatus = groupStatus;
                                    Long finalGroupEndTimestamp = groupEndTimestamp;
                                    imgRef.child(groupPicName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            groupImgUrl[0] = uri.toString();
                                            if (finalGroupType == 0) { //instock
                                                cartItemInfo[0] = new CartItemInfo(groupImgUrl[0], finalGroupPrice, finalGroupName,
                                                        finalStyleName, productName[0], tax[0],
                                                        finalGroupType, inventoryAmount[0], finalGroupStatus);/////
                                            } else if (finalGroupType ==1) { //preorder
                                                cartItemInfo[0] = new CartItemInfo(groupImgUrl[0], finalGroupPrice, finalGroupName,
                                                        finalStyleName, productName[0], tax[0],
                                                        finalGroupType, inventoryAmount[0], finalGroupEndTimestamp, finalGroupStatus);
                                            }
                                            cartItemsInfoMap.put(cartItem, cartItemInfo[0]);
                                            Log.d("vm", "map size: "+ cartItemsInfoMap.size());
                                            callbacksCompleted[0]++;

                                            // Check if all callbacks are completed
                                            if (callbacksCompleted[0] == totalCallbacks) {
                                                // Execute the code after all callbacks are done
                                                //from here
                                                if (!cartItem.getChecked()) {
                                                    allChecked[0] = false;
                                                }
                                                sellerAllItemsCheckedMap.put(sellerId, allChecked[0]);
                                                sellerItemsMap.put(sellerId, sellerItems);

                                                setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
                                                setSellerItemsMapLiveData(sellerItemsMap);
                                                setCartItemsInfoMapLiveData(cartItemsInfoMap);

                                                Log.d("vm", "All callbacks completed!");
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Image download failed
                                            callbacksCompleted[0]++;

                                            // Check if all callbacks are completed
                                            if (callbacksCompleted[0] == totalCallbacks) {
                                                // Execute the code after all callbacks are done
                                                //from here
                                                if (!cartItem.getChecked()) {
                                                    allChecked[0] = false;
                                                }
                                                sellerAllItemsCheckedMap.put(sellerId, allChecked[0]);
                                                sellerItemsMap.put(sellerId, sellerItems);

                                                setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
                                                setSellerItemsMapLiveData(sellerItemsMap);
                                                setCartItemsInfoMapLiveData(cartItemsInfoMap);

                                                Log.d("vm", "All callbacks completed!");
                                            }
                                        }
                                    });

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {}
                        });

                        //from here
//                        if (!cartItem.getChecked()) {
//                            allChecked[0] = false;
//                        }
                    }
//                    sellerAllItemsCheckedMap.put(sellerId, allChecked[0]);
//                    sellerItemsMap.put(sellerId, sellerItems);
                }

//                setSellerAllItemsCheckedMap(sellerAllItemsCheckedMap);
//                setSellerItemsMapLiveData(sellerItemsMap);
//                setCartItemsInfoMapLiveData(cartItemsInfoMap);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("vm", error.getMessage());
            }
        });
        Log.d("vm", "vm finished constructor: "+ sellerItemsMap.size());

    }

    public void setSellerItemsMapLiveData(Map<String, ArrayList<CartItem>> mMap) {
        sellerItemsMapLiveData.setValue(mMap);
    }
    public void setSellerAllItemsCheckedMapLiveData(Map<String, Boolean> sellerAllItemsChecked) {
        this.sellerAllItemsCheckedMapLiveData.setValue(sellerAllItemsChecked);
    }
    public void setCartItemsInfoMapLiveData(Map<CartItem, CartItemInfo> cartItemsInfoMapLiveData) {
        this.cartItemsInfoMapLiveData.setValue(cartItemsInfoMapLiveData);
    }

    public LiveData<Map<String, ArrayList<CartItem>>> getSellerItemsMapLiveData() {
        return sellerItemsMapLiveData;
    }
    public LiveData<Map<String, Boolean>> getSellerAllItemsCheckedMap() {
        return sellerAllItemsCheckedMapLiveData;
    }
    public LiveData<Map<CartItem, CartItemInfo>> getCartItemsInfoMapLiveData() {
        return cartItemsInfoMapLiveData;
    }

    //method


    //inner class
    public static class CartItemInfo {

        private String groupPicUrl;
        private String groupPrice;
        private String groupName;
        private String styleName;
        private String productName;
        private int productTax; //0 no tax 1 GST 2 GST+PST
        private int groupType; //0 instock 1 preorder
        private int inventoryAmount;
        private long groupEndTimestamp;
        private int groupStatus; // 0 not open 1 opening 2 closed
        public CartItemInfo() {
        }

        //with style, preorder
        public CartItemInfo(String groupPicUrl, String groupPrice, String groupName,
                            String styleName, String productName, int productTax,
                            int groupType, int inventoryAmount, long groupEndTimestamp,
                            int groupStatus) {
            this.groupPicUrl = groupPicUrl;
            this.groupPrice = groupPrice;
            this.groupName = groupName;
            this.styleName = styleName;
            this.productName = productName;
            this.productTax = productTax;
            this.groupType = groupType;
            this.inventoryAmount = inventoryAmount;
            this.groupEndTimestamp = groupEndTimestamp;
            this.groupStatus = groupStatus;
        }
        //with style, instock
        public CartItemInfo(String groupPicUrl, String groupPrice, String groupName,
                            String styleName, String productName, int productTax,
                            int groupType, int inventoryAmount,
                            int groupStatus) {
            this.groupPicUrl = groupPicUrl;
            this.groupPrice = groupPrice;
            this.groupName = groupName;
            this.styleName = styleName;
            this.productName = productName;
            this.productTax = productTax;
            this.groupType = groupType;
            this.inventoryAmount = inventoryAmount;
            this.groupStatus = groupStatus;
        }

        public String getGroupPicUrl() {
            return groupPicUrl;
        }

        public String getGroupPrice() {
            return groupPrice;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getStyleName() {
            return styleName;
        }

        public String getProductName() {
            return productName;
        }

        public int getProductTax() {
            return productTax;
        }

        public int getGroupType() {
            return groupType;
        }

        public int getInventoryAmount() {
            return inventoryAmount;
        }

        public long getGroupEndTimestamp() {
            return groupEndTimestamp;
        }

        public int getGroupStatus() {
            return groupStatus;
        }

        //returns true when groupName, productName, and styleName of two CartItemInfo objects are the same:
//        @Override
//        public boolean equals(@Nullable Object obj) {
//            if (this == obj) return true;
//            if (obj == null || getClass() != obj.getClass()) return false;
//
//            CartItemInfo other = (CartItemInfo) obj;
//            // Compare relevant fields to check for equality
//            return Objects.equals(groupName, other.groupName) &&
//                    Objects.equals(productName, other.productName) &&
//                    Objects.equals(styleName, other.styleName);
//        }

        //no style
//        public CartItemInfo(String groupPicUrl, String groupPrice, String groupName,
//                            String productName, int productTax,
//                            int groupType, int inventoryAmount) {
//            this.groupPicUrl = groupPicUrl;
//            this.groupPrice = groupPrice;
//            this.groupName = groupName;
//            this.productName = productName;
//            this.productTax = productTax;
//            this.groupType = groupType;
//            this.inventoryAmount = inventoryAmount;
//        }

    }

}
// 1. group detail set product name and check preorder inventory amount
// 2. cart item card set product name
// 3. seller should prevent going back to login page
// 4. seller group page there is no hint for seller to know -1 is unlimited
// and instock should not be unlimited
// 5. if group is null, make it grey and only delete available
// 6. if the same group same item added, should show already in the cart and lead to the cart
// 7. if the group is expired and is still in the cart... what will happen?
// 8. in seller, groups, closed, swiped left then group disappeared
// 9.

//        switch (product.getTax()) {
//            case 0:
//                tax[0] = 0;
//            case 1:
//                tax[0] = 0.05;
//            case 2:
//                tax[0] = 0.07 ;
//        }
