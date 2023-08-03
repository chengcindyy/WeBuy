package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class SellerProductListRecyclerAdapter extends RecyclerView.Adapter<SellerProductListRecyclerAdapter.ViewHolder> {

    Context context;
    private OnAddProductButtonClickedListener listener;
    private String productId;
    private String sellerId;
    private List<String> productImages;
    private ArrayList<Product> products;
    private Map<String, Product> productMap;

    public SellerProductListRecyclerAdapter(){
        // default constructor
    }

    public SellerProductListRecyclerAdapter(Context context, OnAddProductButtonClickedListener listener) {
        this.context = context;
        this.listener = listener;
    }


    public void setContext(Context context) {
        this.context = context;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void setProductMap(Map<String, Product> productMap) {
        this.productMap = productMap;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_seller_product, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        productImages = product.getProductImages();
        Log.d("Test img",position + ": " + productImages.get(0));

        if(products.get(position).getCoverImgUrl() != null && !productImages.isEmpty()) {
            Picasso.get().load(products.get(position).getCoverImgUrl()).into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.app_default_image);
        }

        // DISPLAY PRODUCT NAME AND PRICE
        holder.productTitle.setText(product.getProductName());
        holder.productCategory.setText(product.getCategory());
        holder.productPrice.setText(product.getProductPrice());
        holder.btn_post.setOnClickListener(v -> listener.onButtonClick(true, position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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

    public void removeItem(int position) {
        // Remove item
        products.remove(position);
        // Notify the adapter when an item has been removed
        notifyItemRemoved(position);
    }

    public interface OnAddProductButtonClickedListener {
        boolean onCreateOptionsMenu(Menu menu);

        void onButtonClick(Boolean btnClicked, int position);
    }
}
