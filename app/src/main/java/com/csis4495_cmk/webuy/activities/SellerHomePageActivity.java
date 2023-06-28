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
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.fragments.SellerAddStyleFragment;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.facebook.login.LoginManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SellerHomePageActivity extends AppCompatActivity {

    private MaterialToolbar topToolbar;
    private NavController navController;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home_page);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_seller);
        navController = navHostFragment.getNavController();
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
                            navController.navigate(R.id.sellerProfileFragment);
                            return true;
                        case R.id.logout:
                            logoutAccount();
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

    private void logoutAccount() {
        auth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(SellerHomePageActivity.this,"Logged Out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SellerHomePageActivity.this, MainActivity.class);
        startActivity(intent);
        SellerHomePageActivity.this.finish();
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        // Set User profile picture (After uploaded)
//        Uri uri = firebaseUser.getPhotoUrl();
//        if (uri != null){
//            Glide.with(SellerHomePageActivity.this)
//                    .load(uri.toString())
//                    .circleCrop() // or .transform(new CircleCrop())
//                    .into(imgUserProfile);
//        }
//    }
}