package com.csis4495_cmk.webuy.models;

import java.util.ArrayList;

public class Store {

    private String storeName;
    //private String facebookGroup;
    private String email;
    private String phone;
    // Full Address
    private String address;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String intro;
    private String storePic;
    private ArrayList<String> employeeList; // sellerIds
    private String[] acceptedPaymentType;
    private ArrayList<DeliveryFee> deliveryFeeList;
    private String[] categories;

    public Store() {
    }

    public Store(String storeName) {
        this.storeName = storeName;
    }

    public Store(String storeName, String email, String phone, String address, String city, String province, String country, String postalCode, String intro) {
        this.storeName = storeName;
        this.email = email;
        this.address = address;
        this.country = country;
        this.province = province;
        this.city = city;
        this.postalCode = postalCode;
        this.phone = phone;
        this.intro = intro;
    }

    public ArrayList<String> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(ArrayList<String> employeeList) {
        this.employeeList = employeeList;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getStorePic() {
        return storePic;
    }

    public void setStorePic(String storePic) {
        this.storePic = storePic;
    }

    public String[] getAcceptedPaymentType() {
        return acceptedPaymentType;
    }

    public void setAcceptedPaymentType(String[] acceptedPaymentType) {
        this.acceptedPaymentType = acceptedPaymentType;
    }

    public ArrayList<DeliveryFee> getDeliveryFeeList() {
        return deliveryFeeList;
    }

    public void setDeliveryFeeList(ArrayList<DeliveryFee> deliveryFeeList) {
        this.deliveryFeeList = deliveryFeeList;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }
}