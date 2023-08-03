package com.csis4495_cmk.webuy.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csis4495_cmk.webuy.R;
import com.google.firebase.auth.FirebaseAuth;


public class SellerHomeFragment extends Fragment {

    private CardView btnProducts, btnPostings, btnInventory, btnStoreMgmt, btnSupport, btnOrderList, btnLogout, btnTestPage;
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
        setOpenNewFragmentOnClickListener(btnProducts,R.id.action_sellerHomeFragment_to_sellerProductListFragment);

        btnPostings = view.findViewById(R.id.btn_seller_postings);
        btnPostings.setOnClickListener(v -> navController.navigate(R.id.action_sellerHomeFragment_to_sellerGroupList2));

        btnInventory = view.findViewById(R.id.btn_seller_inventory);
        setOpenNewFragmentOnClickListener(btnInventory,R.id.action_sellerHomeFragment_to_sellerInventoryFragment);

        btnOrderList = view.findViewById(R.id.btn_seller_order_list);
        // TODO: Add order list fragment

        // TODO: Add Support page
        btnSupport = view.findViewById(R.id.btn_seller_support);

//        btnLogout = view.findViewById(R.id.btn_seller_logout);
//        btnLogout.setOnClickListener(view1 -> {
//            auth.signOut();
//            LoginManager.getInstance().logOut();
//            Toast.makeText(requireActivity(),"Logged Out", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(requireActivity(), MainActivity.class);
//            startActivity(intent);
//            requireActivity().finish();
//        });
    }

    private void setOpenNewFragmentOnClickListener(CardView btnTestPage, int action) {
        btnTestPage.setOnClickListener(view -> navController.navigate(action));
    }
}