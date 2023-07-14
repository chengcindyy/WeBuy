package com.csis4495_cmk.webuy;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.csis4495_cmk.webuy.adapters.SellerInventoryListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerInventoryFragment extends Fragment {

    private SearchView searchBar;
    private TabLayout tabLayout;
    private RecyclerView mRecyclerView;
    private SellerInventoryListRecyclerAdapter adapter;
    private ArrayList<GroupItemEntry> allItemsList;
    private ArrayList<GroupItemEntry> inStockItemsList;
    private ArrayList<GroupItemEntry> preOrderItemsList;
    private ArrayList<String> allCoverImgsList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_inventory, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // References
        // SearchBar
        searchBar = view.findViewById(R.id.search_product);
        // TabLayout
        tabLayout = view.findViewById(R.id.tabLayout_filter);
        // List
        allItemsList = new ArrayList<>();
        inStockItemsList = new ArrayList<>();
        preOrderItemsList = new ArrayList<>();
        allCoverImgsList = new ArrayList<>();
        // RecyclerView
        mRecyclerView = view.findViewById(R.id.recyclerView_seller_inventory_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SellerInventoryListRecyclerAdapter(getContext(), allItemsList);
        setInventoryDetails();
        UpdateRecyclerView();


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tabLayout.getSelectedTabPosition()) {
                    case 1:
                        adapter.updateData(inStockItemsList);
                        Log.d("Test firebase read", "In-stock tab clicked");
                        break;
                    case 2:
                        adapter.updateData(preOrderItemsList);
                        Log.d("Test firebase read", "Pre-order tab clicked");
                        break;
                    default:
                        adapter.updateData(allItemsList);
                        Log.d("Test firebase read", "all Items tab clicked");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setInventoryDetails() {
        Log.d("Test firebase read", "setInventoryDetails in method");
        reference = FirebaseDatabase.getInstance().getReference("Group");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allItemsList.clear();
                inStockItemsList.clear();
                preOrderItemsList.clear();
                allCoverImgsList.clear();

                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    Group group = productSnapshot.getValue(Group.class);
                    String sellerId = group.getSellerId();
                    // Check sellerId to only display a seller's groups
                    if(sellerId != null && sellerId.equals(auth.getCurrentUser().getUid())){

                        GroupItemEntry groupEntry = new GroupItemEntry(group, group.getGroupQtyMap());
                        allItemsList.add(groupEntry);
                        adapter.updateData(allItemsList);
                        if (group.getGroupType() == 0) {
                            inStockItemsList.add(groupEntry);
                            adapter.updateData(inStockItemsList);
                        } else {
                            preOrderItemsList.add(groupEntry);
                            adapter.updateData(preOrderItemsList);
                        }
                    }
                }
//                updateTabData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Test firebase read", "Error reading data", error.toException());
            }
        });
    }

    private void UpdateRecyclerView() {
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

//    private void updateTabData() {
//        switch (tabLayout.getSelectedTabPosition()) {
//            case 1:
//                adapter.updateData(filterInStockItems(allItemsList));
//                break;
//            case 2:
//                adapter.updateData(filterPreOrderItems(allItemsList));
//                break;
//            default:
//                adapter.updateData(new ArrayList<>(allItemsList));
//                break;
//        }
//    }

//    private ArrayList<GroupItemEntry> filterInStockItems(ArrayList<GroupItemEntry> items) {
//        ArrayList<GroupItemEntry> inStockItems = new ArrayList<>();
//        for (GroupItemEntry item : items) {
//            if (item.getGroup().getGroupType() == 0) {
//                inStockItems.add(item);
//            }
//        }
//        return inStockItems;
//    }
//
//    private ArrayList<GroupItemEntry> filterPreOrderItems(ArrayList<GroupItemEntry> items) {
//        ArrayList<GroupItemEntry> preOrderItems = new ArrayList<>();
//        for (GroupItemEntry item : items) {
//            if (item.getGroup().getGroupType() != 0) {
//                preOrderItems.add(item);
//            }
//        }
//        return preOrderItems;
//    }

//    public void onTabSelected(TabLayout.Tab tab) {
//        updateTabData();
//    }

    public class GroupItemEntry {
        private Group group;
        private Map<String, Integer> entries;

        public GroupItemEntry(Group group, Map<String, Integer> entries) {
            this.group = group;
            this.entries = entries;
        }

        public Group getGroup() {
            return group;
        }

        public Map<String, Integer> getEntries() {
            return entries;
        }
    }

}