package com.csis4495_cmk.webuy.models;

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
    private String watchList[];
    private String birth;

    public Customer() {
        
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

    public String[] getWatchList() {
        return watchList;
    }

    public String getBirth() {
        return birth;
    }
}
