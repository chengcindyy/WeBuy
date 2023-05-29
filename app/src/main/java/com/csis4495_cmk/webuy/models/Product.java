package com.csis4495_cmk.webuy.models;

import java.util.ArrayList;

public class Product {

    private String productName;
    private String[] categories;
    private String description;
    private ArrayList<String> productPics;
    private double price; // might be a range
    private double tax;

    public Product(String productName, String[] categories, String description, ArrayList<String> productPics, double price, double tax) {
        this.productName = productName;
        this.categories = categories;
        this.description = description;
        this.productPics = productPics;
        this.price = price;
        this.tax = tax;
    }

    public String getProductName() {
        return productName;
    }

    public String[] getCategories() {
        return categories;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getProductPics() {
        return productPics;
    }

    public double getPrice() {
        return price;
    }

    public double getTax() {
        return tax;
    }
}
