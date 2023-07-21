package com.csis4495_cmk.webuy.models;

public class Inventory {
    private String sellerId;
    private String productId;
    private String styleId;
    private String inventoryName;
    private int inStock;
    private int allocated;
    private int toAllocated;
    private String productStyleKey;
    private String productImageUrl;
    private String inventoryTitle;

    public Inventory() {
    }

    public Inventory(String productId, String styleId, int inStock, int allocated, int toAllocated) {
        this.productId = productId;
        this.styleId = styleId;
        this.inStock = inStock;
        this.allocated = allocated;
        this.toAllocated = toAllocated;
    }

    public Inventory(String sellerId, String productId, String styleId, int inStock, String inventoryName, String productStyleKey, String inventoryTitle) {
        this.sellerId = sellerId;
        this.productId = productId;
        this.styleId = styleId;
        this.inStock = inStock;
        this.productStyleKey = productStyleKey;
        this.inventoryName = inventoryName;
        this.inventoryTitle = inventoryTitle;
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

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductStyleKey() {
        return productStyleKey;
    }

    public void setProductStyleKey(String productStyleKey) {
        this.productStyleKey = productStyleKey;
    }
}
