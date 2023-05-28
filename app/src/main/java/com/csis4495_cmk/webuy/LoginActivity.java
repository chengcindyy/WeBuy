package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.window.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText edit_email, edit_password;
    private Button btn_login, btn_register, btn_forgotPassword, btn_test;

    private FirebaseAuth auth;
    private final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edit_email = findViewById(R.id.edit_loginEmail);
        edit_password = findViewById(R.id.edit_loginPassword);
        btn_login = findViewById(R.id.btn_login);

        // For Testing
        btn_test = findViewById(R.id.btn_test_page);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, TestPageActivity.class));
            }
        });

        // Open register page
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(v -> {
//            Toast.makeText(LoginActivity.this, "Register button clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this, RoleSelectionActivity.class));

        });

        // Reset password
        btn_forgotPassword = findViewById(R.id.btn_forgotPassword);
        btn_forgotPassword.setOnClickListener(v -> {
//            Toast.makeText(LoginActivity.this, "Reset password button clicked", Toast.LENGTH_LONG).show();
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }
}