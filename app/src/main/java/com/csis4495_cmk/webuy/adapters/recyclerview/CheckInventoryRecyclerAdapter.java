package com.csis4495_cmk.webuy.adapters.recyclerview;

import static android.content.ContentValues.TAG;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
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
    private Map<String, Integer> toAddMap = new HashMap<>();

    private List<String> keys;

    public void setToAddMap(Map<String, Integer> toAddMap) {
        this.toAddMap = toAddMap;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    private int errorPosition = -1;


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

    public Map<String, Integer> getToAddMap() {
        return toAddMap;
    }

    @NonNull
    @Override
    public CheckInventoryRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seller_check_group_inventory, parent, false);
        return new CheckInventoryRecyclerAdapter.ViewHolder(v);
    }

    public void checkEmptyPosition(int position){
        errorPosition = position;
        notifyItemChanged(position);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckInventoryRecyclerAdapter.ViewHolder holder, int position) {
        String key = keys.get(position);
        String pName = nameMap.get(key);
        Integer pInventory = inventoryMap.get(key);
        holder.name.setText(pName);
        holder.inventory.setText(Integer.toString(pInventory));
        toAddMap.put(keys.get(position), 0);
        holder.toRestock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String number = s.toString();
                if (!number.isEmpty()) {
                    try {
                        Integer toRestock = Integer.parseInt(number);
                        if (toRestock > pInventory) {
                            holder.toRestock.setError("Restock value should be equal or less than remaining quantity");
                            holder.toRestock.setText("");
                            holder.toRestock.requestFocus();
                        }else if (number.length() > 1 && number.startsWith("0")) {
                            holder.toRestock.setError("The quantity cannot start with 0");
                            holder.toRestock.setText("");
                            holder.toRestock.requestFocus();
                        }
                        toAddMap.put(keys.get(position), toRestock);
                        Log.d("TextWatcher", "to restock" + toAddMap);
                    } catch (NumberFormatException e) {
                        Log.d("TextWatcher", "new price error " + e.toString());
                    }
                }
            }
        });
        if (position == errorPosition) {
            holder.toRestock.setError("Please input the value");
        }
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CheckBox cb;
        private TextView name, inventory;
        private  EditText toRestock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            cb = itemView.findViewById(R.id.tv_group_check_inventory_cb);
            name = itemView.findViewById(R.id.tv_group_check_inventory_name);
            inventory = itemView.findViewById(R.id.tv_group_check_inventory_inventory);
            toRestock = itemView.findViewById(R.id.et_group_check_inventory_to_restock);
        }

        public void setEmptyError(){
            toRestock.setError("Please input the value");
        }

        public boolean isAnyFieldEmpty() {
            return toRestock.getText().toString().trim().isEmpty();
        }
    }

}
