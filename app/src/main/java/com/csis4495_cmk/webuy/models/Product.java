package com.csis4495_cmk.webuy.models;

import java.util.ArrayList;
import java.util.List;

public class Product {

    private String productName;
    private String category;
    private String description;
    private double tax;

    private List<String> productImages;
    private String sellerId;

    private List<ProductStyle> productStyles;

    //private double price; // might be a range


    public Product() {
    }

    public Product(String productName, String category, String description,
                   double tax, List<String> productImages, String sellerId,
                    List<ProductStyle> productStyles) {
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.tax = tax;
        this.productImages = productImages;
        this.sellerId = sellerId;
        this.productStyles = productStyles;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getTax() {
        return tax;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public String getSellerId() {
        return sellerId;
    }

    public List<ProductStyle> getProductStyles() {
        return productStyles;
    }
}
