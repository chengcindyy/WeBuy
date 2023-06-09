package com.csis4495_cmk.webuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerProfileActivity extends AppCompatActivity {

    private static final String TAG = CustomerProfileActivity.class.getSimpleName();
    private TextView labelUsername, labelRole;
    private Button logoutButton, btnTest;
    private ImageButton btnSetting, btnViewAll, btnViewPending, btnViewPayment, btnViewPackaging, btnViewShipped, btnViewDelivered;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_profile);

        // For Testing
        btnTest = findViewById(R.id.btn_test_page);
        btnTest.setOnClickListener(view -> startActivity(new Intent(CustomerProfileActivity.this, TestPageActivity.class))); // End Testing

        // show profile info
        labelUsername = findViewById(R.id.txv_username);
        labelRole = findViewById(R.id.txv_member_role);

        if (firebaseUser != null){
            // Notify user if they have not verified email
            checkIfEmailVerified(firebaseUser);
            showUserProfile(firebaseUser);
        } else {
            Toast.makeText(CustomerProfileActivity.this, "displayUserProfileInfo:failure", Toast.LENGTH_LONG).show();
        }

        // Firebase instance
        auth = FirebaseAuth.getInstance();

        // Display order status
        btnViewAll = findViewById(R.id.btn_view_all_order);
        btnViewAll.setOnClickListener(view -> openOrderStatusDialog("viewAll"));

        btnViewPending = findViewById(R.id.btn_view_pending);
        btnViewPending.setOnClickListener(view -> openOrderStatusDialog("viewPending"));

        btnViewPayment = findViewById(R.id.btn_view_confirmed);
        btnViewPayment.setOnClickListener(view -> openOrderStatusDialog("viewConfirmed"));

        btnViewPackaging = findViewById(R.id.btn_view_processed);
        btnViewPackaging.setOnClickListener(view -> openOrderStatusDialog("viewProcessed"));

        btnViewShipped = findViewById(R.id.btn_view_shipped);
        btnViewShipped.setOnClickListener(view -> openOrderStatusDialog("viewShipped"));

        btnViewDelivered = findViewById(R.id.btn_view_canceled);
        btnViewDelivered.setOnClickListener(view -> openOrderStatusDialog("viewCanceled"));

        // Setting
        btnSetting = findViewById(R.id.btn_setting);
        btnSetting.setOnClickListener(view -> {

        });

        // Logout
        logoutButton = findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(view -> {
            auth.signOut();
            Toast.makeText(CustomerProfileActivity.this,"Logged Out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CustomerProfileActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void openOrderStatusDialog(String tab) {
        CustomerOrderStatusDialog dialog = new CustomerOrderStatusDialog();
        dialog.show(getSupportFragmentManager(), tab);
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null) {
                    String _USERNAME = firebaseUser.getDisplayName();
                    String _ROLE = user.getRole();

                    labelUsername.setText(_USERNAME);
                    labelRole.setText(_ROLE);
                }else{
                    Toast.makeText(CustomerProfileActivity.this, "something went wrong! " +
                            "Show profile was canceled", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CustomerProfileActivity.this, "something went wrong! " +
                        "Show profile was canceled", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (! firebaseUser.isEmailVerified()){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerProfileActivity.this);
        builder.setTitle("You account is not verified!");
        builder.setMessage("Please verify your email now. Or You may not login without email verification next time. If you have already verified your email, please login again.");
        //Open email app if "continue" clicked
        builder.setPositiveButton("Continue", (dialog, which) -> {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //open in a new window
            startActivity(intent);
            finish();
        }).setNeutralButton("Log out", (dialog, which) -> {
            auth.signOut();
            Toast.makeText(CustomerProfileActivity.this,"Logged out successfully, please login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CustomerProfileActivity.this, LoginActivity.class));
            finish();
        });

        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }


}