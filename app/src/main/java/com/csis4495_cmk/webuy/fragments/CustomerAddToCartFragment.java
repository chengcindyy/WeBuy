package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Group;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerAddToCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerAddToCartFragment extends BottomSheetDialogFragment {

    private final static String ARG_GROUPID = "groupId";
    private final static String ARG_GROUPJSON = "groupJson";
    private String groupId;
    private String groupJson;
    private Group group;


    public CustomerAddToCartFragment() {
        // Required empty public constructor
    }

    public static CustomerAddToCartFragment newInstance(String groupId, String groupJson) {
        CustomerAddToCartFragment fragment = new CustomerAddToCartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUPID, groupId);
        args.putString(ARG_GROUPJSON, groupJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUPID);
            groupJson = getArguments().getString(ARG_GROUPJSON);

            // Convert the JSON string back to a Group object
            Gson gson = new Gson();
            group = gson.fromJson(groupJson, Group.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_add_to_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView imvGroupPic = view.findViewById(R.id.imv_add_to_cart_group_pic);
        TextView tvGroupName = view.findViewById(R.id.tv_add_to_cart_group_name);
        TextView tvGroupPrice = view.findViewById(R.id.tv_add_to_cart_group_price);
        TextView tvInventoryAmount = view.findViewById(R.id.tv_add_to_cart_inventory_amount);
        AutoCompleteTextView atvGroupStyle = view.findViewById(R.id.text_input_add_to_cart_group_style);
        Button btnDecrease = view.findViewById(R.id.btn_decrease_amount);
        Button btnIncrease = view.findViewById(R.id.btn_increase_amount);
        EditText etOrderAmount = view.findViewById(R.id.edi_add_to_cart_order_amount);
        Button btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        Button btnDirectCheckout = view.findViewById(R.id.btn_direct_checkout);

        //set the group
        //TODO: imageView, change according to the style
        group.getGroupImages().get(0);
        tvGroupName.setText(group.getGroupName());
        tvGroupPrice.setText(group.getGroupPrice()); //TODO: change according to the style
        int inventoryAmount = 5;
        tvInventoryAmount.setText(inventoryAmount + " left"); //TODO: get product inventory amount
        //TODO: atvGroupStyle
        btnDecrease.setOnClickListener(v -> {
            int orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            Log.d("testAmount", orderAmount +"");
            if(orderAmount > 1) {
                if (orderAmount < inventoryAmount) {
                    btnIncrease.setEnabled(true);
                }
                orderAmount--;
                Log.d("testAmount", orderAmount +"--");
                etOrderAmount.setText(String.valueOf(orderAmount));
                if(orderAmount == 1) {
                    btnDecrease.setEnabled(false);
                }
            }
        });
        btnIncrease.setOnClickListener(v -> {
            int orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            if(orderAmount >= inventoryAmount) {
                btnIncrease.setEnabled(false);
            } else {
                orderAmount++;
                etOrderAmount.setText(String.valueOf(orderAmount));
                btnDecrease.setEnabled(true);
            }
        });
        etOrderAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString().trim();
                if (!inputText.isEmpty()) {
                    try {
                        int amount = Integer.parseInt(inputText);
                        if (amount <= 0 || amount > inventoryAmount) {
                            // Show an error or notify the user that the amount is invalid
                            etOrderAmount.setError("Amount must be greater than 0 and less than "+inventoryAmount);
                        } else {
                            // The amount is valid, do something with it
                        }
                    } catch (NumberFormatException e) {
                        // Handle the case when the input cannot be parsed as an integer
                        etOrderAmount.setError("Invalid input");
                    }
                } else {
                    // Handle the case when the input is empty
                    etOrderAmount.setError("Amount is required");
                }
            }
        });
    }
}