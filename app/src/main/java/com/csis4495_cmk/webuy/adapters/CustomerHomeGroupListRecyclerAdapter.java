package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.activities.SellerHomePageActivity;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Seller;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CustomerHomeGroupListRecyclerAdapter extends RecyclerView.Adapter<CustomerHomeGroupListRecyclerAdapter.ViewHolder> {

    Context context;

    private String groupImage;
    private String groupCategory;
    private int groupType; // in-stock: 0 ; pre-order: 1
    private String groupName;
    private String groupPrice;
    private int soldAmount;
    private int dayLeft;
    private String sellerPic;
    private String sellerName;
    private String sellerId;
    //
    private int groupStatus; // notStarted: 0; started: 1; ended: 2
    private long startTimestamp;
    private long endTimestamp;

    private List<Group> groups;
    private onGroupListener mGroupListener;

    public CustomerHomeGroupListRecyclerAdapter(){

    }

    public CustomerHomeGroupListRecyclerAdapter(Context ct, List<Group> groups){
        this.context = ct;
        this.groups = groups;
    }

    public void setOnGroupListener(onGroupListener mGroupListener) {
        this.mGroupListener = mGroupListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_customer_home_products, parent, false);
        return new CustomerHomeGroupListRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //want image url here
        groupImage = groups.get(position).getGroupImages().get(0);
        //Picasso.with(context).load(groupImage).into(holder.imvGroupImage);

        groupCategory = groups.get(position).getCategory();
        holder.tvGroupCategory.setText(groupCategory);

        groupType = groups.get(position).getGroupType();
        if (groupType == 0) {
            holder.tvGroupType.setText("in-stock");
        } else if (groupType == 1) {
            holder.tvGroupType.setText("pre-order");
        } else {
            holder.tvGroupType.setText("no type");
        }

        groupName = groups.get(position).getGroupName();
        holder.tvGroupName.setText(groupName);

        groupPrice = groups.get(position).getGroupPrice();
        holder.tvGroupPrice.setText(groupPrice);

        ////soldAmount;

        // display the due days if it is preorder
        if (groupType == 1) {
            // Convert the timestamp to LocalDateTime
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(groups.get(position).getEndTimestamp()), ZoneOffset.UTC);
            // Get the current date
            LocalDate currentDate = LocalDate.now();
            // Calculate the duration between the timestamp and the current date
            Duration duration = Duration.between(dateTime.toLocalDate().atStartOfDay(), currentDate.atStartOfDay());
            // the number of days in the duration
            dayLeft = (int) duration.toDays();
            holder.tvTimer.setText("due in " + dayLeft + " day(s)");
        }

        sellerId = groups.get(position).getSellerId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Seller");
        reference.child(sellerId).addValueEventListener(new ValueEventListener() {  // change to addValueEventListener
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Seller seller = snapshot.getValue(Seller.class);
                sellerName = seller.getStoreInfo().getStoreName();
                holder.tvSellerName.setText(sellerName);

                sellerPic = seller.getStoreInfo().getStorePic();

                if (sellerPic != null) {
                    Log.d("Upload img: imageUrl ", sellerPic);
                    Glide.with(context)
                            .load(sellerPic)
                            .circleCrop()
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    holder.imvSellerPic.setImageDrawable(resource);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    holder.imvSellerPic.setImageResource(R.drawable.ic_user_profile);
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.imvSellerPic.setImageResource(R.drawable.ic_user_profile);
            }
        });

        //group item Clicked
        holder.view.setOnClickListener(v -> {
            mGroupListener.onGroupClicked();
        });

        ////
        groupStatus = groups.get(position).getStatus(); // notStarted: 0; started: 1; ended: 2
        startTimestamp = groups.get(position).getStartTimestamp();
        endTimestamp = groups.get(position).getEndTimestamp();
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imvGroupImage;
        TextView tvGroupCategory;
        TextView tvGroupType;
        TextView tvGroupName;
        TextView tvGroupPrice;
        TextView tvSoldAmount;
        TextView tvTimer;
        ImageView imvSellerPic;
        TextView tvSellerName;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imvGroupImage = itemView.findViewById(R.id.groupImage);
            tvGroupCategory = itemView.findViewById(R.id.groupCategory);
            tvGroupType = itemView.findViewById(R.id.groupType);
            tvGroupName = itemView.findViewById(R.id.groupName);
            tvGroupPrice = itemView.findViewById(R.id.groupPrice);
            tvSoldAmount = itemView.findViewById(R.id.soldAmount);
            tvTimer = itemView.findViewById(R.id.timer);
            imvSellerPic = itemView.findViewById(R.id.seller_picture);
            tvSellerName = itemView.findViewById(R.id.seller_name);
            view = itemView;
        }
    }

    public interface onGroupListener{
        void onGroupClicked();
    }
}
