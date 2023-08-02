package com.csis4495_cmk.webuy.adapters.recyclerview;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

public class GroupEditStyleAdapter extends RecyclerView.Adapter<GroupEditStyleAdapter.ViewHolder> {
    private Map<String, String> StyleMap;
    private Map<String, Boolean> toAddMap = new HashMap<>();
    private String productId;
    private List<String> keys;

    private List<ProductStyle> styles;

    public GroupEditStyleAdapter(Map<String, String> styleMap, String productId, List<ProductStyle> styles) {
        StyleMap = styleMap;
        this.productId = productId;
        this.styles = styles;
        this.keys = new ArrayList<>(StyleMap.keySet());
        Log.d(TAG, "GroupAddStyleAdapter keys: " + keys);
    }

    public GroupEditStyleAdapter(Map<String, String> styleMap, String productId) {
        StyleMap = styleMap;
        this.productId = productId;
        this.keys = new ArrayList<>(StyleMap.keySet());
        Log.d(TAG, "GroupAddStyleAdapter keys: " + keys);
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<ProductStyle> getStyles() {
        return styles;
    }

    public void setStyles(List<ProductStyle> styles) {
        this.styles = styles;
    }

    public Map<String, String> getStyleMap() {
        return StyleMap;
    }

    public void setStyleMap(Map<String, String> styleMap) {
        StyleMap = styleMap;
    }

    public Map<String, Boolean> getToAddMap() {
        return toAddMap;
    }

    public void setToAddMap(Map<String, Boolean> toAddMap) {
        this.toAddMap = toAddMap;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @NonNull
    @Override
    public GroupEditStyleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_group_add_style, parent, false);
        return new GroupEditStyleAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupEditStyleAdapter.ViewHolder holder, int position) {
        ProductStyle ps = styles.get(position);
        String styleImg = ps.getStylePicName();
        String styelName = ps.getStyleName();
        String styleId = ps.getStyleId();
        holder.style.setText(styelName);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
        StorageReference imageReference = storageReference.child(productId+"/"+styleImg);
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

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.cb.isChecked()){
                    toAddMap.put("s___"+ps.getStyleId(),true);
                    Log.d(TAG, "Style checked: " + toAddMap);
                }else{
                    toAddMap.remove("s___"+ps.getStyleId());
                    Log.d(TAG, "Style checked: " + toAddMap);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return styles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb;
        TextView style;
        ImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.cb_group_add_style);
            style = itemView.findViewById(R.id.tv_group_add_style_name);
            img = itemView.findViewById(R.id.img_group_add_style);
        }
    }

}
