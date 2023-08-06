package com.csis4495_cmk.webuy.adapters.recyclerview;

import static android.content.ContentValues.TAG;

import android.content.Context;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SellerOrderListRecyclerAdapter extends RecyclerView.Adapter<SellerOrderListRecyclerAdapter.ViewHolder> {
    private List<Order> pendingOrders;

    private List<String> orderIds;

    private String customerEmail;

    private Context context;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

    private SellerOrderListGroupInfoRecyclerAdapter itemAdapter;

    public void setOnItemClickListener(SellerOrderListRecyclerAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public SellerOrderListRecyclerAdapter() {
    }

    public SellerOrderListRecyclerAdapter(List<Order> pendingOrders, List<String> orderIds) {
        this.pendingOrders = pendingOrders;
        this.orderIds = orderIds;
    }

    public SellerOrderListRecyclerAdapter(List<Order> pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    private OnItemClickListener onItemClickListener;

    public List<Order> getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(List<Order> pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    @NonNull
    @Override
    public SellerOrderListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_order_list, parent, false);
        return new SellerOrderListRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrderListRecyclerAdapter.ViewHolder holder, int position) {
        Order order = pendingOrders.get(position);
        String customerId = order.getCustomerId();
        userRef.child(customerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    User customer = snapshot.getValue(User.class);
                    String customerEmail = customer.getEmail();
                    holder.email.setText(customerEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { /* None*/ }
        });

        holder.date.setText(simpleDateFormat.format(new Date(order.getOrderedTimestamp())));
        Double itemTotal = order.getCheckoutItemsTotal();
        Double orderTotal = order.getOrderTotalPrice();
        holder.item_total.setText("Item total: $CA" + String.format(Locale.getDefault(), "%.2f", itemTotal));
        holder.order_total.setText("Order total: $CA" + String.format(Locale.getDefault(), "%.2f", orderTotal));
        Map<String, Map<String, Order.OrderItemInfo>> itemInfoList = order.getGroupsAndItemsMap();
        Log.d(TAG, "onBindViewHolder: order.getCheckoutItemsTotal()" + order.getCheckoutItemsTotal());
        Log.d(TAG, "onBindViewHolder: order.getOrderTotalPrice()" + order.getOrderTotalPrice());

        Map<String, Map<String, Order.OrderItemInfo>> groupIdandItemMap = order.getGroupsAndItemsMap();
        itemAdapter = new SellerOrderListGroupInfoRecyclerAdapter();
        itemAdapter.setGroupIdandItemMap(groupIdandItemMap);
        ArrayList<String> groupIds = new ArrayList<String>(groupIdandItemMap.keySet());
        itemAdapter.setGroupIds(groupIds);
        itemAdapter.notifyDataSetChanged();
        holder.rv.setAdapter(itemAdapter);
        holder.rv.setLayoutManager(new LinearLayoutManager(context));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onItemClick(position);
                    Log.d(TAG, "onClick: position" + position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return pendingOrders.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, email, item_total, order_total;
        RecyclerView rv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_seller_order_card_date);
            email = itemView.findViewById(R.id.tv_seller_order_card_email);
            rv = itemView.findViewById(R.id.rv_seller_order_card_group);
            item_total = itemView.findViewById(R.id.tv_seller_order_card_item_total);
            order_total = itemView.findViewById(R.id.tv_seller_order_card_order_total);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
