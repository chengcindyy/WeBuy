package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Wishlist;
import com.csis4495_cmk.webuy.viewmodels.CustomerWishlistViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class CustomerProfileWishlistItemsAdapter extends RecyclerView.Adapter<CustomerProfileWishlistItemsAdapter.ViewHolder> {
    Context context;
    List<Wishlist> wishlistList;
    CustomerWishlistViewModel wishListViewModel;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    public CustomerProfileWishlistItemsAdapter(Context context, CustomerWishlistViewModel wishListViewModel, List<Wishlist> wishlistList) {
        this.context = context;
        this.wishListViewModel = wishListViewModel;
        this.wishlistList = wishlistList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_customer_wishlist_item_horizental, parent, false);
        return new CustomerProfileWishlistItemsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wishlist wishlist = wishlistList.get(position);

        holder.txvGroupName.setText(wishlist.getGroupName());
        holder.txvGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wishlist.getGroupId();
            }
        });

        holder.txvGroupPrice.setText(wishlist.getGroupPrice());
        Glide.with(context).load(wishlist.getGroupImage()).into(holder.imgGroupPic);

        // Show remove
        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                popupMenu.getMenuInflater().inflate(R.menu.cart_item_popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        wishListViewModel.removeFromWishlist(wishlist, firebaseUser.getUid());
                        Toast.makeText(context,"Item removed from Wish List",Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return wishlistList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imgGroupPic;
        TextView txvGroupName, txvGroupPrice;
        ImageButton btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgGroupPic = itemView.findViewById(R.id.imv_cart_item_group_pic);
            txvGroupName = itemView.findViewById(R.id.tv_cart_item_group_name);
            txvGroupPrice = itemView.findViewById(R.id.tv_cart_item_group_price);
            btnMore = itemView.findViewById(R.id.btn_more);
        }
    }
}
