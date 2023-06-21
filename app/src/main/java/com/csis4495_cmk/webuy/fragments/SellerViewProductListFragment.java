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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.SellerProductListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SellerViewProductListFragment extends Fragment implements SellerProductListRecyclerAdapter.OnAddProductButtonClickedListener {
    private NavController navController;
    private FloatingActionButton btnAddProduct;
    private RecyclerView mRecyclerView;
    private ArrayList<Product> productsArrayList;
    private ArrayList<String> productIds;
    private SellerProductListRecyclerAdapter adapter;
    private FirebaseStorage storage;
    private boolean addProductBtnClicked = false;
    private int position;

    DatabaseReference reference;

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

        // Processing recycler view
        mRecyclerView = view.findViewById(R.id.recyclerView_seller_product_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storage = FirebaseStorage.getInstance();
        productsArrayList = new ArrayList<>();
        productIds = new ArrayList<>();
        adapter = new SellerProductListRecyclerAdapter(getContext(), productsArrayList, this);

        mRecyclerView.setAdapter(adapter);
        showAllProductDetails();


        // Open add product page
        btnAddProduct = view.findViewById(R.id.fab_add_new_product);
        btnAddProduct.setOnClickListener(view1 ->
                Navigation.findNavController(view1).navigate(R.id.action_sellerProductListFragment_to_sellerAddProductFragment));

        // Call swipe helper
        OnRecyclerItemSwipeActionHelper();
    }

    private void showAllProductDetails() {
        reference = FirebaseDatabase.getInstance().getReference("Product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsArrayList.clear();
                productIds.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
                    String productId = productSnapshot.getKey();
                    productIds.add(productId);
                    productsArrayList.add(product);
                }
                adapter.setProducts(productsArrayList);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // do nothing
            }
        });
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
                    productId = productIds.get(position);
                    showConfirmToRemoveDialog(productId, position);

                    // Swiped item right: open edit page
                } else if (direction == ItemTouchHelper.RIGHT) {
                    productId = productIds.get(position);
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
                reference.child(productId).removeValue();
                adapter.removeItem(position);
                productIds.remove(position);
                adapter.notifyItemRemoved(position);
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
    public void onButtonClick(Boolean btnClicked) {
        addProductBtnClicked = true;
        navController.navigate(R.id.action_sellerProductListFragment_to_sellerAddGroupFragment);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyItemChanged(position);
    }
}