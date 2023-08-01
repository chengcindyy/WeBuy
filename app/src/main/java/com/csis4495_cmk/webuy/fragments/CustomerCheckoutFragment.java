package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.viewmodels.CartItemsViewModel;
import com.csis4495_cmk.webuy.adapters.CustomerCheckoutShoppingDetailsAdapter;
import com.csis4495_cmk.webuy.models.Delivery;
import com.csis4495_cmk.webuy.viewmodels.CustomerCheckoutDataViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerCheckoutFragment extends Fragment {

    final DatabaseReference sellersRef = FirebaseDatabase.getInstance().getReference("Seller");
    private CustomerCheckoutDataViewModel model;
    private RecyclerView rvShoppingItemList;
    private TextView txvStoreName, txvShipment, txvReceiverPhone, txvAddress,
                     txvPayment, txvProductAmount, txvShipmentAmount, txvGstAmount, txvPstAmount, txvOrderTotalPrice;
    private EditText customerNote;
    private CustomerCheckoutShoppingDetailsAdapter adapter;
    private CartItemsViewModel cartItemsViewModel;

    public CustomerCheckoutFragment(){}
    public CustomerCheckoutFragment(CartItemsViewModel cartItemsViewModel) {
        this.cartItemsViewModel = cartItemsViewModel;
    }
    public static CustomerCheckoutFragment newInstance(CartItemsViewModel cartItemsViewModel){
        CustomerCheckoutFragment fragment = new CustomerCheckoutFragment(cartItemsViewModel);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_checkout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialized viewModel
        model = new ViewModelProvider(requireActivity()).get(CustomerCheckoutDataViewModel.class);
        // RecyclerView: shopping items
        rvShoppingItemList = view.findViewById(R.id.recyclerView_shopping_info);
        rvShoppingItemList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CustomerCheckoutShoppingDetailsAdapter(getContext());
        // TextViews
        txvStoreName = view.findViewById(R.id.txv_store_name);
        txvShipment = view.findViewById(R.id.txv_shipment_method);
        setShipmentClickListener(txvShipment);
        txvReceiverPhone = view.findViewById(R.id.txv_shipment_method_name_phone);
        txvAddress = view.findViewById(R.id.txv_shipment_method_address);
        txvPayment = view.findViewById(R.id.txv_payment);
        setPaymentClickListener(txvPayment);
        txvProductAmount = view.findViewById(R.id.txv_checkout_total_amount);
        txvShipmentAmount = view.findViewById(R.id.txv_checkout_shipment_amount);
        txvGstAmount = view.findViewById(R.id.txv_checkout_gst_amount);
        txvPstAmount = view.findViewById(R.id.txv_checkout_pst_amount);
        txvOrderTotalPrice = view.findViewById(R.id.txv_checkout_order_total_amount);
        // Edittext
        customerNote = view.findViewById(R.id.edt_customer_notes);


    }

    private void setShipmentClickListener(TextView txvShipment) {
        txvShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // TODO: get sellerId from cart recyclerView position
                        String selectedProductSellerId = "WWQyDcHPuuOjoMwRCbY1EBWChSX2";

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String sellerId = childSnapshot.getKey();
                            if (selectedProductSellerId.equals(sellerId)) {
                                DataSnapshot storeInfoSnapshot = childSnapshot.child("storeInfo");
                                DataSnapshot deliveryInfoListSnapshot = storeInfoSnapshot.child("deliveryInfoList");

                                HashMap<String, Delivery> deliveryInfoListMap = new HashMap<>();
                                for (DataSnapshot deliverySnapshot : deliveryInfoListSnapshot.getChildren()) {
                                    String key = deliverySnapshot.getKey();
                                    Delivery delivery = deliverySnapshot.getValue(Delivery.class);
                                    deliveryInfoListMap.put(key, delivery);
                                }

                                Log.d("Test deliveryHashMap", "In Sheet's Size: "+deliveryInfoListMap.size());
                                model.deliveryMethods(deliveryInfoListMap);
                                break;
                            }
                        }


                        // Create BottomSheet
                        CustomerCheckoutDeliveryMethodFragment bottomSheetDialog = new CustomerCheckoutDeliveryMethodFragment();
                        bottomSheetDialog.show(getParentFragmentManager(), "Delivery BottomSheet Show");


//                        // Set text for payment
//                        model.getSelectedPayment().observe(getViewLifecycleOwner(), new Observer<String>() {
//                            @Override
//                            public void onChanged(String payment) {
//                                if(payment != null){
//                                    txvPayment.setText(payment);
//                                }
//                            }
//                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void setPaymentClickListener(TextView txvPayment) {
        txvPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> acceptedPaymentTypes;
                        // TODO: get sellerId from cart recyclerView position
                        String selectedProductSellerId = "WWQyDcHPuuOjoMwRCbY1EBWChSX2";

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String sellerId = childSnapshot.getKey();
                            if (selectedProductSellerId.equals(sellerId)) {
                                DataSnapshot storeInfoSnapshot = childSnapshot.child("storeInfo");
                                acceptedPaymentTypes = (ArrayList<String>) storeInfoSnapshot.child("acceptedPaymentTypes").getValue();
                                model.payments(acceptedPaymentTypes);
                                break;
                            }
                        }

                        // Create BottomSheet
                        CustomerCheckoutPaymentSelectorFragment bottomSheetDialog = new CustomerCheckoutPaymentSelectorFragment();
                        bottomSheetDialog.show(getParentFragmentManager(), "Payment BottomSheet Show");


                        // Set text for payment
                        model.getSelectedPayment().observe(getViewLifecycleOwner(), new Observer<String>() {
                            @Override
                            public void onChanged(String payment) {
                                if(payment != null){
                                    txvPayment.setText(payment);
                                }
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}