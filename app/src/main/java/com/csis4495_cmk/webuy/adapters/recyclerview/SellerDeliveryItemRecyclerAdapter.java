package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Delivery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerDeliveryItemRecyclerAdapter extends RecyclerView.Adapter<SellerDeliveryItemRecyclerAdapter.ViewHolder> {

    private Context context;
    private HashMap<String, Delivery> deliveryMap;
    private List<String> keys;


    public SellerDeliveryItemRecyclerAdapter(Context context, HashMap<String, Delivery> deliveryMap, List<String> keys) {
        this.context = context;
        this.deliveryMap = deliveryMap;
        this.keys = keys;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_seller_delivery_list_layout, parent, false);
        return new SellerDeliveryItemRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Delivery delivery = deliveryMap.get(keys.get(position));

        holder.deliveryMethod.setText(delivery.getDeliveredMethod());
        holder.location.setText(delivery.getPickUpLocation());
        if(delivery.getDeliveredMethod().equals("[Home delivery] ")){
            holder.location.setText(delivery.getDeliveryCity());
        }

        Map<String, Double> feeMap = delivery.getFeeMap();
        List<Map.Entry<String, Double>> feeEntries = new ArrayList<>();
        if(feeMap != null){
            feeEntries.addAll(feeMap.entrySet());
        }

        holder.adapter = new SellerDeliveryInfoPriceRangeRecyclerAdapter(context, feeEntries);
        holder.mRecyclerView.setAdapter(holder.adapter);
    }

    @Override
    public int getItemCount() {
        return deliveryMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deliveryMethod, location;
        RecyclerView mRecyclerView;
        SellerDeliveryInfoPriceRangeRecyclerAdapter adapter;

        public ViewHolder(View itemView) {
            super(itemView);
            deliveryMethod = itemView.findViewById(R.id.txv_method);
            location = itemView.findViewById(R.id.txv_method_location);

            // Set child recyclerView
            mRecyclerView = itemView.findViewById(R.id.recyclerView_price_range_info);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    public void removeItem(int position) {
        String keyToRemove = keys.get(position);
        keys.remove(position);
        deliveryMap.remove(keyToRemove);
        notifyItemRemoved(position);
    }
}