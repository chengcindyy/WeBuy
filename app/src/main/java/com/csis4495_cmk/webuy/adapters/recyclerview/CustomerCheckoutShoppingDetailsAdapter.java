package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.viewmodels.CustomerCartItemsViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerCheckoutShoppingDetailsAdapter extends RecyclerView.Adapter<CustomerCheckoutShoppingDetailsAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CartItem> shoppingItems;
    private ArrayList<CustomerCartItemsViewModel.CartItemInfo> cartItemInfos;


    public CustomerCheckoutShoppingDetailsAdapter(Context context, ArrayList<CartItem> shoppingItems, ArrayList<CustomerCartItemsViewModel.CartItemInfo> cartItemInfos) {
        this.context = context;
        this.shoppingItems = shoppingItems;
        this.cartItemInfos = cartItemInfos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_checkout_shopping_item, parent, false);
        return new CustomerCheckoutShoppingDetailsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = holder.getBindingAdapterPosition();

        Picasso.get().load(cartItemInfos.get(pos).getGroupPicUrl()).into(holder.imgProduct);
        holder.txvProductName.setText(cartItemInfos.get(pos).getProductName());
        String style = cartItemInfos.get(pos).getStyleName();
        if (style != null) {
            holder.txvStyle.setText(style);
        } else {
            holder.txvStyle.setText("");
            holder.txvStyle.setHint(null);
        }
        holder.txvPrice.setText(cartItemInfos.get(pos).getGroupPrice());
        holder.tvxItemCount.setText("x " + shoppingItems.get(pos).getAmount());

    }

    @Override
    public int getItemCount() {
        return shoppingItems.size();
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
