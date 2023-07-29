package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class CustomerCartItemsAdapter extends RecyclerView.Adapter<CustomerCartItemsAdapter.ViewHolder> {

    Context context;
    Map<String, ArrayList<CartItem>> sellerItemsMap;
    ArrayList<String> sellerIds;

    onCartSellerBannerListener mCartSellerBannerListener;

    DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("Seller");

    public CustomerCartItemsAdapter(Context context, Map<String, ArrayList<CartItem>> sellerItemsMap) {
        this.context = context;
        this.sellerItemsMap = sellerItemsMap;
        this.sellerIds = new ArrayList<>(sellerItemsMap.keySet());
    }

    public void setOnCartSellerBannerListener(onCartSellerBannerListener mCartSellerBannerListener) {
        this.mCartSellerBannerListener = mCartSellerBannerListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_customer_cart_seller_banner, parent, false);
        return new CustomerCartItemsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String sellerId = sellerIds.get(position);
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String storeName = snapshot.child(sellerId).child("storeInfo").child("storeName").getValue(String.class);
                String storePic = snapshot.child(sellerId).child("storeInfo").child("storePic").getValue(String.class);

                holder.tvSellerName.setText(storeName);
                if (storePic != null && storePic != "") {
                    Picasso.get().load(storePic).into(holder.imvSellerPic);
                } else {
                    holder.imvSellerPic.setImageResource(R.drawable.app_logo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.rvCartItemsWithSameSeller.setLayoutManager(new LinearLayoutManager(context));

        CustomerCartItemsWithSameSellerAdapter customerCartItemsWithSameSellerAdapter =
                new CustomerCartItemsWithSameSellerAdapter(context, sellerItemsMap.get(sellerId));
        holder.rvCartItemsWithSameSeller.setAdapter(customerCartItemsWithSameSellerAdapter);
    }

    @Override
    public int getItemCount() {
        return sellerIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBoxAllGroups;
        ImageView imvSellerPic;
        TextView tvSellerName;
        RecyclerView rvCartItemsWithSameSeller;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxAllGroups = itemView.findViewById(R.id.checkbox_all_groups);
            imvSellerPic = itemView.findViewById(R.id.imv_cart_seller_banner_seller_picture);
            tvSellerName = itemView.findViewById(R.id.tv_cart_seller_banner_seller_name);
            rvCartItemsWithSameSeller = itemView.findViewById(R.id.rv_cust_cart_items_same_seller);
            view = itemView;
        }
    }

    public interface onCartSellerBannerListener {
        void onSellerBannerChecked(String sellerId);
    }

}
