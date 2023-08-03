package com.csis4495_cmk.webuy.fragments;

import static androidx.navigation.Navigation.findNavController;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerProductListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class SellerProductListFragment extends Fragment implements SellerProductListRecyclerAdapter.OnAddProductButtonClickedListener {
    private NavController navController;
    private RecyclerView mRecyclerView;
    private SellerProductListRecyclerAdapter adapter;
    private int position;
    private ArrayList<Product> allProductsList;
    private ArrayList<String> allProductIds;
    private Map<String, Product> idsToProductsMap;
    private ArrayList<String> allCoverImgsList;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference;
    final StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");

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
        navController = NavHostFragment.findNavController(SellerProductListFragment.this);

        allProductsList = new ArrayList<>();
        allProductIds = new ArrayList<>();
        idsToProductsMap = new HashMap<>();
        allCoverImgsList = new ArrayList<>();

        // Set recycler view
        mRecyclerView = view.findViewById(R.id.recyclerView_seller_product_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SellerProductListRecyclerAdapter(getContext(), this);

        // Update recycler view content
        setAllProductDetails();

        // Call swipe helper
        OnRecyclerItemSwipeActionHelper();

        // ButtonAppBar and search
        BottomAppBar bottomAppBar = view.findViewById(R.id.bottomAppBar);
        Menu menu = bottomAppBar.getMenu();
        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView =
                (androidx.appcompat.widget.SearchView) searchItem.getActionView();
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

        // Fab navigation
        FloatingActionButton floatingActionButton = view.findViewById(R.id.seller_fab);
        floatingActionButton.setOnClickListener(v -> {
            // Open add product page
            navController.navigate(R.id.action_sellerProductListFragment_to_sellerAddProductFragment);
        });
    }

    private void setAllProductDetails() {
        reference = FirebaseDatabase.getInstance().getReference("Product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allProductsList.clear();
                allProductIds.clear();
                allCoverImgsList.clear();
                List<Task<Uri>> tasks = new ArrayList<>();

                // Get all products
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    String sellerId = product.getSellerId();
                    String currentUser = auth.getCurrentUser().getUid();

                    // But only filter one seller's products
                    if(sellerId.equals(currentUser)){
                        String productId = productSnapshot.getKey();
                        allProductsList.add(product);
                        allProductIds.add(productId);
                        idsToProductsMap.put(productId, product);
                        // Pass this productId to adapter for later use
                        adapter.setProductId(productId);

                        //get coverImgUrl
                        String coverImgName = product.getProductImages().get(0);
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
                                UpdateRecyclerView(allProductsList);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure for any of the tasks
                                Log.e("Test StorageGetUrl", "Download Url Failed");
                                allCoverImgsList.add(null);
                            }
                        });

                        Log.d("Current product is: ", allProductsList + "!");
                        Log.d("Current product id is: ", allProductIds + "!");
                        Log.d("Now map content are: ", idsToProductsMap + "!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing
            }
        });
    }

    private void UpdateRecyclerView(ArrayList<Product> pList) {
        Log.d("Do update, current product is: ", pList + "");
        Log.d("Test coverImgs size: ", allCoverImgsList.size() + "");
        for(int i = 0; i< pList.size(); i++) {
            pList.get(i).setCoverImgUrl(allCoverImgsList.get(i));
        }
        adapter.setProducts(pList);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
                    findNavController(viewHolder.itemView).navigate(R.id.action_sellerProductListFragment_to_sellerAddProductFragment, bundle);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onButtonClick(Boolean btnClicked, int position) {

        //use bundle to save selected productId
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
    }
}