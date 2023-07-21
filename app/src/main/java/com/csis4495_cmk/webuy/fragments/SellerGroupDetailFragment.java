package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;

public class SellerGroupDetailFragment extends Fragment {

    TextView tv;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_group_detail, container, false);

        tv=view.findViewById(R.id.group_detail_frag_title);

        //Get passed bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
                String groupId = bundle.getString("detail_groupId");
            tv.setText(groupId);
            }


        return view;
    }
}