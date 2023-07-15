package com.csis4495_cmk.webuy.models;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Group {
    private String groupName;

    private int groupType;

    private String category;

    private String description;

    private List<String> groupImages;

    private int tax;

    private String sellerId;

    private List<ProductStyle> groupStyles;

    private Map<String, Integer> groupQtyMap;

    private Date startTime;
    private Date endTime;

    private Long startTimestamp;

    private Long endTimestamp;


    private int status;

    private String groupPrice; // product price or might be (minStylePrice, maxStylePrice)

    private String productId;
    private String coverImgUrl;

    public Group() {
    }

    public Group(String groupName, int groupType, String category, String description, List<String> groupImages, int tax, String sellerId, List<ProductStyle> groupStyles, Date startTime, Date endTime, int status, String groupPrice) {
        this.groupName = groupName;
        this.groupType = groupType;
        this.category = category;
        this.description = description;
        this.groupImages = groupImages;
        this.tax = tax;
        this.sellerId = sellerId;
        this.groupStyles = groupStyles;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.groupPrice = groupPrice;
    }

    public Long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(Long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public Long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(Long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public Map<String, Integer> getGroupQtyMap() {
        return groupQtyMap;
    }

    public void setGroupQtyMap(Map<String, Integer> groupQtyMap) {
        this.groupQtyMap = groupQtyMap;
    }

    public List<String> getGroupImages() {
        return groupImages;
    }

    public void setGroupImages(List<String> groupImages) {
        this.groupImages = groupImages;
    }

    public String getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(String groupPrice) {
        this.groupPrice = groupPrice;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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


    public int getTax() {
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

    public List<ProductStyle> getGroupStyles() {
        return groupStyles;
    }

    public void setGroupStyles(List<ProductStyle> groupStyles) {
        this.groupStyles = groupStyles;
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

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }
}
