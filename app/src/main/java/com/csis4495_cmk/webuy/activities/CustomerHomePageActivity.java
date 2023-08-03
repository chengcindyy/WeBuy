package com.csis4495_cmk.webuy.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.fragments.CustomerHomeGroupsFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CustomerHomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home_page);

        BottomAppBar bottomAppBar = findViewById(R.id.bottomAppBar);
        FloatingActionButton floatingActionButton = findViewById(R.id.fab);
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_customer);
        NavController navController = navHostFragment.getNavController();

        // Set up the BottomAppBar menu click events
        bottomAppBar.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.customerProfileFragment:
                    navController.navigate(R.id.customerProfileFragment);
                    return true;
                case R.id.customerCartFragment:
                    navController.navigate(R.id.customerCartFragment);
                    return true;
                default:
                    return false;
            }
        });

        // Set up the FloatingActionButton click event if needed
        floatingActionButton.setOnClickListener(v -> {
            // Navigate or perform some action
            new CustomerHomeGroupsFragment();
            navController.navigate(R.id.customerHomeFragment);
        });

        // Navigate to the initial fragment
        navController.navigate(R.id.customerHomeFragment);


    }
}