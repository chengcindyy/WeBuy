package com.csis4495_cmk.webuy.models;

public class CartItem {
    private String groupId;
    private String productId;
    private String subPId;
    private String amount;

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

    public String getAmount() {
        return amount;
    }
}
