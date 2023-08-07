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
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerGroupDetailStyleListRecyclerAdapter extends RecyclerView.Adapter<SellerGroupDetailStyleListRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<ProductStyle> styles = new ArrayList<>();
    private Map<String, Integer> qtyMap = new HashMap<>();
    private String productId;

    private List<Inventory> inventoryList;


    public List<Inventory> getInventoryList() {
        return inventoryList;
    }

    public void setInventoryList(List<Inventory> inventoryList) {
        this.inventoryList = inventoryList;
    }

    public SellerGroupDetailStyleListRecyclerAdapter() {
    }

    public SellerGroupDetailStyleListRecyclerAdapter(Context context, List<ProductStyle> styles, Map<String, Integer> qtyMap, String productId) {
        this.context = context;
        this.styles = styles;
        this.qtyMap = qtyMap;
        this.productId = productId;
    }

    public SellerGroupDetailStyleListRecyclerAdapter(List<ProductStyle> styles, Map<String, Integer> qtyMap, String productId) {
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
    public SellerGroupDetailStyleListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_group_detail_style, parent, false);
        SellerGroupDetailStyleListRecyclerAdapter.ViewHolder viewHolder = new SellerGroupDetailStyleListRecyclerAdapter.ViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellerGroupDetailStyleListRecyclerAdapter.ViewHolder holder, int position) {
        ProductStyle ps = styles.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
        StorageReference imageReference = storageReference.child(productId + "/" + ps.getStylePicName());
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
        Integer sQty = qtyMap.get("s___" + ps.getStyleId());
        if (sQty != null) {
            holder.styleQty.setText("Quantity: " + String.valueOf(sQty));
        }
        if (inventoryList != null) {
            for (Inventory i : inventoryList) {
                if (i != null) {
                    String styleId = i.getProductStyleKey().split("_")[1];
                    if (styleId.equals(ps.getStyleId())) {
                        Integer inventory = i.getInStock();
                        Integer allocated = i.getAllocated();
                        Integer ordered = i.getOrdered();
                        Integer toAllocate = i.getToAllocate();
                        if (inventory != null && inventory > 0) {
                            holder.inventory.setText("Inventory: " + Integer.toString(inventory));
                        } else {
                            holder.inventory.setText("Inventory: 0");
                        }
                        if (allocated != null && allocated > 0) {
                            holder.allocated.setText("Allocated: " + Integer.toString(allocated));
                        } else {
                            holder.allocated.setText("Allocated: 0");
                        }
                        if (ordered != null && ordered > 0) {
                            holder.ordered.setText("Ordered: " + Integer.toString(ordered));
                        } else {
                            holder.ordered.setText("Ordered: 0");
                        }
                        if (toAllocate != null && toAllocate > 0) {
                            holder.toAllocate.setText("To Allocate: " + Integer.toString(toAllocate));
                        } else {
                            holder.toAllocate.setText("To Allocate: 0");
                        }
                    }
                }
            }
        } else {
            holder.allocated.setText("Allocated: 0");
            holder.ordered.setText("Ordered: 0");
            holder.toAllocate.setText("To Allocate: 0");
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

        TextView inventory;

        TextView ordered;

        TextView allocated;

        TextView toAllocate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            styleImg = itemView.findViewById(R.id.img_group_style);
            styleName = itemView.findViewById(R.id.tv_group_style_name);
            stylePrice = itemView.findViewById(R.id.tv_group_style_price);
            styleQty = itemView.findViewById(R.id.tv_group_style_qty);
            inventory = itemView.findViewById(R.id.tv_group_style_stock);
            ordered = itemView.findViewById(R.id.tv_group_style_ordered);
            allocated = itemView.findViewById(R.id.tv_group_style_allocated);
            toAllocate = itemView.findViewById(R.id.tv_group_style_toAllocated);

        }
    }
}
