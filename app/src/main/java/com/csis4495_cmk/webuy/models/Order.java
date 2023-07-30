package com.csis4495_cmk.webuy.models;

import java.util.ArrayList;
import java.util.Map;

public class Order {
    private String customerId;
    private Map<String, Map<String, Integer>> groupsAndItemsMap; //groupId, p___ + productId + s___ + styleId
//    String styleId = key.split("s___")[1];
//    String productId = key.split("s___")[0].split("p___")[1];
//    p___thisIsAProductIds___thisIsAStyleId
    private double totalPrice;
    //= itemsTotal + gstTotal + pstTotal + deliveryFee
    private double itemsTotal;
    //= {style price * qty}
    private double gstTotal;
    private double pstTotal;
    private String orderedDate;
    private double deliveryFee;
    private String address;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String paymentType;
    private int orderStatus;

    public Order(String customerId, Map<String, Map<String, Integer>> groupsAndItemsMap, double totalPrice, double itemsTotal, double gstTotal, double pstTotal, String orderedDate,
                 double deliveryFee, String address, String country, String province, String city, String postalCode, String paymentType, int orderStatus) {
        this.customerId = customerId;
        this.groupsAndItemsMap = groupsAndItemsMap;
        this.totalPrice = totalPrice;
        this.itemsTotal = itemsTotal;
        this.gstTotal = gstTotal;
        this.pstTotal = pstTotal;
        this.orderedDate = orderedDate;
        this.deliveryFee = deliveryFee;
        this.address = address;
        this.country = country;
        this.province = province;
        this.city = city;
        this.postalCode = postalCode;
        this.paymentType = paymentType;
        this.orderStatus = orderStatus;
    }

    public Order() {
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setGroupsAndItemsMap(Map<String, Map<String, Integer>> groupsAndItemsMap) {
        this.groupsAndItemsMap = groupsAndItemsMap;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setItemsTotal(double itemsTotal) {
        this.itemsTotal = itemsTotal;
    }

    public void setGstTotal(double gstTotal) {
        this.gstTotal = gstTotal;
    }

    public void setPstTotal(double pstTotal) {
        this.pstTotal = pstTotal;
    }

    public void setOrderedDate(String orderedDate) {
        this.orderedDate = orderedDate;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getCustomerId() {
        return customerId;
    }


    public double getTotalPrice() {
        return totalPrice;
    }

    public String getOrderedDate() {
        return orderedDate;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public Map<String, Map<String, Integer>> getGroupsAndItemsMap() {
        return groupsAndItemsMap;
    }

    public double getItemsTotal() {
        return itemsTotal;
    }

    public double getGstTotal() {
        return gstTotal;
    }

    public double getPstTotal() {
        return pstTotal;
    }
}
