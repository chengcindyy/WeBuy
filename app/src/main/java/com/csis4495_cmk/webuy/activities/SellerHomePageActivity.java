package com.csis4495_cmk.webuy.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.fragments.SellerGroupDetailFragment;
import com.csis4495_cmk.webuy.models.Seller;
import com.csis4495_cmk.webuy.tools.OnGroupOrderInventoryAllocatedListener;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SellerHomePageActivity extends AppCompatActivity {

    private MaterialToolbar topToolbar;
    private NavController navController;
    private String imageUrl;
    PopupMenu popupMenu;
    FirebaseAuth auth = FirebaseAuth.getInstance();
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home_page);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_seller);
        navController = navHostFragment.getNavController();
        navController.navigate(R.id.sellerHomeFragment);

        checkIfEmailVerified(auth.getCurrentUser());

        // Top toolbar
        topToolbar = findViewById(R.id.topAppBar);
        topToolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.ic_user_profile) {
                popupMenu = new PopupMenu(this, findViewById(R.id.ic_user_profile));
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.seller_profile_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId()) {
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
        findStoreProfileImgAndSetToICon();
    }

    private void findStoreProfileImgAndSetToICon() {
        String uid = auth.getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Seller");
        reference.child(uid).addValueEventListener(new ValueEventListener() {  // change to addValueEventListener
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Seller seller = snapshot.getValue(Seller.class);
                if(seller.getStoreInfo() !=null) {

                    String storePic = seller.getStoreInfo().getStorePic();

                    if (storePic != null) {
                        imageUrl = storePic;
                        Log.d("Upload img: imageUrl ", imageUrl);
                        MenuItem profileItem = topToolbar.getMenu().findItem(R.id.ic_user_profile);
                        Glide.with(SellerHomePageActivity.this)
                                .load(imageUrl)
                                .circleCrop()
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        profileItem.setIcon(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        profileItem.setIcon(R.drawable.ic_user_profile);
                                    }
                                });
                    }
                }

                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing
            }
        });
    }


    private void logoutAccount() {
        auth.signOut();
        LoginManager.getInstance().logOut();
        Toast.makeText(SellerHomePageActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SellerHomePageActivity.this, MainActivity.class);
        startActivity(intent);
        SellerHomePageActivity.this.finish();
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        firebaseUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    FirebaseUser updatedUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (updatedUser.isEmailVerified()) {
                        // The email is verified, so you can bypass the dialog.
                        // Continue your normal flow here.
                    } else {
                        // The email is still not verified, show the dialog.
                        showAlertDialog();
                    }
                } else {
                    Log.e("checkIfEmailVerified: ", "Failed to reload user.");
                }
            }
        });
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