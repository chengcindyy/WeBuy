package com.csis4495_cmk.webuy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private EditText edit_username, edit_email, edit_password, edit_confirm_password;
    private Button btn_create, btn_cancel;
    private LoginButton btn_login_facebook;
    CallbackManager callbackManager = CallbackManager.Factory.create();

    private final int CUSTOMER = 1;
    private final int SELLER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // TODO: Get data form roleSelectionActivity
        int selectedRole = getIntent().getIntExtra("role", 0);
        Toast.makeText(RegistrationActivity.this, String.valueOf(selectedRole), Toast.LENGTH_SHORT).show();

        // TODO: To create account & back to login
        btn_create = findViewById(R.id.btn_create_account);

        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(view ->
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));

        // TODO: To Create Account using Facebook
        btn_login_facebook = findViewById(R.id.btn_login_facebook);
        btn_login_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(RegistrationActivity.this, Arrays.asList("email"));
            }
        });

        // Callback registration
        btn_login_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (selectedRole == CUSTOMER) {
//                    Intent intent = new Intent(this, CustomerHomepageActivity.class);
//                    startActivity(intent);
                } else if (selectedRole == SELLER) {
//                    Intent intent = new Intent(this, SellerHomepageActivity.class);
//                    startActivity(intent);
                }
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}