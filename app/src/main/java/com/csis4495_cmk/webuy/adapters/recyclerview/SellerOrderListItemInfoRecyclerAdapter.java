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

import java.util.ArrayList;
import java.util.Map;

public class SellerOrderListItemInfoRecyclerAdapter extends RecyclerView.Adapter<SellerOrderListItemInfoRecyclerAdapter.ViewHolder>{
    private Map<String, Order.OrderItemInfo> itemMap;
    ArrayList<String> styleNames;

    public Map<String, Order.OrderItemInfo> getItemMap() {
        return itemMap;
    }

    public SellerOrderListItemInfoRecyclerAdapter() {
    }

    public SellerOrderListItemInfoRecyclerAdapter(Map<String, Order.OrderItemInfo> itemMap, ArrayList<String> styleNames) {
        this.itemMap = itemMap;
        this.styleNames = new ArrayList<String>(itemMap.keySet());
    }

    public void setItemMap(Map<String, Order.OrderItemInfo> itemMap) {
        this.itemMap = itemMap;
    }

    public ArrayList<String> getStyleNames() {
        return styleNames;
    }

    public void setStyleNames(ArrayList<String> styleNames) {
        this.styleNames = styleNames;
    }

    @NonNull
    @Override
    public SellerOrderListItemInfoRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_order_item_detail_view, parent, false);
        return new SellerOrderListItemInfoRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrderListItemInfoRecyclerAdapter.ViewHolder holder, int position) {
        String styleName = styleNames.get(position);
        Order.OrderItemInfo item = itemMap.get(styleName);
        holder.sName.setText(styleName);
        Log.d(TAG, "onBindViewHolder: item" + item);
        holder.sQty.setText("Ordered:" + Integer.toString(item.getOrderAmount()));
        if(item.isAllocated()){
            holder.sAllocated.setText("Allocated");
        }else{
            holder.sAllocated.setText("Not Yet Allocate");
        }
    }

    @Override
    public int getItemCount() {
        return itemMap.size();
    }

    public class
    ViewHolder extends RecyclerView.ViewHolder {
        TextView sName, sQty, sAllocated;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sName = itemView.findViewById(R.id.tv_seller_order_list_item_style_name);
            sQty = itemView.findViewById(R.id.tv_seller_order_list_item_style_qty);
            sAllocated = itemView.findViewById(R.id.tv_seller_order_list_item_allocate);
        }
    }
}
