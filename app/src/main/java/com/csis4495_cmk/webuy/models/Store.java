package com.csis4495_cmk.webuy.models;

import java.util.ArrayList;

public class Store {

    private String owner; // sellerId
    private ArrayList<String> employeeList; // sellerIds
    private String storeName;
    //private String facebookGroup;
    // Full Address
    private String address;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String phone;
    private String intro;
    private String storePic;
    private String[] acceptedPaymentType;
    private ArrayList<DeliveryFee> deliveryFeeList;
    private String[] categories;

    public Store(String owner, ArrayList<String> employeeList, String storeName, String address,
                 String country, String province, String city, String postalCode, String phone,
                 String intro, String storePic, String[] acceptedPaymentType,
                 ArrayList<DeliveryFee> deliveryFeeList, String[] categories) {
        this.owner = owner;
        this.employeeList = employeeList;
        this.storeName = storeName;
        this.address = address;
        this.country = country;
        this.province = province;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
        this.intro = intro;
        this.storePic = storePic;
        this.acceptedPaymentType = acceptedPaymentType;
        this.deliveryFeeList = deliveryFeeList;
        this.categories = categories;
    }

    public String getOwner() {
        return owner;
    }

    public ArrayList<String> getEmployeeList() {
        return employeeList;
    }

    public String getStoreName() {
        return storeName;
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

    public String getPhone() {
        return phone;
    }

    public String getIntro() {
        return intro;
    }

    public String getStorePic() {
        return storePic;
    }

    public String[] getAcceptedPaymentType() {
        return acceptedPaymentType;
    }

    public ArrayList<DeliveryFee> getDeliveryFeeList() {
        return deliveryFeeList;
    }

    public String[] getCategories() {
        return categories;
    }
}
