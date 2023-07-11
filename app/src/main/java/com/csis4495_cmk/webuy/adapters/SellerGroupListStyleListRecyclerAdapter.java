package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerGroupListStyleListRecyclerAdapter extends RecyclerView.Adapter<SellerGroupListStyleListRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<ProductStyle> styles = new ArrayList<>();
    private Map<String, Integer> qtyMap = new HashMap<>();
    private String productId;

    public SellerGroupListStyleListRecyclerAdapter() {
    }

    public SellerGroupListStyleListRecyclerAdapter(Context context, List<ProductStyle> styles, Map<String, Integer> qtyMap, String productId) {
        this.context = context;
        this.styles = styles;
        this.qtyMap = qtyMap;
        this.productId = productId;
    }

    public SellerGroupListStyleListRecyclerAdapter(List<ProductStyle> styles, Map<String, Integer> qtyMap, String productId) {
        this.styles = styles;
        this.qtyMap = qtyMap;
        this.productId = productId;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<ProductStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<ProductStyle> styles) {
        this.styles = styles;
    }

    public Map<String, Integer> getQtyMap() {
        return qtyMap;
    }

    public void setQtyMap(Map<String, Integer> qtyMap) {
        this.qtyMap = qtyMap;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @NonNull
    @Override
    public SellerGroupListStyleListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_groupd_detail_style,parent,false);
        SellerGroupListStyleListRecyclerAdapter.ViewHolder viewHolder = new SellerGroupListStyleListRecyclerAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerGroupListStyleListRecyclerAdapter.ViewHolder holder, int position) {
        ProductStyle ps = styles.get(position);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
        StorageReference imageReference = storageReference.child(productId+"/"+ps.getStylePic());
        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL and pass it to Picasso to download, show in ImageView and caching
            Picasso.get().load(uri.toString()).into(holder.styleImg);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                holder.styleImg.setImageResource(R.drawable.baseline_add_photo_alternate_50);
            }
        });

        holder.styleName.setText(ps.getStyleName());
        Double sPrice = ps.getStylePrice();
        holder.stylePrice.setText("CA$ " + Double.valueOf(sPrice));
        Integer sQty = qtyMap.get(ps.getStyleName());
        if(sQty != null) {
            holder.styleQty.setText("Qty: "+ String.valueOf(sQty));
        }

    }

    @Override
    public int getItemCount() {
        return styles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView styleImg;
        TextView styleName;
        TextView stylePrice;
        TextView styleQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            styleImg = itemView.findViewById(R.id.img_group_style);
            styleName = itemView.findViewById(R.id.tv_group_style_name);
            stylePrice = itemView.findViewById(R.id.tv_group_style_price);
            styleQty = itemView.findViewById(R.id.tv_group_style_qty);
        }
    }
}
