package com.csis4495_cmk.webuy.dialog;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerOrderListRecyclerAdapter;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerStatusOrderListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Order;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerOrderStatusDialog extends DialogFragment {

    private RecyclerView rv;

    private TextView tv;

    private int status;

    private FirebaseUser firebaseUser;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference dBRef;

    private DatabaseReference orderRef;

    private String customerId;

    private List<Order> requestedOrders = new ArrayList<>();

    private List<String> orderIds = new ArrayList<>();

    public CustomerOrderStatusDialog(int status) {
        this.status = status;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageButton btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // Request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflating the layout for the dialog
        View view = inflater.inflate(R.layout.fragment_customer_orderstatus, container, false);

        rv = view.findViewById(R.id.rv_customer_all_status_order_list);

        tv = view.findViewById(R.id.tv_customer_all_status_order_list);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            customerId = firebaseUser.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        dBRef = firebaseDatabase.getReference();
        orderRef = dBRef.child("Order");

        getCustomerOrderList();
        return view;
    }

    private void getCustomerOrderList() {
        requestedOrders.clear();
        orderIds.clear();
        Log.d(TAG, "getCustomerOrderList: status" + Integer.toString(status));
        Log.d(TAG, "getCustomerOrderList: customerId" + customerId);
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    String orderId = dataSnapshot.getKey();
                    if (order.getOrderStatus() == status && order.getCustomerId().equals(customerId)) {
                        Log.d(TAG, "getCustomerOrderList: order" + order);
                        requestedOrders.add(order);
                        orderIds.add(orderId);
                    }
                }
                if (requestedOrders.size() == 0) {
                    tv.setText("There are no orders found");
                }else{
                    SellerStatusOrderListRecyclerAdapter adapter = new SellerStatusOrderListRecyclerAdapter(requestedOrders);
//                    SellerOrderListRecyclerAdapter adapter = new SellerOrderListRecyclerAdapter(requestedOrders, orderIds);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                    tv.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
