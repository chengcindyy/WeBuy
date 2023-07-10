package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerGroupListRecyclerAdapter extends RecyclerView.Adapter<SellerGroupListRecyclerAdapter.ViewHolder> {
    Context context;
    List<Group> groups = new ArrayList<>();


    public SellerGroupListRecyclerAdapter() {
    }

    public SellerGroupListRecyclerAdapter(Context context, List<Group> groups) {
        this.context = context;
        this.groups = groups;

    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }


    @NonNull
    @Override
    public SellerGroupListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_group_detail,parent,false);
        SellerGroupListRecyclerAdapter.ViewHolder viewHolder = new SellerGroupListRecyclerAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerGroupListRecyclerAdapter.ViewHolder holder, int position) {
        Group g = groups.get(position);
        List<ProductStyle> groupStyles = g.getGroupStyles();
        Map<String, Integer> groupQty = g.getGroupQtyMap();
        String productId = g.getProductId();
        String groupImg = g.getGroupImages().get(0);
        Map<String, Integer> qtyMap = g.getGroupQtyMap();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
        StorageReference imageReference = storageReference.child(productId+"/"+groupImg);
        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL and pass it to Picasso to download, show in ImageView and caching
            Picasso.get().load(uri.toString()).into(holder.groupImg);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle errors, if image doesn't exist, show a default image
                holder.groupImg.setImageResource(R.drawable.baseline_add_photo_alternate_50);
            }
        });

        holder.groupName.setText(g.getGroupName());
        holder.groupPrice.setText(g.getGroupPrice());

        if(groupStyles.size()>0){
            holder.groupQty.setVisibility(View.GONE);
            SellerGroupListStyleListRecyclerAdapter styleAdapter = new SellerGroupListStyleListRecyclerAdapter(context, groupStyles, groupQty, productId);
            holder.groupStylesRV.setAdapter(styleAdapter);
            holder.groupStylesRV.setLayoutManager(new LinearLayoutManager(context));
        }else{
            holder.groupQty.setText(qtyMap.get(g.getGroupName()));
            holder.groupStylesRV.setVisibility(View.GONE);
        }

        if(g.getStartTimestamp()==null){
            holder.groupStart.setVisibility(View.GONE);
            Date startTime = new Date(g.getStartTimestamp());
            holder.groupStart.setText("From " + startTime);

        }else{

        }
        if(g.getEndTime()==null){
            holder.groupEnd.setVisibility(View.GONE);
            Date endTime = new Date(g.getEndTimestamp());
            holder.groupStart.setText("From " + endTime);
        }

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView groupImg;
        TextView groupName;
        TextView groupPrice;
        TextView groupQty;
        RecyclerView groupStylesRV;
        TextView groupStart;
        TextView groupEnd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groupImg = itemView.findViewById(R.id.img_group_img);
            groupName = itemView.findViewById(R.id.tv_group_name);
            groupPrice = itemView.findViewById(R.id.tv_group_price);
            groupQty = itemView.findViewById(R.id.tv_group_qty);
            groupStylesRV = itemView.findViewById(R.id.rv_group_style);
            groupStart = itemView.findViewById(R.id.tv_group_start);
            groupEnd = itemView.findViewById(R.id.tv_group_end);
        }
    }
}
