package com.csis4495_cmk.webuy;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.models.Customer;
import com.csis4495_cmk.webuy.models.Seller;
import com.csis4495_cmk.webuy.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = RegistrationActivity.class.getSimpleName();
    private EditText edit_email, edit_password, edit_confirm_password;
    private Button btn_create, btn_cancel;
    private ProgressBar loadingPB;
    boolean isProgressVisible = false;
    private String user_role = "";
    private final String CUSTOMER = "customer";
    private final String SELLER = "seller";

    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;
    private LoginButton btn_register_facebook;
    private CallbackManager callbackManager = CallbackManager.Factory.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Get data form roleSelectionActivity
        String role = String.valueOf(getIntent().getIntExtra("role", 0));
        // To check user role, using switch in order to add more role in the future
        switch (role) {
            case "1":
                user_role = CUSTOMER;
                break;
            case "2":
                user_role = SELLER;
                break;
            default:
                user_role = "";
                Toast.makeText(RegistrationActivity.this, "User role haven't selected, please select one of these role", Toast.LENGTH_SHORT).show();
                break;
        }
        Toast.makeText(RegistrationActivity.this, user_role, Toast.LENGTH_SHORT).show();
        // XML reference
        edit_email = findViewById(R.id.edit_email);
        edit_password = findViewById(R.id.edit_password);
        edit_confirm_password = findViewById(R.id.edit_confirm_password);

        // To create account
        btn_create = findViewById(R.id.btn_create_account);
        btn_create.setOnClickListener(view -> {
            String email = edit_email.getText().toString();
            String password = edit_password.getText().toString();
            String confirm_password = edit_confirm_password.getText().toString();

            // Checking user entered values if it fit the correct format
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(RegistrationActivity.this,
                        "Please enter your email.", Toast.LENGTH_SHORT).show();
                edit_email.setError("Email is required.");
                edit_email.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegistrationActivity.this,
                        "Please enter your email.", Toast.LENGTH_SHORT).show();
                edit_email.setError("Valid email is required.");
                edit_email.requestFocus();
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(RegistrationActivity.this,
                        "Please enter your password.", Toast.LENGTH_SHORT).show();
                edit_password.setError("Password is required.");
                edit_password.requestFocus();
            } else if (password.length() < 6) {
                Toast.makeText(RegistrationActivity.this,
                        "Password should be at least 6 digits.", Toast.LENGTH_SHORT).show();
                edit_password.setError("Password too weak.");
                edit_password.requestFocus();
            } else if (TextUtils.isEmpty(confirm_password)) {
                Toast.makeText(RegistrationActivity.this,
                        "Please enter your password again.", Toast.LENGTH_SHORT).show();
                edit_confirm_password.setError("Password confirmation is required.");
                edit_confirm_password.requestFocus();
            } else if (!password.equals(confirm_password)) {
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
                registerUser(email, password);

                // Create progress bar
                loadingPB = findViewById(R.id.progress_circular_loading);
                if (isProgressVisible) {
                    loadingPB.setVisibility(View.GONE);
                    isProgressVisible = false;
                } else {
                    isProgressVisible = true;
                    loadingPB.setVisibility(View.VISIBLE);
                }
            }
        });

        // To cancel and back to login page
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(view ->
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));

        // Firebase Authentication
        auth = FirebaseAuth.getInstance();

        // [START FACEBOOK REGISTRATION]
        btn_register_facebook = findViewById(R.id.btn_register_facebook);
        btn_register_facebook.setPermissions("email");
        btn_register_facebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String email = firebaseUser.getEmail();
                            // user data into the firebase realtime db
                            User newUser = new User(email, user_role);
                            CreateUserProfile(firebaseUser, newUser);
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure" + task.getException(), task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed. Your email has been registered, please login your account",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser currentUser) {

//        if (currentUser != null) {
//            String username = currentUser.getUsername();
//            String email = currentUser.getEmail();
//            // 更新使用者名稱和電子郵件
//            TextView usernameTextView = findViewById(R.id.usernameTextView);
//            TextView emailTextView = findViewById(R.id.emailTextView);
//            usernameTextView.setText(username);
//            emailTextView.setText(email);
//            // 顯示登出按鈕
//            Button logoutButton = findViewById(R.id.logoutButton);
//            logoutButton.setVisibility(View.VISIBLE);
//        } else {
//            // 清除使用者名稱和電子郵件
//            TextView usernameTextView = findViewById(R.id.usernameTextView);
//            TextView emailTextView = findViewById(R.id.emailTextView);
//            usernameTextView.setText("");
//            emailTextView.setText("");
//            // 隱藏登出按鈕
//            Button logoutButton = findViewById(R.id.logoutButton);
//            logoutButton.setVisibility(View.GONE);
//        }
    }


    // [END FACEBOOK REGISTRATION]

    // [START FIREBASE REGISTRATION]
    private void registerUser(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    // Update display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(email).build();
                    firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                // user data into the firebase realtime db
                                User newUser = new User(email, user_role);
                                CreateUserProfile(firebaseUser, newUser);

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    String uid = user.getUid();
                                    if (user_role.equals(CUSTOMER)) {
                                        Customer newCustomer = new Customer(uid);
                                        ExtractingCustomerReference(firebaseUser, newCustomer);
                                    } else if (user_role.equals(SELLER)) {
                                        Seller newSeller = new Seller(uid);
                                        ExtractingSellerReference(firebaseUser, newSeller);
                                    }
                                }
                            } else {
                                // Handle failure
                                Toast.makeText(RegistrationActivity.this, "Cannot register a new user, please try again latter.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegistrationActivity.this, "Cannot login with this email, please try again latter.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateUserProfile(FirebaseUser firebaseUser, User newUser) {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("User");
        mFirebaseDatabase.child(firebaseUser.getUid()).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(TAG, "extractingUserReference:success");
                if (user_role == CUSTOMER) {
                    Customer newCustomer = new Customer(firebaseUser.getUid());
                    ExtractingCustomerReference(firebaseUser, newCustomer);
                } else if (user_role == SELLER) {
                    Seller newSeller = new Seller(firebaseUser.getUid());
                    ExtractingSellerReference(firebaseUser, newSeller);
                }
            }
        });
    }

    private void ExtractingSellerReference(FirebaseUser firebaseUser, Seller newSeller) {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Seller");
        mFirebaseDatabase.child(firebaseUser.getUid()).setValue(newSeller).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    // Send Verification Email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(RegistrationActivity.this, "Seller registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegistrationActivity.this, SellerHomePageActivity.class);
                    // Prevent user back to Registration activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(RegistrationActivity.this, "Failed to write Seller data! because " + task.getException(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Failed to write Seller data - unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void ExtractingCustomerReference(FirebaseUser firebaseUser, Customer newCustomer) {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Customer");
        Toast.makeText(RegistrationActivity.this, firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
        mFirebaseDatabase.child(firebaseUser.getUid()).setValue(newCustomer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    // Send Verification Email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(RegistrationActivity.this, "Customer registered successfully!", Toast.LENGTH_SHORT).show();
                    // Open User homepage once successfully register
                    Intent intent = new Intent(RegistrationActivity.this, CustomerHomePageActivity.class);
                    // Prevent user back to Registration activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(RegistrationActivity.this, "Failed to write Customer data! because " + task.getException(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegistrationActivity.this, "Failed to write Customer data - unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    // [END FIREBASE REGISTRATION]

}
