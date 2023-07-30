package com.csis4495_cmk.webuy.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Delivery;
import com.csis4495_cmk.webuy.viewmodels.CustomerCheckoutDataViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.HashMap;
import java.util.Map;

public class CustomerCheckoutDeliveryMethodFragment extends BottomSheetDialogFragment {

    private Button btnConfirm;
    private CustomerCheckoutDataViewModel model;
    private View rootView;

    public CustomerCheckoutDeliveryMethodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_customer_payment_selector, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        model = new ViewModelProvider(requireActivity()).get(CustomerCheckoutDataViewModel.class);
        model.getSelectedDeliveryMethods().observe(getViewLifecycleOwner(), new Observer<HashMap<String, Delivery>>() {
            @Override
            public void onChanged(HashMap<String, Delivery> deliveryHashMap) {
                Log.d("Test deliveryHashMap", "Size: "+deliveryHashMap.size());
                // Find checkboxes layout
                LinearLayout layout = rootView.findViewById(R.id.checkbox_container);
                layout.removeAllViews();

                for (String key : deliveryHashMap.keySet()) {
                    CheckBox checkBox = new CheckBox(new ContextThemeWrapper(getContext(), R.style.DeliveryCheckBoxStyle));
                    Delivery delivery = deliveryHashMap.get(key);

                    String method = delivery.getDeliveredMethod();
                    String location;

                    if(method.contains("delivery")){
                        location = delivery.getDeliveryCity();
                        // Set icon drawable
                        checkBox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_local_shipping_24, 0, 0, 0);
                    }else{
                        location =  delivery.getPickUpLocation();
                        // Set icon drawable
                        checkBox.setCompoundDrawablesWithIntrinsicBounds(R.drawable.outline_storefront_24, 0, 0, 0);
                    }


                    String begin;
                    Double fee = .0;
                    Map<String, Double> feeMap = delivery.getFeeMap();
                    for (String feeKey : feeMap.keySet()) {
                        begin = feeKey.split("_")[1];
                        fee = feeMap.get(feeKey);
                        Log.d("Test feeMap", "from: "+ begin + " fee: "+ fee);
                    }
                    checkBox.setText(method + location + " | C$" + fee);

                    checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                    Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.ysabeauoffice_medium);
                    checkBox.setTypeface(typeface);

                    layout.addView(checkBox);
                }
            }
        });

        // Button
        btnConfirm = rootView.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout layout = rootView.findViewById(R.id.checkbox_container);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    if (child instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) child;
                        if (checkBox.isChecked()) {
                            // do something with the checked checkbox
                        }
                    }
                }
            }
        });
    }
}