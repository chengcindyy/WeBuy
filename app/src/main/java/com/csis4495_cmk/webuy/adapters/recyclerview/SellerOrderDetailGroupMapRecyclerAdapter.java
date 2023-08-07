package com.csis4495_cmk.webuy.adapters.recyclerview;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerOrderDetailGroupMapRecyclerAdapter extends RecyclerView.Adapter<SellerOrderDetailGroupMapRecyclerAdapter.ViewHolder> {
    private List<String> productIdStyleIdKeys;

    private Map<String, String> productIdandGroupIdMap;
    private Map<String, Order.OrderItemInfo> orderItemInfoMap;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference dBRef = firebaseDatabase.getReference();
    private DatabaseReference groupRef = dBRef.child("Group");

    public SellerOrderDetailGroupMapRecyclerAdapter() {
    }

    public SellerOrderDetailGroupMapRecyclerAdapter(Map<String, String> productIdandGroupIdMap, Map<String, Order.OrderItemInfo> orderItemInfoMap) {
        this.productIdandGroupIdMap = productIdandGroupIdMap;
        this.orderItemInfoMap = orderItemInfoMap;
        this.productIdStyleIdKeys = new ArrayList<>(orderItemInfoMap.keySet());
    }

    @NonNull
    @Override
    public SellerOrderDetailGroupMapRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_order_detail_group_map, parent, false);
        SellerOrderDetailGroupMapRecyclerAdapter.ViewHolder viewHolder = new SellerOrderDetailGroupMapRecyclerAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerOrderDetailGroupMapRecyclerAdapter.ViewHolder holder, int position) {
        String pIdSidKey = productIdStyleIdKeys.get(position);
        Order.OrderItemInfo orderItemInfo = orderItemInfoMap.get(pIdSidKey);
        String groupId = productIdandGroupIdMap.get(pIdSidKey);

        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot != null) {
                    Group group = snapshot.getValue(Group.class);
                    if (!pIdSidKey.contains("s___")) {
                        String productId = pIdSidKey.split("p___")[1];
                        holder.name.setText(group.getGroupName());
                        holder.qty.setText(Integer.toString(orderItemInfo.getOrderAmount()));
                        if (orderItemInfo.isAllocated()) {
                            holder.allocate.setText("Allocated");
                        } else {
                            holder.allocate.setText("Not yet allocated");
                        }
                        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
                        StorageReference imageReference = storageReference.child(productId + "/" + group.getGroupImages().get(0));
                        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Got the download URL and pass it to Picasso to download, show in ImageView and caching
                            Picasso.get().load(uri.toString()).into(holder.img);
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle errors, if image doesn't exist, show a default image
                                holder.img.setImageResource(R.drawable.baseline_add_photo_alternate_50);
                            }
                        });
                    } else {
                        String styleId = pIdSidKey.split("s___")[1];
                        String productId =  pIdSidKey.split("p___")[1].split("s___")[0];
                        List<ProductStyle> groupStyles = group.getGroupStyles();
                        for (ProductStyle ps : groupStyles) {
                            if (ps.getStyleId().equals(styleId)) {
                                holder.name.setText(group.getGroupName() + " - " + ps.getStyleName());
                                holder.qty.setText(Integer.toString(orderItemInfo.getOrderAmount()));
                                if (orderItemInfo.isAllocated()) {
                                    holder.allocate.setText("Allocated");
                                } else {
                                    holder.allocate.setText("Not yet allocated");
                                }
                                StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
                                StorageReference imageReference = storageReference.child(productId + "/" + ps.getStylePicName());
                                imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // Got the download URL and pass it to Picasso to download, show in ImageView and caching
                                    Picasso.get().load(uri.toString()).into(holder.img);
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle errors, if image doesn't exist, show a default image
                                        holder.img.setImageResource(R.drawable.baseline_add_photo_alternate_50);
                                    }
                                });
                            }
                        }

                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItemInfoMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, qty, allocate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img_seller_order_info);
            name = itemView.findViewById(R.id.tv_seller_order_info_product);
            qty = itemView.findViewById(R.id.tv_seller_order_info_qty);
            allocate = itemView.findViewById(R.id.tv_seller_order_info_allocate);
        }
    }

}
