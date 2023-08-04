package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;

import org.w3c.dom.Text;

public class SellerOrderListRecyclerAdapter extends RecyclerView.Adapter<SellerOrderListRecyclerAdapter.ViewHolder> {
    @NonNull
    @Override
    public SellerOrderListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrderListRecyclerAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
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

        }
    }
}
