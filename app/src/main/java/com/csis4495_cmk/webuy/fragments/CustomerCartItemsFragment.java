package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.viewmodels.CustomerCartItemsViewModel;
import com.csis4495_cmk.webuy.adapters.recyclerview.CustomerCartItemsAdapter;
import com.csis4495_cmk.webuy.models.CartItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomerCartItemsFragment extends Fragment
                    implements CustomerCartItemsAdapter.onCartSellerBannerListener{

    private final int CART = 1;
    private NavController navController;
    RecyclerView recyclerView;
    TextView tvNoItems, tvCartItemsTotal, tvCartItemsCheckoutAmount;
    CheckBox cbxSelectAll;
    CustomerCartItemsAdapter customerCartItemsAdapter;
    CustomerCartItemsViewModel viewModel;
//    Map<String, ArrayList<CartItem>> sellerItemsMap;
//    Map<String, Boolean> sellerAllItemsCheckedMap;
//    Map<CartItem, CustomerCartItemsViewModel.CartItemInfo> cartItemsInfoMap;
    Map<String, ArrayList<CartItem>> sellerItemsMap = new HashMap<>();
    Map<String, Boolean> sellerAllItemsCheckedMap = new HashMap<>();
    Map<CartItem, CustomerCartItemsViewModel.CartItemInfo> cartItemsInfoMap = new HashMap<>();


    public CustomerCartItemsFragment() {
        // Required empty public constructor
    }

    public static CustomerCartItemsFragment newInstance() {
        CustomerCartItemsFragment fragment = new CustomerCartItemsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        navController = NavHostFragment.findNavController(CustomerCartItemsFragment.this);
        tvNoItems = view.findViewById(R.id.tv_no_items);
        tvCartItemsTotal = view.findViewById(R.id.tv_cart_items_total);
        tvCartItemsCheckoutAmount = view.findViewById(R.id.tv_cart_items_checkout_amount);
        cbxSelectAll = view.findViewById(R.id.checkbox_select_all);

        viewModel = new ViewModelProvider(requireActivity()).get(CustomerCartItemsViewModel.class);

        recyclerView = view.findViewById(R.id.rv_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        viewModel.getSellerItemsMapLiveData().observe(getViewLifecycleOwner(), new Observer<Map<String, ArrayList<CartItem>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<CartItem>> stringArrayListMap) {
                sellerItemsMap = stringArrayListMap;
                Log.d("vm","getSellerItemsMap: "+ sellerItemsMap);

                //set no item label if no items in the cart
                if (sellerItemsMap.size() != 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNoItems.setVisibility(View.GONE);

                    customerCartItemsAdapter = new CustomerCartItemsAdapter(getContext(), viewModel, getViewLifecycleOwner());
                    recyclerView.setAdapter(customerCartItemsAdapter);
                    customerCartItemsAdapter.setOnCartSellerBannerListener(CustomerCartItemsFragment.this);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    tvNoItems.setVisibility(View.VISIBLE);
                }

                //set group n/a uncheck

                //set checkout amount
                final int[] checkoutAmount = {0};
                //set total
                final double[] total = {0};

                for (String sellerId: sellerItemsMap.keySet()) {
                    for (CartItem sellerCartItem: stringArrayListMap.get(sellerId)) {
                        if (sellerCartItem.getChecked()) {

                            checkoutAmount[0]++;
                            viewModel.getCartItemsInfoMapLiveData().observe(getViewLifecycleOwner(), new Observer<Map<CartItem, CustomerCartItemsViewModel.CartItemInfo>>() {
                                @Override
                                public void onChanged(Map<CartItem, CustomerCartItemsViewModel.CartItemInfo> cartItemCartItemInfoMap) {
                                    if (cartItemCartItemInfoMap.get(sellerCartItem) != null) {
                                        if (cartItemCartItemInfoMap.get(sellerCartItem).getGroupName().equals("Group N/A")) {
                                            sellerCartItem.setChecked(false);
                                            sellerAllItemsCheckedMap.put(sellerId, false);
                                            //viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
                                            checkoutAmount[0]--;

                                        } else {
                                            String strPrice = cartItemCartItemInfoMap.get(sellerCartItem).getGroupPrice();
                                            if (strPrice != null) {
                                                double price;
                                                try {
                                                    price = Double.parseDouble(strPrice.split("CA\\$ ")[1]);
                                                } catch (Exception e) {
                                                    price = 0;
                                                }
                                                total[0] += price * sellerCartItem.getAmount();
                                            }
                                        }

                                    }
                                }
                            });

                            //viewModel.setSellerItemsMapLiveData(sellerItemsMap);

                        }
                    }
                }
                tvCartItemsCheckoutAmount.setText("Checkout("+ checkoutAmount[0] +")");
                tvCartItemsTotal.setText("CA$ " + total[0]);
            }
        });
        viewModel.getSellerAllItemsCheckedMap().observe(getViewLifecycleOwner(), new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> stringBooleanMap) {
                sellerAllItemsCheckedMap = stringBooleanMap;
                boolean allGroupChecked = false;
                for (String sellerId : sellerAllItemsCheckedMap.keySet()) {
                    boolean allChecked = sellerAllItemsCheckedMap.get(sellerId);
                    //cbxSelectAll.setOnCheckedChangeListener(null);
                    if (!allChecked) {
                        allGroupChecked = false;
                        break;
                    } else {
                        allGroupChecked = true;
                    }
                }
                //cbxSelectAll.setOnCheckedChangeListener(null);
                //cbxSelectAll.setChecked(allGroupChecked);
            }
        });

        //cbx clicked //
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

            viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
            viewModel.setSellerItemsMapLiveData(sellerItemsMap);
        });

        viewModel.getCartItemsInfoMapLiveData().observe(getViewLifecycleOwner(), new Observer<Map<CartItem, CustomerCartItemsViewModel.CartItemInfo>>() {
            @Override
            public void onChanged(Map<CartItem, CustomerCartItemsViewModel.CartItemInfo> cartItemCartItemInfoMap) {
                cartItemsInfoMap = cartItemCartItemInfoMap;
            }
        });

        //checkout clicked
        tvCartItemsCheckoutAmount.setOnClickListener(v -> {
            boolean isAllValidOrderAmount = true; //all the orderAmount cannot be more than inventory amount
            Set<String> checkOutSellerSet = new HashSet<>(); // to know how many sellers checked

            //check all selected items are available
//            viewModel.getSellerItemsMapLiveData()

            for (String sellerId: sellerItemsMap.keySet() ) {
                for(CartItem cartItem: sellerItemsMap.get(sellerId)) {
                    if(cartItem.getChecked()) {
                        checkOutSellerSet.add(cartItem.getSellerId());
                        Log.d("checkInfoMap", cartItemsInfoMap.size()+" cart items");
                        int inventoryAmount = cartItemsInfoMap.get(cartItem).getInventoryAmount();
                        int orderAmount = cartItem.getAmount();
                        boolean condValidOrderAmount;
                        if (orderAmount > 0) {
                            switch (inventoryAmount) {
                                case -1:
                                    condValidOrderAmount = true;
                                    break;
                                case 0:
                                    condValidOrderAmount = false;
                                    break;
                                default:
                                    condValidOrderAmount = orderAmount <= inventoryAmount;
                                    break;
                            }
                            Log.d("checkInfoMap", orderAmount+" order amount");
                            Log.d("checkInfoMap", inventoryAmount+" inventory amount");
                        } else { //orderAmount == 0 or < 0
                            condValidOrderAmount = false;
                        }

                        if (!condValidOrderAmount) {
                            Log.d("checkInfoMap", orderAmount+" invalid amount");
                            isAllValidOrderAmount = false;
                            break;
                        }
                    }
                }
                if (!isAllValidOrderAmount) {
                    break;
                }
            }

            if(!isAllValidOrderAmount) { //invalid amount
                Toast.makeText(getContext(),"Please change your order amount",Toast.LENGTH_SHORT).show();
            } else if (checkOutSellerSet.size() > 1) { //not the same seller
                Log.d("checkInfoMap", checkOutSellerSet.size()+" sellers");
                Toast.makeText(getContext(),"Please select items from one store",Toast.LENGTH_SHORT).show();
            } else if (checkOutSellerSet.size() == 0) { //no checkout item
                Toast.makeText(getContext(),"Please select at least one item",Toast.LENGTH_SHORT).show();
            }  else {
                CustomerCheckoutFragment.newInstance(CART);
                navController.navigate(R.id.customerCheckoutFragment);
            }
        });
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

            viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
            viewModel.setSellerItemsMapLiveData(sellerItemsMap);
            recyclerView.setAdapter(customerCartItemsAdapter);
            customerCartItemsAdapter.setOnCartSellerBannerListener(this);
        });
    }
}