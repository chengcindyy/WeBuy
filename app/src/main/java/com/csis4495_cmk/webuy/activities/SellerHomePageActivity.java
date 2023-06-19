package com.csis4495_cmk.webuy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.fragments.SellerAddStyleFragment;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class SellerHomePageActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home_page);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_seller);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.sellerHomeFragment);
    }

}