package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Delivery;
import com.csis4495_cmk.webuy.viewmodels.CustomerCheckoutDataViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerCheckoutDeliveryMethodFragment extends BottomSheetDialogFragment {

    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
    private Button btnConfirm;
    private CustomerCheckoutDataViewModel model;
    private String payment;

    public CustomerCheckoutDeliveryMethodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_payment_selector, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(CustomerCheckoutDataViewModel.class);
        model.getSelectedDeliveryMethods().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Delivery>>() {
            @Override
            public void onChanged(HashMap<String, Delivery> deliveryHashMap) {
                Log.d("Test deliveryHashMap", "Size: "+deliveryHashMap.size());

            }
        });

        // CheckBox
        checkBox1 = view.findViewById(R.id.checkbox_child_1);
        checkBox2 = view.findViewById(R.id.checkbox_child_2);
        checkBox3 = view.findViewById(R.id.checkbox_child_3);
        checkBox4 = view.findViewById(R.id.checkbox_child_4);

        // Button
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBox1.isChecked()){
                    payment = "e-Transfer";
                } else if (checkBox2.isChecked()) {
                    payment = "Cash on delivery";
                } else if (checkBox3.isChecked()) {
                    payment = "Pay in store";
                } else {
                    payment = "Debit or Credit";
                }
                Log.d("PaymentType", "Selected payment: "+payment );

                model.payment(payment);
                dismiss();
            }
        });
    }
}