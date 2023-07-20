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
import com.csis4495_cmk.webuy.fragments.SellerInventoryFragment;
import com.csis4495_cmk.webuy.models.ProductStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InventoryRecyclerViewAdapter extends RecyclerView.Adapter<InventoryRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Map.Entry<String, Integer>> entriesList;
    private SellerInventoryFragment.GroupItemEntry groupEntry;
    private OnItemClickListener listener;
    private String styleId, productId, styleName, productName;
    private int qty, ordered, allocated, toAllocate, toOrder;

    public InventoryRecyclerViewAdapter(Context context, Map<String, Integer> entries, SellerInventoryFragment.GroupItemEntry groupEntry) {
        this.context = context;
        this.entriesList = new ArrayList<>(entries.entrySet());
        this.groupEntry = groupEntry;
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
        Map.Entry<String, Integer> entry = entriesList.get(position);

        // Processing styleId
        String styleCombinedKey = entry.getKey();
        String[] keySet = styleCombinedKey.split("___");

        if (keySet[0].equals("s")){
            styleId = keySet[1];
            qty = entry.getValue();
            Log.d("TestKeySet", "styleId:"+styleId+" qty:"+qty);
            for (ProductStyle style : groupEntry.getGroup().getGroupStyles()) {
                if (styleId.equals(style.getStyleId())){
                    styleName = style.getStyleName();
                    holder.txvStyleName.setText(styleName);
                    break;
                }
            }
        } else {
            productId = keySet[1];
            qty = entry.getValue();
            Log.d("TestKeySet", "productId:"+productId+" qty:"+qty);
            if (productId.equals(groupEntry.getGroup().getProductId())){
                productName = groupEntry.getGroup().getGroupName();
                holder.txvStyleName.setText(productName);
            }
        }

        // TODO: After customer placed order should change this value
        ordered = 2;
        toOrder = qty - ordered;

        holder.txvOrdered.setText(String.valueOf(ordered));
        holder.txvInStock.setText(String.valueOf(qty));
        holder.txvAllocated.setText(String.valueOf(allocated));
        holder.txvToAllocate.setText(String.valueOf(toAllocate));
        holder.txvToOrder.setText(String.valueOf(toOrder));

        holder.btnStockIn.setOnClickListener(view -> listener.onStockInClick(position));
        holder.btnStockOut.setOnClickListener(view -> listener.onStockOutClick(position));
        holder.btnAllocate.setOnClickListener(view -> listener.onAllocateClick(position));
    }

    @Override
    public int getItemCount() {
        return entriesList.size();
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
