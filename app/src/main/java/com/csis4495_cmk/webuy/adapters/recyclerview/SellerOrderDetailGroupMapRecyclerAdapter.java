package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.models.Order;

import java.util.Map;

public class SellerOrderDetailGroupMapRecyclerAdapter extends RecyclerView.Adapter<SellerOrderDetailGroupMapRecyclerAdapter.ViewHolder> {

    private Map<String, Map<String, Order.OrderItemInfo>> GroupsAndItemsMap;

    public SellerOrderDetailGroupMapRecyclerAdapter() {
    }

    public SellerOrderDetailGroupMapRecyclerAdapter(Map<String, Map<String, Order.OrderItemInfo>> groupsAndItemsMap) {
        this.GroupsAndItemsMap = groupsAndItemsMap;
    }

    @NonNull
    @Override
    public SellerOrderDetailGroupMapRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrderDetailGroupMapRecyclerAdapter.ViewHolder holder, int position) {


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
