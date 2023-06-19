package com.csis4495_cmk.webuy.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.csis4495_cmk.webuy.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CustomerHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        BottomNavigationView navView = findViewById(R.id.bottomNavigationView);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_customer);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(navView, navController);

        navController.navigate(R.id.customerHomeFragment);

    }
}