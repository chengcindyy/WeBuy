package com.csis4495_cmk.webuy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Bundle;

import com.csis4495_cmk.webuy.R;

public class MainActivity extends AppCompatActivity {

    private final String TAG = MainActivity.class.getSimpleName();
    private int selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_user);
        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.userLoginFragment);

    }

    public void saveData(int role) {
        this.selectedRole = role;
    }

    public int getSelectedRole() {
        return this.selectedRole;
    }
}