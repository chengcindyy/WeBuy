package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.SellerInventoryFragment;
import com.csis4495_cmk.webuy.models.Group;
import com.google.android.material.tabs.TabLayout;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerInventoryListRecyclerAdapter extends RecyclerView.Adapter<SellerInventoryListRecyclerAdapter.ViewHolder> {

    private Context context;
    private TableLayout tableLayout;
    private ArrayList<SellerInventoryFragment.GroupItemEntry> displayItemsList;
    private List<String> coverImages;

    public SellerInventoryListRecyclerAdapter(Context context, ArrayList<SellerInventoryFragment.GroupItemEntry> displayItemsList) {
        this.context = context;
        this.displayItemsList = displayItemsList;
    }

    public void updateData(ArrayList<SellerInventoryFragment.GroupItemEntry> displayItems) {
        this.displayItemsList = displayItems;
        Log.d("Test RecyclerView", "Updating data with " + displayItems.size() + " items");
        for (SellerInventoryFragment.GroupItemEntry item : displayItems) {
            Log.d("Test RecyclerView", "Item: " + item.toString());
        }
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
        SellerInventoryFragment.GroupItemEntry groupItemEntry = displayItemsList.get(position);
        Map<String, Integer> entries = groupItemEntry.getEntries();

        holder.txvProductName.setText(groupItemEntry.getGroup().getGroupName());


        for(Map.Entry<String, Integer> entry : entries.entrySet()) {
            // Create a new row
            TableRow row = new TableRow(context);

            int orderedNum = 14;
            int inStockNum = entry.getValue();
            int toOrderNumber = inStockNum - orderedNum;

            // Create a TextView for each column in the row
            // TextView: Style name
            TextView txvStyleName = new TextView(context);
            txvStyleName.setText(entry.getKey());
            // TextView: Ordered
            TextView txvOrdered = new TextView(context);
            txvOrdered.setText(String.valueOf(orderedNum));
            // TextView: In-stock
            TextView inStockTextView = new TextView(context);
            inStockTextView.setText(String.valueOf(inStockNum));
            // TextView: To order
            TextView toOrderTextView = new TextView(context);
            if(toOrderNumber < 0){
                toOrderTextView.setText(String.valueOf(toOrderNumber));
            }else{
                toOrderTextView.setText("0");
            }


            // Add the TextViews to the row
            row.addView(txvStyleName);
            row.addView(txvOrdered);
            row.addView(inStockTextView);
            row.addView(toOrderTextView);

            // Add the row to the TableLayout
            holder.tableLayout.addView(row);

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tableLayout = itemView.findViewById(R.id.tableLayout_inventory_items);
            txvProductName = itemView.findViewById(R.id.txv_product_name);
            imgProductImage = itemView.findViewById(R.id.img_product_img);
            btnViewProduct = itemView.findViewById(R.id.btn_view_product);
        }
    }

}
