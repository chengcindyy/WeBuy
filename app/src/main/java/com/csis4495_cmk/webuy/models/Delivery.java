package com.csis4495_cmk.webuy.models;

public class Delivery {
    private String deliveredMethod; //pickup, locations(Vancouver, Burnaby...)
    private String displayName;
    private Double from;
    private double fee;

    public Delivery() {
    }

    public Delivery(String method, String name, Double from, double fee) {
        this.deliveredMethod = method;
        this.displayName = name;
        this.from = from;
        this.fee = fee;
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

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
