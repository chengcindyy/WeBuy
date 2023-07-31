package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.viewmodels.SharedGroupInventoryListViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

    private DatabaseReference groupRef;

    private String groupId;

    private List<Inventory> inventoryList;

    private List<Order> orders;

    private Map<String, Map<String, Order.OrderItemInfo>> customerIdandItemsMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupRef = firebaseDatabase.getReference("Group");
        orderRef = firebaseDatabase.getReference("Order");
        orders = new ArrayList<>();
        inventoryList = new ArrayList<>();
        customerIdandItemsMap = new HashMap<>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_group_order, container, false);

        SharedGroupInventoryListViewModel listViewModel = new ViewModelProvider(requireActivity()).get(SharedGroupInventoryListViewModel.class);
        listViewModel.getGroupId().observe(this, s -> {
            if (s != null) {
                groupId = s;
            }
        });
        listViewModel.getInventoryList().observe(this, inventories -> {
            if (inventories != null) {
                inventoryList = inventories;
            }
        });

        tv_no = view.findViewById(R.id.tv_group_pending);

        rv = view.findViewById(R.id.rv_group_pending);

        getOrderData();

        return view;
    }

    public void getOrderData() {
        orders.clear();
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count =0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null) {
                        Order o = dataSnapshot.getValue(Order.class);
                        if(o.getOrderStatus() == 0 || o.getOrderStatus() == 1){
                            Set<String> groupIds = o.getGroupsAndItemsMap().keySet();
                            for (String key : groupIds) {
                                if (key.equals(groupId)) {
//                                orders.add(o);
//                                break;

//
                                    Map<String, Order.OrderItemInfo> orderItemList = o.getGroupsAndItemsMap().get(key);
                                    Map<String, Order.OrderItemInfo> notAllocatedItems = new HashMap<>();
                                    for (Map.Entry<String, Order.OrderItemInfo> entry : orderItemList.entrySet()){
                                        if(!entry.getValue().isAllocated()){
                                            notAllocatedItems.put(entry.getKey(), entry.getValue());
                                        }
                                    }
                                    count += orderItemList.size();
                                    customerIdandItemsMap.put(o.getCustomerId(),notAllocatedItems);
                                }
                            }
                        }
                    }
                }

                Log.d(TAG, "get Order item" + count + " "+  customerIdandItemsMap.keySet());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}