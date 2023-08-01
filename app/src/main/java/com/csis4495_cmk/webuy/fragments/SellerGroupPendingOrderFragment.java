package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.GroupDetailInventoryRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.User;
import com.csis4495_cmk.webuy.viewmodels.SharedGroupInventoryListViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SellerGroupPendingOrderFragment extends Fragment {

    private TextView tv_no;
    private RecyclerView rv;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference orderRef;

    private Group group;

    private List<Inventory> inventoryList;

    private Map<String, Map<String, Order.OrderItemInfo>> orderIdandItemsMap;

    private Map<String, Map<String, Boolean>> selectedOrderMap;

    private Button btnCancel, btnAllocate;

    private Map<String, String> inventoryIdMap;

    private String groupId;

    boolean allAllocated = true;

    private GroupDetailInventoryRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        orderRef = firebaseDatabase.getReference("Order");
        inventoryList = new ArrayList<>();
        orderIdandItemsMap = new HashMap<>();
        inventoryIdMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_group_order, container, false);

        selectedOrderMap = new HashMap<>();

        tv_no = view.findViewById(R.id.tv_group_pending);

        rv = view.findViewById(R.id.rv_group_pending);

        btnAllocate = view.findViewById(R.id.btn_group_order_item_allocate);

//        btnCancel = view.findViewById(R.id.btn_group_order_item_cancel);

        SharedGroupInventoryListViewModel listViewModel = new ViewModelProvider(requireActivity()).get(SharedGroupInventoryListViewModel.class);
        listViewModel.getInventoryList().observe(this, inventories -> {
            if (inventories != null) {
                inventoryList.clear();
                inventoryList = inventories;
                Log.d(TAG, "livemodel inventory: " + inventoryList);
                for (Inventory i : inventories) {
                    if (i.getToAllocated() != 0) {
                        allAllocated = false;
                        break;
                    }
                }
                if (allAllocated == true) {
                    Log.d(TAG, "check btnAllocate visibility: allSoldOut " + allAllocated);
                    btnAllocate.setVisibility(View.GONE);
                } else {
                    btnAllocate.setVisibility(View.VISIBLE);
                }
            }
        });

        listViewModel.getGroupId().observe(this, s -> {
            if (s != null) {
                groupId = s;
                Log.d(TAG, "livemodel groupId: " + groupId);
            }
        });

        listViewModel.getGroup().observe(this, g -> {
            if (g != null) {
                group = g;
                Log.d(TAG, "livemodel group: " + group);
            }
        });

        listViewModel.getinventoryIdMap().observe(this, stringStringMap -> {
            if (stringStringMap != null) {
                inventoryIdMap = stringStringMap;
                Log.d(TAG, "livemodel stringStringMap: " + stringStringMap);
            }
        });

        getOrderData();

        btnAllocate.setOnClickListener(v -> {
            selectedOrderMap = adapter.getToAllocateMap();
            Log.d(TAG, "Allocate click selectedOrderMap: " + selectedOrderMap);
            if (selectedOrderMap == null || selectedOrderMap.size() == 0) {
                Toast.makeText(getContext(), "Please select an order", Toast.LENGTH_SHORT).show();
            } else {
                boolean isEnough = true;
                List<Inventory> toUpdateInventory = new ArrayList<>();
                Map<String, ArrayList<String>> toUpdateOrder = new HashMap<>();

                outerLoop:
                // Iterate the selectedOrderMap
                for (Map.Entry<String, Map<String, Boolean>> selectedOrderEntry : selectedOrderMap.entrySet()) {
                    String orderId = selectedOrderEntry.getKey();

                    Map<String, Boolean> selectedItemsMap = selectedOrderEntry.getValue();
                    Log.d(TAG, "Allocate click: selectedItems " + selectedItemsMap);

                    // Check if the selected orderId exists in orderIdandItemsMap
                    if (orderIdandItemsMap.containsKey(orderId)) {
                        Map<String, Order.OrderItemInfo> orderItemInfoMap = orderIdandItemsMap.get(orderId);
                        Log.d(TAG, "Allocate click: orderItemInfoMap " + orderItemInfoMap);

                        Order.OrderItemInfo item;
                        String productId;
                        String styleId;
                        String pSplit = "p___";
                        String sSplit = "s___";
                        String order_style_key;

                        // Iterate through the selected items
                        for (String pid_sid : selectedItemsMap.keySet()) {
                            item = orderItemInfoMap.get(pid_sid);
                            Integer orderAmount = item.getOrderAmount();
                            Log.d(TAG, "Allocate click: orderAmount: " + Integer.toString(orderAmount));

                            if (!pid_sid.contains(sSplit)) {
                                productId = pid_sid.split(pSplit)[1].split(sSplit)[0];
                                order_style_key = productId;
                                Log.d(TAG, "Allocate click: productId_styleId: " + order_style_key);
                            } else {
                                productId = pid_sid.split(pSplit)[1].split(sSplit)[0];
                                styleId = pid_sid.split(pSplit)[1].split(sSplit)[1];
                                order_style_key = productId + "_" + styleId;
                                Log.d(TAG, "Allocate click: productId_styleId: " + order_style_key);
                            }
                            for (Inventory i : inventoryList) {
                                if (i.getProductStyleKey().contains(order_style_key)) {
                                    Log.d(TAG, "Allocate click: inventory name: " + i.getInventoryTitle());
                                    Integer oldToAllocated = i.getToAllocated();
                                    Integer oldAllocated = i.getAllocated();
                                    Log.d(TAG, "Allocate click: inventory to allocated Before: " + Integer.toString(oldToAllocated));
                                    Log.d(TAG, "Allocate click: inventory allocated Before: " + Integer.toString(oldAllocated));

                                    if (i.getToAllocated() < orderAmount) {
                                        isEnough = false;
                                        toUpdateOrder.clear();
                                        toUpdateInventory.clear();
                                        break outerLoop;
                                    } else {
                                        Integer newToAllocated = oldToAllocated - orderAmount;
                                        Integer newAllocated = oldAllocated + orderAmount;
                                        Log.d(TAG, "Allocate click: inventory to allocated After: " + Integer.toString(newToAllocated));
                                        Log.d(TAG, "Allocate click: inventory allocated After: " + Integer.toString(newAllocated));
                                        i.setToAllocated(newToAllocated);
                                        i.setAllocated(newAllocated);
                                        Log.d(TAG, "Allocate click: inventory i.getToAllocated: " + Integer.toString(i.getToAllocated()));
                                        Log.d(TAG, "Allocate click: inventory i.getAllocated: " + Integer.toString(i.getAllocated()));
                                        toUpdateInventory.add(i);
                                        ArrayList<String> addedOrderItems = toUpdateOrder.getOrDefault(orderId, new ArrayList<>());
                                        addedOrderItems.add(pid_sid);
                                        toUpdateOrder.put(orderId, addedOrderItems);
                                    }
                                }
                            }
                        }
                    }
                }
                if (isEnough) {
                    Log.d(TAG, "Allocate click toUpdateInventory: " + toUpdateInventory);
                    Log.d(TAG, "Allocate click toUpdateOrder: " + toUpdateOrder);
                    for (String orderId : toUpdateOrder.keySet()) {
                        ArrayList<String> pid_sids = toUpdateOrder.get(orderId);
                        for (String id : pid_sids) {
                            DatabaseReference toUpdateGroupOrder = orderRef.child(orderId).child("groupsAndItemsMap").child(groupId).child(id).child("allocated");
                            toUpdateGroupOrder.setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "Successfully allocated order: " + orderId + ", " + id);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Failed to allocate order: " + orderId + ", " + id);
                                }
                            });
                        }
                    }

                    for (Inventory i : toUpdateInventory) {
                        for (Map.Entry<String, String> entry : inventoryIdMap.entrySet()) {
                            String inventoryId = entry.getKey();
                            String value = entry.getValue();
                            String psKey = i.getProductStyleKey();
                            Integer allocated = i.getAllocated();
                            Integer toAllocated = i.getToAllocated();
                            if (psKey.equals(value)) {
                                DatabaseReference toUpdateInventoryRef = FirebaseDatabase.getInstance().getReference("Inventory").child(inventoryId);
                                Map<String, Object> toUpdates = new HashMap<>();
                                toUpdates.put("allocated", allocated);
                                toUpdates.put("toAllocated", toAllocated);
                                toUpdateInventoryRef.updateChildren(toUpdates)
                                        .addOnSuccessListener(unused -> Log.d(TAG, "Successfully updated inventory: " + inventoryId))
                                        .addOnFailureListener(e -> Log.d(TAG, "Failed to updated : " + inventoryId));
                            }
                        }
                    }

                } else {
                    Toast.makeText(getContext(), "Not enough inventory, please select again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    public void getOrderData() {
        orderIdandItemsMap.clear();
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        Order o = dataSnapshot.getValue(Order.class);
                        String orderId = dataSnapshot.getKey();
                        if (o.getOrderStatus() == 0 || o.getOrderStatus() == 1) {
                            Set<String> groupIds = o.getGroupsAndItemsMap().keySet();
                            for (String key : groupIds) {
                                if (key.equals(groupId)) {
                                    Map<String, Order.OrderItemInfo> orderItemList = o.getGroupsAndItemsMap().get(key);
                                    Map<String, Order.OrderItemInfo> notAllocatedItems = new HashMap<>();
                                    for (Map.Entry<String, Order.OrderItemInfo> entry : orderItemList.entrySet()) {
                                        if (entry.getValue().isAllocated() == false) {
                                            notAllocatedItems.put(entry.getKey(), entry.getValue());
                                        }
                                    }
                                    Log.d(TAG, "notAllocatedItems: " + notAllocatedItems);
                                    if (!notAllocatedItems.isEmpty()) {
                                        orderIdandItemsMap.put(orderId, notAllocatedItems);
                                    }
                                }
                            }
                        }
                    }
                }

                if (orderIdandItemsMap.size() > 0 && inventoryList != null) {
                    adapter = new GroupDetailInventoryRecyclerAdapter(orderIdandItemsMap, groupId);
                    rv.setAdapter(adapter);
                    if (inventoryList != null) {
                        adapter.setInventoryList(inventoryList);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "setInventoryList: " + inventoryList);
                    }
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    tv_no.setText("To Allocate");
                    Log.d(TAG, "check orderIdandItemsMap: " + orderIdandItemsMap);
                    Log.d(TAG, "check inventoryList: " + inventoryList);
                }
                if (orderIdandItemsMap.size() == 0) {
                    Log.d(TAG, "check btnAllocate setVisibility orderIdandItemsMap.size: " + orderIdandItemsMap.size());
                    btnAllocate.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}