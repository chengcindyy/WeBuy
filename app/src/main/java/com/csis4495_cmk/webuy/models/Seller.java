package com.csis4495_cmk.webuy.models;

public class Seller {
    private String uid;
    private String name;
    private String phone;
    private String address;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String profilePic;
    private String email;

    public Seller() {

    }

    public Seller(String uid) {
        this.uid = uid;
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

    public String getEmail() {
        return email;
    }

}
