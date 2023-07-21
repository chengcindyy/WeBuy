package com.csis4495_cmk.webuy.models;

public class Inventory {
    private String productId;
    private String styleId;
    private int inStock;
    private int allocated;
    private int toAllocated;

    public Inventory() {
    }

    public Inventory(String productId, String styleId, int inStock, int allocated, int toAllocated) {
        this.productId = productId;
        this.styleId = styleId;
        this.inStock = inStock;
        this.allocated = allocated;
        this.toAllocated = toAllocated;
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
}
