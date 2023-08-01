package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.viewmodels.CartItemsViewModel;
import com.csis4495_cmk.webuy.adapters.CustomerCartItemsAdapter;
import com.csis4495_cmk.webuy.models.CartItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerCartItemsFragment extends Fragment
                    implements CustomerCartItemsAdapter.onCartSellerBannerListener{

    private NavController navController;
    RecyclerView recyclerView;
    TextView tvNoItems, tvCartItemsTotal, tvCartItemsCheckoutAmount;
    CheckBox cbxSelectAll;
    CustomerCartItemsAdapter customerCartItemsAdapter;
    CartItemsViewModel viewModel;
//    Map<String, ArrayList<CartItem>> sellerItemsMap;
//    Map<String, Boolean> sellerAllItemsCheckedMap;
//    Map<CartItem, CartItemsViewModel.CartItemInfo> cartItemsInfoMap;
    Map<String, ArrayList<CartItem>> sellerItemsMap = new HashMap<>();
    Map<String, Boolean> sellerAllItemsCheckedMap = new HashMap<>();
    Map<CartItem, CartItemsViewModel.CartItemInfo> cartItemsInfoMap = new HashMap<>();


    public CustomerCartItemsFragment() {
        // Required empty public constructor
    }

    public static CustomerCartItemsFragment newInstance() {
        CustomerCartItemsFragment fragment = new CustomerCartItemsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_cart_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = NavHostFragment.findNavController(CustomerCartItemsFragment.this);
        tvNoItems = view.findViewById(R.id.tv_no_items);
        tvCartItemsTotal = view.findViewById(R.id.tv_cart_items_total);
        tvCartItemsCheckoutAmount = view.findViewById(R.id.tv_cart_items_checkout_amount);
        cbxSelectAll = view.findViewById(R.id.checkbox_select_all);
        recyclerView = view.findViewById(R.id.rv_cust_seller_with_cart_items);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel = new ViewModelProvider(CustomerCartItemsFragment.this).get(CartItemsViewModel.class);

        //get data from firebase and set two maps
//        Log.d("vm", "vm constructor created");
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        DatabaseReference customerRef = firebaseDatabase.getReference("Customer").child(customerId);
//        DatabaseReference groupRef = firebaseDatabase.getReference("Group");
//        DatabaseReference productRef = firebaseDatabase.getReference("Product");
//        Log.d("vm", "vm constructor created");
//        customerRef.child("Cart").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                sellerItemsMap.clear();
//                sellerAllItemsCheckedMap.clear();
//                cartItemsInfoMap.clear();
//
//                for (DataSnapshot sellerSnapshot : snapshot.getChildren()) {
//                    String sellerId = sellerSnapshot.getKey();
//                    //Log.d("vm", sellerId);
//                    ArrayList<CartItem> sellerItems = new ArrayList<>();
//                    boolean allChecked = true;
//                    for (DataSnapshot cartItemSnapshot : sellerSnapshot.getChildren()) {
//                        CartItem cartItem = cartItemSnapshot.getValue(CartItem.class);
//                        if (cartItem == null) {
//                            //Log.d("vm", "null");
//                        }
//                        //Log.d("vm", "productId: "+cartItem.getProductId());
//                        sellerItems.add(cartItem);
//
//                        //get cart info
//                        String groupId = cartItem.getGroupId();
//                        String productId = cartItem.getProductId();
//                        String styleId = cartItem.getStyleId();
//                        final CartItemsViewModel.CartItemInfo[] cartItemInfo = new CartItemsViewModel.CartItemInfo[1];
//                        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                String groupPicName = null;
//                                final String[] groupImgUrl = {null};
//                                String groupPrice = "CA$ N/A";
//                                String styleName = "Style N/A";
//                                String groupName = "Group N/A";
//                                final int[] inventoryAmount = {9999};
//                                final String[] productName = {"Product N/A"};
//                                final int[] tax = {-1};
//                                int groupType = -1;
//
//                                Group group = snapshot.getValue(Group.class);
//                                if (group != null) {
//                                    groupName = group.getGroupName();
//                                    groupType = group.getGroupType();
//                                    if (styleId == null) {
//                                        styleName = null;
//                                        groupPicName = group.getGroupImages().get(0);
//                                        groupPrice = group.getGroupPrice();
//                                        inventoryAmount[0] = group.getGroupQtyMap().get("p___" + productId);
//                                    } else {
//                                        for (DataSnapshot styleShot : snapshot.child("groupStyles").getChildren()) {
//                                            if (styleShot.child("styleId").getValue(String.class).equals(styleId)) {
//                                                groupPicName = styleShot.child("stylePicName").getValue(String.class);
//                                                groupPrice = "CA$ " + styleShot.child("stylePrice").getValue(Double.class);
//                                                styleName = styleShot.child("styleName").getValue(String.class);
//                                                inventoryAmount[0] = group.getGroupQtyMap().get("s___" + styleId);
//                                            }
//                                        }
//                                    }
//                                    //imgUrl
//                                    StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage").child(productId);
//                                    imgRef.child(groupPicName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri uri) {
//                                            groupImgUrl[0] = uri.toString();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            //pic not found
//                                        }
//                                    });
//                                    //product
//                                    productRef.child(productId).addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                            Product product = snapshot.getValue(Product.class);
//                                            if (product != null) {
//                                                productName[0] = product.getProductName();
//                                                tax[0] = product.getTax();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                        }
//                                    });
//                                }
//
//                                cartItemInfo[0] = new CartItemsViewModel.CartItemInfo(groupImgUrl[0], groupPrice, groupName,
//                                        styleName, productName[0], tax[0],
//                                        groupType, inventoryAmount[0]);
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                            }
//                        });
//
//                        cartItemsInfoMap.put(cartItem, cartItemInfo[0]);
//
//                        //
//                        if (!cartItem.getChecked()) {
//                            allChecked = false;
//                        }
//                    }
//                    sellerAllItemsCheckedMap.put(sellerId, allChecked);
//                    sellerItemsMap.put(sellerId, sellerItems);
//                }
//
//                viewModel.setSellerAllItemsCheckedMap(sellerAllItemsCheckedMap);
//                viewModel.setSellerItemsMapLiveData(sellerItemsMap);
//                viewModel.setCartItemsInfoMapLiveData(cartItemsInfoMap);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.d("vm", error.getMessage());
//            }
//        });
//        Log.d("vm", "vm finished constructor: " + sellerItemsMap.size());

        viewModel.getSellerItemsMapLiveData().observe(getViewLifecycleOwner(), new Observer<Map<String, ArrayList<CartItem>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<CartItem>> stringArrayListMap) {
                sellerItemsMap = stringArrayListMap;
                Log.d("vm","getSellerItemsMap: "+ sellerItemsMap);
                if (sellerItemsMap.size() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoItems.setVisibility(View.GONE);

                    customerCartItemsAdapter = new CustomerCartItemsAdapter(getContext(), viewModel, getViewLifecycleOwner());
                    recyclerView.setAdapter(customerCartItemsAdapter);
                    customerCartItemsAdapter.setOnCartSellerBannerListener(CustomerCartItemsFragment.this);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                }
                //set checkout amount
                int checkoutAmount = 0;
                for (String sellerId: sellerItemsMap.keySet()) {
                    for (CartItem sellerCartItem: stringArrayListMap.get(sellerId)) {
                        if (sellerCartItem.getChecked()) {
                            checkoutAmount++;
                        }
                    }
                }
                tvCartItemsCheckoutAmount.setText("Checkout("+checkoutAmount+")");
            }
        });
        viewModel.getSellerAllItemsCheckedMap().observe(getViewLifecycleOwner(), new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> stringBooleanMap) {
                sellerAllItemsCheckedMap = stringBooleanMap;
                boolean allGroupChecked = false;
                for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
                    boolean allChecked = sellerAllItemsCheckedMap.get(sellerId);
                    //cbxSelectAll.setOnCheckedChangeListener(null);
                    if (!allChecked) {
                        allGroupChecked = false;
                        break;
                    } else {
                        allGroupChecked = true;
                    }
                }
                //cbxSelectAll.setOnCheckedChangeListener(null);
                //cbxSelectAll.setChecked(allGroupChecked);
            }
        });

        //cbx clicked //
        cbxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {

            for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
                sellerAllItemsCheckedMap.put(sellerId, isChecked);
                Log.d("TestSelectAll","Group set to "+ isChecked);
            }
            for (String sellerId: sellerItemsMap.keySet()) {
                for (CartItem sellerCartItem: sellerItemsMap.get(sellerId)) {
                    sellerCartItem.setChecked(isChecked);
                    Log.d("TestSelectAll","Item set to "+ isChecked);
                }
            }

            viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
            viewModel.setSellerItemsMapLiveData(sellerItemsMap);
        });

        viewModel.getCartItemsInfoMapLiveData().observe(getViewLifecycleOwner(), new Observer<Map<CartItem, CartItemsViewModel.CartItemInfo>>() {
            @Override
            public void onChanged(Map<CartItem, CartItemsViewModel.CartItemInfo> cartItemCartItemInfoMap) {
                double total = 0;
                for(CartItem cartItem: cartItemCartItemInfoMap.keySet()) {
                    if (cartItem.getChecked()){
                        if (cartItemCartItemInfoMap.get(cartItem) != null) {
                            String strPrice = cartItemCartItemInfoMap.get(cartItem).getGroupPrice();
                            if (strPrice != null) {
                                double price;
                                try {
                                    price = Double.parseDouble(strPrice.split("CA\\$ ")[1]);
                                } catch (Exception e) {
                                    price = 0;
                                }
                                total += price * cartItem.getAmount();
                            }
                        }

                    }
                }
                tvCartItemsTotal.setText("CA$ " + total);
            }
        });

        //checkout clicked
        tvCartItemsCheckoutAmount.setOnClickListener(v -> {
            CustomerCheckoutFragment.newInstance(viewModel);
            navController.navigate(R.id.customerCheckoutFragment);
        });
    }

    @Override
    public void onSellerBannerChecked(Map<String,Boolean> sellerAllItemsCheckedMap,Map<String, ArrayList<CartItem>> sellerItemsMap) {

        boolean allGroupChecked = false;
        Log.d("TestSelectAll", sellerAllItemsCheckedMap.size()+"");
        for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
            boolean allChecked = sellerAllItemsCheckedMap.get(sellerId);
            Log.d("TestSelectAll", allChecked + "!");
            if (!allChecked) {
                allGroupChecked = false;
                break;
            } else {
                allGroupChecked = true;
            }
        }

        cbxSelectAll.setOnCheckedChangeListener(null);
        cbxSelectAll.setChecked(allGroupChecked);

        cbxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
                sellerAllItemsCheckedMap.put(sellerId, isChecked);
                Log.d("TestSelectAll","Group set to "+ isChecked);
            }
            for (String sellerId: sellerItemsMap.keySet()) {
                for (CartItem sellerCartItem: sellerItemsMap.get(sellerId)) {
                    sellerCartItem.setChecked(isChecked);
                    Log.d("TestSelectAll","Item set to "+ isChecked);
                }
            }

            viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
            viewModel.setSellerItemsMapLiveData(sellerItemsMap);
            recyclerView.setAdapter(customerCartItemsAdapter);
            customerCartItemsAdapter.setOnCartSellerBannerListener(this);
        });
    }
}