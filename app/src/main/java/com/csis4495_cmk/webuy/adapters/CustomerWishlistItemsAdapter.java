package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Wishlist;

import java.util.List;

public class CustomerWishlistItemsAdapter extends RecyclerView.Adapter<CustomerWishlistItemsAdapter.ViewHolder> {
    Context context;
    List<Wishlist> wishlistList;

    public CustomerWishlistItemsAdapter(Context context, List<Wishlist> wishlistList) {
        this.context = context;
        this.wishlistList = wishlistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_wishlist_item, parent, false);
        return new CustomerWishlistItemsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wishlist wishlist = wishlistList.get(position);

        holder.txvGroupName.setText(wishlist.getGroupName());
        holder.txvGroupPrice.setText(wishlist.getGroupPrice());
        Glide.with(context).load(wishlist.getGroupImage()).into(holder.imgGroupPic);


        // Add to cart
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("btnAddToCart", "btnAddToCart clicked");
            }
        });

        // Show remove

    }

    @Override
    public int getItemCount() {
        return wishlistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgGroupPic;
        TextView txvGroupName, txvGroupPrice;
        Button btnAddToCart;
        ImageButton btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGroupPic = itemView.findViewById(R.id.imv_cart_item_group_pic);
            txvGroupName = itemView.findViewById(R.id.tv_cart_item_group_name);
            txvGroupPrice = itemView.findViewById(R.id.tv_cart_item_group_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            btnMore = itemView.findViewById(R.id.btn_more);
        }
    }
}
