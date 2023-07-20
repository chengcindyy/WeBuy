package com.csis4495_cmk.webuy.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.InventoryRecyclerViewAdapter;
import com.csis4495_cmk.webuy.adapters.SellerInventoryListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SellerInventoryFragment extends Fragment implements SellerInventoryListRecyclerAdapter.OnButtonClickListener{

    private NavController navController;
    private SearchView searchBar;
    private TabLayout tabLayout;
    private RecyclerView mRecyclerView;
    private SellerInventoryListRecyclerAdapter adapter;
    private ArrayList<GroupItemEntry> allItemsList;
    private ArrayList<GroupItemEntry> inStockItemsList;
    private ArrayList<GroupItemEntry> preOrderItemsList;
    private ArrayList<String> allCoverImgsList;
    private Map<String, Integer> qtyMap;
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
        navController = NavHostFragment.findNavController(SellerInventoryFragment.this);
        // SearchBar
        searchBar = view.findViewById(R.id.search_product);
        // TabLayout
        tabLayout = view.findViewById(R.id.tabLayout_filter);
        // List
        allItemsList = new ArrayList<>();
        inStockItemsList = new ArrayList<>();
        preOrderItemsList = new ArrayList<>();
        allCoverImgsList = new ArrayList<>();
        // RecyclerView - inventory items
        mRecyclerView = view.findViewById(R.id.recyclerView_seller_inventory_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SellerInventoryListRecyclerAdapter(getContext(), this);
        findDataFromGroupInformation();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tabLayout.getSelectedTabPosition()) {
                    case 1:
                        adapter.setDisplayItemsList(inStockItemsList);
                        break;
                    case 2:
                        adapter.setDisplayItemsList(preOrderItemsList);
                        break;
                    default:
                        adapter.setDisplayItemsList(allItemsList);
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

    private void findDataFromGroupInformation() {
        StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");
        reference = FirebaseDatabase.getInstance().getReference("Group");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allItemsList.clear();
                inStockItemsList.clear();
                preOrderItemsList.clear();
                allCoverImgsList.clear();
                List<Task<Uri>> tasks = new ArrayList<>();

                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    Group group = productSnapshot.getValue(Group.class);
                    String sellerId = group.getSellerId();
                    String productId = group.getProductId();

                    // Check sellerId to only display a seller's groups
                    if(sellerId != null && sellerId.equals(auth.getCurrentUser().getUid())){
                        GroupItemEntry groupEntry = new GroupItemEntry(group, group.getGroupQtyMap());

                        allItemsList.add(groupEntry);
                        adapter.setDisplayItemsList(allItemsList);

                        // Set tabLayout categories
                        if (group.getGroupType() == 0) {
                            inStockItemsList.add(groupEntry);
                        } else {
                            preOrderItemsList.add(groupEntry);
                        }

                        //get coverImgUrl
                        String coverImgName = group.getGroupImages().get(0);
                        Log.d("Test StoragePath" ,imgRef.child(productId).child(coverImgName).getPath());
                        Log.d("Test StorageGetUrl", "pId: "+ productId + ", Name: " +coverImgName);
                        tasks.add(imgRef.child(productId).child(coverImgName).getDownloadUrl());

                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                            @Override
                            public void onSuccess(List<Object> objects) {
                                // All tasks are successful, and each object corresponds to a download URL
                                allCoverImgsList.clear();
                                for (Object object : objects) {
                                    Uri uri = (Uri) object;

                                    allCoverImgsList.add(uri.toString());
                                }

                                UpdateRecyclerView(allItemsList);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure for any of the tasks
                                Log.e("Test StorageGetUrl", "Download Url Failed");
                                allCoverImgsList.add(null);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Test firebase read", "Error reading data", error.toException());
            }
        });
    }

    private void UpdateRecyclerView(ArrayList<GroupItemEntry> pList) {
        Log.d("Do update, current product is: ", pList + "");
        Log.d("Test coverImgs size: ", allCoverImgsList.size() + "");
        for(int i = 0; i< pList.size(); i++) {
            pList.get(i).setCoverImgUrl(allCoverImgsList.get(i));
        }
        adapter.setDisplayItemsList(pList);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }


    @Override
    public void onOpenProductPageButtonClick( int position) {
        Log.d("Test stock", "onOpenProductPageButtonClick()");
    }

    @Override
    public void onStockInClick(int position) {
        Log.d("Test stock", "onStockInClick()");
    }

    @Override
    public void onStockOutClick(int position) {
        Log.d("Test stock", "onStockOutClick()");
    }

    @Override
    public void onOpenAllocateButtonClick(int position) {
        Log.d("Test stock", "onAllocateClick()");
    }

    public class GroupItemEntry {
        private Group group;
        private Map<String, Integer> entries;
        private String coverImgUrl;

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

        public void setCoverImgUrl(String coverImgUrl) {
            this.coverImgUrl = coverImgUrl;
        }

        public String getCoverImgUrl() {
            return coverImgUrl;
        }
    }

}