package com.csis4495_cmk.webuy.models;

public class CartItem {
    private String groupId;
    private String productId;
    private String subPId;
    private int amount;

    public CartItem() {
    }

    public String getGroupId() {
        return groupId;
    }

    public String getProductId() {
        return productId;
    }

    public String getSubPId() {
        return subPId;
    }

    public int getAmount() {
        return amount;
    }
}
