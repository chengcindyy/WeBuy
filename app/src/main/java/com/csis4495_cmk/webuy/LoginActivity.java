package com.csis4495_cmk.webuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.window.SplashScreen;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_email, edit_password;
    private Button btn_login, btn_register, btn_forgotPassword, btn_test;

    private int selected_role = 0;
    private final int CUSTOMER = 1;
    private final int SELLER = 2;

    private FirebaseAuth auth;
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // For Testing
        btn_test = findViewById(R.id.btn_test_page);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, TestPageActivity.class));
            }
        }); // End Testing

        // Two Text field
        edit_email = findViewById(R.id.edit_loginEmail);
        edit_password = findViewById(R.id.edit_loginPassword);

        // Open registration page
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RoleSelectionActivity.class));
        });

        // Reset password
        btn_forgotPassword = findViewById(R.id.btn_forgotPassword);
        btn_forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });

        // Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // After Clicked login Button, it will lead to homepage
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> {
            String str_loginEmail = edit_email.getText().toString();
            String str_loginPassword = edit_password.getText().toString();
            if (TextUtils.isEmpty(str_loginEmail)){
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                edit_email.setError("Email is required.");
                edit_email.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(str_loginEmail).matches()) {
                Toast.makeText(LoginActivity.this,
                        "Please enter your email.", Toast.LENGTH_SHORT).show();
                edit_email.setError("Valid email is required.");
                edit_email.requestFocus();
            } else if (TextUtils.isEmpty(str_loginPassword)) {
                Toast.makeText(LoginActivity.this,
                        "Please enter your password.", Toast.LENGTH_SHORT).show();
                edit_password.setError("Password is required.");
                edit_password.requestFocus();
            } else {
                loginUser(str_loginEmail,str_loginPassword);
            }
        });
    }

    private void loginUser(String text_loginEmail, String text_loginPassword) {
        auth.signInWithEmailAndPassword(text_loginEmail,text_loginPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // Get instance of current user
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    // Check if the user is verified before user can access their profile
                    if (firebaseUser.isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                        // Open Home page
                        startActivity(new Intent(LoginActivity.this, CustomerHomePageActivity.class));
                        finish();
                    } else {
                        firebaseUser.sendEmailVerification();
                        auth.signOut();
                        showAlertDialog();
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        edit_email.setError("User is not existed. Please re-enter the email or register.");
                        edit_email.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        edit_password.setError("Invalid password. Please check and re-enter.");
                        edit_password.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void showAlertDialog() {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("You account is not verified!");
        builder.setMessage("Please verify your email now. You cannot login without email verification.");
        //Open email app if "continue" clicked
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //open in a new window
                startActivity(intent);
            }
        });
        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null){
            Toast.makeText(LoginActivity.this, "Already logged in!", Toast.LENGTH_SHORT).show();
            if(selected_role == CUSTOMER){
                startActivity(new Intent(LoginActivity.this, CustomerHomePageActivity.class));
                finish();
            }else if (selected_role == SELLER) {
//                startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                finish();
            }else{
                Toast.makeText(this, "Something when wrong on registration, please try again.", Toast.LENGTH_SHORT).show();
            }
            //start HomePage activity

        } else {
            Toast.makeText(LoginActivity.this, "You can log in now!", Toast.LENGTH_SHORT).show();

        }
    }
}