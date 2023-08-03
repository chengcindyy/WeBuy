package com.csis4495_cmk.webuy.fragments;

import static androidx.navigation.Navigation.findNavController;

import android.content.DialogInterface;
import android.graphics.Canvas;
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
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerAllProductAdapter;
import com.csis4495_cmk.webuy.models.Product;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SellerAllProductListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SellerAllProductListFragment extends Fragment {

    private NavController navController;
    private RecyclerView recyclerView;
    private BottomAppBar bottomAppBar;
    private List <Product> productList;
    private SellerAllProductAdapter sellerAllProductAdapter;
    private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    final DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SellerAllProductListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SellerAllProductListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SellerAllProductListFragment newInstance(String param1, String param2) {
        SellerAllProductListFragment fragment = new SellerAllProductListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

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

        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(SellerAllProductListFragment.this);

        // ButtonAppBar
        bottomAppBar = view.findViewById(R.id.seller_all_products_bottomAppBar);
        Menu menu = bottomAppBar.getMenu();
        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView =
                (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        // Do search
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String str) {
                search(str);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String str) {
                search(str);
                return false;
            }
        });
        // Fab navigation
        FloatingActionButton floatingActionButton = view.findViewById(R.id.seller_fab);
        floatingActionButton.setOnClickListener(v -> {
            // Open add product page
            navController.navigate(R.id.action_sellerAllProductListFragment2_to_sellerAddProductFragment);
        });
        // List
        productList = new LinkedList<>();
        // Set recyclerView
        recyclerView = view.findViewById(R.id.recyclerView_seller_product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Update recycler view content
        setAllProductsDetails();
        // Call swipe helper
        OnRecyclerItemSwipeActionHelper();
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
                int position = viewHolder.getLayoutPosition();

                // Swiped item left: remove item from database
                if (direction == ItemTouchHelper.LEFT) {
                    productId = productList.get(position).getKey();
                    showConfirmToRemoveDialog(productId, position);
                    Log.d("Test swipe", "currently swipe left: " + productList.get(position).getProductName());
                    // Swiped item right: open edit page
                } else if (direction == ItemTouchHelper.RIGHT) {
                    productId = productList.get(position).getKey();
                    Log.d("Test swipe", "currently swipe right: " + productList.get(position).getProductName());
                    Bundle bundle = new Bundle();
                    bundle.putString("productId", productId);
                    SellerAddProductFragment sellerAddProductFragment = new SellerAddProductFragment();
                    sellerAddProductFragment.setArguments(bundle);
                    findNavController(viewHolder.itemView).navigate(R.id.action_sellerAllProductListFragment2_to_sellerAddProductFragment, bundle);
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
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void showConfirmToRemoveDialog(String productId, int position) {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove this product");
        builder.setMessage("Are you sure you want to remove this product?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                productList.remove(position);
                sellerAllProductAdapter.notifyItemRemoved(position);
                productRef.child(productId).removeValue();

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
                sellerAllProductAdapter.notifyItemChanged(position);
            }
        });
        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }

    private void setAllProductsDetails() {
        productRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Once a new product has been added to Product module, first clean the list
                productList.clear();

                // Start to get all products
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Product product = dataSnapshot.getValue(Product.class);
                    // In order to matching user and product's poster, needs to get sellerId and currentUser
                    String currentUserId = currentUser.getUid();
                    String sellerId = product.getSellerId();
                    // Matching ... so it will only show this seller's products
                    if (currentUserId.equals(sellerId)){
                        product.setKey(dataSnapshot.getKey());
                        // once finds the product, added this product to the product list, and update recyclerView
                        productList.add(product);
                        updateRecyclerView(productList);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void search(String str) {
        Log.d("do search", "search() clicked");
        List<Product> filteredProducts = productList.stream()
                .filter(product -> product.getProductName().toLowerCase().contains(str.toLowerCase())
                        || product.getCategory().toLowerCase().contains(str.toLowerCase()))
                .collect(Collectors.toList());
        updateRecyclerView(filteredProducts);
    }

    private void updateRecyclerView(List<Product> pList) {
        sellerAllProductAdapter = new SellerAllProductAdapter(getContext(), pList);
        recyclerView.setAdapter(sellerAllProductAdapter);
        sellerAllProductAdapter.notifyDataSetChanged();
    }
}