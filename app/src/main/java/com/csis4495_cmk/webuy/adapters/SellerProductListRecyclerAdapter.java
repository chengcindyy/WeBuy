package com.csis4495_cmk.webuy.adapters;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.SellerEditProductActivity;
import com.csis4495_cmk.webuy.SellerPublishProductActivity;
import com.google.android.material.snackbar.Snackbar;

public class SellerProductListRecyclerAdapter extends RecyclerView.Adapter<SellerProductListRecyclerAdapter.ViewHolder> {

    private String[] testTitles = {"Popcorn", "Chips", "Coke"};

    private int[] testImages =  {R.drawable.splash_logo_icon,R.drawable.app_logo,R.drawable.ic_backgrund_square};

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.seller_product_card_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.productImage.setImageResource(testImages[i]);
        viewHolder.productTitle.setText(testTitles[i]);
        viewHolder.imgBtn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Context context = v.getContext();
                context.startActivity(new Intent(context, SellerPublishProductActivity.class));
            }
        });

        viewHolder.imgBtn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                context.startActivity(new Intent(context, SellerEditProductActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return testTitles.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        ImageButton imgBtn_post;
        ImageButton imgBtn_edit;
        ImageButton imgBtn_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.iv_seller_product_card_img);
            productTitle = itemView.findViewById(R.id.seller_product_card_title);
            imgBtn_post = itemView.findViewById(R.id.imgBtn_product_card_post);
            imgBtn_edit = itemView.findViewById(R.id.imgBtn_product_card_edit);
            imgBtn_delete = itemView.findViewById(R.id.imgBtn_product_card_delete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Snackbar.make(v, "Click detected on" + productTitle.getText().toString() + "on position " + position,
                            Snackbar.LENGTH_SHORT).show();
                }
            });

            imgBtn_post.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Snackbar.make(v, "Clicked post on item" + position,
                            Snackbar.LENGTH_SHORT).show();
                }
            });

            imgBtn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Snackbar.make(v, "Clicked edit on item" + position,
                            Snackbar.LENGTH_SHORT).show();
                }
            });

            imgBtn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Snackbar.make(v, "Clicked delete on item" + position,
                            Snackbar.LENGTH_SHORT).show();
                }
            });

        }
    }
}
