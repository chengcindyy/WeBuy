package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

public class SellerViewProductListFragment extends Fragment implements SellerProductListRecyclerAdapter.OnAddProductButtonClickedListener {
    private NavController navController;
    private FloatingActionButton btnAddProduct;
    RecyclerView mRecyclerView;
    ArrayList<Product> productsArrayList;
    SellerProductListRecyclerAdapter adapter;
    FirebaseStorage storage;
    boolean AddProductBtnClicked = false;


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
        adapter = new SellerProductListRecyclerAdapter(getContext(), productsArrayList,this);

        mRecyclerView.setAdapter(adapter);
        showAllProductDetails();


        // Open add product page
        btnAddProduct = view.findViewById(R.id.fab_add_new_product);
        btnAddProduct.setOnClickListener(view1 -> Navigation.findNavController(view1).navigate(R.id.action_sellerProductListFragment_to_sellerAddProductFragment));
    }

    private void showAllProductDetails() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Product");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productsArrayList.clear();
                for (DataSnapshot productSnapshot: dataSnapshot.getChildren()) {
                    Product product = productSnapshot.getValue(Product.class);
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

    @Override
    public void onButtonClick(Boolean btnClicked) {
        AddProductBtnClicked = true;
        navController.navigate(R.id.action_sellerProductListFragment_to_sellerAddGroupFragment);
    }


}