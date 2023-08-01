package com.csis4495_cmk.webuy.models;

import android.util.Log;

import androidx.annotation.Nullable;

import com.csis4495_cmk.webuy.viewmodels.CartItemsViewModel;

import java.util.Objects;

public class CartItem {
    private String groupId;
    private String sellerId;
    private String productId;
    private String styleId = null;
    private int amount;
    private boolean checked = false;

    public CartItem() {
    }

    public CartItem(String groupId, String sellerId, String productId, String styleId, int amount) {
        this.groupId = groupId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.styleId = styleId;
        this.amount = amount;
    }

    public CartItem(String groupId, String sellerId, String productId, int amount) {
        this.groupId = groupId;
        this.sellerId = sellerId;
        this.productId = productId;
        this.amount = amount;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getProductId() {
        return productId;
    }

    public String getStyleId() {
        return styleId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    @Override
    public boolean equals(Object o) {
        Log.d("aaa","equals");
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(groupId, cartItem.groupId) &&
                Objects.equals(sellerId, cartItem.sellerId) &&
                Objects.equals(productId, cartItem.productId) &&
                Objects.equals(styleId, cartItem.styleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupId, sellerId, productId, styleId);
    }

}
