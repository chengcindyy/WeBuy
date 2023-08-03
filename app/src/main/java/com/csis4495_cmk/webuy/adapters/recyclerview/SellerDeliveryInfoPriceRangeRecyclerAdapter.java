package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;

import java.util.List;
import java.util.Map;

public class SellerDeliveryInfoPriceRangeRecyclerAdapter extends RecyclerView.Adapter<SellerDeliveryInfoPriceRangeRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Map.Entry<String, Double>> feeEntries;

    public SellerDeliveryInfoPriceRangeRecyclerAdapter(Context context, List<Map.Entry<String, Double>> feeEntries) {
        this.context = context;
        this.feeEntries = feeEntries;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_seller_delivery_price_range_info, parent, false);
        return new SellerDeliveryInfoPriceRangeRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, Double> entry = feeEntries.get(position);
        String keyString = entry.getKey();
        String[] parts = keyString.split("_");
        String price = parts[1];
        Double fee = entry.getValue();

        String priceRangeEnd;
        if (position < feeEntries.size() - 1) {
            Map.Entry<String, Double> nextEntry = feeEntries.get(position + 1);
            String nextFullKey = nextEntry.getKey();
            String[] nextParts = nextFullKey.split("_");
            String nextPrice = nextParts[1];
            int endPrice = Integer.parseInt(nextPrice) - 1;
            priceRangeEnd = String.valueOf(endPrice);
        } else {
            priceRangeEnd = price;
        }

        Log.d("Test key values", "Key: " + keyString + ", Price: " + price + ", Value: " + fee);
        holder.txvFrom.setText(price);
        holder.txvTo.setText("~  " + priceRangeEnd);
        holder.txvFee.setText(fee.toString());
    }


    @Override
    public int getItemCount() {
        return feeEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txvFrom, txvTo, txvFee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvFrom = itemView.findViewById(R.id.tvx_from);
            txvTo = itemView.findViewById(R.id.tvx_to);
            txvFee = itemView.findViewById(R.id.txv_shipping_fee);
        }
    }
}
