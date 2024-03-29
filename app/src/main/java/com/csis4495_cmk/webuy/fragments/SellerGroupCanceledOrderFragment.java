package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.GroupDetailInventoryRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.viewmodels.SharedGroupInventoryListViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SellerGroupCanceledOrderFragment extends Fragment {

    private TextView tv_no;
    private RecyclerView rv;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference orderRef;

    private Group group;

    private Map<String, Map<String, Order.OrderItemInfo>> orderIdandItemsMap;

    private String groupId;

    private String productId;

    private GroupDetailInventoryRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        orderRef = firebaseDatabase.getReference("Order");
        orderIdandItemsMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_group_canceled_order, container, false);

        tv_no = view.findViewById(R.id.tv_group_order_canceled);

        rv = view.findViewById(R.id.rv_group_order_canceled);

        getViewModelData();

        getOrderData();

        return view;
    }

    public void getViewModelData() {
        if (getActivity() != null) {
            SharedGroupInventoryListViewModel listViewModel = new ViewModelProvider(requireActivity()).get(SharedGroupInventoryListViewModel.class);

            listViewModel.getGroupId().observe(this, s -> {
                if (s != null) {
                    groupId = s;
                    Log.d(TAG, "livemodel groupId: " + groupId);
                }
            });

            listViewModel.getProductId().observe(this, s -> {
                if (s != null) {
                    productId = s;
                    Log.d(TAG, "livemodel productId: " + productId);
                }
            });

            listViewModel.getGroup().observe(this, g -> {
                if (g != null) {
                    group = g;
                    Log.d(TAG, "livemodel group: " + group);
                }
            });

        }
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
                        if (o.getOrderStatus() == -1) {
                            Set<String> groupIds = o.getGroupsAndItemsMap().keySet();
                            for (String key : groupIds) {
                                if (key.equals(groupId)) {
                                    Map<String, Order.OrderItemInfo> orderItemList = o.getGroupsAndItemsMap().get(key);
                                    Map<String, Order.OrderItemInfo> allAllocatedItems = new HashMap<>();
                                    for (Map.Entry<String, Order.OrderItemInfo> entry : orderItemList.entrySet()) {
                                        if (entry.getValue().isAllocated() == true) {
                                            allAllocatedItems.put(entry.getKey(), entry.getValue());
                                        }
                                    }
                                    Log.d(TAG, "allAllocatedItems: " + allAllocatedItems);
                                    if (!allAllocatedItems.isEmpty()) {
                                        orderIdandItemsMap.put(orderId, allAllocatedItems);
                                    }
                                }
                            }
                        }
                    }
                }

                if (orderIdandItemsMap.size() > 0) {
                    adapter = new GroupDetailInventoryRecyclerAdapter(orderIdandItemsMap, groupId);
                    rv.setAdapter(adapter);
                    adapter.setAllocatedOrder(true);
                    adapter.notifyDataSetChanged();
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    tv_no.setText("Canceled:");
                    Log.d(TAG, "check orderIdandItemsMap: " + orderIdandItemsMap.size() + orderIdandItemsMap);
                }
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}