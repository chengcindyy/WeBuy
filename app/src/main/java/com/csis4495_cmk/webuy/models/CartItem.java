package com.csis4495_cmk.webuy.models;

public class CartItem {
    private String groupId;
    private String sellerId;
    private String productId;
    private String styleId;
    private int amount;

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
}
