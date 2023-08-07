package com.csis4495_cmk.webuy.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerInventoryListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.Product;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.csis4495_cmk.webuy.viewmodels.SharedInventoryViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    private androidx.appcompat.widget.SearchView searchView;
    private TabLayout tabLayout;
    private RecyclerView mRecyclerView;
    private SellerInventoryListRecyclerAdapter adapter;
    private List<String> allCoverImgsList;
    private Map<String, List<Inventory>> inventoryMap;
    private Map<String, List<Inventory>> inStockItemsMap;
    private Map<String, List<Inventory>> preOrderItemsMap;
    private Map<String, Integer> groupTypeMap;
    private Map<String, String> allImagesMap;
    private String styleId, productId, groupId, sellerId, name, inventoryTitle;
    private int inStock, toSell, status, ordered, allocated, toAllocate, toOrder;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    SharedInventoryViewModel inventoryViewModel;
    final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
    final DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory");
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
        // ViewModel
        inventoryViewModel = new ViewModelProvider(requireActivity()).get(SharedInventoryViewModel.class);
        // SearchBar
        searchView = view.findViewById(R.id.search_product);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    search(s);
                    Log.d("Test search", "onQueryTextSubmit");
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    Log.d("Test search", "onQueryTextChange");
                    return false;
                }
            });
        }
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

        checkToAllocateStatusListener();
    }

    private void checkToAllocateStatusListener() {
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot != null){
                        Order order = dataSnapshot.getValue(Order.class);
                        // Filter current seller's products
                        String sellerId = order.getSellerId();
                        if (sellerId.equals(firebaseUser.getUid())){
                            String productStyleKey = "";
                            boolean isAllocated = false;
                            int orderAmount = 0;
                            Map<String, Map<String, Order.OrderItemInfo>> groupsAndItemsMap = order.getGroupsAndItemsMap();
                            for (Map.Entry<String, Map<String, Order.OrderItemInfo>> groupEntry : groupsAndItemsMap.entrySet()) {
                                Map<String, Order.OrderItemInfo> innerMap = groupEntry.getValue();
                                for (Map.Entry<String, Order.OrderItemInfo> entry : innerMap.entrySet()) {
                                    String orderProductId = entry.getKey();
                                    Order.OrderItemInfo orderItemInfo = entry.getValue();
                                    isAllocated = orderItemInfo.isAllocated();
                                    productStyleKey = orderProductId.split("p___")[1];
                                    orderAmount = orderItemInfo.getOrderAmount();
                                }
                                updateToAllocateData(productStyleKey, isAllocated, orderAmount);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void updateToAllocateData(String psId, boolean isAllocated, int orderAmount) {
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Fill the inventoryMap (this is for all products).
                for (DataSnapshot inventorySnapshot : snapshot.getChildren()) {
                    Inventory inventory = inventorySnapshot.getValue(Inventory.class);
                    Log.d("Test allocate number", " productStyleKey: "+ inventory.getProductStyleKey());
                    Log.d("Test allocate number", " Passed productStyleKey: "+ psId + "allocated: "+ isAllocated);
                    if (inventory.getProductStyleKey().equals(psId) && isAllocated == false){
                        int currentAllocateNum = inventory.getToAllocate();
                        int newToAllocate = currentAllocateNum + orderAmount;
                        inventoryRef.child(inventorySnapshot.getKey()).child("toAllocate").setValue(newToAllocate);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    private void search(String str) {
        Map<String, List<Inventory>> mInventoryMap = inventoryMap.entrySet()
                .stream()
                .filter(map -> {
                    for (Inventory inventory : map.getValue()) {
                        if (inventory.getInventoryName().toLowerCase().contains(str.toLowerCase()) ||
                            inventory.getInventoryTitle().toLowerCase().contains(str.toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));

        // Update inventoryMap
        UpdateRecyclerView(mInventoryMap);
        adapter.notifyDataSetChanged();
    }

    private void UpdateRecyclerView(Map<String, List<Inventory>> inventoryMap) {
        Log.d("Test UpdateRecyclerView", inventoryMap + "");
        adapter.setDisplayItemsList(inventoryMap, allCoverImgsList);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    private void setInventoryRecyclerViewList() {
        inventoryRef.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                inventoryMap.clear();
                inStockItemsMap.clear();
                preOrderItemsMap.clear();

                // Fill the inventoryMap (this is for all products).
                for (DataSnapshot inventorySnapshot : snapshot.getChildren()){
                    Inventory inventory = inventorySnapshot.getValue(Inventory.class);
                    String productId = inventory.getProductId();
                    String inventoryId = inventorySnapshot.getKey();
                    String sellerId = inventory.getSellerId();

                    Log.d("Test sellerId", "SellerId: "+ sellerId+ "currentUser: "+ auth.getCurrentUser().getUid());
                    if (sellerId.equals(auth.getCurrentUser().getUid())){
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
                }
                // Save it as a list
                List<List<Inventory>> inventoryLists = new ArrayList<>(inventoryMap.values());

                // Set data to parent recyclerView
                adapter.setDisplayItemsList(inventoryMap, allCoverImgsList);
                adapter.notifyDataSetChanged();
                mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                mRecyclerView.setAdapter(adapter);
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });
    }

    private void createNewInventory(int status, String groupId) {
        if(status == 1){
            // Set a key to check if this inventory exist
            String productStyleKey;

            if (styleId != null && !styleId.isEmpty()) {
                productStyleKey = productId + "___" + styleId;
            } else {
                productStyleKey = productId;
            }
            Log.d("Test create inventory", "data:"+ " sellerId "+sellerId+" productId "+productId+" groupId "+ this.groupId +" styleId "+styleId);
            Inventory inventory = new Inventory(sellerId, productId, groupId, styleId, toSell, inStock, name, productStyleKey, inventoryTitle);

            DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory");
            Query query = inventoryRef.orderByChild("productStyleKey").equalTo(inventory.getProductStyleKey());

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        // Generate a new child location using a unique key
                        String inventoryKey = inventoryRef.push().getKey();
                        Product product = new Product();
                        product.setInventoryId(inventoryKey);

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
                    //return false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Inventory", "Failed to check if inventory exists.", error.toException());
                }
            });
        }
    }

    private void findDataFromGroupInformation() {
        StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");
        reference = FirebaseDatabase.getInstance().getReference("Group");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<DownloadTaskWithId> tasks = new ArrayList<>();
                if (snapshot.exists()){
                    for (DataSnapshot groupSnapshot : snapshot.getChildren()){

                        Group group = groupSnapshot.getValue(Group.class);
                        // Check sellerId to only display a seller's groups
                        sellerId = group.getSellerId();
                        Log.d("Test create inventory", sellerId);
                        if(sellerId != null && sellerId.equals(auth.getCurrentUser().getUid())) {
                            Map<String, Integer> groupQtyMap = group.getGroupQtyMap();
                            Set<String> keys = groupQtyMap.keySet();

                            groupId = groupSnapshot.getKey();
                            for (String qty : keys) {
                                String[] parts = qty.split("___");
                                Log.d("TestKeySet", "type:" + parts[0] + " id:" + parts[1]);

                                // Get StyleId or ProductId
                                if (parts[0].equals("s")) {
                                    styleId = parts[1];
                                    productId = group.getProductId();  // Add this line to set productId
                                    toSell = groupQtyMap.get(qty);
                                    Log.d("TestKeySet", "styleId:" + styleId + " toSell:" + toSell);

                                    for (ProductStyle style : group.getGroupStyles()) {
                                        if (styleId.equals(style.getStyleId())) {
                                            name = style.getStyleName();
                                            break;
                                        }
                                    }
                                } else {
                                    styleId = null;
                                    productId = parts[1];
                                    toSell = groupQtyMap.get(qty);
                                    Log.d("TestKeySet", "productId:" + productId + " toSell:" + toSell);

                                    if (productId.equals(group.getProductId())) {
                                        name = group.getGroupName();
                                    }
                                }
                                inventoryTitle = group.getGroupName();
                                status = group.getStatus();
                                createNewInventory(status, groupId);
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
                } else {
                    Log.d("Test create inventory", "Group node does not exist!");
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
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Test firebase read", "Error reading data", error.toException());
            }
        });
    }

    private void addInStockToProductDb(String productId, int newInStock) {
        Log.d("Test product inStock","addInStockToProductDb()");
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productRef.child("inStock").setValue(newInStock).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getContext(),"InStock Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addInStockToStyleDb(String productId, String styleId, int newInStock) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId).child("productStyles");
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot styleSnapshot : dataSnapshot.getChildren()) {
                    int styleIndex = Integer.parseInt(styleSnapshot.getKey());
                    String matchingStyleId = styleSnapshot.child("styleId").getValue(String.class);

                    if (matchingStyleId.equals(styleId)) {
                        productRef.child(String.valueOf(styleIndex)).child("inStock").setValue(newInStock);
                    }
                }
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.d("Product", "Failed to read product styles.", databaseError.toException());
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
    public void onStockInButtonClicked(String inventoryId, int stockIn) {
        Log.d("Test stock", "onStockInButtonClicked()");
        String inventoryKey = inventoryId;
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory").child(inventoryKey);
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentInStock = snapshot.child("inStock").getValue(Integer.class);
                int newInStock = currentInStock + stockIn;
                Log.d("Test in stock", "passed amount:"+ stockIn+ " current in-stock:"+ currentInStock + " new in-stock:"+newInStock);
                inventoryRef.child("inStock").setValue(newInStock);

                // Update inventoryMap
                for (List<Inventory> list : inventoryMap.values()) {
                    for (Inventory inventory : list) {
                        if (inventory.getInventoryId().equals(inventoryId)) {
                            inventory.setInStock(newInStock);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });
    }

    @Override
    public void onStockOutButtonClicked(String inventoryId, int stockOut) {
        Log.d("Test stock", "onStockOutButtonClicked()");
        String inventoryKey = inventoryId;
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory").child(inventoryKey);
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentInStock = snapshot.child("inStock").getValue(Integer.class);
                int newInStock = currentInStock - stockOut;
                Log.d("Test in stock", "passed amount:"+ stockOut+ " current in-stock:"+ currentInStock + " new in-stock:"+newInStock);
                inventoryRef.child("inStock").setValue(newInStock);

                // Update inventoryMap
                for (List<Inventory> list : inventoryMap.values()) {
                    for (Inventory inventory : list) {
                        if (inventory.getInventoryId().equals(inventoryId)) {
                            inventory.setInStock(newInStock);
                            break;
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });

    }
    @Override
    public void onOpenProductPageButtonClick(String productId) {
        Log.d("Test stock", "onOpenProductPageButtonClick()");

        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Group");
        groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    String currentGroupId = groupSnapshot.getKey();
                    String matchingProductId = groupSnapshot.child("productId").getValue(String.class);

                    if (matchingProductId.equals(productId)) {
                        Bundle bundle = new Bundle();
                        bundle.putString("detail_groupId", currentGroupId);
                        SellerGroupDetailFragment sellerGroupDetailFragment = new SellerGroupDetailFragment();
                        sellerGroupDetailFragment.setArguments(bundle);

                        Navigation.findNavController(getView()).navigate(R.id.action_sellerInventoryFragment_to_sellerGroupDetailFragment, bundle);
                    }
                }
                //return false;
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Log.d("Product", "Failed to read product styles.", databaseError.toException());
            }
        });
    }

    @Override
    public void onOpenAllocateButtonClick(String groupId) {
        Log.d("Test stock", "onOpenAllocateButtonClick()");
        //use bundle to save selected productId
        Bundle bundle = new Bundle();
        bundle.putString("detail_groupId", groupId);
        Log.d("Test detail_groupId", groupId);
        //pass bundle to fragment
        navController.navigate(R.id.action_sellerInventoryFragment_to_sellerGroupDetailFragment, bundle);
    }

    @Override
    public void onOpenStoreRestoreButtonClick(int restoreAmount, String inventoryId) {
        String inventoryKey = inventoryId;
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory").child(inventoryKey);
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int currentInStock = snapshot.child("inStock").getValue(Integer.class);
                Log.d("Test restore", "passed restoreAmount:"+" current in-stock:"+ currentInStock);

                String productId = snapshot.child("productId").getValue(String.class);
                String styleId = snapshot.child("styleId").getValue(String.class);
                Log.d("Test in restore", "productId: "+ productId+ " current styleId: "+ styleId);
                if(styleId != null){
                    addInStockToStyleDb(productId, styleId, currentInStock);
                } else {
                    addInStockToProductDb(productId, currentInStock);
                }
                inventoryRef.child("inStock").setValue(0);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error here
            }
        });
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