package com.csis4495_cmk.webuy.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.csis4495_cmk.webuy.models.Delivery;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerCheckoutDataViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<String>> acceptedPaymentList = new MutableLiveData<>();
    private final MutableLiveData<String> selectedPayment = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String ,Delivery>> deliveryInfoList = new MutableLiveData<>();

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
}
