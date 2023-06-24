package com.csis4495_cmk.webuy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.fragments.SellerAddStyleFragment;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class SellerHomePageActivity extends AppCompatActivity {

    private MaterialToolbar topToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home_page);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_seller);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.sellerHomeFragment);

        // Top toolbar
        topToolbar = findViewById(R.id.topAppBar);
        topToolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.ic_user_profile) {
                PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.ic_user_profile));
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.seller_profile_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()){
                        case R.id.profile:
                            return true;
                        case R.id.logout:
                            return true;
                    }
                    return false;
                });

                popupMenu.show();
                return true;
            }
            return false;
        });
    }
}