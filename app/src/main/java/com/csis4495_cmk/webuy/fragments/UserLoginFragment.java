package com.csis4495_cmk.webuy.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.Toast;

import com.csis4495_cmk.webuy.activities.CustomerHomePageActivity;
import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.activities.SellerHomePageActivity;
import com.csis4495_cmk.webuy.models.Customer;
import com.csis4495_cmk.webuy.models.Seller;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserLoginFragment extends Fragment {

    private final String TAG = MainActivity.class.getSimpleName();
    private EditText edit_email, edit_password;
    private Button btnLogin, btn_register, btn_forgotPassword, fb;
    private String user_role = "";
    private final String CUSTOMER = "customer";
    private final String SELLER = "seller";
    private boolean isEmailExisted;
    private FirebaseAuth auth;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    private FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
    private DatabaseReference mFirebaseDatabase;
    private LoginResult mLoginResult;

    private FirebaseUser firebaseUser;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_login, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(UserLoginFragment.this);

        // When user clicked register button : open registration page
        btn_register = view.findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_userLoginFragment_to_roleSelectionFragment);
            }
        });

        // When user clicked login Button, verify email and login
        btnLogin = view.findViewById(R.id.btn_loginWithId);
        btnLogin.setOnClickListener(v -> {
            showLoginDialog();
        });

        // [START FACEBOOK LOGIN]
        auth = FirebaseAuth.getInstance();
        fbLoginButton = view.findViewById(R.id.btn_login_facebook);
        fb = view.findViewById(R.id.fb);
        fb.setOnClickListener(view1 -> {
            if (view1 == fb) {
                fbLoginButton.performClick();
            }
        });
        fbLoginButton.setPermissions("email");
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mLoginResult = loginResult;
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
                            firebaseUser = auth.getCurrentUser();
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                showRoleSelectionAlertDialog();
                            } else {
                                // Returning user, go to home page
                                if (firebaseUser != null) {
                                    loginUser(firebaseUser);
                                } else {
                                    // Handle case where user object is null
                                    Log.d(TAG,"firebaseUser is null");
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure" + task.getException(), task.getException());
                            Toast.makeText(getContext(), "Authentication failed. Your email has been registered, please login your account",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END FACEBOOK LOGIN]

    // [START FIREBASE REGISTRATION]
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
                    // Send Verification Email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(getContext(), "Seller registered successfully!", Toast.LENGTH_SHORT).show();
                    if(getActivity() != null) {
                        getActivity().finish();
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
                    // Open User homepage once successfully register
                    if(getActivity() != null) {
                        getActivity().finish();
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

    // [START FIREBASE LOGIN]
    private void loginUserWithFirebase(String loginEmail, String loginPassword) {
        auth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Get instance of current user
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    loginUser(firebaseUser);

                } else {
                    if(task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(getContext(), "The email address is not registered. Please check and try again!", Toast.LENGTH_SHORT).show();
                    } else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getContext(), "Incorrect password. Please check and try again!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Login Failed, please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void loginUser(FirebaseUser firebaseUser) {
        // Check if the user is verified before user can access their profile
        if (firebaseUser.isEmailVerified()) {
            Log.d(TAG, "Email verified");
            // Query user role from database
            String uid = firebaseUser.getUid();
            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
            mFirebaseDatabase.child("User").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DataSnapshot dataSnapshot = task.getResult();
                        User user = dataSnapshot.getValue(User.class);
                        if (user != null) {
                            String user_role = user.getRole();
                            switch (user_role) {
                                case "customer":
                                    Toast.makeText(getContext(), "Login Customer", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), CustomerHomePageActivity.class));
                                    //navController.navigate(R.id.action_userLoginFragment_to_customerHomeFragment);
                                    if(getActivity() != null) { getActivity().finish(); }
                                    break;
                                case "seller":
                                    Toast.makeText(getContext(), "Login Seller", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getActivity(), SellerHomePageActivity.class));
                                    if(getActivity() != null) { getActivity().finish(); }
                                    break;
                                default:
                                    // Handle case where user role is unknown
                                    Log.e(TAG,"User role is unknown");
                                    break;
                            }
                        } else {
                            // Handle case where user object is null
                            Log.e(TAG,"User object in realDB is null");
                        }
                    } else {
                        // Handle error
                        Log.e(TAG,"realDB connection failed");
                    }
                }
            });
        } else {
            firebaseUser.sendEmailVerification();
            auth.signOut();
            showAlertDialog();
        }
    }
    private void userEnteredDataVerification(String loginEmail, String loginPassword, AlertDialog alertDialog) {
        if (TextUtils.isEmpty(loginEmail)) {
            Toast.makeText(getContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
            edit_email.setError("Email is required.");
            edit_email.requestFocus();
            //return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
            edit_email.setError("Valid email is required.");
            edit_email.requestFocus();
            //return false;
        } else if (TextUtils.isEmpty(loginPassword)) {
            Toast.makeText(getContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
            edit_password.setError("Password is required.");
            edit_password.requestFocus();

           //return false;
        } else {
            //checkEmailExists(loginEmail);
            boolean isVerified;
            auth.fetchSignInMethodsForEmail(loginEmail)
                    .addOnCompleteListener(task -> {
                        boolean emailExists = task.getResult().getSignInMethods() != null &&
                                !task.getResult().getSignInMethods().isEmpty();
                        handleEmailExists(emailExists);
                        loginUserWithFirebase(loginEmail,loginPassword);
                        alertDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        if (e instanceof FirebaseAuthInvalidUserException) {
                            // Email is not registered
                            handleEmailExists(false);
                        } else {
                            // Handle other exceptions
                            handleEmailExists(false);
                            Log.d("Login", e.toString());
                        }
                        Toast.makeText(getContext(), "Your WeBuy account could not be found.", Toast.LENGTH_SHORT).show();
                        edit_email.setError("Please ensure that your email is correct.");
                        edit_email.requestFocus();
                    });
        }
    }

    private void handleEmailExists(boolean emailExists) {
        if (emailExists) {
            // Email exists in Firebase Authentication
            Log.d("Login","Email exists in Firebase Authentication.");
            isEmailExisted = true;
        } else {
            // Email does not exist in Firebase Authentication
            Log.d("Login","Email does not exist in Firebase Authentication.");
            isEmailExisted = false;

        }
    }
    // [END FIREBASE LOGIN]

    // [START EMAIL VERIFY DIALOG]
    private void showAlertDialog() {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
    // [END EMAIL VERIFY DIALOG]

    // [START SHOW ROLE SELECTION DIALOG]
    private void showRoleSelectionAlertDialog() {
         //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Oops!");
        builder.setMessage("It seems you haven't visited us before, let's get you started with a new account!");

        builder.setPositiveButton("Proceed to the registration page", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginManager.getInstance().logOut();
                navController.navigate(R.id.roleSelectionFragment);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                LoginManager.getInstance().logOut();
                dialogInterface.dismiss();
            }
        });

        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }
    // [CLOSE SHOW ROLE SELECTION DIALOG]

    // [START SHOW LOGIN DIALOG]
    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Login");

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_user_login, null);
        builder.setView(dialogView);

        // Get references to dialog views
        edit_email = dialogView.findViewById(R.id.edit_loginEmail);
        edit_password = dialogView.findViewById(R.id.edit_loginPassword);

        String _email = edit_email.getText().toString();
        String _password = edit_password.getText().toString();

        // Set up login button click listener
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        // Set up cancel button click listener
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginEmail = edit_email.getText().toString();
                String loginPassword = edit_password.getText().toString();

                userEnteredDataVerification(loginEmail, loginPassword, alertDialog);
//                if (userEnteredDataVerification(loginEmail, loginPassword, alertDialog)) {
//                    alertDialog.dismiss();
//                    //loginUserWithFirebase(loginEmail, loginPassword);
//                }
            }
        });

        // When user clicked forget password button : open forget password fragment
        btn_forgotPassword = dialogView.findViewById(R.id.btn_forgotPassword);
        btn_forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                navController.navigate(R.id.action_userLoginFragment_to_forgotPasswordFragment);

            }
        });
    }
    // [END SHOW LOGIN DIALOG]

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart:success");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        checkIfUserLoggedIn(currentUser, navController);
        //FirebaseAuth.getInstance().signOut();
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume:success");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        checkIfUserLoggedIn(currentUser, navController);
    }
    private void checkIfUserLoggedIn(FirebaseUser currentUser, NavController navController) {
        Log.d(TAG, "checkIfUserLoggedIn: Current user:" + currentUser);
        if (currentUser != null) {
            String uid = currentUser.getUid();
            Log.d(TAG, "checkIfUserLoggedIn: Uid:" + uid);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);


                        if (user != null) {
                            String role = user.getRole();
                            if (role.equals("customer")) {
                                if (!(getActivity() instanceof CustomerHomePageActivity)) {
                                    Log.d(TAG, "customer");
                                    startActivity(new Intent(getActivity(), CustomerHomePageActivity.class));
                                    if (getActivity() != null) {
                                        getActivity().finish();
                                    }
                                }
                                Log.d(TAG, "is customer");
                                navController.navigate(R.id.customerHomeFragment);

                            } else {
                                if (!(getActivity() instanceof SellerHomePageActivity)) {
                                    startActivity(new Intent(getActivity(), SellerHomePageActivity.class));
                                    if (getActivity() != null) {
                                        getActivity().finish();
                                    }
                                }
                                navController.navigate(R.id.sellerHomeFragment);
                            }
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
    }

}