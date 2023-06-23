package com.csis4495_cmk.webuy.models;

import java.util.Date;
import java.util.List;

public class Group {
    private String groupName;

    private int groupType;

    private String category;

    private String description;

    private List<String> productImages;

    private int tax;

    private String sellerId;

    private List<ProductStyle> productStyles;

    private Date startTime;
    private Date endTime;

    private int status;

    private String productPrice; // product price or might be (minStylePrice, maxStylePrice)

    public Group() {
    }

    public Group(String groupName, int groupType, String category, String description, List<String> productImages, int tax, String sellerId, List<ProductStyle> productStyles, Date startTime, Date endTime, int status, String productPrice) {
        this.groupName = groupName;
        this.groupType = groupType;
        this.category = category;
        this.description = description;
        this.productImages = productImages;
        this.tax = tax;
        this.sellerId = sellerId;
        this.productStyles = productStyles;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.productPrice = productPrice;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(List<String> productImages) {
        this.productImages = productImages;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(int tax) {
        this.tax = tax;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public List<ProductStyle> getProductStyles() {
        return productStyles;
    }

    public void setProductStyles(List<ProductStyle> productStyles) {
        this.productStyles = productStyles;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    //    private String groupName;
//    private String duration;
//    private String productId;
//    private String description;
//    private int status;
//
//    public Group() {
//    }
//
//    public String getGroupName() {
//        return groupName;
//    }
//
//    public String getDuration() {
//        return duration;
//    }
//
//    public String getProductId() {
//        return productId;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public int getStatus() {
//        return status;
//    }
}
