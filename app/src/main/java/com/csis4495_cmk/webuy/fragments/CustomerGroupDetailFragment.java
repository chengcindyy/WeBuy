package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.csis4495_cmk.webuy.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerGroupDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerGroupDetailFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_group_detail, container, false);
    }
}