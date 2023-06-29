package com.csis4495_cmk.webuy.fragments;


import android.content.Intent;
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
import android.widget.Toast;

import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.R;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;


public class SellerHomeFragment extends Fragment {

    private Button btnProducts, btnPostings, btnInventory, btnStoreMgmt, btnSupport, btnProfile, btnLogout, btnTestPage;
    FirebaseAuth auth = FirebaseAuth.getInstance();
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
                navController.navigate(R.id.action_sellerHomeFragment_to_sellerProductListFragment);
            }
        });

        // TODO: Add Posting page
        btnPostings = view.findViewById(R.id.btn_seller_postings);
        // TODO: Add Inventory page
        btnInventory = view.findViewById(R.id.btn_seller_inventory);
        // TODO: Add Store management page
        btnStoreMgmt = view.findViewById(R.id.btn_seller_store_mgmt);
        // TODO: Add Support page
        btnSupport = view.findViewById(R.id.btn_seller_support);

        btnLogout = view.findViewById(R.id.btn_seller_logout);
        btnLogout.setOnClickListener(view1 -> {
            auth.signOut();
            LoginManager.getInstance().logOut();
            Toast.makeText(requireActivity(),"Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        btnTestPage = view.findViewById(R.id.btn_open_test_page);
        btnTestPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_sellerHomeFragment_to_testFragment);
            }
        });


    }


}