package com.csis4495_cmk.webuy.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.Delivery;
import com.csis4495_cmk.webuy.models.Order;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerCheckoutDataViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<String>> acceptedPaymentList = new MutableLiveData<>();
    private final MutableLiveData<String> selectedPayment = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, Delivery>> deliveryInfoList = new MutableLiveData<>();
    private final MutableLiveData<String> selectedDeliveryMethod = new MutableLiveData<>();
    private final MutableLiveData<ShipmentInfo> ShipmentInfoList = new MutableLiveData<>();



    public void shipmentInfoObject(ShipmentInfo items) {
        ShipmentInfoList.setValue(items);
        Log.d("Test shipmentInfoObject",
                    "name: "+ items.receiver +
                        "phone: " + items.phone +
                        "email: " + items.email +
                        "address: " + items.address +
                        "city: " + items.city +
                        "postalCode: " + items.postalCode +
                        "province: " + items.province +
                        "country: " + items.country +
                        "deliveryFee: " + items.shippingFee);
    }

    public MutableLiveData<ShipmentInfo> getShipmentInfoObject() {
        return ShipmentInfoList;

    }

    public void deliveryMethod(String method) {
        selectedDeliveryMethod.setValue(method);
    }

    public MutableLiveData<String> getDeliveryMethod() {
        return selectedDeliveryMethod;
    }

    public void deliveryMethods(HashMap<String, Delivery> deliveryInfoListMap) {
        deliveryInfoList.setValue(deliveryInfoListMap);
    }

    public MutableLiveData<HashMap<String, Delivery>> getSelectedDeliveryMethods() {
        return deliveryInfoList;
    }

    public void payment(String payment) {
        selectedPayment.setValue(payment);
    }

    public MutableLiveData<String> getSelectedPayment() {
        return selectedPayment;
    }

    public void payments(ArrayList<String> items) {
        acceptedPaymentList.setValue(items);
    }

    public MutableLiveData<ArrayList<String>> getPayments() {
        return acceptedPaymentList;
    }

    public static class ShipmentInfo{
        private String receiver;
        private String phone;
        private String email;
        private String address;
        private String postalCode;
        private String city;
        private String country;
        private String province;
        private Double shippingFee;
        private String method;

        public ShipmentInfo() {
        }

        // home delivery
        public ShipmentInfo(String method, String receiver, String phone, String email, String address, String postalCode,
                            String city, String country, String province, Double shippingFee) {
            this.method = method; //Home delivery
            this.receiver = receiver;
            this.phone = phone;
            this.email = email;
            this.address = address;
            this.postalCode = postalCode;
            this.city = city;
            this.country = country;
            this.province = province;
            this.shippingFee = shippingFee;

        }


        // pick up
        public ShipmentInfo(String method, String receiver, String phone, String address, Double shippingFee) {
            this.method = method; //Store pickup
            this.receiver = receiver;
            this.phone = phone;
            this.address = address;
            this.shippingFee = shippingFee;
        }

        public String getReceiver() {
            return receiver;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getAddress() {
            return address;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        public String getProvince() {
            return province;
        }

        public Double getShippingFee() {
            return shippingFee;
        }

        public String getMethod() {
            return method;
        }
    }




}
