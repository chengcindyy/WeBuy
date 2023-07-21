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
import com.csis4495_cmk.webuy.adapters.SellerInventoryListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SellerInventoryFragment extends Fragment implements SellerInventoryListRecyclerAdapter.OnButtonClickListener{

    private NavController navController;
    private SearchView searchBar;
    private TabLayout tabLayout;
    private RecyclerView mRecyclerView;
    private SellerInventoryListRecyclerAdapter adapter;
    private ArrayList<Inventory> allInventoryItemsList;
    private ArrayList<Inventory> inStockItemsList;
    private ArrayList<Inventory> preOrderItemsList;
    private ArrayList<String> allCoverImgsList;
    private Map<String, Integer> groupTypeMap;
    private String styleId, productId, styleName, productName, sellerId, name, inventoryTitle;
    private int qty, ordered, allocated, toAllocate, toOrder;
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
        // List
        allInventoryItemsList = new ArrayList<>();
        inStockItemsList = new ArrayList<>();
        preOrderItemsList = new ArrayList<>();
        allCoverImgsList = new ArrayList<>();
        groupTypeMap = new HashMap<>();
        // RecyclerView - inventory items
        mRecyclerView = view.findViewById(R.id.recyclerView_seller_inventory_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SellerInventoryListRecyclerAdapter(getContext(), this);
        findDataFromGroupInformation();
        setInventoryRecyclerViewList();
        // TabLayout
        tabLayout = view.findViewById(R.id.tabLayout_filter);
//        setTabLayout(tabLayout);

    }

    private void setInventoryRecyclerViewList() {
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory");
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allInventoryItemsList.clear();
                inStockItemsList.clear();
                preOrderItemsList.clear();

                HashMap<String, List<Inventory>> inventoryMap = new HashMap<>();

                // First, fill the inventoryMap.
                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    Inventory inventory = productSnapshot.getValue(Inventory.class);
                    String productId = inventory.getProductId();

                    if (!inventoryMap.containsKey(productId)) {
                        inventoryMap.put(productId, new ArrayList<>());
                    }
                    inventoryMap.get(productId).add(inventory);
                }
                Log.d("Test inventoryMap", inventoryMap.size()+"!");

                List<List<Inventory>> inventoryLists = new ArrayList<>(inventoryMap.values());
                Log.d("Test inventoryMap", inventoryLists+"!");

//                // Then, distribute the inventory lists based on the group type.
//                for (Map.Entry<String, List<Inventory>> entry : inventoryMap.entrySet()) {
//                    String productId = entry.getKey();
//                    List<Inventory> inventories = entry.getValue();
//
//                    if (groupTypeMap.containsKey(productId)) {
//                        int typeNum = groupTypeMap.get(productId);
//                        if (typeNum == 0) {
//                            inStockItemsList.addAll(inventories);
//                        } else {
//                            preOrderItemsList.addAll(inventories);
//                        }
//                    } else {
//                        // Handle the case where the productId is not in the groupTypeMap.
//                        // For example, you could log some information:
//                        Log.w("Inventory", "No group type information for productId: " + productId);
//                    }
//                }


//                Log.d("Test groupType", inStockItemsList+"!");
//                Log.d("Test groupType", preOrderItemsList+"!");

                adapter.setDisplayItemsList(inventoryLists);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });
    }


    private void createNewInventory() {
        // Set a key to check if this inventory exist
        String productStyleKey;
        if (styleId != null && !styleId.isEmpty()) {
            productStyleKey = productId + "_" + styleId;
        } else {
            productStyleKey = productId;
        }

        Inventory inventory = new Inventory(sellerId, productId, styleId, qty, name, productStyleKey, inventoryTitle);

        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory");
        Query query = inventoryRef.orderByChild("productStyleKey").equalTo(inventory.getProductStyleKey());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // Generate a new child location using a unique key
                    String inventoryKey = inventoryRef.push().getKey();
                    if (inventoryKey != null) {
                        inventoryRef.child(inventoryKey).setValue(inventory).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("Inventory", "Inventory created successfully.");
                                } else {
                                    Log.d("Inventory", "Failed to create inventory.", task.getException());
                                }
                            }
                        });
                    }
                } else {
                    Log.d("Inventory", "Inventory already exists, no need to create it.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Inventory", "Failed to check if inventory exists.", error.toException());
            }
        });
    }

    private void findDataFromGroupInformation() {
        groupTypeMap.clear();
        StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");
        reference = FirebaseDatabase.getInstance().getReference("Group");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allCoverImgsList.clear();
                List<Task<Uri>> tasks = new ArrayList<>();

                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    Group group = productSnapshot.getValue(Group.class);

                    // Check sellerId to only display a seller's groups
                    sellerId = group.getSellerId();
                    Log.d("TestSellerId", sellerId);
                    if(sellerId != null && sellerId.equals(auth.getCurrentUser().getUid())){
                        Map<String, Integer> groupQtyMap = group.getGroupQtyMap();
                        Set<String> keys = groupQtyMap.keySet();

                        for (String key : keys){
                            String[] parts = key.split("___");
                            Log.d("TestKeySet", "type:"+parts[0]+" id:"+parts[1]);

                            // Get StyleId or ProductId
                            if (parts[0].equals("s")){
                                styleId = parts[1];
                                productId = group.getProductId();  // Add this line to set productId
                                qty = groupQtyMap.get(key);
                                Log.d("TestKeySet", "styleId:"+styleId+" qty:"+qty);

                                for (ProductStyle style : group.getGroupStyles()) {
                                    if (styleId.equals(style.getStyleId())){
                                        name = style.getStyleName();
                                        break;
                                    }
                                }
                            } else {
                                styleId = null;
                                productId = parts[1];
                                qty = groupQtyMap.get(key);
                                Log.d("TestKeySet", "productId:"+productId+" qty:"+qty);

                                if (productId.equals(group.getProductId())){
                                    name = group.getGroupName();
                                }
                            }
                            inventoryTitle = group.getGroupName();

                            createNewInventory();
                        }

                        // Check group status for filter
                        groupTypeMap.put(productId, group.getGroupType());
                        Log.d("Test group type", String.valueOf(groupTypeMap.entrySet()));

                        //get coverImgUrl
                        String coverImgName = group.getGroupImages().get(0);
                        Log.d("Test coverImgName" ,coverImgName);
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
                                    Log.d("Test uri", allCoverImgsList+"!");
                                }
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



//    private void UpdateRecyclerView(ArrayList<Inventory> pList) {
//        Log.d("Do update, current product is: ", pList + "");
//        Log.d("Test coverImgs size: ", allCoverImgsList.size() + "");
//        for(int i = 0; i< pList.size(); i++) {
//            pList.get(i).setCoverImgUrl(allCoverImgsList.get(i));
//        }
//        adapter.setDisplayItemsList(pList);
//        adapter.notifyDataSetChanged();
//        mRecyclerView.setAdapter(adapter);
//    }

//    private void setTabLayout(TabLayout tabLayout) {
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//
//                switch (tabLayout.getSelectedTabPosition()) {
//                    case 1:
//                        adapter.setDisplayItemsList(inStockItemsList);
//                        break;
//                    case 2:
//                        adapter.setDisplayItemsList(preOrderItemsList);
//                        break;
//                    default:
//                        adapter.setDisplayItemsList(allInventoryItemsList);
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//    }


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