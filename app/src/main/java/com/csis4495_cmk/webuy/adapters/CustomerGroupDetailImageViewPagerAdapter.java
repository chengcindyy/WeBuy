package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.csis4495_cmk.webuy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomerGroupDetailImageViewPagerAdapter extends RecyclerView.Adapter<CustomerGroupDetailImageViewPagerAdapter.ViewHolder> {

    Context context;
    List<String> images;

    public CustomerGroupDetailImageViewPagerAdapter(Context context, List<String> images) {
        this.context = context;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.viewpager_customer_group_detail_images, parent, false);
        return new CustomerGroupDetailImageViewPagerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = images.get(position);
        Picasso.get().load(url).into(holder.imvSingleImg);

        holder.tvImgCount.setText((position+1) + "/" + getItemCount());
    }

    @Override
    public int getItemCount() {
        return images.size();
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
