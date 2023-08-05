package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.CustomerWishlistItemsAdapter;
import com.csis4495_cmk.webuy.models.Wishlist;
import com.csis4495_cmk.webuy.viewmodels.CustomerWishlistViewModel;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class CustomerWishListItemsFragment extends Fragment {

    public StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");
    RecyclerView recyclerView;
    CustomerWishlistItemsAdapter customerWishlistItemsAdapter;
    List<Wishlist> wishlistDisplayList;
    CustomerWishlistViewModel wishListViewModel;
    TextView tvNoItems;

    public CustomerWishListItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_cart_items, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get data from viewModel
        wishListViewModel = new ViewModelProvider(requireActivity()).get(CustomerWishlistViewModel.class);

        tvNoItems = view.findViewById(R.id.tv_no_items);
        wishlistDisplayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        customerWishlistItemsAdapter = new CustomerWishlistItemsAdapter(getContext(), wishListViewModel, wishlistDisplayList);
        recyclerView.setAdapter(customerWishlistItemsAdapter);

        wishListViewModel.getWishlistObject().observe(getViewLifecycleOwner(), new Observer<ArrayList<Wishlist>>() {
            @Override
            public void onChanged(ArrayList<Wishlist> wishlists) {
                if (wishlists.size() != 0) {
                    wishlistDisplayList.clear();
                    wishlistDisplayList.addAll(wishlists);
                    customerWishlistItemsAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoItems.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoItems.setText("There is no items in the wishlist.");
                    tvNoItems.setVisibility(View.VISIBLE);
                }

            }
        });

    }
}