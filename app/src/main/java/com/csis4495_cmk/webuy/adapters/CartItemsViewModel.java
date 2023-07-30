package com.csis4495_cmk.webuy.adapters;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.CartItem;

import java.util.ArrayList;
import java.util.Map;

public class CartItemsViewModel extends ViewModel {
    private MutableLiveData<Map<String, ArrayList<CartItem>>> sellerItemsMap = new MutableLiveData<>();
    private MutableLiveData<Map<String, Boolean>> sellerAllItemsCheckedMap = new MutableLiveData<>();

    public void setSellerItemsMap(Map<String, ArrayList<CartItem>> mMap) {
        sellerItemsMap.setValue(mMap);
    }
    public void setSellerAllItemsCheckedMap(Map<String, Boolean> sellerAllItemsChecked) {
        this.sellerAllItemsCheckedMap.setValue(sellerAllItemsChecked);
    }

    public LiveData<Map<String, ArrayList<CartItem>>> getSellerItemsMap() {
        return sellerItemsMap;
    }
    public LiveData<Map<String, Boolean>> getSellerAllItemsCheckedMap() {
        return sellerAllItemsCheckedMap;
    }

}
