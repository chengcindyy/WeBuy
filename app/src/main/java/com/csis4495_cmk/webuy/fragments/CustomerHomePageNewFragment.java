package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;

public class CustomerHomePageNewFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_customer_home_page_new, container, false);

        final RecyclerView recyclerView = rootview.findViewById(R.id.rv_cust_home_product);


        return rootview;
    }
}