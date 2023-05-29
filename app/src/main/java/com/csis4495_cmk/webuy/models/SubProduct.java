package com.csis4495_cmk.webuy.models;

public class SubProduct {

    private String productId;
    private String subProductName;
    private double price;
    private String subProductPic;
    private String description;

    public SubProduct(String productId, String subProductName, double price, String subProductPic, String description) {
        this.productId = productId;
        this.subProductName = subProductName;
        this.price = price;
        this.subProductPic = subProductPic;
        this.description = description;
    }

    public String getProductId() {
        return productId;
    }

    public String getSubProductName() {
        return subProductName;
    }

    public double getPrice() {
        return price;
    }

    public String getSubProductPic() {
        return subProductPic;
    }

    public String getDescription() {
        return description;
    }
}
