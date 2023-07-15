package com.csis4495_cmk.webuy.models;

public class CartItem {
    private String groupId;
    private String productId;
    private String styleName;
    private int amount;

    public CartItem() {
    }

    public String getGroupId() {
        return groupId;
    }

    public String getProductId() {
        return productId;
    }

    public String getStyleName() {
        return styleName;
    }

    public int getAmount() {
        return amount;
    }
}
