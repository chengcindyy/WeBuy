package com.csis4495_cmk.webuy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TestFragment extends Fragment {

    private NavController navController;
    private Button btnProductList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(TestFragment.this);

//        btnProductList = view.findViewById(R.id.btn_product_list_test);
//        btnProductList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                navController.navigate(R.id.action_testFragment_to_sellerProductListFragment);
//            }
//        });


    }
}
