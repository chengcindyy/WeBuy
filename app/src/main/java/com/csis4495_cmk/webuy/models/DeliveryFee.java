package com.csis4495_cmk.webuy.models;

public class DeliveryFee {
    private String deliveredMethod; //pickup, locations(Vancouver, Burnaby...)
    private double fee;

    public DeliveryFee(String deliveredMethod, double fee) {
        this.deliveredMethod = deliveredMethod;
        this.fee = fee;
    }

    public String getDeliveredMethod() {
        return deliveredMethod;
    }

    public double getFee() {
        return fee;
    }
}
