package com.csis4495_cmk.webuy.models;

public class Group {
    private String groupName;
    private String duration;
    private String productId;
    private String description;
    private int status;

    public Group() {
    }

    public String getGroupName() {
        return groupName;
    }

    public String getDuration() {
        return duration;
    }

    public String getProductId() {
        return productId;
    }

    public String getDescription() {
        return description;
    }

    public int getStatus() {
        return status;
    }
}
