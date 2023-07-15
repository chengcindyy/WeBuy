package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.SellerInventoryFragment;

import java.util.ArrayList;
import java.util.Map;

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter<InventoryRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SellerInventoryFragment.GroupItemEntry> displayItemsList;
    private String _STYLE;
    private int _QTY, _ORDERED, _INSTOCK, _ALLOCATED, _TOALLOCATE, _TOORDER;

    public InventoryRecyclerViewAdapter(Context context, ArrayList<SellerInventoryFragment.GroupItemEntry> displayItemsList) {
        this.context = context;
        this.displayItemsList = displayItemsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_inventory_info, parent, false);
        return new InventoryRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SellerInventoryFragment.GroupItemEntry groupItemEntry = displayItemsList.get(position);
        Map<String, Integer> entries = groupItemEntry.getEntries();

        for(Map.Entry<String, Integer> entry : entries.entrySet()) {
            _STYLE = entry.getKey();
            _QTY = entry.getValue();
        }
        _ORDERED = 2;
        _TOORDER = _INSTOCK - _ORDERED;

        holder.txvStyleName.setText(_STYLE);
        holder.txvOrdered.setText(String.valueOf(_ORDERED));
        holder.txvInStock.setText(String.valueOf(_QTY));
        holder.txvAllocated.setText(String.valueOf(_ALLOCATED));
        holder.txvToAllocate.setText(String.valueOf(_TOALLOCATE));
        holder.txvToOrder.setText(String.valueOf(_TOORDER));
    }

    @Override
    public int getItemCount() {
        return displayItemsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txvStyleName, txvOrdered, txvAllocated, txvInStock, txvToAllocate, txvToOrder;
        Button btnStockIn, btnStockOut;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txvStyleName = itemView.findViewById(R.id.txv_style);
            txvOrdered = itemView.findViewById(R.id.txv_ordered);
            txvAllocated = itemView.findViewById(R.id.txv_allocated);
            txvInStock = itemView.findViewById(R.id.txv_in_stock);
            txvToAllocate = itemView.findViewById(R.id.txv_to_allocated);
            txvToOrder = itemView.findViewById(R.id.txv_to_order);
            btnStockIn = itemView.findViewById(R.id.btn_stock_in);
            btnStockOut = itemView.findViewById(R.id.btn_stock_out);
        }
    }
}
