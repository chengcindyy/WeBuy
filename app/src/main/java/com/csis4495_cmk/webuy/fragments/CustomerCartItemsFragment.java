package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.CustomerCartItemsAdapter;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.models.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomerCartItemsFragment extends Fragment {

    RecyclerView recyclerView;
    CustomerCartItemsAdapter customerCartItemsAdapter;
    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerId);

    public CustomerCartItemsFragment() {
        // Required empty public constructor
    }

    public static CustomerCartItemsFragment newInstance(int position) {
        CustomerCartItemsFragment fragment = new CustomerCartItemsFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            tabSelection = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_cart_items, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rv_cust_seller_with_cart_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        Set<String> sellerIds = new HashSet<>();
//        ArrayList<CartItem> cartItems = new ArrayList<>();
        Map<String, ArrayList<CartItem>> sellerItemsMap = new HashMap<>();
        customerRef.child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sellerItemsMap.clear();
                //all cartItems
                for (DataSnapshot cartItemSnapshot : snapshot.getChildren()) {

                    CartItem cartItem = cartItemSnapshot.getValue(CartItem.class);

                    String sellerId = cartItem.getSellerId();

                    ArrayList<CartItem> sellerItems;
                    if (!sellerItemsMap.containsKey(sellerId)) {
                        sellerItems = new ArrayList<>();
                    } else { //existed key
                        sellerItems = sellerItemsMap.get(sellerId);
                    }
                    sellerItems.add(cartItem);
                    sellerItemsMap.put(sellerId, sellerItems);
                }

                Log.d("TestSet", sellerItemsMap.size()+" maps");
                Log.d("TestSet", sellerItemsMap.get("XBJefGNuxLMUDLuc2qMMabNGuLA3").size()+" items");

                // get sellerId , name , pic
                customerCartItemsAdapter = new CustomerCartItemsAdapter(getContext(), sellerItemsMap);
                recyclerView.setAdapter(customerCartItemsAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

}