package com.csis4495_cmk.webuy.activities;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;


import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.fragments.CustomerHomeGroupsFragment;
import com.facebook.login.LoginManager;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CustomerHomePageActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();

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

        checkIfEmailVerified(auth.getCurrentUser());
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (! firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your account is not verified!");
        builder.setMessage("Please verify your email now. Or You may not login without email verification next time. If you have already verified your email, please login again.");
        //Open email app if "continue" clicked
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //open in a new window
            startActivity(intent);
        }).setNeutralButton("Log out", (dialog, which) -> {
            auth.signOut();
            LoginManager.getInstance().logOut();
            Toast.makeText(this,"Logged out successfully, please login again", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish(); // if you want to finish the current activity
        });

        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }
}