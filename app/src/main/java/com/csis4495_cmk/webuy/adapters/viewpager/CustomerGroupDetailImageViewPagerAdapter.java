package com.csis4495_cmk.webuy.adapters.viewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomerGroupDetailImageViewPagerAdapter extends RecyclerView.Adapter<CustomerGroupDetailImageViewPagerAdapter.ViewHolder> {

    Context context;
    List<String> imageUrls;

    public CustomerGroupDetailImageViewPagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewpager_customer_group_detail_images, parent, false);
        return new CustomerGroupDetailImageViewPagerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Picasso.get().load(url).into(holder.imvSingleImg);

        holder.tvImgCount.setText((position+1) + "/" + getItemCount());
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imvSingleImg;
        TextView tvImgCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imvSingleImg = itemView.findViewById(R.id.imv_group_single_img);
            tvImgCount = itemView.findViewById(R.id.tv_img_count);
        }
    }

}
