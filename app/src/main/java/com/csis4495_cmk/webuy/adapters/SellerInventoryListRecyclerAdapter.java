package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.SellerInventoryFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerInventoryListRecyclerAdapter extends RecyclerView.Adapter<SellerInventoryListRecyclerAdapter.ViewHolder> {

    private Context context;
    private OnDirectToProductPageOnClickListener listener;
    private ArrayList<SellerInventoryFragment.GroupItemEntry> displayItemsList;
    private List<String> coverImages;
    private InventoryRecyclerViewAdapter adapter;

    public SellerInventoryListRecyclerAdapter(Context context, OnDirectToProductPageOnClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setDisplayItemsList(ArrayList<SellerInventoryFragment.GroupItemEntry> displayItemsList) {
        this.displayItemsList = displayItemsList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_inventory_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        coverImages = displayItemsList.get(position).getGroup().getGroupImages();
        SellerInventoryFragment.GroupItemEntry groupItemEntry = displayItemsList.get(position);
        Map<String, Integer> entries = groupItemEntry.getEntries();

        holder.txvProductName.setText(groupItemEntry.getGroup().getGroupName());

        // Initialize child RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerViewInvInfo.setLayoutManager(layoutManager);
        adapter = new InventoryRecyclerViewAdapter(context, displayItemsList);
        holder.recyclerViewInvInfo.setAdapter(adapter);

        if(displayItemsList.get(position).getCoverImgUrl() != null) {
            Picasso.get().load(displayItemsList.get(position).getCoverImgUrl()).into(holder.imgProductImage);
        } else {
            holder.imgProductImage.setImageResource(R.drawable.app_default_image);
        }
    }

    @Override
    public int getItemCount() {
        return displayItemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txvProductName;
        ImageButton btnViewProduct;
        ImageView imgProductImage;
        TableLayout tableLayout;
        RecyclerView recyclerViewInvInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tableLayout = itemView.findViewById(R.id.tableLayout_inventory_items);
            txvProductName = itemView.findViewById(R.id.txv_product_name);
            imgProductImage = itemView.findViewById(R.id.img_product_img);
            btnViewProduct = itemView.findViewById(R.id.btn_view_product);
            recyclerViewInvInfo = itemView.findViewById(R.id.recyclerView_inventory_info);
        }
    }

    public interface OnDirectToProductPageOnClickListener {
        void onButtonClick(Boolean btnClicked, int position);
    }

}