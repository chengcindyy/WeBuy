package com.csis4495_cmk.webuy.models;

import java.util.List;

public class Seller {
    private String uid;
    private String name;
    private String phone;
    private String email;
    private List<Store> storeInfo;


    public Seller() {

    }

    public Seller(String uid) {
        this.uid = uid;
    }

    public Seller(String name, String phone, String email, List<Store> storeInfo) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.storeInfo = storeInfo;
    }

    public List<Store> getStoreInfo() {
        return storeInfo;
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

    public String getEmail() {
        return email;
    }

}
