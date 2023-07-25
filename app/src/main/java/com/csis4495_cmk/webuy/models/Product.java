package com.csis4495_cmk.webuy.models;

import java.util.List;

public class Product {

    private String productName;
    private String category;
    private String description;
    private int tax; // 0: no tax  1: GST  2: GST+PST
    private int inStock;

    private List<String> productImages;
    private String sellerId;

    private List<ProductStyle> productStyles;

    private String productPrice; // product price or might be (minStylePrice, maxStylePrice)
    private List<String> tags;

    private String coverImgUrl;

    public Product() {
    }

    public Product(String productName, String category, String description,
                   int tax, List<String> productImages, String sellerId,
                    List<ProductStyle> productStyles,String productPrice) {
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.tax = tax;
        this.productImages = productImages;
        this.sellerId = sellerId;
        this.productStyles = productStyles;
        this.productPrice = productPrice;
    }

    public Product(String productName, String category, String description, int tax,
                   List<String> productImages, String sellerId, List<ProductStyle> productStyles,
                   String productPrice, List<String> categories) {
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.tax = tax;
        this.productImages = productImages;
        this.sellerId = sellerId;
        this.productStyles = productStyles;
        this.productPrice = productPrice;
        this.tags = categories;
    }

    public Product(int inStock) {
        this.inStock = inStock;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
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

    public int getTax() {
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

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }
}
