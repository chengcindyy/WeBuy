package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomerGroupStyleAdapter extends RecyclerView.Adapter<CustomerGroupStyleAdapter.ViewHolder>{

    Context context;
    List<ProductStyle> styles;
    String productId;
    onStyleItemListener mStyleItemListener;
    StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");
    int selectedItemPosition = -1;

    public CustomerGroupStyleAdapter(Context context, String productId, List<ProductStyle> styles) {
        this.context = context;
        this.productId = productId;
        this.styles = styles;
    }

    public CustomerGroupStyleAdapter(Context context, String productId, List<ProductStyle> styles, int selectedItemPosition) {
        this.context = context;
        this.productId = productId;
        this.styles = styles;
        this.selectedItemPosition = selectedItemPosition;
    }

    public void setmOnStyleItemListener(onStyleItemListener mListener) {
        this.mStyleItemListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_customer_group_detail_style, parent, false);
        return new CustomerGroupStyleAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String styleName = styles.get(position).getStyleName();
        holder.tvStyleName.setText(styleName);

        String stylePicName = styles.get(position).getStylePicName();
        imgRef.child(productId).child(stylePicName).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String stylePicUrl = uri.toString();
                        Picasso.get().load(stylePicUrl).into(holder.imvStyleImg);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occur while getting the download URL
                e.printStackTrace();
            }
        });

        holder.view.setOnClickListener(v -> {
            mStyleItemListener.onStyleClicked(holder.getBindingAdapterPosition(), styles.get(holder.getBindingAdapterPosition()));
            //set selected layout
            selectedItemPosition = holder.getBindingAdapterPosition();
            notifyDataSetChanged();
        });

        if (position == selectedItemPosition) {
            GradientDrawable shapeDrawable = new GradientDrawable();
            shapeDrawable.setShape(GradientDrawable.RECTANGLE);
            shapeDrawable.setStroke(2, Color.RED);
            holder.view.setBackground(shapeDrawable);
        } else {
            holder.view.setBackgroundColor(ContextCompat.getColor(context, R.color.cancel_grey));///
        }

    }



    @Override
    public int getItemCount() {
        return styles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imvStyleImg;
        TextView tvStyleName;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imvStyleImg = itemView.findViewById(R.id.imv_group_detail_style);
            tvStyleName = itemView.findViewById(R.id.tv_group_detail_style_name);
            view = itemView;
        }
    }

    public interface onStyleItemListener {
        void onStyleClicked(int stylePosition, ProductStyle style);
    }
}
