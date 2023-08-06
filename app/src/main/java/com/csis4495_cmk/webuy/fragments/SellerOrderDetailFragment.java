package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;


public class SellerOrderDetailFragment extends Fragment {

    private String orderId;
    private String customerEmail;
    private TextView email, receiver, status, date, address, note, itemTotal, deliveryFee, orderTotal, paymentType, deliveryType;
    private Button btnPrevious, btnNext;
    RecyclerView rv;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ");
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dBRef = firebaseDatabase.getReference();
    DatabaseReference orderRef = dBRef.child("Order");

    DatabaseReference userRef = dBRef.child("User");

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

        btnPrevious = view.findViewById(R.id.btn_seller_order_detail_previous);
        btnNext = view.findViewById(R.id.btn_seller_order_detail_next);

        getOrderData();

        return view;
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
                    int orderStatus = order.getOrderStatus();
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
                            btnPrevious.setText("CANCEL Order");

                            btnNext.setEnabled(true);
                            btnPrevious.setVisibility(View.VISIBLE);
                            btnNext.setText("Accept Order");
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
                    email.setText(customerEmail);
                    date.setText(simpleDateFormat.format(new Date(order.getOrderedTimestamp())));
                    address.setText("Address: "+ order.getAddress());
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}