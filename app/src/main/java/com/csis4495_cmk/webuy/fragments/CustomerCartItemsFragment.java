package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.activities.CustomerHomePageActivity;
import com.csis4495_cmk.webuy.adapters.CartItemsViewModel;
import com.csis4495_cmk.webuy.adapters.CustomerCartItemsAdapter;
import com.csis4495_cmk.webuy.adapters.CustomerCartItemsWithSameSellerAdapter;
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

public class CustomerCartItemsFragment extends Fragment
                    implements CustomerCartItemsAdapter.onCartSellerBannerListener{

    RecyclerView recyclerView;
    TextView tvNoItems, tvCartItemsTotal, tvCartItemsCheckoutAmount;
    CheckBox cbxSelectAll;
    CustomerCartItemsAdapter customerCartItemsAdapter;
    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerId);

    CartItemsViewModel viewModel;

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
        viewModel = new ViewModelProvider(CustomerCartItemsFragment.this).get(CartItemsViewModel.class);
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

        tvNoItems = view.findViewById(R.id.tv_no_items);
        tvCartItemsTotal = view.findViewById(R.id.tv_cart_items_total);
        tvCartItemsCheckoutAmount = view.findViewById(R.id.tv_cart_items_checkout_amount);
        cbxSelectAll = view.findViewById(R.id.checkbox_select_all);
        recyclerView = view.findViewById(R.id.rv_cust_seller_with_cart_items);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Map<String, ArrayList<CartItem>> sellerItemsMap = new HashMap<>();
        Map<String, Boolean> sellerAllItemsCheckedMap = new HashMap<>();
        Map<CartItem, CartItemsViewModel.CartItemInfo> cartItemsInfoMap = new HashMap<>();
        customerRef.child("Cart").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sellerItemsMap.clear();
                cartItemsInfoMap.clear();

                for (DataSnapshot sellerSnapshot: snapshot.getChildren()) {
                    String sellerId = sellerSnapshot.getKey();
                    ArrayList<CartItem> sellerItems = new ArrayList<>();
                    for (DataSnapshot cartItemSnapshot: sellerSnapshot.getChildren()) {
                        CartItem cartItem = cartItemSnapshot.getValue(CartItem.class);
                        sellerItems.add(cartItem);
                        cartItemsInfoMap.put(cartItem, null);
                    }
                    sellerItemsMap.put(sellerId, sellerItems);
                }

                viewModel.setCartItemsInfoMap(cartItemsInfoMap);

                Log.d("TestSet", sellerItemsMap.size()+" maps");
                Log.d("TestInfoMap", cartItemsInfoMap.size() +" maps");

                // get sellerId , name , pic
                if (sellerItemsMap.size() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoItems.setVisibility(View.GONE);

                    //new ViewModel
                    //viewModel = new ViewModelProvider(CustomerCartItemsFragment.this).get(CartItemsViewModel.class);
                    viewModel.setSellerItemsMap(sellerItemsMap);

                    sellerAllItemsCheckedMap.clear();

                    for (String sellerId : sellerItemsMap.keySet()) {
                        sellerAllItemsCheckedMap.put(sellerId, false);
                    }
                    viewModel.setSellerAllItemsCheckedMap(sellerAllItemsCheckedMap);
                    customerCartItemsAdapter = new CustomerCartItemsAdapter(getContext(), viewModel, getViewLifecycleOwner());

                    recyclerView.setAdapter(customerCartItemsAdapter);
                    customerCartItemsAdapter.setOnCartSellerBannerListener(CustomerCartItemsFragment.this);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set cbx initial status
        boolean allGroupChecked = false;
        Log.d("TestSelectAll", sellerAllItemsCheckedMap.size()+"");
        for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
            boolean allChecked = sellerAllItemsCheckedMap.get(sellerId);
            Log.d("TestSelectAll", allChecked + "!");
            if (!allChecked) {
                allGroupChecked = false;
                break;
            } else {
                allGroupChecked = true;
            }
        }
        cbxSelectAll.setChecked(allGroupChecked);


        //cbx clicked
        cbxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {

            for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
                sellerAllItemsCheckedMap.put(sellerId, isChecked);
                Log.d("TestSelectAll","Group set to "+ isChecked);
            }
            for (String sellerId: sellerItemsMap.keySet()) {
                for (CartItem sellerCartItem: sellerItemsMap.get(sellerId)) {
                    sellerCartItem.setChecked(isChecked);
                    Log.d("TestSelectAll","Item set to "+ isChecked);
                }
            }

            viewModel.setSellerAllItemsCheckedMap(sellerAllItemsCheckedMap);
            recyclerView.setAdapter(customerCartItemsAdapter);
            customerCartItemsAdapter.setOnCartSellerBannerListener(this);

        });

        //TODO: set total (need to change when to put the map)
        viewModel.getCartItemsInfoMap().observe(getViewLifecycleOwner(), new Observer<Map<CartItem, CartItemsViewModel.CartItemInfo>>() {
            @Override
            public void onChanged(Map<CartItem, CartItemsViewModel.CartItemInfo> cartItemCartItemInfoMap) {
                double total = 0;
                for(CartItem cartItem: cartItemCartItemInfoMap.keySet()) {
                    if (cartItem.getChecked()){
                        String strPrice = cartItemCartItemInfoMap.get(cartItem).getGroupPrice();
                        if (strPrice != null) {
                            double price;
                            try {
                                price = Double.parseDouble(strPrice.split("CA\\$ ")[1]);
                            } catch (Exception e) {
                                price = 0;
                            }
                            total += price * cartItem.getAmount();
                        }
                    }
                }
                tvCartItemsTotal.setText("CA$ " + total);
            }
        });

        viewModel.getSellerItemsMap().observe(getViewLifecycleOwner(), new Observer<Map<String, ArrayList<CartItem>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<CartItem>> stringArrayListMap) {
                int checkoutAmount = 0;
                for (String sellerId: stringArrayListMap.keySet()) {
                    for (CartItem sellerCartItem: stringArrayListMap.get(sellerId)) {
                        if (sellerCartItem.getChecked()) {
                            //total += sellerCartItem.getAmount() * sellerCartItem.getPrice();
                            checkoutAmount++;
                        }
                    }
                }
                //set checkout amount
                tvCartItemsCheckoutAmount.setText("Checkout("+checkoutAmount+")");
            }
        });

        //set checkout onClickedListener
//        tvCartItemsCheckoutAmount.setOnClickListener(v -> {
//            tvCartItemsCheckoutAmount.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
//            tvCartItemsCheckoutAmount.setTextColor(ContextCompat.getColor(getContext(), R.color.add_business_orange));
//
//            CustomerGroupDetailFragment customerGroupDetailFragment = new CustomerGroupDetailFragment();
//            customerGroupDetailFragment.setArguments(bundle);
//
//            Log.d("Test view", getView().getRootView().toString());
//            navController.navigate(R.id.action_customerHomeFragment_to_customerGroupDetailFragment, bundle);
//        });

    }

    @Override
    public void onSellerBannerChecked(Map<String,Boolean> sellerAllItemsCheckedMap,Map<String, ArrayList<CartItem>> sellerItemsMap) {

        boolean allGroupChecked = false;
        Log.d("TestSelectAll", sellerAllItemsCheckedMap.size()+"");
        for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
            boolean allChecked = sellerAllItemsCheckedMap.get(sellerId);
            Log.d("TestSelectAll", allChecked + "!");
            if (!allChecked) {
                allGroupChecked = false;
                break;
            } else {
                allGroupChecked = true;
            }
        }

        cbxSelectAll.setOnCheckedChangeListener(null);
        cbxSelectAll.setChecked(allGroupChecked);

        cbxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
                sellerAllItemsCheckedMap.put(sellerId, isChecked);
                Log.d("TestSelectAll","Group set to "+ isChecked);
            }
            for (String sellerId: sellerItemsMap.keySet()) {
                for (CartItem sellerCartItem: sellerItemsMap.get(sellerId)) {
                    sellerCartItem.setChecked(isChecked);
                    Log.d("TestSelectAll","Item set to "+ isChecked);
                }
            }

            viewModel.setSellerAllItemsCheckedMap(sellerAllItemsCheckedMap);
            recyclerView.setAdapter(customerCartItemsAdapter);
            customerCartItemsAdapter.setOnCartSellerBannerListener(this);
        });
    }
}