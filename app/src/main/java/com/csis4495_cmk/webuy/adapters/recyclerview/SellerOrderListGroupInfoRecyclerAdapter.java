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
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SellerOrderListGroupInfoRecyclerAdapter extends RecyclerView.Adapter<SellerOrderListGroupInfoRecyclerAdapter.ViewHolder> {
    private Map<String, Map<String, Order.OrderItemInfo>> groupIdandItemMap;
    private ArrayList<String> groupIds;

    private Context context;

    private SellerOrderListItemInfoRecyclerAdapter adapter;

    public ArrayList<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<String> groupIds) {
        this.groupIds = groupIds;
    }

    public Map<String, Map<String, Order.OrderItemInfo>> getGroupIdandItemMap() {
        return groupIdandItemMap;
    }

    public void setGroupIdandItemMap(Map<String, Map<String, Order.OrderItemInfo>> groupIdandItemMap) {
        this.groupIdandItemMap = groupIdandItemMap;
    }

    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Group");

    public SellerOrderListGroupInfoRecyclerAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_order_list_item_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String groupId = groupIds.get(position);
        Log.d(TAG, "onBindViewHolder: groupId: " + groupId);
        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group g = snapshot.getValue(Group.class);
                Log.d(TAG, "onBindViewHolder: Group g = snapshot.getValue(Group.class)" + g);
                String gName = g.getGroupName();
                holder.groupName.setText(gName);
                Map<String, Order.OrderItemInfo> itemMap = groupIdandItemMap.get(groupId);
                Map<String, Order.OrderItemInfo> itemMapWithSytleName = new HashMap<>();
                for (Map.Entry<String, Order.OrderItemInfo> entry : itemMap.entrySet()) {
                    String key = entry.getKey();
                    Order.OrderItemInfo item = entry.getValue();
                    if (g.getGroupStyles() == null) {
                        itemMapWithSytleName.put(gName, item);
                    } else {
                        String pSplit = "p___";
                        String sSplit = "s___";
                        String pid = key.split(sSplit)[0].split(pSplit)[1];
                        String sid = key.split(pSplit)[1].split(sSplit)[1];
                        for (ProductStyle ps : g.getGroupStyles()) {
                            if(ps.getStyleId().equals(sid)){
                                itemMapWithSytleName.put(ps.getStyleName(), item);
                            }
                        }
                    }
                }
                ArrayList<String> styleNames = new ArrayList<String>(itemMap.keySet());
                adapter = new SellerOrderListItemInfoRecyclerAdapter(itemMapWithSytleName, styleNames);
                Log.d(TAG, "onDataChange: itemMapWithSytleName" + itemMapWithSytleName);
                holder.rv_gropuId.setAdapter(adapter);
                holder.rv_gropuId.setLayoutManager(new LinearLayoutManager(context));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupIdandItemMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView groupName;

        RecyclerView rv_gropuId;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupName = itemView.findViewById(R.id.tv_seller_order_list_item_group_name);
            rv_gropuId = itemView.findViewById(R.id.rv_seller_order_list_item_detail);

        }
    }
}
