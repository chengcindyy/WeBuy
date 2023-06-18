package com.csis4495_cmk.webuy;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;


public class SellerHomeFragment extends Fragment {

    private Button btnProducts, btnPostings, btnInventory, btnStoreMgmt, btnSupport, btnProfile, btnLogout, btnTestPage;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_home, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(SellerHomeFragment.this);

        btnProducts = view.findViewById(R.id.btn_seller_products);
        btnProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_sellerHomeFragment_to_sellerAddProductFragment);
            }
        });

        btnPostings = view.findViewById(R.id.btn_seller_postings);
        btnInventory = view.findViewById(R.id.btn_seller_inventory);
        btnStoreMgmt = view.findViewById(R.id.btn_seller_store_mgmt);
        btnSupport = view.findViewById(R.id.btn_seller_support);
        btnProfile = view.findViewById(R.id.btn_seller_profile);
        btnLogout = view.findViewById(R.id.btn_seller_logout);
        btnTestPage = view.findViewById(R.id.btn_open_test_page);


    }


}