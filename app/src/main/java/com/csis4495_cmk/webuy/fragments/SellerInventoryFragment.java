package com.csis4495_cmk.webuy.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import com.csis4495_cmk.webuy.adapters.SellerInventoryInfoRecyclerViewAdapter;
import com.csis4495_cmk.webuy.adapters.SellerInventoryListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnCompleteListener;
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
import java.util.stream.Collectors;

public class SellerInventoryFragment extends Fragment implements SellerInventoryListRecyclerAdapter.OnButtonClickListener,
                                                                 SellerInventoryStockManagementFragment.onStockButtonClickListener{

    private NavController navController;
    private SearchView searchBar;
    private TabLayout tabLayout;
    private RecyclerView mRecyclerView;
    private SellerInventoryListRecyclerAdapter adapter;
    private List<String> allCoverImgsList;
    private HashMap<String, List<Inventory>> inventoryMap;
    private HashMap<String, List<Inventory>> inStockItemsMap;
    private HashMap<String, List<Inventory>> preOrderItemsMap;
    private Map<String, Integer> groupTypeMap;
    private Map<String, String> allImagesMap;
    private String styleId, productId, sellerId, name, inventoryTitle;
    private int qty, ordered, allocated, toAllocate, toOrder;
    private SellerInventoryStockManagementFragment.onStockButtonClickListener stockListener;
    private SellerInventoryInfoRecyclerViewAdapter infoFragment;
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
        allCoverImgsList = new ArrayList<>();
        groupTypeMap = new HashMap<>();
        allImagesMap = new HashMap<>();
        inventoryMap = new HashMap<>();
        inStockItemsMap = new HashMap<>();
        preOrderItemsMap = new HashMap<>();
        // RecyclerView - inventory items
        mRecyclerView = view.findViewById(R.id.recyclerView_seller_inventory_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SellerInventoryListRecyclerAdapter(getContext(), this, this);
        findDataFromGroupInformation();
        // TabLayout
        tabLayout = view.findViewById(R.id.tabLayout_filter);
        setTabLayout(tabLayout);
    }

    private void setInventoryRecyclerViewList() {
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory");
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryMap.clear();
                inStockItemsMap.clear();
                preOrderItemsMap.clear();

                // Fill the inventoryMap (this is for all products).
                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    Inventory inventory = productSnapshot.getValue(Inventory.class);
                    String productId = inventory.getProductId();
                    String inventoryId = productSnapshot.getKey();

                    inventory.setInventoryId(inventoryId);

                    if (!inventoryMap.containsKey(productId)) {
                        inventoryMap.put(productId, new ArrayList<>());
                    }
                    inventoryMap.get(productId).add(inventory);

                    // Matching images.
                    if (allImagesMap.containsKey(productId)){
                        String imageUrl = allImagesMap.get(productId);
                        inventory.setImageUrl(imageUrl);
                    }

                    // Fill the inventoryMap (this is for inStock & preOrder).
                    if (groupTypeMap.containsKey(productId)){
                        int typeNum = groupTypeMap.get(productId);

                        if (typeNum == 0) {
                            if (!inStockItemsMap.containsKey(productId)) {
                                inStockItemsMap.put(productId, new ArrayList<>());
                            }
                            inStockItemsMap.get(productId).add(inventory);
                            Log.d("Test group type", "inStockItemsMap key:"+inStockItemsMap.keySet()+"value:"+inStockItemsMap.values());
                        } else {
                            if (!preOrderItemsMap.containsKey(productId)) {
                                preOrderItemsMap.put(productId, new ArrayList<>());
                            }
                            preOrderItemsMap.get(productId).add(inventory);
                            Log.d("Test group type", "preOrderItemsMap key:"+preOrderItemsMap.keySet()+"value:"+preOrderItemsMap.values());
                        }
                    }
                }
                // Save it as a list
                List<List<Inventory>> inventoryLists = new ArrayList<>(inventoryMap.values());

                // Set data to parent recyclerView
                adapter.setDisplayItemsList(inventoryMap, allCoverImgsList);
                adapter.notifyDataSetChanged();
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(adapter);
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
                List<List<String>> allCoverImgUrls = new ArrayList<>();
                List<DownloadTaskWithId> tasks = new ArrayList<>();

                for (DataSnapshot productSnapshot : snapshot.getChildren()){
                    Group group = productSnapshot.getValue(Group.class);

                    // Check sellerId to only display a seller's groups
                    sellerId = group.getSellerId();
                    Log.d("TestSellerId", sellerId);
                    if(sellerId != null && sellerId.equals(auth.getCurrentUser().getUid())) {
                        Map<String, Integer> groupQtyMap = group.getGroupQtyMap();
                        Set<String> keys = groupQtyMap.keySet();

                        for (String key : keys) {
                            String[] parts = key.split("___");
                            Log.d("TestKeySet", "type:" + parts[0] + " id:" + parts[1]);

                            // Get StyleId or ProductId
                            if (parts[0].equals("s")) {
                                styleId = parts[1];
                                productId = group.getProductId();  // Add this line to set productId
                                qty = groupQtyMap.get(key);
                                Log.d("TestKeySet", "styleId:" + styleId + " qty:" + qty);

                                for (ProductStyle style : group.getGroupStyles()) {
                                    if (styleId.equals(style.getStyleId())) {
                                        name = style.getStyleName();
                                        break;
                                    }
                                }
                            } else {
                                styleId = null;
                                productId = parts[1];
                                qty = groupQtyMap.get(key);
                                Log.d("TestKeySet", "productId:" + productId + " qty:" + qty);

                                if (productId.equals(group.getProductId())) {
                                    name = group.getGroupName();
                                }
                            }
                            inventoryTitle = group.getGroupName();

                            createNewInventory();
                        }

                        // Check group status for filter
                        groupTypeMap.put(productId, group.getGroupType());

                        //get coverImgUrl
                        final String[] imgUri = {""};
                        String coverImgName = group.getGroupImages().get(0);
                        Log.d("Test coverImgName", coverImgName);
                        Log.d("Test StoragePath", imgRef.child(productId).child(coverImgName).getPath());
                        Log.d("Test StorageGetUrl", "pId: " + productId + ", Name: " + coverImgName);
                        tasks.add(new DownloadTaskWithId(imgRef.child(productId).child(coverImgName).getDownloadUrl(), productId));
                    }
                }

                Tasks.whenAllSuccess(tasks.stream().map(t -> t.task).collect(Collectors.toList())).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> objects) {
                        for (int i = 0; i < objects.size(); i++) {
                            Object object = objects.get(i);
                            Uri uri = (Uri) object;
                            if (uri != null) {
                                String productId = tasks.get(i).productId;
                                allImagesMap.put(productId, uri.toString());
                            }
                            Log.d("Test uri", allImagesMap + "!");
                            setInventoryRecyclerViewList();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Test firebase read", "Error reading data", error.toException());
            }
        });
    }

    private void setTabLayout(TabLayout tabLayout) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch (tabLayout.getSelectedTabPosition()) {
                    case 1:
                        adapter.setDisplayItemsList(inStockItemsMap, allCoverImgsList);
                        break;
                    case 2:
                        adapter.setDisplayItemsList(preOrderItemsMap, allCoverImgsList);
                        break;
                    default:
                        adapter.setDisplayItemsList(inventoryMap, allCoverImgsList);
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


    @Override
    public void onOpenProductPageButtonClick( int position) {
        Log.d("Test stock", "onOpenProductPageButtonClick()");
    }

    @Override
    public void onOpenAllocateButtonClick(int position) {
        Log.d("Test stock", "onOpenAllocateButtonClick()");
    }

    @Override
    public void onStockInButtonClicked(int stockIn) {
        Log.d("Test stock", "onStockInButtonClicked()");

//        SellerInventoryStockManagementFragment inventoryFragment = SellerInventoryStockManagementFragment.newInstance("inAmount", "outAmount");
//        // Get the fragmentManager
//        FragmentManager fragmentManager = getParentFragmentManager();
//        // Show the inventoryFragment
//        inventoryFragment.show(fragmentManager, "Inventory Management Frag show");
//        // set interface listener(inventoryFragment -> currentFragment)
//        inventoryFragment.setSellerButtonClickListener(new SellerInventoryStockManagementFragment.onStockButtonClickListener() {
//            @Override
//            public void onStockInButtonClicked(int stockIn) {
//                int inAmount = stockIn;
//
//                DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory");
//                inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot productSnapshot : snapshot.getChildren()){
//                            Inventory inventory = productSnapshot.getValue(Inventory.class);
//
//                            int newInStock = inventory.getInStock() + inAmount;
//                            Log.d("Test in stock", "passed amount:"+ inAmount);
//                            Log.d("Test in stock", "current in-stock:"+inventory.getInStock());
//                            Log.d("Test in stock", "new in-stock:"+newInStock);
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        // Handle error here
//                    }
//                });
//            }
//
//            @Override
//            public void onStockOutButtonClicked(int stockOut) {
//                // 做一些處理，例如更新界面或數據
//            }
//        });
    }

    @Override
    public void onStockOutButtonClicked(int stockOut) {
        Log.d("Test stock", "onStockOutButtonClicked()");
    }

    @Override
    public void onUpdateCompleted() {

    }

    @Override
    public void onError(Exception e) {

    }

    class DownloadTaskWithId {
        public Task<Uri> task;
        public String productId;

        public DownloadTaskWithId(Task<Uri> task, String productId) {
            this.task = task;
            this.productId = productId;
        }
    }




}