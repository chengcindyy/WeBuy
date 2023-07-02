package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Delivery;
import java.util.HashMap;
import java.util.List;

public class SellerDeliveryRecyclerAdapter extends RecyclerView.Adapter<SellerDeliveryRecyclerAdapter.ViewHolder> {

    private Context context;
    private HashMap<String, Delivery> deliveryMap;
    private List<String> keys;

    public SellerDeliveryRecyclerAdapter(Context context, HashMap<String, Delivery> deliveryMap, List<String> keys) {
        this.context = context;
        this.deliveryMap = deliveryMap;
        this.keys = keys;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_list_layout, parent, false);
        return new SellerDeliveryRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Delivery delivery = deliveryMap.get(keys.get(position));

        holder.deliveryMethod.setText(delivery.getDeliveredMethod());
        holder.customName.setText(delivery.getDisplayName());
        holder.spendOver.setText(String.valueOf(delivery.getFrom()));
        holder.deliveryFee.setText(String.valueOf(delivery.getFee()));
    }

    @Override
    public int getItemCount() {
        return deliveryMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView deliveryMethod, customName, spendOver, deliveryFee;

        public ViewHolder(View itemView) {
            super(itemView);
            deliveryMethod = itemView.findViewById(R.id.txv_method);
            customName = itemView.findViewById(R.id.txv_method_display_name);
            spendOver = itemView.findViewById(R.id.tvx_from);
            deliveryFee = itemView.findViewById(R.id.txv_shipping_fee);
        }
    }

}