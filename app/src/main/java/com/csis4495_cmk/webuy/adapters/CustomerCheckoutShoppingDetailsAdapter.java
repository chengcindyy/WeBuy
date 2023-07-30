package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;

public class CustomerCheckoutShoppingDetailsAdapter extends RecyclerView.Adapter<CustomerCheckoutShoppingDetailsAdapter.ViewHolder> {
    private Context context;

    public CustomerCheckoutShoppingDetailsAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txvProductName, txvStyle, txvPrice, tvxItemCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // ImageView
            imgProduct = itemView.findViewById(R.id.image_product_image);

            //TextView
            txvProductName = itemView.findViewById(R.id.txv_product_name);
            txvStyle = itemView.findViewById(R.id.txv_style_option);
            txvPrice = itemView.findViewById(R.id.txv_price_amount);
            tvxItemCount = itemView.findViewById(R.id.txv_quantity);
        }
    }
}
