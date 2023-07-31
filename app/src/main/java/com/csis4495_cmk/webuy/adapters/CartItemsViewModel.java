package com.csis4495_cmk.webuy.adapters;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.CartItem;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class CartItemsViewModel extends ViewModel {
    private MutableLiveData<Map<String, ArrayList<CartItem>>> sellerItemsMap = new MutableLiveData<>();
    private MutableLiveData<Map<String, Boolean>> sellerAllItemsCheckedMap = new MutableLiveData<>();
    private MutableLiveData<Map<CartItem, CartItemInfo>> cartItemsInfoMap = new MutableLiveData<>();

    public void setSellerItemsMap(Map<String, ArrayList<CartItem>> mMap) {
        sellerItemsMap.setValue(mMap);
    }
    public void setSellerAllItemsCheckedMap(Map<String, Boolean> sellerAllItemsChecked) {
        this.sellerAllItemsCheckedMap.setValue(sellerAllItemsChecked);
    }
    public void setCartItemsInfoMap(Map<CartItem, CartItemInfo> cartItemsInfoMap) {
        this.cartItemsInfoMap.setValue(cartItemsInfoMap);
    }

    public LiveData<Map<String, ArrayList<CartItem>>> getSellerItemsMap() {
        return sellerItemsMap;
    }
    public LiveData<Map<String, Boolean>> getSellerAllItemsCheckedMap() {
        return sellerAllItemsCheckedMap;
    }
    public LiveData<Map<CartItem, CartItemInfo>> getCartItemsInfoMap() {
        return cartItemsInfoMap;
    }

    public static class CartItemInfo {
        private String groupPicUrl;
        private String groupPrice;
        private String groupName;
        private String styleName;
        private String productName;
        private int productTax; //0 no tax 1 GST 2 GST+PST
        private int groupType; //0 instock 1 preorder
        private int inventoryAmount;
        public CartItemInfo() {
        }

        //with style
        public CartItemInfo(String groupPicUrl, String groupPrice, String groupName,
                            String styleName, String productName, int productTax,
                            int groupType, int inventoryAmount) {
            this.groupPicUrl = groupPicUrl;
            this.groupPrice = groupPrice;
            this.groupName = groupName;
            this.styleName = styleName;
            this.productName = productName;
            this.productTax = productTax;
            this.groupType = groupType;
            this.inventoryAmount = inventoryAmount;
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
