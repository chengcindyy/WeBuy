package com.csis4495_cmk.webuy.models;

public class Order {
    private String customerId;
    private String orderList[];
    private String totalPrice;
    private String orderedDate;
    private String deliveryFee;
    private String address;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String paymentType;
    private int status;

    public Order() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public String[] getOrderList() {
        return orderList;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getOrderedDate() {
        return orderedDate;
    }

    public String getDeliveryFee() {
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

    public int getStatus() {
        return status;
    }
}
