package com.csis4495_cmk.webuy.models;

import java.util.Map;

public class Delivery {
    private String deliveredMethod; //Self pick up / Home delivery
    private String pickUpLocation;
    private String deliveryCity;
    private Map<String, Double> feeMap;

    public Delivery() {
    }

    public Delivery(String method, String location, Map<String, Double> feeMap) {
        this.deliveredMethod = method;
        this.pickUpLocation = location;
        this.feeMap = feeMap;
    }

    public Delivery(String method, Map<String, Double> feeMap, String city) {
        this.deliveredMethod = method;
        this.feeMap = feeMap;
        this.deliveryCity = city;
    }


    public String getDeliveredMethod() {
        return deliveredMethod;
    }

    public void setDeliveredMethod(String deliveredMethod) {
        this.deliveredMethod = deliveredMethod;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public Map<String, Double> getFeeMap() {
        return feeMap;
    }

    public void setFeeMap(Map<String, Double> feeMap) {
        this.feeMap = feeMap;
    }

    public String getDeliveryCity() {
        return deliveryCity;
    }

    public void setDeliveryCity(String deliveryCity) {
        this.deliveryCity = deliveryCity;
    }
}
