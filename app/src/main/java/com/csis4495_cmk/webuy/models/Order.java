package com.csis4495_cmk.webuy.models;

import java.util.ArrayList;

public class Order {
    private String customerId;
    private ArrayList<String> productId_styleIdList;
    private ArrayList<Integer> amountList;
    private double totalPrice;
    //= itemsTotal + gstTotal + pstTotal + deliveryFee

    private double itemsTotal;
    //E {style price * qty}

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

    public Order() {
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
}
