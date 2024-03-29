package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SellerAddGroupImagesAdapter extends RecyclerView.Adapter<SellerAddGroupImagesAdapter.ViewHolder> {

    Context context;
    List<String> paths = new ArrayList<>();

    String productId;

    private onGroupDeleteImgListener onGroupDeleteImgListener;

    private onGroupAddImgListener onGroupAddImgListener;

    public void setOnGroupAddImgListener(onGroupAddImgListener listener) {
        this.onGroupAddImgListener = listener;
    }

    public void setOnGroupDeleteImgListener(onGroupDeleteImgListener listener) {
        this.onGroupDeleteImgListener = listener;
    }

    public SellerAddGroupImagesAdapter() {
    }

    public SellerAddGroupImagesAdapter(Context context, List<String> paths) {
        this.context = context;
        this.paths = paths;
    }

    @NonNull
    @Override
    public SellerAddGroupImagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_image_with_delete, parent, false);
        return new SellerAddGroupImagesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerAddGroupImagesAdapter.ViewHolder holder, int position) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImage");
        StorageReference imageReference = storageReference.child(productId+"/"+paths.get(position));
        imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            // Got the download URL and pass it to Picasso to download, show in ImageView and caching
            Picasso.get().load(uri.toString()).into(holder.imgViewSingleImg);
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle errors, if image doesn't exist, show a default image
                holder.imgViewSingleImg.setImageResource(R.drawable.baseline_add_photo_alternate_50);
            }
        });

        if (paths.size() == 1) {
            holder.btnDelete.setVisibility(View.GONE);
        } else {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onGroupDeleteImgListener != null){
                        onGroupDeleteImgListener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
//        return paths.size() + 1;
        return paths.size();
    }

    public void updateGroupImgPaths(List<String> uriPaths) {
        paths.clear();
        paths.addAll(uriPaths);
        notifyDataSetChanged();
    }

    public void updateGroupImgPaths2(String productId, List<String> uriPaths) {
        this.productId = productId;
        paths.clear();
        paths.addAll(uriPaths);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgViewSingleImg;
        Button btnDelete;
        TextView tvCoverImg;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViewSingleImg = itemView.findViewById(R.id.imv_single_product_img);
            btnDelete = itemView.findViewById(R.id.btn_delete_img);
            tvCoverImg = itemView.findViewById(R.id.tv_cover_img);
            view = itemView;
        }

    }

    public interface onGroupDeleteImgListener {
        void onDeleteClick(int position);
    }

    public interface onGroupAddImgListener {
        void onAddClick(int position);
    }

}
