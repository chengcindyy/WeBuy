package com.csis4495_cmk.webuy.adapters.recyclerview;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SellerStatusOrderListRecyclerAdapter extends RecyclerView.Adapter<SellerStatusOrderListRecyclerAdapter.ViewHolder> {
    private List<Order> requestedOrders;

    private List<String> orderIds;

    public SellerStatusOrderListRecyclerAdapter(){

    }
    public SellerStatusOrderListRecyclerAdapter(List<Order> requestedOrders) {
        this.requestedOrders = requestedOrders;
    }

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dBRef = firebaseDatabase.getReference();
    DatabaseReference orderRef = dBRef.child("Order");
    DatabaseReference userRef = dBRef.child("User");

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ");

    @NonNull
    @Override
    public SellerStatusOrderListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_customer_status_order_list, parent, false);
        return new SellerStatusOrderListRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerStatusOrderListRecyclerAdapter.ViewHolder holder, int position) {
        Order order = requestedOrders.get(position);
        if(order.getReceiverName() != null){
            holder.name.setText("Name: " + order.getReceiverName());
        }
        int orderStatus = order.getOrderStatus();
        switch (orderStatus){
            case 0:
                holder.status.setText("Pending");
                break;
            case 1:
                holder.status.setText("Confirmed");
                break;
            case 3:
                holder.status.setText("Processing");
                break;
            case 4:
                holder.status.setText("Received");
                break;
            case -1:
                holder.status.setText("Canceled");
                break;
        }
        if (order.getPhone() != null) {
            holder.phone.setText("Phone: " + order.getPhone());
        } else {
            holder.phone.setText("Phone: N/A");
        }
        holder.date.setText(simpleDateFormat.format(new Date(order.getOrderedTimestamp())));
        String addressLong = "";
        if (order.getAddress() != null) {
            addressLong = order.getAddress();
            Log.d(TAG, "order.getAddress: addressLong: " + addressLong);
        }
        if (order.getCity() != null) {
            addressLong += ", " + order.getCity();
            Log.d(TAG, "order.getCity: addressLong: " + addressLong);

        }
        if (order.getPostalCode() != null) {
            addressLong += ", " + order.getPostalCode();
            Log.d(TAG, "order.getPostalCode: addressLong: " + addressLong);
        }
        if (order.getProvince() != null) {
            addressLong += ", " + order.getProvince();
            Log.d(TAG, "order.getProvince: addressLong: " + addressLong);
        }
        if (order.getCountry() != null) {
            addressLong += ", " + order.getCountry();
            Log.d(TAG, "order.getCountry: addressLong: " + addressLong);
        }
        holder.address.setText("Address: " + addressLong);
        Log.d(TAG, "addressLong: " + addressLong);

        holder.note.setText("Note: " + order.getNote());
        holder.paymentType.setText(order.getPaymentType());
        holder.deliveryType.setText(order.getShippingMethod());
        Double itemsTotal = order.getCheckoutItemsTotal();
        Double totalPrice = order.getOrderTotalPrice();
        Double shippingFee = order.getDeliveryFee();
        holder.itemTotal.setText("Item total: $CA" + String.format(Locale.getDefault(), "%.2f", itemsTotal));
        holder.deliveryFee.setText("Delivery Fee: $CA" + String.format(Locale.getDefault(), "%.2f", shippingFee));
        holder.orderTotal.setText("Order total: $CA" + String.format(Locale.getDefault(), "%.2f", totalPrice));
    }

    @Override
    public int getItemCount() {
        return requestedOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView status, date, name, address, note, itemTotal, deliveryFee, orderTotal, paymentType, deliveryType, phone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_customer_order_list_date);
            status = itemView.findViewById(R.id.tv_customer_order_list_status);
            phone = itemView.findViewById(R.id.tv_customer_order_list_phone);
            name = itemView.findViewById(R.id.tv_customer_order_list_name);
            address = itemView.findViewById(R.id.tv_customer_order_list_address);
            note = itemView.findViewById(R.id.tv_customer_order_list_note);
            paymentType = itemView.findViewById(R.id.tv_customer_order_list_payment);
            deliveryType = itemView.findViewById(R.id.tv_customer_order_list_delivery_type);
            itemTotal = itemView.findViewById(R.id.tv_customer_order_list_item_total);
            deliveryFee = itemView.findViewById(R.id.tv_customer_order_list_delivery_fee);
            orderTotal = itemView.findViewById(R.id.tv_customer_order_list_order_total);
        }
    }
}
