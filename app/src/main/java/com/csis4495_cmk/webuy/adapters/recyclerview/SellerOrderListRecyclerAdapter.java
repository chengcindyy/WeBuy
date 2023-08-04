package com.csis4495_cmk.webuy.adapters.recyclerview;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SellerOrderListRecyclerAdapter extends RecyclerView.Adapter<SellerOrderListRecyclerAdapter.ViewHolder> {
    private List<Order> pendingOrders;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

    public SellerOrderListRecyclerAdapter() {
    }

    public SellerOrderListRecyclerAdapter(List<Order> pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

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
        return  new SellerOrderListRecyclerAdapter.ViewHolder(view);
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
                    String email = customer.getEmail();
                    holder.email.setText(email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { /* None*/ }
        });

        holder.date.setText(simpleDateFormat.format(new Date(order.getOrderedTimestamp())));
        holder.item_total.setText("Item total: "+Double.toString(order.getCheckoutItemsTotal()));
        holder.order_total.setText("Order total: "+ Double.toString(order.getOrderTotalPrice()));
        Map<String, Map<String, Order.OrderItemInfo>> itemInfoList = order.getGroupsAndItemsMap();
        Log.d(TAG, "onBindViewHolder: order.getCheckoutItemsTotal()" + order.getCheckoutItemsTotal());
        Log.d(TAG, "onBindViewHolder: order.getOrderTotalPrice()" + order.getOrderTotalPrice());

        ///USE LATEST ORDER MODEL!!!!


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
}
