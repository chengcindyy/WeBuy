package com.csis4495_cmk.webuy.models;

import java.util.ArrayList;

public class Customer {
    private String uid;
    private String name;
    private String phone;
    private String address;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String profilePic;
    private String cartId;
    private ArrayList<String> watchList;
    private String birth;

    public Customer() {
        
    }

    public Customer(String uid) {
        this.uid = uid;
    }

    public Customer(String name, String phone, String address, String city, String province,
                    String postcode, ArrayList<String> favorite, String dob) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.province = province;
        this.postalCode = postcode;

        this.birth = dob;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
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

    public String getProfilePic() {
        return profilePic;
    }

    public String getCartId() {
        return cartId;
    }

    public ArrayList<String> getWatchList() {
        return watchList;
    }

    public void setWatchList(ArrayList<String> watchList) {
        this.watchList = watchList;
    }

    public String getBirth() {
        return birth;
    }

}
