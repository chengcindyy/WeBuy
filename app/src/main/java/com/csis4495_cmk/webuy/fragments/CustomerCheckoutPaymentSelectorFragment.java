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

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.viewmodels.CustomerCheckoutDataViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class CustomerCheckoutPaymentSelectorFragment extends BottomSheetDialogFragment {

    private CheckBox checkBoxEmt, checkBoxCashDelivery, checkBoxCashStore, checkBoxCard;
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

        model = new ViewModelProvider(requireActivity()).get(CustomerCheckoutDataViewModel.class);
        model.getPayments().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> paymentsList) {
                Log.d("PaymentTypesList", "List: " + paymentsList.toString());
                for (String payment : paymentsList) {
                    if (payment.equals("e-Transfer")) {
                        checkBoxEmt.setVisibility(View.VISIBLE);
                    } else if (payment.equals("Cash on delivery")) {
                        checkBoxCashDelivery.setVisibility(View.VISIBLE);
                    } else if (payment.equals("Pay in store")) {
                        checkBoxCashStore.setVisibility(View.VISIBLE);
                    } else if (payment.equals("Debit or Credit")) {
                        checkBoxCard.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // CheckBox
        checkBoxEmt = view.findViewById(R.id.checkbox_child_1);
        checkBoxCashDelivery = view.findViewById(R.id.checkbox_child_2);
        checkBoxCashStore  = view.findViewById(R.id.checkbox_child_3);
        checkBoxCard = view.findViewById(R.id.checkbox_child_4);

        // Button
        btnConfirm = view.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxEmt.isChecked()){
                    payment = "e-Transfer";
                } else if (checkBoxCashDelivery.isChecked()) {
                    payment = "Cash on delivery";
                } else if (checkBoxCashStore.isChecked()) {
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