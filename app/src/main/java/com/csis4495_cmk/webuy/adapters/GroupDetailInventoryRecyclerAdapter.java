package com.csis4495_cmk.webuy.adapters;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.csis4495_cmk.webuy.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailInventoryRecyclerAdapter extends RecyclerView.Adapter<GroupDetailInventoryRecyclerAdapter.ViewHolder> {

    private Map<String, Map<String, Boolean>>  selectedItems = new HashMap<>();

    private List< Map.Entry <String, Map.Entry<String, Order.OrderItemInfo> > > allItems;

    private String groupId;
    private Group group;

    public GroupDetailInventoryRecyclerAdapter(Map<String, Map<String, Order.OrderItemInfo>> customerIdandItemsMap, String groupId) {
        allItems = new ArrayList<>();
        for (Map.Entry<String, Map<String, Order.OrderItemInfo>> customerOrder : customerIdandItemsMap.entrySet()) {
            for (Map.Entry<String, Order.OrderItemInfo> ItemOrder : customerOrder.getValue().entrySet()) {
                Map.Entry<String, Map.Entry<String, Order.OrderItemInfo>> combinedEntry = new AbstractMap.SimpleEntry<>(
                        customerOrder.getKey(), ItemOrder
                );
                allItems.add(combinedEntry);
            }
        }

        this.groupId = groupId;

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Group");
        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    Group g = snapshot.getValue(Group.class);
                    group = g;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { /* None*/  } });

    }

    public GroupDetailInventoryRecyclerAdapter() {
    }

    public Map<String, Map<String, Boolean>> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(Map<String, Map<String, Boolean>> selectedItems) {
        this.selectedItems = selectedItems;
    }

    @NonNull
    @Override
    public GroupDetailInventoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_group_order_item, parent, false);
        return new GroupDetailInventoryRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupDetailInventoryRecyclerAdapter.ViewHolder holder, int position) {
        Map.Entry<String, Map.Entry<String, Order.OrderItemInfo>> item = allItems.get(position);
        String customerId = item.getKey();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        userRef.child(customerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot != null){
                    User customer = snapshot.getValue(User.class);
                    String email = customer.getEmail();
                    holder.email.setText(email);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { /* None*/  } });

        String styleId = item.getValue().getKey().split("p___")[1].split("s___")[1];
        holder.style.setText(styleId);


        Order.OrderItemInfo orderItemInfo = item.getValue().getValue();
        Integer ordered = orderItemInfo.getOrderAmount();
        holder.ordered.setText(Integer.toString(ordered));

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView email, style, ordered;
        CheckBox cb;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.tv_group_order_item_email);
            style = itemView.findViewById(R.id.tv_group_order_item_style);
            ordered = itemView.findViewById(R.id.tv_group_order_item_ordered);
            cb = itemView.findViewById(R.id.cb_group_order_item);
        }
    }

}
