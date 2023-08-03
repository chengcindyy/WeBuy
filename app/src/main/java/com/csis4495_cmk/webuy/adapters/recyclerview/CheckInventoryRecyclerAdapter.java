package com.csis4495_cmk.webuy.adapters.recyclerview;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckInventoryRecyclerAdapter extends RecyclerView.Adapter<CheckInventoryRecyclerAdapter.ViewHolder> {
    private Map<String, Integer> inventoryMap;
    private Map<String, String> nameMap;
    private Map<String, Boolean> toAddMap = new HashMap<>();

    private List<String> keys;

    public void setToAddMap(Map<String, Boolean> toAddMap) {
        this.toAddMap = toAddMap;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public CheckInventoryRecyclerAdapter() {
    }

    public CheckInventoryRecyclerAdapter(Map<String, Integer> inventoryMap, Map<String, String> nameMap) {
        this.inventoryMap = inventoryMap;
        this.nameMap = nameMap;
        this.keys = new ArrayList<>(nameMap.keySet());
        Log.d(TAG, "CheckInventoryRecyclerAdapter: " + keys);
    }

    public Map<String, Integer> getInventoryMap() {
        return inventoryMap;
    }

    public void setInventoryMap(Map<String, Integer> inventoryMap) {
        this.inventoryMap = inventoryMap;
    }

    public Map<String, String> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<String, String> nameMap) {
        this.nameMap = nameMap;
    }

    public Map<String, Boolean> getToAddMap() {
        return toAddMap;
    }

    @NonNull
    @Override
    public CheckInventoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_check_group_inventory, parent, false);
        return new CheckInventoryRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInventoryRecyclerAdapter.ViewHolder holder, int position) {
        String key  = keys.get(position);
        String pName = nameMap.get(key);
        Integer pInventory = inventoryMap.get(key);
        holder.name.setText(pName);
        holder.inventory.setText(Integer.toString(pInventory));

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.cb.isChecked()){
                    toAddMap.put(keys.get(position),true);
                    Log.d(TAG, "Checkbox onClick: " + toAddMap);
                }else{
                    toAddMap.remove(keys.get(position));
                    Log.d(TAG, "Checkbox onClick: " + toAddMap);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cb;
        TextView name;
        TextView inventory;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb = itemView.findViewById(R.id.tv_group_check_inventory_cb);
            name = itemView.findViewById(R.id.tv_group_check_inventory_name);
            inventory = itemView.findViewById(R.id.tv_group_check_inventory_inventory);
        }
    }

}
