package com.csis4495_cmk.webuy.models;

public class Inventory {
    private String sellerId;
    private String productId;
    private String styleId;
    private String groupId;
    private String inventoryName;
    private int toSell;
    private int inStock;
    private int allocated;
    private int toAllocated;
    private int ordered;
    private int toOrder;
    private String productStyleKey;
    private String imageUrl;
    private String inventoryTitle;
    private String inventoryId;

    public Inventory() {
    }

    public Inventory(String sellerId, String productId, String groupId, String styleId, int toSell, int inStock, String inventoryName, String productStyleKey, String inventoryTitle) {
        this.sellerId = sellerId;
        this.toSell = toSell;
        this.groupId = groupId;
        this.productId = productId;
        this.styleId = styleId;
        this.inStock = inStock;
        this.productStyleKey = productStyleKey;
        this.inventoryName = inventoryName;
        this.inventoryTitle = inventoryTitle;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public int getToSell() {
        return toSell - ordered;
    }

    public void setToSell(int toSell) {
        this.toSell = toSell;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public int getToOrder() {
        int num = inStock - ordered;
        if (num >= 0)
            toOrder = 0;
        else {
            toOrder = Math.abs(num);
        }
        return toOrder;
    }

    public void setToOrder(int toOrder) {
        this.toOrder = toOrder;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStyleId() {
        return styleId;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public void setInventoryTitle(String inventoryTitle) {
        this.inventoryTitle = inventoryTitle;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public int getAllocated() {
        return allocated;
    }

    public void setAllocated(int allocated) {
        this.allocated = allocated;
    }

    public int getToAllocated() {
        return toAllocated;
    }

    public void setToAllocated(int toAllocated) {
        this.toAllocated = toAllocated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProductStyleKey() {
        return productStyleKey;
    }

    public void setProductStyleKey(String productStyleKey) {
        this.productStyleKey = productStyleKey;
    }
}
