package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SellerAllProductAdapter extends RecyclerView.Adapter<SellerAllProductAdapter.ViewHolder> {

    private NavController navController;
    Context context;
    List<Product> productList;

    public SellerAllProductAdapter(Context context, List<Product> productList, NavController navController) {
        this.context = context;
        this.productList = productList;
        this.navController = navController;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_seller_product, parent, false);
        return new SellerAllProductAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.productTitle.setText(product.getProductName());
        holder.productCategory.setText(product.getCategory());
        holder.productPrice.setText(product.getProductPrice());

        // Get coverImage
        String coverImageName = product.getProductImages().get(0);
        getDownloadUrl(coverImageName, product.getProductId(), holder.productImage);

        // Button
        holder.btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //use bundle to save selected productId
                String productId = product.getProductId();
                Bundle bundle  = new Bundle();
                bundle.putString("new_group_productId", productId);
                //pass bundle to fragment
                navController.navigate(R.id.action_sellerAllProductListFragment2_to_sellerAddGroupFragment, bundle);
            }
        });
    }

    private void getDownloadUrl(String coverImageName, String key, ImageView productImage) {
        StorageReference imgRef = FirebaseStorage.getInstance()
                .getReference("ProductImage")
                .child(key)
                .child(coverImageName);

        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri)
                        .placeholder(R.drawable.loading_image)
                        .error(R.drawable.app_default_image)
                        .into(productImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDownloadUrl(coverImageName, key, productImage);
                    }
                }, 100);
                productImage.setImageResource(R.drawable.loading_image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productTitle;
        TextView productCategory;
        TextView productPrice;
        ImageButton btn_post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.img_product_img);
            productTitle = itemView.findViewById(R.id.tv_product_name);
            productCategory = itemView.findViewById(R.id.tv_product_category);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            btn_post = itemView.findViewById(R.id.btn_product_post);
        }
    }
}
