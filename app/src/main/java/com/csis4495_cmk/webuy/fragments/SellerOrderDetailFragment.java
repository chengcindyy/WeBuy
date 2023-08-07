package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerOrderDetailGroupMapRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.file.ClosedFileSystemException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


public class SellerOrderDetailFragment extends Fragment {

    private String orderId;
    private String customerEmail;
    private TextView email, receiver, status, date, address, note, itemTotal, deliveryFee, orderTotal, paymentType, deliveryType, phone;
    private Button btnPrevious, btnNext;
    RecyclerView rv;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dBRef = firebaseDatabase.getReference();
    DatabaseReference orderRef = dBRef.child("Order");

    DatabaseReference userRef = dBRef.child("User");

    private SellerOrderDetailGroupMapRecyclerAdapter adapter;

    private int orderStatus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_seller_order_detail, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            orderId = bundle.getString("detail_orderId");
            Log.d(TAG, "onCreateView: customer_email" + customerEmail);
            Log.d(TAG, "onCreateView: detail_orderId" + orderId);
        }

        email = view.findViewById(R.id.tv_seller_order_detail_email);
        receiver = view.findViewById(R.id.tv_seller_order_detail_receiver);
        status = view.findViewById(R.id.tv_seller_order_detail_status);
        date = view.findViewById(R.id.tv_seller_order_detail_date);
        address = view.findViewById(R.id.tv_seller_order_detail_address);
        note = view.findViewById(R.id.tv_seller_order_detail_note);
        itemTotal = view.findViewById(R.id.tv_seller_order_detail_item_total);
        deliveryFee = view.findViewById(R.id.tv_seller_order_detail_delivery_fee);
        orderTotal = view.findViewById(R.id.tv_seller_order_detail_order_total);
        paymentType = view.findViewById(R.id.tv_seller_order_detail_payment);
        deliveryType = view.findViewById(R.id.tv_seller_order_detail_delivery_type);
        rv = view.findViewById(R.id.rv_seller_order_detail_groups);
        phone = view.findViewById(R.id.tv_seller_order_detail_phone);

        btnPrevious = view.findViewById(R.id.btn_seller_order_detail_previous);
        btnNext = view.findViewById(R.id.btn_seller_order_detail_next);

        getOrderData();

        switch (orderStatus){
            //Order status = canceled
            case -1:
                status.setText("Canceled");

                btnPrevious.setEnabled(false);
                btnPrevious.setVisibility(View.GONE);

                btnNext.setEnabled(false);
                btnNext.setVisibility(View.GONE);
                break;

            //Order status = pending
            case 0:
                status.setText("Pending");

                btnPrevious.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);
                btnPrevious.setText("CANCEL ORDER");
                btnPrevious.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openCancelDialog();
                    }
                });

                btnNext.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setText("Accept Order");
                btnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference ref = orderRef.child(orderId);
                        ref.child("orderStatus").setValue(1);
                        status.setText("To Allocate");
                        getOrderData();
                    }
                });
                break;

            //Order status = To Allocate
            case 1:
                status.setText("To Allocate");
                btnPrevious.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setText("Cancel Order");

                btnNext.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setText("To Allocate item");
                break;

            //Order status = Allocated
            case 2:
                status.setText("Allocated");

                btnPrevious.setEnabled(false);
                btnPrevious.setVisibility(View.GONE);

                btnNext.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setText("Process the order");
                break;

            //Order status = Processing
            case 3:
                status.setText("Processing");

                btnPrevious.setEnabled(false);
                btnPrevious.setVisibility(View.GONE);

                btnNext.setEnabled(true);
                btnNext.setText("Complete the order");
                break;

            //Order status = Received
            case 4:
                status.setText("Received");


                btnPrevious.setEnabled(false);
                btnPrevious.setVisibility(View.GONE);

                btnNext.setEnabled(false);
                btnPrevious.setVisibility(View.GONE);
                break;
        }
        return view;
    }

    private void openCancelDialog(){
        new AlertDialog.Builder(getContext())
                .setTitle("Cancel the order") // Set the title of the dialog
                .setMessage("Are you sure to cancel this order?") // Set the message of the dialog
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference ref = orderRef.child(orderId);
                        ref.child("orderStatus").setValue(-1);
                        status.setText("Cancled");
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void getOrderData(){
        orderRef.child(orderId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null){
                    Order order = snapshot.getValue(Order.class);
                    userRef.child(order.getCustomerId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot != null) {
                                User customer = snapshot.getValue(User.class);
                                customerEmail = customer.getEmail();
                                email.setText(customerEmail);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) { /* None*/ }
                    });
                    receiver.setText("Name: " + order.getReceiverName());
                    orderStatus = order.getOrderStatus();

                    email.setText(customerEmail);
                    if(order.getPhone() !=null){
                        phone.setText("Phone: " + order.getPhone());
                    }else{
                        phone.setText("Phone: N/A");
                    }
                    date.setText(simpleDateFormat.format(new Date(order.getOrderedTimestamp())));
                    String addressLong ="";
                    if(order.getAddress() != null){
                        addressLong = order.getAddress();
                        Log.d(TAG, "order.getAddress: addressLong: " + addressLong);
                    }
                    if(order.getCity() != null){
                        addressLong += ", " + order.getCity();
                        Log.d(TAG, "order.getCity: addressLong: " + addressLong);

                    }
                    if(order.getPostalCode() != null){
                        addressLong += ", " + order.getPostalCode();
                        Log.d(TAG, "order.getPostalCode: addressLong: " + addressLong);
                    }
                    if(order.getProvince() != null){
                        addressLong += ", " + order.getProvince();
                        Log.d(TAG, "order.getProvince: addressLong: " + addressLong);
                    }
                    if(order.getCountry() != null){
                        addressLong += ", " + order.getCountry();
                        Log.d(TAG, "order.getCountry: addressLong: " + addressLong);
                    }
                    address.setText("Address: "+ addressLong);
                    Log.d(TAG, "addressLong: " + addressLong);

                    note.setText("Note: " + order.getNote());
                    paymentType.setText(order.getPaymentType());
                    deliveryType.setText(order.getShippingMethod());
                    Double itemsTotal = order.getCheckoutItemsTotal();
                    Double totalPrice = order.getOrderTotalPrice();
                    Double shippingFee = order.getDeliveryFee();
                    itemTotal.setText("Item total: $CA" + String.format(Locale.getDefault(), "%.2f", itemsTotal));
                    deliveryFee.setText("Delivery Fee: $CA" + String.format(Locale.getDefault(), "%.2f", shippingFee));
                    orderTotal.setText("Order total: $CA" + String.format(Locale.getDefault(), "%.2f", totalPrice));

                    Map<String, Map<String, Order.OrderItemInfo>> GroupsAndItemsMap = order.getGroupsAndItemsMap();

                    Map<String, String> productIdandGroupIdMap = new HashMap<>();
                    Map<String, Order.OrderItemInfo> orderItemInfoMap = new HashMap<>();
//                    List<String> groupIds = new ArrayList<>(GroupsAndItemsMap.keySet());
                    for(Map.Entry<String, Map<String, Order.OrderItemInfo>> gIdEntry : GroupsAndItemsMap.entrySet()){
                        String gId = gIdEntry.getKey();
                        Map<String, Order.OrderItemInfo> productsMap = gIdEntry.getValue();
                        for (String pid : productsMap.keySet()) {
                            productIdandGroupIdMap.put(pid, gId);
                            orderItemInfoMap.put(pid,productsMap.get(pid));
                        }
                    }
                    Log.d(TAG, "onDataChange: productIdandGroupIdMap" + productIdandGroupIdMap);
                    Log.d(TAG, "onDataChange: orderItemInfoMap" + orderItemInfoMap);
                    adapter = new SellerOrderDetailGroupMapRecyclerAdapter(productIdandGroupIdMap, orderItemInfoMap);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}