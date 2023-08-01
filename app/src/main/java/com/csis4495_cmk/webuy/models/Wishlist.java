package com.csis4495_cmk.webuy.models;

public class Wishlist {
    private String groupId;
    private String sellerId;
    private String productId;
    private String groupName;
    private String groupPrice;
    private int groupStatus;
    private int groupType;
    private String groupImage;

    public Wishlist() {
    }

    public Wishlist(String groupName, String groupPrice, int groupStatus, int groupType, String groupImage) {
        this.groupName = groupName;
        this.groupPrice = groupPrice;
        this.groupStatus = groupStatus;
        this.groupType = groupType;
        this.groupImage = groupImage;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String styleId) {
        this.sellerId = styleId;
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

    public String getGroupPrice() {
        return groupPrice;
    }

    public void setGroupPrice(String groupPrice) {
        this.groupPrice = groupPrice;
    }

    public int getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(int groupStatus) {
        this.groupStatus = groupStatus;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }
}
