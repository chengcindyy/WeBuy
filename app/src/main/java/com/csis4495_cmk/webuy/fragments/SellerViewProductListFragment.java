package com.csis4495_cmk.webuy.fragments;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.SellerProductListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Product;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SellerViewProductListFragment extends Fragment implements SellerProductListRecyclerAdapter.OnAddProductButtonClickedListener {
    private NavController navController;
    private FloatingActionButton btnAddProduct;
    private RecyclerView mRecyclerView;
    private SellerProductListRecyclerAdapter adapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    private androidx.appcompat.widget.SearchView searchView;
    private boolean addProductBtnClicked = false;
    private int position;
    private ArrayList<Product> allProductsList;
    private ArrayList<String> allProductIds;
    private Map<String, Product> idsToProductsMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_all_product_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set navigation controller, how to navigate simply call -> controller.navigate(destination id)
        navController = NavHostFragment.findNavController(SellerViewProductListFragment.this);

        allProductsList = new ArrayList<>();
        allProductIds = new ArrayList<>();
        idsToProductsMap = new HashMap<>();

        // Set recycler view
        mRecyclerView = view.findViewById(R.id.recyclerView_seller_product_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SellerProductListRecyclerAdapter(getContext(), this);

        // Update recycler view content
        SetAllProductDetails();

        // Call swipe helper
        OnRecyclerItemSwipeActionHelper();

        // Search
        searchView = view.findViewById(R.id.sv_seller_product_list);
        if (searchView != null) {
            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    search(s);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return false;
                }
            });
        }

        // Open add product page
        btnAddProduct = view.findViewById(R.id.fab_add_new_product);
        btnAddProduct.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_sellerProductListFragment_to_sellerAddProductFragment));
    }




    private void SetAllProductDetails() {
        reference = FirebaseDatabase.getInstance().getReference("Product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProductsList.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    String sellerId = product.getSellerId();
                    String currentUser = auth.getCurrentUser().getUid();
//                    Log.d("Seller filter: ", sellerId);
//                    Log.d("Seller filter: ", currentUser);
                    if(sellerId.equals(currentUser)){
                        SetSellerProductDetails(sellerId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: handle if canceled
            }
        });
    }

    private void SetSellerProductDetails(String sellerId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
        Query query = reference.orderByChild("sellerId").equalTo(sellerId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProductsList.clear();
                allProductIds.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    String productId = productSnapshot.getKey();

                    allProductsList.add(product);
                    allProductIds.add(productId);
                    idsToProductsMap.put(productId, product);
                    adapter.setProductId(productId);
                    //adapter.setProducts(allProductsList);

                    Log.d("Current product is: ", allProductsList + "!");
                    Log.d("Current product id is: ", allProductIds + "!");
                    Log.d("Now map content are: ", idsToProductsMap + "!");
                }
                UpdateRecyclerView(allProductsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // TODO: handle if canceled
            }
        });
    }

    private void UpdateRecyclerView(ArrayList<Product> pList) {
        Log.d("Do update, current product is: ", pList + "!");
        adapter.setProducts(pList);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    private void search(String str) {
        Map<String, Product> mProductMap = idsToProductsMap.entrySet()
                .stream()
                .filter(map -> map.getValue().getProductName().toLowerCase().contains(str.toLowerCase()) ||
                        map.getValue().getCategory().toLowerCase().contains(str.toLowerCase())
                ).collect(Collectors.toMap(map -> map.getKey(), map -> map.getValue()));
        allProductsList = new ArrayList<>(mProductMap.values());
        UpdateRecyclerView(allProductsList);
    }

    private void OnRecyclerItemSwipeActionHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                String productId = "";
                position = viewHolder.getLayoutPosition();

                // Swiped item left: remove item from database
                if (direction == ItemTouchHelper.LEFT) {
                    productId = allProductIds.get(position);
                    showConfirmToRemoveDialog(productId, position);

                    // Swiped item right: open edit page
                } else if (direction == ItemTouchHelper.RIGHT) {
                    productId = allProductIds.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("productId", productId);
                    SellerAddProductFragment sellerAddProductFragment = new SellerAddProductFragment();
                    sellerAddProductFragment.setArguments(bundle);
                    Navigation.findNavController(viewHolder.itemView).navigate(R.id.action_sellerProductListFragment_to_sellerAddProductFragment, bundle);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete_red))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.android_green))
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void showConfirmToRemoveDialog(String productId, int position) {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove this product");
        builder.setMessage("Are you sure you want to remove this product?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                adapter.removeItem(position);
                allProductIds.remove(position);
                adapter.notifyItemRemoved(position);
                reference.child(productId).removeValue();

                //remove images from storage
                Executors.newSingleThreadExecutor().execute(() -> {
                    //remove images from storage
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference productImgsRef = storage.getReference().child("ProductImage/" + productId);

                    // List all files in the directory
                    ListResult listResult = null;
                    try {
                        listResult = Tasks.await(productImgsRef.listAll());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (listResult != null) {
                        List<StorageReference> items = listResult.getItems();

                        // Delete each file
                        for (StorageReference item : items) {
                            try {
                                Tasks.await(item.delete());
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                adapter.notifyItemChanged(position);
            }
        });
        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }

    @Override
    public void onButtonClick(Boolean btnClicked, int position) {
//        addProductBtnClicked = true;
//        navController.navigate(R.id.action_sellerProductListFragment_to_sellerAddGroupFragment);

        //use bundel to save selected productId
        String productId = allProductIds.get(position);
        Bundle bundle  = new Bundle();
        bundle.putString("new_group_productId", productId);
        //pass bundle to fragment
        navController.navigate(R.id.action_sellerProductListFragment_to_sellerAddGroupFragment, bundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyItemChanged(position);
        //adapter.notifyDataSetChanged();
    }
}