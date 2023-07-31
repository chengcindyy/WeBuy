package com.csis4495_cmk.webuy.fragments;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.CustomerWishlistItemsAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Wishlist;
import com.csis4495_cmk.webuy.viewmodels.CustomerWishlistViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;


public class CustomerAddToWishListItemsFragment extends Fragment {

    public StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");
    RecyclerView recyclerView;
    CustomerWishlistItemsAdapter customerWishlistItemsAdapter;
    List<Wishlist> wishlistDisplayList;
    CustomerWishlistViewModel wishListViewModel;

    public CustomerAddToWishListItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wish_list_items, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wishlistDisplayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView_wishlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        customerWishlistItemsAdapter = new CustomerWishlistItemsAdapter(getContext(), wishlistDisplayList);
        recyclerView.setAdapter(customerWishlistItemsAdapter);

        // Get data from viewModel
        wishListViewModel = new ViewModelProvider(requireActivity()).get(CustomerWishlistViewModel.class);
        wishListViewModel.getWishlistObject().observe(getViewLifecycleOwner(), new Observer<ArrayList<Wishlist>>() {
            @Override
            public void onChanged(ArrayList<Wishlist> wishlists) {
                wishlistDisplayList.clear();
                wishlistDisplayList.addAll(wishlists);
                customerWishlistItemsAdapter.notifyDataSetChanged();
            }
        });

    }
}