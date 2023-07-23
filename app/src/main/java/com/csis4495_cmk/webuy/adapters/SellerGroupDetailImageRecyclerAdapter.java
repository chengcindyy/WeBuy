package com.csis4495_cmk.webuy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SellerGroupDetailImageRecyclerAdapter extends RecyclerView.Adapter<SellerGroupDetailImageRecyclerAdapter.ImageViewHolder> {
    private List<String> imgUrls = new ArrayList<>();

    private String groupId;
    private String productId;

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @NonNull
    @Override
    public SellerGroupDetailImageRecyclerAdapter.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_group_detail_image, parent, false);

        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerGroupDetailImageRecyclerAdapter.ImageViewHolder holder, int position) {
        String groupImg = imgUrls.get(position);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
        StorageReference imageReference = storageReference.child(productId + "/" + groupImg);
        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL and pass it to Picasso to download, show in ImageView and caching
            Picasso.get().load(uri.toString()).into(holder.imageView);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle errors, if image doesn't exist, show a default image
                holder.imageView.setImageResource(R.drawable.baseline_add_photo_alternate_50);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imgUrls.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_group_detail);
        }
    }
}
