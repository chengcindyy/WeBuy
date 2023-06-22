package com.csis4495_cmk.webuy.adapters;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Product;
import com.google.android.gms.tasks.OnFailureListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class SellerProductListRecyclerAdapter extends RecyclerView.Adapter<SellerProductListRecyclerAdapter.ViewHolder> {

    Context context;
    private OnAddProductButtonClickedListener listener;
    private List<String> productImages;
    private ArrayList<Product> products;
    private Map<String, Product> productMap;

    public SellerProductListRecyclerAdapter(Context context, ArrayList<Product> products, Map<String, Product> mProductMap, OnAddProductButtonClickedListener listener) {
        this.context = context;
        this.products = products;
        this.productMap = mProductMap;
        this.listener = listener;
        notifyDataSetChanged();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_seller_product, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        productImages = products.get(position).getProductImages();

        // DISPLAY PRODUCT IMAGE
        // Before read firebase storage image, set rules: allow read, write: if request.auth != null; (For testing)
        // getReference should pass Storage image folder name
        if (productImages != null && !productImages.isEmpty()) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("ProductImages");
            StorageReference imageReference = storageReference.child(productImages.get(0));
            imageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                // Got the download URL and pass it to Picasso to download, show in ImageView and caching
                Picasso.with(context).load(uri.toString()).into(holder.productImage);
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle errors, if image doesn't exist, show a default image
                    holder.productImage.setImageResource(R.drawable.default_image);
                }
            });
        } else {
            holder.productImage.setImageResource(R.drawable.default_image);
        }

        // DISPLAY PRODUCT NAME AND PRICE
        holder.productTitle.setText(products.get(position).getProductName());
        holder.productCategory.setText(products.get(position).getCategory());
        holder.productPrice.setText(products.get(position).getProductPrice());

        holder.btn_post.setOnClickListener(v -> listener.onButtonClick(true));

        Log.d("Test Map: ", productMap+"!");

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productCategory;
        TextView productPrice;
        ImageButton btn_post;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imv_product_img);
            productTitle = itemView.findViewById(R.id.tv_product_name);
            productCategory = itemView.findViewById(R.id.tv_product_category);
            productPrice = itemView.findViewById(R.id.tv_product_price);
            btn_post = itemView.findViewById(R.id.btn_product_post);
        }
    }

    public void removeItem(int position) {
        // Remove item
        products.remove(position);
        // Notify the adapter that an item has been removed
        notifyItemRemoved(position);
    }

    public interface OnAddProductButtonClickedListener {
        void onButtonClick(Boolean btnClicked);
    }
}
