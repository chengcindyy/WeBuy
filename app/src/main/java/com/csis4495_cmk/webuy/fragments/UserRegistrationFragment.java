package com.csis4495_cmk.webuy.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.csis4495_cmk.webuy.activities.CustomerHomePageActivity;
import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.activities.SellerHomePageActivity;
import com.csis4495_cmk.webuy.models.Customer;
import com.csis4495_cmk.webuy.models.Seller;
import com.csis4495_cmk.webuy.models.Store;
import com.csis4495_cmk.webuy.models.User;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserRegistrationFragment extends Fragment {

    private static final String TAG = "UserRegistrationFragment";
    private EditText editEmail, editPassword, editConfirmPassword, editStoreName;
    private Button btnCreate, btnCancel, fb;
    private TextInputLayout storeNameLayout;
    private ProgressBar loadingPB;
    boolean isProgressVisible = false;
    private String storeName = "";
    private String user_role = "";
    private final String CUSTOMER = "customer";
    private final String SELLER = "seller";
    private String _STORE_NAME;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;
    private LoginButton btn_register_facebook;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    private NavController navController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_registration, container, false);

        // Get data form roleSelection fragment
        MainActivity activity = (MainActivity) getActivity();
        int roleNum = activity.getSelectedRole();
        switch (roleNum) {
            case 1:
                user_role = CUSTOMER;
                break;
            case 2:
                user_role = SELLER;
                break;
            default:
                user_role = "";
                Toast.makeText(getContext(), "User role haven't selected, please select one of these role", Toast.LENGTH_SHORT).show();
                break;
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(UserRegistrationFragment.this);

        Toast.makeText(getContext(), user_role, Toast.LENGTH_SHORT).show();
        // XML reference
        editEmail = view.findViewById(R.id.edit_email);
        editPassword = view.findViewById(R.id.edit_password);
        editConfirmPassword = view.findViewById(R.id.edit_confirm_password);
        editStoreName = view.findViewById(R.id.edit_store_name);
        storeNameLayout = view.findViewById(R.id.edit_layout_store_name);

        if(user_role.equals(SELLER)){
            storeNameLayout.setVisibility(View.VISIBLE);
        }

        // To create account
        btnCreate = view.findViewById(R.id.btn_create_account);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String _EMAIL = editEmail.getText().toString();
                String _PWD = editPassword.getText().toString();
                String _CONFIRM_PWD = editConfirmPassword.getText().toString();
                _STORE_NAME = editStoreName.getText().toString();

                // Checking user entered values if it fit the correct format
                if (TextUtils.isEmpty(_EMAIL)) {
                    Toast.makeText(getContext(),
                            "Please enter your email.", Toast.LENGTH_SHORT).show();
                    editEmail.setError("Email is required.");
                    editEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(_EMAIL).matches()) {
                    Toast.makeText(getContext(),
                            "Please enter your email.", Toast.LENGTH_SHORT).show();
                    editEmail.setError("Valid email is required.");
                    editEmail.requestFocus();
                } else if (TextUtils.isEmpty(_PWD)) {
                    Toast.makeText(getContext(),
                            "Please enter your password.", Toast.LENGTH_SHORT).show();
                    editPassword.setError("Password is required.");
                    editPassword.requestFocus();
                } else if (_PWD.length() < 6) {
                    Toast.makeText(getContext(),
                            "Password should be at least 6 digits.", Toast.LENGTH_SHORT).show();
                    editPassword.setError("Password too weak.");
                    editPassword.requestFocus();
                } else if (TextUtils.isEmpty(_CONFIRM_PWD)) {
                    Toast.makeText(getContext(),
                            "Please enter your password again.", Toast.LENGTH_SHORT).show();
                    editConfirmPassword.setError("Password confirmation is required.");
                    editConfirmPassword.requestFocus();
                } else if (!_PWD.equals(_CONFIRM_PWD)) {
                    Toast.makeText(getContext(),
                            "Please enter same password.", Toast.LENGTH_SHORT).show();
                    editConfirmPassword.setError("Password confirmation is required.");
                    editConfirmPassword.requestFocus();
                    // Clean the entered passwords
                    editPassword.clearComposingText();
                    editConfirmPassword.clearComposingText();
                } else if (user_role.equals("SELLER") && TextUtils.isEmpty(_STORE_NAME)) {
                    Toast.makeText(getContext(),
                            "Please enter your store name.", Toast.LENGTH_SHORT).show();
                    editStoreName.setError("A store name is required, but you can change it at any time.");
                    editStoreName.requestFocus();

//                } else if (!checkBoxAgree.isChecked()) {
//                    Toast.makeText(getContext(),
//                            "Please confirm term and policy.", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(_EMAIL, _PWD);
                    storeName = _STORE_NAME;
                    Log.d("Test store name",storeName+"!");
//                    // Create progress bar
//                    loadingPB = view.findViewById(R.id.progress_circular_loading);
//                    if (isProgressVisible) {
//                        loadingPB.setVisibility(View.GONE);
//                        isProgressVisible = false;
//                    } else {
//                        isProgressVisible = true;
//                        loadingPB.setVisibility(View.VISIBLE);
//                    }

                }
            }
        });

        // To cancel and back to login page
        btnCancel = view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        // [START FACEBOOK REGISTRATION]
        // Firebase Authentication
        auth = FirebaseAuth.getInstance();
        btn_register_facebook = view.findViewById(R.id.btn_register_facebook);
        fb = view.findViewById(R.id.fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == fb) {

                    _STORE_NAME = editStoreName.getText().toString();
                    if(TextUtils.isEmpty(_STORE_NAME)){
                        Toast.makeText(getContext(),
                                "Please enter your store name.", Toast.LENGTH_SHORT).show();
                        editStoreName.setError("A store name is required, you can change it in anytime.");
                        editStoreName.requestFocus();
                    }else {
                        storeName = _STORE_NAME;
                        btn_register_facebook.performClick();
                    }

                }
            }
        });
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
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
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure" + task.getException(), task.getException());
                            Toast.makeText(getContext(), "Authentication failed. Your email has been registered, please login your account",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void registerUser(String email, String password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
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

                            } else {
                                // Handle failure
                                Toast.makeText(getContext(), "Cannot register a new user, please try again latter.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getContext(), "Cannot login with this email, please try again latter.", Toast.LENGTH_SHORT).show();
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
                if (user_role.equals(CUSTOMER)) {
                    Customer newCustomer = new Customer(firebaseUser.getUid());
                    ExtractingCustomerReference(firebaseUser, newCustomer);
                } else if (user_role.equals(SELLER)) {
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
                    // Create a new store
                    DatabaseReference storeRef = mFirebaseDatabase.child(firebaseUser.getUid()).child("storeInfo");
                    Store store = new Store(storeName);
                    storeRef.setValue(store);

                    // Send Verification Email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(getContext(), "Seller registered successfully!", Toast.LENGTH_SHORT).show();
                    if (!(getActivity() instanceof CustomerHomePageActivity)) {
                        startActivity(new Intent(getActivity(), SellerHomePageActivity.class));
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(getContext(), "Failed to write Seller data! because " + task.getException(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to write Seller data - unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void ExtractingCustomerReference(FirebaseUser firebaseUser, Customer newCustomer) {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Customer");
        Toast.makeText(getContext(), firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
        mFirebaseDatabase.child(firebaseUser.getUid()).setValue(newCustomer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    // Send Verification Email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(getContext(), "Customer registered successfully!", Toast.LENGTH_SHORT).show();
                    if (!(getActivity() instanceof CustomerHomePageActivity)) {
                        startActivity(new Intent(getActivity(), CustomerHomePageActivity.class));
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(getContext(), "Failed to write Customer data! because " + task.getException(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Failed to write Customer data - unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    // [END FIREBASE REGISTRATION]


    @Override
    public void onStart() {
        super.onStart();
        LoginManager.getInstance().logOut();
    }
}