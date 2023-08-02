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
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.viewmodels.CustomerCartItemsViewModel;
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
import java.util.Map;

public class CustomerCheckoutFragment extends Fragment {

    final DatabaseReference sellersRef = FirebaseDatabase.getInstance().getReference("Seller");
    private CustomerCheckoutDataViewModel model;
    private CustomerCartItemsViewModel cartItemsViewModel;
    private ArrayList<CartItem> shoppingItems = new ArrayList<>();
    private ArrayList<CustomerCartItemsViewModel.CartItemInfo> cartItemInfos = new ArrayList<>();
    private String sellerId;
    private RecyclerView rvShoppingItemList;
    private TextView txvStoreName, txvShipment, txvReceiverPhone, txvAddress,
                     txvPayment, txvCheckoutAmount, txvShipmentAmount, txvGstAmount, txvPstAmount, txvOrderTotalPrice;
    private EditText customerNote;
    private CustomerCheckoutShoppingDetailsAdapter adapter;
    private CustomerCartItemsViewModel customerCartItemsViewModel;
    private double checkoutTotal = 0;
    private double gstTotal = 0;
    private double pstTotal = 0;
    private double orderTotal = 0;

    public CustomerCheckoutFragment(){}

    public static CustomerCheckoutFragment newInstance(){
        CustomerCheckoutFragment fragment = new CustomerCheckoutFragment();
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
        cartItemsViewModel = new ViewModelProvider(requireActivity()).get(CustomerCartItemsViewModel.class);
        //set shoppingItems and cartInfos
        cartItemsViewModel.getSellerItemsMapLiveData().observe(getViewLifecycleOwner(), new Observer<Map<String, ArrayList<CartItem>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<CartItem>> stringArrayListMap) {
                //clear data
                shoppingItems.clear();
                cartItemInfos.clear();
                checkoutTotal = 0;
                gstTotal = 0;
                pstTotal = 0;

                for (String sellerId: stringArrayListMap.keySet()) {
                    for (CartItem cartItem: stringArrayListMap.get(sellerId)) {
                        if (cartItem.getChecked()) {
                            shoppingItems.add(cartItem);
                        }
                    }
                }

                cartItemsViewModel.getCartItemsInfoMapLiveData().observe(getViewLifecycleOwner(), cartItemCartItemInfoMap -> {
                    for (CartItem cartItem: shoppingItems) {
                        CustomerCartItemsViewModel.CartItemInfo cartItemInfo = cartItemCartItemInfoMap.get(cartItem);
                        cartItemInfos.add(cartItemInfo);

                        //calculate total
                        int amount = cartItem.getAmount();
                        double price = Double.parseDouble(cartItemInfo.getGroupPrice().split("CA\\$ ")[1]);
                        double subTotal = price * amount;

                        checkoutTotal += subTotal;
                        switch (cartItemInfo.getProductTax()) {
                            case 0:
                                break;
                            case 1:
                                gstTotal += 0.05 * subTotal;
                                break;
                            case 2:
                                gstTotal += 0.05 * subTotal;
                                pstTotal += 0.07 * subTotal;
                                break;
                        }
                    }
                    Log.d("shopping", "Item size:"+shoppingItems.size()+ " Info size:"+ cartItemInfos.size());
                    orderTotal = checkoutTotal + gstTotal + pstTotal;
                    txvCheckoutAmount.setText("CA$ "+ String.format("%.2f", checkoutTotal));
                    txvGstAmount.setText("CA$ "+ String.format("%.2f", gstTotal));
                    txvPstAmount.setText("CA$ "+ String.format("%.2f", pstTotal));
                    txvOrderTotalPrice.setText("CA$ " + String.format("%.2f", orderTotal));

                    // two maps ready

                    //set recyclerview
                    rvShoppingItemList.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new CustomerCheckoutShoppingDetailsAdapter(getContext(),shoppingItems,cartItemInfos);
                    rvShoppingItemList.setAdapter(adapter);

                });
                // set seller information
                setSellerInfo();
            }
        });

        // RecyclerView: shopping items
        rvShoppingItemList = view.findViewById(R.id.recyclerView_shopping_info);
        // TextViews
        txvStoreName = view.findViewById(R.id.txv_store_name);
        txvShipment = view.findViewById(R.id.txv_shipment_method);
        setShipmentClickListener(txvShipment);
        txvReceiverPhone = view.findViewById(R.id.txv_shipment_method_name_phone);
        txvAddress = view.findViewById(R.id.txv_shipment_method_address);
        txvPayment = view.findViewById(R.id.txv_payment);
        setPaymentClickListener(txvPayment);
        txvCheckoutAmount = view.findViewById(R.id.txv_checkout_total_amount);
        txvShipmentAmount = view.findViewById(R.id.txv_checkout_shipment_amount);
        txvGstAmount = view.findViewById(R.id.txv_checkout_gst_amount);
        txvPstAmount = view.findViewById(R.id.txv_checkout_pst_amount);
        txvOrderTotalPrice = view.findViewById(R.id.txv_checkout_order_total_amount);
        // Edittext
        customerNote = view.findViewById(R.id.edt_customer_notes);


    }

    private void setSellerInfo () {

        if(shoppingItems != null || !shoppingItems.isEmpty()) {
            sellerId = shoppingItems.get(0).getSellerId();
            sellersRef.child(sellerId).child("storeInfo").child("storeName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // set seller store name
                    txvStoreName.setText(snapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    txvStoreName.setText("Store N/A");
                }
            });
        }
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


                        // Set text for deliveryInfo
                        model.getShipmentInfoObject().observe(getViewLifecycleOwner(), new Observer<CustomerCheckoutDataViewModel.ShipmentInfo>() {
                            @Override
                            public void onChanged(CustomerCheckoutDataViewModel.ShipmentInfo shipmentInfo) {
                                if (shipmentInfo != null){
                                    txvShipment.setText(shipmentInfo.getMethod());
                                    txvReceiverPhone.setText(shipmentInfo.getReceiver()+" "+shipmentInfo.getPhone());
                                    txvAddress.setText(shipmentInfo.getAddress());
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