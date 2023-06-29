package com.csis4495_cmk.webuy.models;

public class DeliveryFee {
    private String deliveredMethod; //pickup, locations(Vancouver, Burnaby...)
    private String displayName;
    private String from;
    private double fee;

    public DeliveryFee() {
    }



    public String getDeliveredMethod() {
        return deliveredMethod;
    }

    public void setDeliveredMethod(String deliveredMethod) {
        this.deliveredMethod = deliveredMethod;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
