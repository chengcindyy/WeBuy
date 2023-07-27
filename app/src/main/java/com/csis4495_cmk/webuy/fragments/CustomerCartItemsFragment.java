package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csis4495_cmk.webuy.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerCartItemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerCartItemsFragment extends Fragment {
    private static final String ARG_POSITION = "pos";
    private static final int CART = 0;
    private static final int SAVED = 1;
    private int tabSelection;

    public CustomerCartItemsFragment() {
        // Required empty public constructor
    }

    public static CustomerCartItemsFragment newInstance(int position) {
        CustomerCartItemsFragment fragment = new CustomerCartItemsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tabSelection = getArguments().getInt(ARG_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_cart_items, container, false);
    }
}