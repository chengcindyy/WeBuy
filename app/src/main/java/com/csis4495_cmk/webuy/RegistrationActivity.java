package com.csis4495_cmk.webuy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();

    private EditText edit_username, edit_email, edit_password, edit_confirm_password;
    private Button btn_create, btn_cancel;
    private ProgressBar loadingPB;
    boolean isProgressVisible = false;
    private LoginButton btn_login_facebook;
    CallbackManager callbackManager = CallbackManager.Factory.create();
    private final int CUSTOMER = 1;
    private final int SELLER = 2;

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Get data form roleSelectionActivity
        int selectedRole = getIntent().getIntExtra("role", 0);
        Toast.makeText(RegistrationActivity.this, String.valueOf(selectedRole), Toast.LENGTH_SHORT).show();

        // XML reference
        edit_username = findViewById(R.id.edit_username);
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        edit_confirm_password = findViewById(R.id.edit_confirm_password);

        // To create account
        btn_create = findViewById(R.id.btn_create_account);
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str_username = edit_username.getText().toString();
                String str_email = edit_email.getText().toString();
                String str_password = edit_password.getText().toString();
                String str_confirm_password = edit_confirm_password.getText().toString();

                if (TextUtils.isEmpty(str_username)) {
                    Toast.makeText(RegistrationActivity.this,
                            "Please enter your username.", Toast.LENGTH_SHORT).show();
                    edit_username.setError("Username is required.");
                    edit_username.requestFocus();
                } else if (TextUtils.isEmpty(str_email)) {
                    Toast.makeText(RegistrationActivity.this,
                            "Please enter your email.", Toast.LENGTH_SHORT).show();
                    edit_email.setError("Email is required.");
                    edit_email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
                    Toast.makeText(RegistrationActivity.this,
                            "Please enter your email.", Toast.LENGTH_SHORT).show();
                    edit_email.setError("Valid email is required.");
                    edit_email.requestFocus();
                }else if (TextUtils.isEmpty(str_password)) {
                    Toast.makeText(RegistrationActivity.this,
                            "Please enter your password.", Toast.LENGTH_SHORT).show();
                    edit_password.setError("Password is required.");
                    edit_password.requestFocus();
                } else if (str_password.length() < 6) {
                    Toast.makeText(RegistrationActivity.this,
                            "Password should be at least 6 digits.", Toast.LENGTH_SHORT).show();
                    edit_password.setError("Password too weak.");
                    edit_password.requestFocus();
                } else if (TextUtils.isEmpty(str_confirm_password)) {
                    Toast.makeText(RegistrationActivity.this,
                            "Please enter your password again.", Toast.LENGTH_SHORT).show();
                    edit_confirm_password.setError("Password confirmation is required.");
                    edit_confirm_password.requestFocus();
                } else if (!str_password.equals(str_confirm_password)) {
                    Toast.makeText(RegistrationActivity.this,
                            "Please enter same password.", Toast.LENGTH_SHORT).show();
                    edit_confirm_password.setError("Password confirmation is required.");
                    edit_confirm_password.requestFocus();
                    // Clean the entered passwords
                    edit_password.clearComposingText();
                    edit_confirm_password.clearComposingText();
//                } else if (!checkBoxAgree.isChecked()) {
//                    Toast.makeText(RegistrationActivity.this,
//                            "Please confirm term and policy.", Toast.LENGTH_SHORT).show();

                } else {
                    registerUser(str_username, str_email, str_password, str_confirm_password);

                    if (isProgressVisible){
                        loadingPB.setVisibility(View.GONE);
                        isProgressVisible = false;
                    } else {
                        isProgressVisible = true;
                        loadingPB.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        // To cancel and back to login page
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

    private void registerUser(String str_username, String str_email, String str_password, String str_confirm_password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(str_email, str_password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    // Update display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(str_username).build();
                    firebaseUser.updateProfile(profileChangeRequest);


                }
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