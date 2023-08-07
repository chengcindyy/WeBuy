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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerCheckoutDeliveryMethodFragment extends BottomSheetDialogFragment {

    private LinearLayout linearLayoutContactLayout, linearLayoutPickUpInfoLayout;
    private RadioGroup radioGroupContainer;
    private RadioButton radioButtonDelivery, radioButtonPickup;
    private Button btnConfirm, btnCheck;
    private TextInputLayout txvHDname, txvHDphone, txvEmail, txvAddress, txvPostalCode,  txvCity, txvPKname, txvPKphone;
    private AutoCompleteTextView txvProvince, txvCountry, txvLocation;
    private TextView txvShowFee;
    private CustomerCheckoutDataViewModel model;
    private double checkoutTotal;


    public CustomerCheckoutDeliveryMethodFragment(double checkoutTotal) {
        this.checkoutTotal = checkoutTotal;
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
        txvHDname = view.findViewById(R.id.edit_delivery_full_name);
        txvHDphone = view.findViewById(R.id.edit_delivery_phone);
        txvEmail = view.findViewById(R.id.edit_delivery_email);
        txvAddress = view.findViewById(R.id.edit_delivery_address);
        txvPostalCode = view.findViewById(R.id.edit_delivery_postal_code);
        txvCity = view.findViewById(R.id.edit_delivery_city);
        txvProvince = view.findViewById(R.id.autoComplete_delivery_province);
        setProvinceAutoCompleteAdapter(txvProvince);
        txvCountry = view.findViewById(R.id.autoComplete_delivery_country);
        setCountryAutoCompleteAdapter(txvCountry);
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
                Map <String, Map<String, Double>> HD_CityFeeMap = new HashMap<>();
                List<String> PK_locationsList = new ArrayList<>();
                //final Double[] shippingFeeContainer = {0.0};

                // Loop through the deliveryHashMap and check provided delivery services
                for (String key : deliveryHashMap.keySet()) {
                    Delivery delivery = deliveryHashMap.get(key);
                    String providedMethod = delivery.getDeliveredMethod();
                    String providedLocations = delivery.getPickUpLocation();

                    // Display provided options:
                    if ("[Home delivery] ".equals(providedMethod)) {
                        // 1. Delivery: if seller has provided delivery show the text fields
                        radioButtonDelivery.setVisibility(View.VISIBLE);

                        // Check HD shipping fee
                        HD_CityFeeMap.put(delivery.getDeliveryCity(), delivery.getFeeMap());

                    } else if ("[Self pick up] ".equals(providedMethod)) {
                        // 2. Pickup: if seller has provided delivery show store location dropdown
                        radioButtonPickup.setVisibility(View.VISIBLE);

                        String shippingFee = "";

                        // Check PK shipping fee
                        Map<String, Double> feeMap = delivery.getFeeMap();
                        for (String feeKey : feeMap.keySet()) {
                            Double keyPrice = Double.parseDouble(feeKey.split("_")[1]);
                            Double valuePrice = feeMap.get(feeKey);

                            if (checkoutTotal >= keyPrice){
                                if (valuePrice == 0){
                                    shippingFee = "Free";
                                } else {
                                    shippingFee = String.valueOf(valuePrice);
                                }
                            }
                        }
                        PK_locationsList.add(providedLocations+ " (CA$ "+ shippingFee+")");
                    }
                } //for

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

                                    if(!city.isEmpty()) {

                                        for (String hd_city: HD_CityFeeMap.keySet()) {
                                            String shippingFeeStr = "";
                                            Log.d("test city", hd_city);
                                            if (city.trim().equalsIgnoreCase(hd_city)){
                                                Log.d("test city", hd_city+ " "+ city.trim());
                                                // Matching shipping fees
                                                Map<String, Double> feeMap = HD_CityFeeMap.get(hd_city);
                                                for (String feeKey : feeMap.keySet()) {
                                                    Double keyPrice = Double.parseDouble(feeKey.split("_")[1]);
                                                    Double valuePrice = feeMap.get(feeKey);

                                                    if (checkoutTotal >= keyPrice){
                                                        if (valuePrice == 0){
                                                            shippingFeeStr = "CA$ Free delivery";
                                                        } else {
                                                            shippingFeeStr = "CA$ " + valuePrice;
                                                        }
                                                    }
                                                }
                                                // Set the fee to textView
                                                txvShowFee.setText("Shipped to " + hd_city + " (" + shippingFeeStr+")");
                                                Log.d("Test shipping fee", "shippingFee: "+shippingFeeStr);

                                                break;
                                            } else {
                                                Log.d("test city", hd_city+ " "+ city.trim());
                                                txvShowFee.setText("Cannot deliver to your city");
                                            }
                                        }

                                    } else {
                                        txvShowFee.setText("");
                                    }

                                }
                            });

                            // Send data to viewModel
                            btnConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String method = "Home delivery";
                                    String name = txvHDname.getEditText().getText().toString();
                                    String phone = txvHDphone.getEditText().getText().toString();
                                    String email = txvEmail.getEditText().getText().toString();
                                    String address = txvAddress.getEditText().getText().toString();
                                    String postalCode = txvPostalCode.getEditText().getText().toString();
                                    String province = txvProvince.getText().toString();
                                    String country = txvCountry.getText().toString();
                                    String city = txvCity.getEditText().getText().toString();

                                    btnCheck.performClick();

                                    String strShippingFee = txvShowFee.getText().toString();
                                    Double shippingFee = null;
                                    if (!strShippingFee.isEmpty() && !strShippingFee.equals("Cannot deliver to your city")) {
                                        if (strShippingFee.split("\\$ ")[1].split("\\)")[0].equals("Free delivery")) {
                                            shippingFee = 0.0;
                                        } else {
                                            shippingFee = Double.parseDouble(strShippingFee.split("\\$ ")[1].split("\\)")[0]);
                                        }
                                    }

                                    if (verifyingHomeDeliveryEnteredContent(name,phone,email,address,postalCode,city,country,province,shippingFee)) {
                                        CustomerCheckoutDataViewModel.ShipmentInfo shipmentInfo =
                                                new CustomerCheckoutDataViewModel.ShipmentInfo(method, name, phone, email, address, postalCode,
                                                        city,country, province, shippingFee);
                                        model.shipmentInfoObject(shipmentInfo);

                                        dismiss();
                                    } else {

                                    }

                                }
                            });
                            break;

                        // when user selected pickup
                        case R.id.radio_btn_self_pickup:
                            linearLayoutPickUpInfoLayout.setVisibility(View.VISIBLE);
                            linearLayoutContactLayout.setVisibility(View.GONE);

                            String[] locationsArray = PK_locationsList.toArray(new String[0]);
                            Log.d("Test location", "locationsArray: " + locationsArray);
                            setLocationsAutoCompleteAdapter(txvLocation, locationsArray);

                            btnConfirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    String method = "Store pickup"; //location + " " + "CA$ 8.0"
                                    String location = txvLocation.getText().toString().split(" \\(CA\\$")[0];
                                    String PKname = txvPKname.getEditText().getText().toString();
                                    String PKphone = txvPKphone.getEditText().getText().toString();
                                    String strShippingFee = txvLocation.getText().toString();
                                    Double shippingFee = null;

                                    if (!strShippingFee.isEmpty()) {
                                        if (strShippingFee.split("\\$ ")[1].split("\\)")[0].equals("Free")) {
                                            shippingFee = 0.0;
                                        } else {
                                            shippingFee = Double.parseDouble(strShippingFee.split("\\$ ")[1].split("\\)")[0]);
                                        }
                                    }

                                    Log.d("Test pickup info", "method: " + method + " location: " + location + " PKname: " + PKname + " PKphone: " + PKphone);

                                    if (verifyingPickUpEnteredContent(PKname,PKphone,location)) {
                                        CustomerCheckoutDataViewModel.ShipmentInfo shipmentInfo =
                                                new CustomerCheckoutDataViewModel.ShipmentInfo(method, PKname, PKphone, location, shippingFee);
                                        model.shipmentInfoObject(shipmentInfo);

                                        dismiss();
                                    } else {

                                    }
                                }
                            });
                            break;

                    }
                });
            }
        });

        // preset if user already have input data
        model.getShipmentInfoObject().observe(getViewLifecycleOwner(), new Observer<CustomerCheckoutDataViewModel.ShipmentInfo>() {
            @Override
            public void onChanged(CustomerCheckoutDataViewModel.ShipmentInfo shipmentInfo) {
                if (shipmentInfo.getMethod().equals("Home delivery")) {
                    radioButtonDelivery.setChecked(true);
                    txvHDname.getEditText().setText(shipmentInfo.getReceiver());
                    txvHDphone.getEditText().setText(shipmentInfo.getReceiver());
                    txvEmail.getEditText().setText(shipmentInfo.getEmail());
                    txvAddress.getEditText().setText(shipmentInfo.getAddress());
                    txvPostalCode.getEditText().setText(shipmentInfo.getPostalCode());
                    txvProvince.setText(shipmentInfo.getProvince()); //editText
                    txvCountry.setText(shipmentInfo.getCountry()); //editText
                    txvCity.getEditText().setText(shipmentInfo.getCity());
                    //check shipment fee
                    btnCheck.performClick();
                    Log.d("Test shipping fee", btnCheck.callOnClick() +"");

                } else if (shipmentInfo.getMethod().equals("Store pickup")) {
                    radioButtonPickup.setChecked(true);

                    Double shippingFee = shipmentInfo.getShippingFee();
                    if (shippingFee != null) {
                        String strShippingFee;
                        if (shippingFee == 0){
                            strShippingFee = "Free";
                        } else {
                            strShippingFee = String.valueOf(shippingFee);
                        }

                        if (shipmentInfo.getAddress() != "") {
                            txvLocation.setText(shipmentInfo.getAddress() + " (CA$ " + strShippingFee + ")");
                        }
                    }
                    txvPKname.getEditText().setText(shipmentInfo.getReceiver());
                    txvPKphone.getEditText().setText(shipmentInfo.getPhone());

                } else {
                    // no preselection
                }


            }
        });
    }

    private void setProvinceAutoCompleteAdapter(AutoCompleteTextView editProvince) {
        // States (Canada)
        String[] states = new String[]{"Alberta", "British Columbia", "Manitoba", "New Brunswick",
                "Newfoundland and Labrador", "Northwest Territories", "Nova Scotia", "Nunavut",
                "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Yukon"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, states);
        editProvince.setAdapter(stateAdapter);
        editProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCountryAutoCompleteAdapter(AutoCompleteTextView editCountry) {
        // Countries
        String[] countries = new String[]{"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe", "Palestine"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, countries);
        editCountry.setAdapter(countryAdapter);
        editCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
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


    private boolean verifyingHomeDeliveryEnteredContent(String name, String phone, String email, String address,
                                                        String postalCode, String city, String province,
                                                        String country, Double shipmentFee) {

        if (TextUtils.isEmpty(name)){
            txvHDname.setError("Name is required");
            txvHDname.requestFocus();
        } else if (TextUtils.isEmpty(phone)){
            txvHDphone.setError("Phone is required");
            txvHDphone.requestFocus();
        } else if (phone != null && phone.length() != 10) {
            txvHDphone.setError("Invalid phone number");
            txvHDphone.requestFocus();
        } else if (TextUtils.isEmpty(email)){
            txvEmail.setError("Email is required");
            txvEmail.requestFocus();
        } else if (email != null && !isValidEmail(email)) {
            txvHDphone.setError("Invalid email");
            txvHDphone.requestFocus();
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
        } else if (shipmentFee == null) {
            txvShowFee.setError("");
            txvShowFee.requestFocus();
        } else {
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean verifyingPickUpEnteredContent(String name, String phone, String pickupLocation) {

        if (TextUtils.isEmpty(name)){
            txvPKname.setError("Name is required");
            txvPKname.requestFocus();
        } else if (TextUtils.isEmpty(phone)){
            txvPKphone.setError("Phone is required");
            txvPKphone.requestFocus();
        } else if (phone != null && phone.length() != 10) {
            txvPKphone.setError("Invalid phone number");
            txvPKphone.requestFocus();
        } else if (TextUtils.isEmpty(pickupLocation)){
            txvLocation.setError("Location is required");
            txvLocation.requestFocus();
        } else {
            return true;
        }
        return false;
    }
}