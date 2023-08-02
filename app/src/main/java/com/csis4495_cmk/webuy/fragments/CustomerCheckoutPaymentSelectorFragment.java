package com.csis4495_cmk.webuy.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.viewmodels.CustomerCheckoutDataViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class CustomerCheckoutPaymentSelectorFragment extends BottomSheetDialogFragment {

    private RadioGroup radioGroup;
    private Button btnConfirm;
    private CustomerCheckoutDataViewModel model;
    private String payment;

    public CustomerCheckoutPaymentSelectorFragment() {
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

        radioGroup = view.findViewById(R.id.radio_group_container);

        // Button
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = radioGroup.findViewById(selectedId);
                if (selectedRadioButton != null) {
                    payment = selectedRadioButton.getText().toString();
                    Log.d("PaymentType", "Selected payment: " + payment);

                    model.payment(payment);
                    dismiss();
                }
            }
        });


        model = new ViewModelProvider(requireActivity()).get(CustomerCheckoutDataViewModel.class);
        model.getPayments().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> paymentsList) {
                Log.d("PaymentTypesList", "List: " + paymentsList.toString());
                for (String payment : paymentsList) {
                    RadioButton radioButton = view.findViewById(getRadioButtonId(payment));
                    if (radioButton != null) {
                        radioButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private int getRadioButtonId(String payment) {
        switch (payment) {
            case "e-Transfer":
                return R.id.radio_child_1;
            case "Cash on delivery":
                return R.id.radio_child_2;
            case "Pay in store":
                return R.id.radio_child_3;
            case "Debit or Credit":
                return R.id.radio_child_4;
            default:
                return -1;
        }
    }
}