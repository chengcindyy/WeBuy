package com.csis4495_cmk.webuy.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer implements Serializable {
    private String uid;
    private String name;
    private String phone;
    private String address;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String profilePic;
    private ArrayList<String> saveList;
    private ArrayList<CartItem> cart;
    private ArrayList<Wishlist> wishlist;

    private String birth;

    public Customer() {
        
    }

    public Customer(ArrayList<CartItem> cart) {
        this.cart = cart;
    }

    public Customer(String uid) {
        this.uid = uid;
    }

    public Customer(String name, String phone, String address, String city, String province,
                    String postcode, String dob, ArrayList<String> favorite) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.province = province;
        this.postalCode = postcode;
        this.birth = dob;
        this.saveList = favorite;
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

    public ArrayList<String> getSaveList() {
        return saveList;
    }

    public String getBirth() {
        return birth;
    }

    public ArrayList<CartItem> getCart() {
        return cart;
    }

    public ArrayList<Wishlist> getWishlist() {
        return wishlist;
    }

    public void setWishlist(ArrayList<Wishlist> wishlist) {
        this.wishlist = wishlist;
    }
}
