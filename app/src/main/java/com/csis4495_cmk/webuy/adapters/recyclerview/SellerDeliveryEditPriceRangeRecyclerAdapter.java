package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.csis4495_cmk.webuy.R;

import java.util.List;
import java.util.Map;

public class SellerDeliveryEditPriceRangeRecyclerAdapter extends RecyclerView.Adapter<SellerDeliveryEditPriceRangeRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Map.Entry<String, Double>> feeEntries;
    private OnItemClickListener listener;


    public SellerDeliveryEditPriceRangeRecyclerAdapter(Context context, List<Map.Entry<String, Double>> feeEntries, OnItemClickListener listener) {
        this.context = context;
        this.feeEntries = feeEntries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_seller_delivery_price_range, parent, false);
        return new SellerDeliveryEditPriceRangeRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map.Entry<String, Double> entry = feeEntries.get(position);
        String price = entry.getKey();
        Double fee = entry.getValue();

        if (!price.equals("") || !fee.equals(0.0)) {
            holder.edtCostOver.setText(price);
            holder.edtShippingFee.setText(fee.toString());
        } else {
            holder.edtCostOver.setText("");
            holder.edtShippingFee.setText("");
        }

        holder.btnRemoveItem.setOnClickListener(view -> {
            Log.d("Text remove button", "remove button clicked");
            if (position != RecyclerView.NO_POSITION) { // Check if item still exists
                removeItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return feeEntries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        CurrencyEditText edtCostOver, edtShippingFee;
        ImageButton btnRemoveItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edtCostOver = itemView.findViewById(R.id.edit_shipping_fee_range_over);
            edtShippingFee = itemView.findViewById(R.id.edit_shipping_fee_range_fee);
            btnRemoveItem = itemView.findViewById(R.id.btn_remove_text_Field);
        }

        public String getCostOver() {
            return String.valueOf((int)edtCostOver.getNumericValue());
        }

        public Double getShippingFee() {
            return edtShippingFee.getNumericValue();
        }

        public void clearFields() {
            edtCostOver.setText("");
            edtShippingFee.setText("");
        }
    }

    public void removeItem(int position) {
        feeEntries.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, feeEntries.size());
    }



    public interface OnItemClickListener {
        void onRemoveItemButtonClick(int position);
    }
}
