package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter<InventoryRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private Map<String, Integer> entries;
    private OnItemClickListener listener;
    private String _STYLE;
    private int _QTY, _ORDERED,  _ALLOCATED, _TOALLOCATE, _TOORDER;

    public InventoryRecyclerViewAdapter(Context context, Map<String, Integer> entries) {
        this.context = context;
        this.entries = entries;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_inventory_info, parent, false);
        return new InventoryRecyclerViewAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<Map.Entry<String, Integer>> entryList = new ArrayList<>(entries.entrySet());
        Map.Entry<String, Integer> entry = entryList.get(position);
        Log.d("Text entry", "entries count: "+entryList.size());
        _STYLE = entry.getKey();
        _QTY = entry.getValue();
        // TODO: After customer placed order should change this value
        _ORDERED = 2;
        _TOORDER = _QTY - _ORDERED;

        holder.txvStyleName.setText(_STYLE);
        holder.txvOrdered.setText(String.valueOf(_ORDERED));
        holder.txvInStock.setText(String.valueOf(_QTY));
        holder.txvAllocated.setText(String.valueOf(_ALLOCATED));
        holder.txvToAllocate.setText(String.valueOf(_TOALLOCATE));
        holder.txvToOrder.setText(String.valueOf(_TOORDER));

        holder.btnStockIn.setOnClickListener(view -> listener.onStockInClick(position));
        holder.btnStockOut.setOnClickListener(view -> listener.onStockOutClick(position));
        holder.btnAllocate.setOnClickListener(view -> listener.onAllocateClick(position));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView txvStyleName, txvOrdered, txvAllocated, txvInStock, txvToAllocate, txvToOrder;
        Button btnStockIn, btnStockOut, btnAllocate;

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
            btnAllocate = itemView.findViewById(R.id.btn_allocate);
        }
    }

    public interface OnItemClickListener {
        void onStockInClick(int position);
        void onStockOutClick(int position);
        void onAllocateClick(int position);
    }
}
