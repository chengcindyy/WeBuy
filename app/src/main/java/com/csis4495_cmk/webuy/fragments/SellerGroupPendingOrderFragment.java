package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;


public class SellerGroupPendingOrderFragment extends Fragment {

    private TextView tv_no;
    private RecyclerView rv;

    private String groupId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_group_order, container, false);

        tv_no = view.findViewById(R.id.tv_group_pending);
        rv = view.findViewById(R.id.rv_group_pending);


        return view;
    }
}