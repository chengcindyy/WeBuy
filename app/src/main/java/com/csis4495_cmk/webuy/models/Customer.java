package com.csis4495_cmk.webuy.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

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

    private ArrayList<CartItem> cart ;
    private Map<String, Wishlist> wishlistMap;

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
                    String postcode, String dob) {
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

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public ArrayList<CartItem> getCart() {
        return cart;
    }

    public void setCart(ArrayList<CartItem> cart) {
        this.cart = cart;
    }

    public Map<String, Wishlist> getWishlistMap() {
        return wishlistMap;
    }

    public void setWishlistMap(Map<String, Wishlist> wishlistMap) {
        this.wishlistMap = wishlistMap;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
}
