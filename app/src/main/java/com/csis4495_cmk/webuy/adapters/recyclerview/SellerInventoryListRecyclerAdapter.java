package com.csis4495_cmk.webuy.adapters.recyclerview;

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

import com.bumptech.glide.Glide;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.fragments.SellerInventoryFragment;
import com.csis4495_cmk.webuy.models.Inventory;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerInventoryListRecyclerAdapter extends RecyclerView.Adapter<SellerInventoryListRecyclerAdapter.ViewHolder> implements SellerInventoryInfoRecyclerViewAdapter.OnActionButtonClickListener {

    private Context context;
    private static OnButtonClickListener onButtonClickListener;
    private Map<String, List<Inventory>> displayItemsMap;
    private List<String> productIds;
    private List<String> allCoverImgsList;
    private SellerInventoryInfoRecyclerViewAdapter adapter;
    private SellerInventoryFragment sellerInventoryFragment;

    public SellerInventoryListRecyclerAdapter(Context context, OnButtonClickListener listener, SellerInventoryFragment sellerInventoryFragment) {
        this.context = context;
        this.onButtonClickListener = listener;
        this.sellerInventoryFragment = sellerInventoryFragment;
    }

    public void setDisplayItemsList(Map<String, List<Inventory>> inventoryMap, List<String> allCoverImgsList) {
        this.displayItemsMap = inventoryMap;
        this.allCoverImgsList = allCoverImgsList;
        this.productIds = new ArrayList<>(displayItemsMap.keySet());
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
        String productId = productIds.get(position);
        List<Inventory> inventoryItems = displayItemsMap.get(productId);

        // Set product name (title)
        Inventory firstInventory = inventoryItems.get(0);
        holder.txvProductName.setText(firstInventory.getInventoryTitle());

        // Initialize child RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
        holder.recyclerViewInvInfo.setLayoutManager(layoutManager);
        adapter = new SellerInventoryInfoRecyclerViewAdapter(context, inventoryItems, sellerInventoryFragment);
        adapter.setOnItemClickListener(this);
        adapter.setOnStockButtonClickListener(sellerInventoryFragment);
        holder.recyclerViewInvInfo.setAdapter(adapter);


        // Set images
        String imageUrl = firstInventory.getImageUrl();
        Glide.with(holder.imgProductImage.getContext()).load(imageUrl).into(holder.imgProductImage);

        // Set product image button
        holder.btnViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClickListener.onOpenProductPageButtonClick(productId);
            }
        });

//        holder.bind(position);
    }


    @Override
    public int getItemCount() {
        return displayItemsMap.size();
    }

    @Override
    public void onAllocateClicked(String groupId) {
        onButtonClickListener.onOpenAllocateButtonClick(groupId);
    }

    @Override
    public void onRestoreClicked(int restoreAmount) {
        onButtonClickListener.onOpenStoreRestoreButtonClick(restoreAmount);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ExpandableLayout exp_inventory_card;
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
//            exp_inventory_card = itemView.findViewById(R.id.expandableLayout_inventory_card);
        }

//        public void bind(int position) {
//            itemView.setOnClickListener(v -> {
//                if (exp_inventory_card.isExpanded()) {
//                    exp_inventory_card.collapse();
//                } else {
//                    exp_inventory_card.expand();
//                }
//            });
//        }
    }


    public interface OnButtonClickListener {
        void onOpenProductPageButtonClick(String productId);
        void onOpenAllocateButtonClick(String productId);
        void onOpenStoreRestoreButtonClick(int restoreAmount);
    }
}
