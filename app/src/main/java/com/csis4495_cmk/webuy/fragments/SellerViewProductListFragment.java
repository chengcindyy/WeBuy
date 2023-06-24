package com.csis4495_cmk.webuy.fragments;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Button;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.SellerProductListRecyclerAdapter;
import com.csis4495_cmk.webuy.dialog.CustomerOrderStatusDialog;
import com.csis4495_cmk.webuy.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SellerViewProductListFragment extends Fragment implements SellerProductListRecyclerAdapter.OnAddProductButtonClickedListener{
    private NavController navController;
    private FloatingActionButton btnAddProduct;
    RecyclerView mRecyclerView;
    ArrayList<Product> productsArrayList;
    ArrayList<String> productIds;
    SellerProductListRecyclerAdapter adapter;
    FirebaseStorage storage;
    boolean addProductBtnClicked = false;


    DatabaseReference reference;
    FirebaseDatabase db;

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
        adapter = new SellerProductListRecyclerAdapter(getContext(), productsArrayList,this);

        mRecyclerView.setAdapter(adapter);
        showAllProductDetails();


        // Open add product page
        btnAddProduct = view.findViewById(R.id.fab_add_new_product);
        btnAddProduct.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_sellerProductListFragment_to_sellerAddProductFragment));

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
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()) {
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
                int position = viewHolder.getLayoutPosition();

                // Swiped item left: remove item from database
                if(direction == ItemTouchHelper.LEFT){
                    String productId = productIds.get(position);
                    reference.child(productId).removeValue();
                    adapter.removeItem(position);
                    productIds.remove(position);
                    adapter.notifyItemRemoved(position);

                // Swiped item right: open edit page
                }else if (direction == ItemTouchHelper.RIGHT){
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position);
                    Navigation.findNavController(viewHolder.itemView).navigate(R.id.action_sellerProductListFragment_to_sellerEditProductDialog, bundle);

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

    @Override
    public void onButtonClick(Boolean btnClicked, int position) {
        addProductBtnClicked = true;
        String productId = productIds.get(position);
//        Toast.makeText(getContext(),productId,Toast.LENGTH_SHORT).show();

        //use bundel to save selected productId
        Bundle bundle  = new Bundle();
        bundle.putString("new_group_productId", productId);
        //pass bundle to fragment
        navController.navigate(R.id.action_sellerProductListFragment_to_sellerAddGroupFragment, bundle);
    }
}