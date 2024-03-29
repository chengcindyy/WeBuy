package com.csis4495_cmk.webuy.models;

import java.util.List;

public class Seller {
    private String uid;
    private String name;
    private String phone;
    private String email;
    private Store storeInfo;


    public Seller() {

    }

    public Seller(String uid) {
        this.uid = uid;
    }

    public Seller(String name, String email, String phone, Store storeInfo) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.storeInfo = storeInfo;
    }

    public Store getStoreInfo() {
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
