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

import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDetailInventoryRecyclerAdapter extends RecyclerView.Adapter<GroupDetailInventoryRecyclerAdapter.ViewHolder> {

    private Map<String, Map<String, Boolean>> toAllocateMap = new HashMap<>();

    private List<Map.Entry<String, Map.Entry<String, Order.OrderItemInfo>>> allItems;

    private List<Inventory> inventoryList;

    private boolean isAllocatedOrder = false;
    private String groupId;
    private Group group;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    private List<ViewHolder> viewHolders = new ArrayList<>();

    public void setAllocatedOrder(boolean allocatedOrder) {
        isAllocatedOrder = allocatedOrder;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setInventoryList(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public Map<String, Map<String, Boolean>> getToAllocateMap() {
        return toAllocateMap;
    }

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
    }

    public GroupDetailInventoryRecyclerAdapter() {
    }

    public Map<String, Map<String, Boolean>> getSelectedItems() {
        return toAllocateMap;
    }

    public void setSelectedItems(Map<String, Map<String, Boolean>> toAllocateMap) {
        this.toAllocateMap = toAllocateMap;
    }

    @NonNull
    @Override
    public GroupDetailInventoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_group_order_item, parent, false);
        return new GroupDetailInventoryRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupDetailInventoryRecyclerAdapter.ViewHolder holder, int position) {
        viewHolders.add(holder);
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Group");
//        Log.d(TAG, "onBindViewHolder: check inventory" + inventoryList);
        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    Group g = snapshot.getValue(Group.class);
                    group = g;
                }

                Map.Entry<String, Map.Entry<String, Order.OrderItemInfo>> item = allItems.get(position);
                String orderId = item.getKey();
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

                orderRef.child(orderId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot != null) {
                            Order order = snapshot.getValue(Order.class);
                            String customerId = order.getCustomerId();
                            holder.date.setText(simpleDateFormat.format(new Date(order.getOrderedTimestamp())));
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
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { /* None*/ }
                });

                String product_style_id = item.getValue().getKey();
                String pSplit = "p___";
                String sSplit = "s___";
                if (!product_style_id.contains(sSplit)) {
                    String productId = product_style_id.split(pSplit)[1].split(sSplit)[0];
                    if (productId.equals(group.getProductId())) {
                        holder.style.setText(group.getGroupName());
                    }
                } else {
                    int index = product_style_id.indexOf(sSplit);
                    if (index != -1) {
                        String sId = product_style_id.substring(index + sSplit.length());
                        for (ProductStyle style : group.getGroupStyles()) {
                            if (style.getStyleId().equals(sId)) {
                                holder.style.setText(style.getStyleName());

                            }
                        }
                    }
                }
                Order.OrderItemInfo orderItemInfo = item.getValue().getValue();
                Integer ordered = orderItemInfo.getOrderAmount();
                holder.ordered.setText(Integer.toString(ordered));

                if(isAllocatedOrder){
                    holder.cb.setEnabled(false);
                    holder.cb.setVisibility(View.GONE);
                }
                holder.cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (holder.cb.isChecked()) {
                            Map<String, Boolean> innerMap = toAllocateMap.get(orderId);
                            if (innerMap == null) {
                                innerMap = new HashMap<>();
                                toAllocateMap.put(orderId, innerMap);
                            }
                            innerMap.put(product_style_id, true);
                            Log.d(TAG, "Checkbox onClick: " + toAllocateMap);
                        } else {
                            Map<String, Boolean> innerMap = toAllocateMap.get(orderId);
                            if (innerMap != null) {
                                innerMap.remove(product_style_id);
                                if (innerMap.isEmpty()) {
                                    toAllocateMap.remove(orderId);
                                }
                            }
                            Log.d(TAG, "Checkbox onClick: " + toAllocateMap);
                        }
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { /* None*/ }
        });

    }

    public void uncheckAll() {
        for (ViewHolder viewHolder : viewHolders) {
            viewHolder.cb.setChecked(false);
        }
        toAllocateMap.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        viewHolders.remove(holder);
    }

    @Override
    public int getItemCount() {
        return allItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView email, style, ordered, date;
        CheckBox cb;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_group_order_item_date);
            email = itemView.findViewById(R.id.tv_group_order_item_email);
            style = itemView.findViewById(R.id.tv_group_order_item_style);
            ordered = itemView.findViewById(R.id.tv_group_order_item_ordered);
            cb = itemView.findViewById(R.id.cb_group_order_item);
        }
    }

}
