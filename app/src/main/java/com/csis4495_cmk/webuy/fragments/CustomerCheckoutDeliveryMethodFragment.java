package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Delivery;
import com.csis4495_cmk.webuy.viewmodels.CustomerCheckoutDataViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerCheckoutDeliveryMethodFragment extends BottomSheetDialogFragment {

    private LinearLayout linearLayoutContactLayout, linearLayoutPickUpInfoLayout;
    private RadioGroup radioGroupContainer;
    private RadioButton radioButtonDelivery, radioButtonPickup;
    private Button btnConfirm, btnCheck;
    private TextInputLayout txvName, txvPhone, txvEmail, txvAddress, txvPostalCode,  txvCity, txvPKname, txvPKphone;
    private AutoCompleteTextView txvProvince, txvCountry, txvLocation;
    private TextView txvShowFee;
    private CustomerCheckoutDataViewModel model;
    private Boolean isDataVerified = false;

    public CustomerCheckoutDeliveryMethodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_delivery_selector, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Layouts
        linearLayoutContactLayout = view.findViewById(R.id.linearLayout_contact_info_layout);
        linearLayoutPickUpInfoLayout = view.findViewById(R.id.linearLayout_pick_up_info_layout);

        // Radio Buttons
        radioGroupContainer = view.findViewById(R.id.radio_group_container);
        radioButtonDelivery = view.findViewById(R.id.radio_btn_home_delivery);
        radioButtonPickup = view.findViewById(R.id.radio_btn_self_pickup);

        // TextViews
        txvName = view.findViewById(R.id.edit_delivery_full_name);
        txvPhone = view.findViewById(R.id.edit_delivery_phone);
        txvEmail = view.findViewById(R.id.edit_delivery_email);
        txvAddress = view.findViewById(R.id.edit_delivery_address);
        txvPostalCode = view.findViewById(R.id.edit_delivery_postal_code);
        txvCity = view.findViewById(R.id.edit_delivery_city);
        txvProvince = view.findViewById(R.id.autoComplete_delivery_province);
        txvCountry = view.findViewById(R.id.autoComplete_delivery_country);
        txvLocation = view.findViewById(R.id.autoComplete_delivery_store_location);
        txvShowFee = view.findViewById(R.id.txv_show_fee);
        txvPKname = view.findViewById(R.id.edit_pick_up_full_name);
        txvPKphone = view.findViewById(R.id.edit_pick_up_phone);
        // Button
        btnCheck = view.findViewById(R.id.btn_check_shipping_fee);
        btnConfirm = view.findViewById(R.id.btn_confirm);

        // Monitor data from CustomerCheckoutFragment: Get deliveryHashMap
        model = new ViewModelProvider(requireActivity()).get(CustomerCheckoutDataViewModel.class);
        model.getSelectedDeliveryMethods().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Delivery>>() {
            @Override
            public void onChanged(HashMap<String, Delivery> deliveryHashMap) {
                Log.d("Test deliveryHashMap", "Size: " + deliveryHashMap.size());
                List<String> locationsList = new ArrayList<>();
                final Double[] shippingFeeContainer = {0.0};
                Double orderPrice = 100.0;

                // Loop through the deliveryHashMap and check provided delivery services
                for (String key : deliveryHashMap.keySet()) {
                    Delivery delivery = deliveryHashMap.get(key);
                    String providedMethods = delivery.getDeliveredMethod();
                    String providedLocations = delivery.getPickUpLocation();
                    Log.d("Test deliveryHashMap", "Methods: "+providedMethods);

                    // Display provided options:
                    if ("[Home delivery] ".equals(providedMethods)) {
                        // 1. Delivery: if seller has provided delivery show the text fields
                        radioButtonDelivery.setVisibility(View.VISIBLE);
                    } else if ("[Self pick up] ".equals(providedMethods)) {
                        // 2. Pickup: if seller has provided delivery show store location dropdown
                        radioButtonPickup.setVisibility(View.VISIBLE);

                        String shippingFee = "";

                        // Check shipping fee
                        Map<String, Double> feeMap = delivery.getFeeMap();
                        for (String feeKey : feeMap.keySet()) {
                            Double keyPrice = Double.parseDouble(feeKey.split("_")[1]);
                            Double valuePrice = feeMap.get(feeKey);

                            if (orderPrice >= keyPrice){
                                if (valuePrice == 0){
                                    shippingFee = "Free";
                                } else {
                                    shippingFee = String.valueOf(valuePrice);
                                }
                            }
                        }
                        locationsList.add(providedLocations+ " (CA$:"+ shippingFee+")");
                    }

                    // Display more options:
                    radioGroupContainer.setOnCheckedChangeListener((radioGroup, checkedId) -> {
                        switch (checkedId) {
                            // when user selected delivery, do...
                            case R.id.radio_btn_home_delivery:
                                linearLayoutContactLayout.setVisibility(View.VISIBLE);
                                linearLayoutPickUpInfoLayout.setVisibility(View.GONE);

                                // Check shipping fee
                                btnCheck.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Log.d("Test shipping fee", "btnCheck clicked");
                                        // Get data from text fields
                                        String city = txvCity.getEditText().getText().toString();
                                        // Get city : It's better to use API to check the location
                                        String deliveryLocation = delivery.getDeliveryCity();
                                        Log.d("Test shipping fee", "city: " + city + " deliveryLocation: " + deliveryLocation);

                                        String shippingFeeStr = "";

                                        if (city.trim().equalsIgnoreCase(deliveryLocation)){
                                            // Matching shipping fees
                                            Map<String, Double> feeMap = delivery.getFeeMap();
                                            for (String feeKey : feeMap.keySet()) {
                                                Double keyPrice = Double.parseDouble(feeKey.split("_")[1]);
                                                Double valuePrice = feeMap.get(feeKey);

                                                if (orderPrice >= keyPrice){
                                                    if (valuePrice == 0){
                                                        shippingFeeStr = "Free delivery";
                                                    } else {
                                                        shippingFeeStr = "CA$"+valuePrice;
                                                    }
                                                }
                                                shippingFeeContainer[0] = valuePrice;
                                            }
                                            // Set the fee to textView
                                            txvShowFee.setText("Shipped to " + deliveryLocation + " (" + shippingFeeStr+")");

                                            Log.d("Test shipping fee", "shippingFee: "+shippingFeeStr);
                                        }
                                    }
                                });

                                // Send data to viewModel
                                btnConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String name = txvName.getEditText().getText().toString();
                                        String phone = txvPhone.getEditText().getText().toString();
                                        String email = txvEmail.getEditText().getText().toString();
                                        String address = txvAddress.getEditText().getText().toString();
                                        String postalCode = txvPostalCode.getEditText().getText().toString();
                                        String province = txvProvince.getText().toString();
                                        String country = txvCountry.getText().toString();
                                        String city = txvCity.getEditText().getText().toString();

                                        CustomerCheckoutDataViewModel.ShipmentInfo shipmentInfo =
                                                new CustomerCheckoutDataViewModel.ShipmentInfo("Home delivery", name, phone, email, address, postalCode, province, country, city, shippingFeeContainer[0]);
                                        model.shipmentInfoObject(shipmentInfo);
                                        dismiss();
                                    }
                                });
                                break;

                            // when user selected pickup
                            case R.id.radio_btn_self_pickup:
                                linearLayoutPickUpInfoLayout.setVisibility(View.VISIBLE);
                                linearLayoutContactLayout.setVisibility(View.GONE);

                                String[] locationsArray = locationsList.toArray(new String[0]);
                                Log.d("Test location", "locationsArray: " + locationsArray);
                                setLocationsAutoCompleteAdapter(txvLocation, locationsArray);

                                btnConfirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        String method = txvLocation.getText().toString();
                                        String location = txvLocation.getText().toString().split(" ")[0];
                                        String PKname = txvPKname.getEditText().getText().toString();
                                        String PKphone = txvPKphone.getEditText().getText().toString();

                                        Log.d("Test pickup info", "method: " + method + " location: " + location + " PKname: " + PKname + " PKphone: " + PKphone);

                                        // Set to Order
                                        CustomerCheckoutDataViewModel.ShipmentInfo shipmentInfo =
                                                new CustomerCheckoutDataViewModel.ShipmentInfo(method, PKname, PKphone, location, shippingFeeContainer[0]);
                                        model.shipmentInfoObject(shipmentInfo);
                                        dismiss();
                                    }
                                });
                                break;

                        }
                    });
                }
            }
        });
    }

    private void setLocationsAutoCompleteAdapter(AutoCompleteTextView editLocation, String[] locations) {
        Log.d("Test location", "locationsInAdapter: "+ locations[0]);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, locations);
        editLocation.setAdapter(locationAdapter);
        editLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void VerifyingEnteredContent(String name, String phone, String email, String address, String postalCode, String city, String province, String country) {
        if (TextUtils.isEmpty(name)){
            txvName.setError("Name is required");
            txvName.requestFocus();
        } else if (TextUtils.isEmpty(phone)){
            txvPhone.setError("Phone is required");
            txvPhone.requestFocus();
        } else if (TextUtils.isEmpty(email)){
            txvEmail.setError("Email is required");
            txvEmail.requestFocus();
        } else if (TextUtils.isEmpty(address)){
            txvAddress.setError("Address is required");
            txvAddress.requestFocus();
        } else if (TextUtils.isEmpty(postalCode)){
            txvPostalCode.setError("PostalCode is required");
            txvPostalCode.requestFocus();
        } else if (TextUtils.isEmpty(city)){
            txvCity.setError("City is required");
            txvCity.requestFocus();
        } else if (TextUtils.isEmpty(province)){
            txvProvince.setError("Province is required");
            txvProvince.requestFocus();
        } else if (TextUtils.isEmpty(country)){
            txvCountry.setError("Country is required");
            txvCountry.requestFocus();
        } else {
            isDataVerified = true;
        }
    }
}