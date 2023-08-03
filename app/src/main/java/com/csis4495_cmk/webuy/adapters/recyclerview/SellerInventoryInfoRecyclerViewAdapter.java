package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.fragments.SellerInventoryStockManagementFragment;
import com.csis4495_cmk.webuy.models.Inventory;

import java.util.List;

public class SellerInventoryInfoRecyclerViewAdapter extends RecyclerView.Adapter<SellerInventoryInfoRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Inventory> inventoryList;
    private Fragment fragment;
    private OnAllocateClickListener allocateListener;
    private SellerInventoryStockManagementFragment.onStockButtonClickListener stockListener;


    public SellerInventoryInfoRecyclerViewAdapter(Context context, List<Inventory> inventoryList, Fragment fragment) {
        this.context = context;
        this.inventoryList = inventoryList;
        this.fragment = fragment;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnAllocateClickListener listener) {
        this.allocateListener = listener;
    }

    public void setOnStockButtonClickListener(SellerInventoryStockManagementFragment.onStockButtonClickListener listener) {
        stockListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_inventory_info, parent, false);
        return new SellerInventoryInfoRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inventory inventory = inventoryList.get(position);

        holder.txvStyleName.setText(inventory.getInventoryName());
        holder.txvToSell.setText(String.valueOf(inventory.getToSell()));
        holder.txvOrdered.setText(String.valueOf(inventory.getOrdered()));
        holder.txvInStock.setText(String.valueOf(inventory.getInStock()));
        holder.txvAllocated.setText(String.valueOf(inventory.getAllocated()));
        holder.txvToAllocate.setText(String.valueOf(inventory.getToAllocated()));
        holder.txvToOrder.setText(String.valueOf(inventory.getToOrder()));
        holder.btnStockMgmt.setOnClickListener(view -> {
            String inventoryId = inventory.getInventoryId();
            String styleName = inventory.getInventoryName();
            int inStock = inventory.getInStock();
            int ordered = inventory.getOrdered();
            int toOrder = inventory.getToOrder();
            Log.d("test content", "inventoryId: "+inventoryId+"Style name: "+ styleName+ "inStock: "+ inStock+"ordered: "+ ordered+"toOrder: "+ toOrder);

            // Create Button menu
            SellerInventoryStockManagementFragment inventoryFragment = SellerInventoryStockManagementFragment.newInstance(inventoryId, styleName, inStock, ordered, toOrder);
            FragmentManager fragmentManager = fragment.getParentFragmentManager();
            inventoryFragment.show(fragmentManager, "Inventory Management Frag show");
            inventoryFragment.setOnStockButtonClickListener(stockListener);
        });
        holder.btnAllocate.setOnClickListener(view -> allocateListener.onAllocateClick(position));
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txvStyleName, txvToSell, txvOrdered, txvAllocated, txvInStock, txvToAllocate, txvToOrder;
        Button btnStockMgmt, btnAllocate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // TextView
            txvStyleName = itemView.findViewById(R.id.txv_style);
            txvToSell = itemView.findViewById(R.id.txv_to_sell);
            txvOrdered = itemView.findViewById(R.id.txv_ordered);
            txvAllocated = itemView.findViewById(R.id.txv_allocated);
            txvInStock = itemView.findViewById(R.id.txv_in_stock);
            txvToAllocate = itemView.findViewById(R.id.txv_to_allocate);
            txvToOrder = itemView.findViewById(R.id.txv_to_order);
            // Button
            btnStockMgmt = itemView.findViewById(R.id.btn_stock_mgmt);
            btnAllocate = itemView.findViewById(R.id.btn_allocate);
        }
    }

    public interface OnAllocateClickListener {
        void onAllocateClick(int position);
    }
}

