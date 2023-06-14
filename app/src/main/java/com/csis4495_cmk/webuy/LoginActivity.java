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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private final String TAG = LoginActivity.class.getSimpleName();
    private EditText edit_email, edit_password;
    private Button btn_login, btn_register, btn_forgotPassword;
    private String user_role = "";
    private final String CUSTOMER = "customer";
    private final String SELLER = "seller";
        private FirebaseAuth auth;
    private LoginButton fbLoginButton;
    private CallbackManager callbackManager = CallbackManager.Factory.create();
    private FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
    private DatabaseReference mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Edit text
        edit_email = findViewById(R.id.edit_loginEmail);
        edit_password = findViewById(R.id.edit_loginPassword);

        // When user clicked register button : open registration page
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RoleSelectionActivity.class)));

        // When user clicked forget password button : open forget password page
        btn_forgotPassword = findViewById(R.id.btn_forgotPassword);
        btn_forgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        // When user clicked login Button, varify email and login
        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(v -> {
            String loginEmail = edit_email.getText().toString();
            String loginPassword = edit_password.getText().toString();

            if (TextUtils.isEmpty(loginEmail)) {
                Toast.makeText(LoginActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                edit_email.setError("Email is required.");
                edit_email.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(loginEmail).matches()) {
                Toast.makeText(LoginActivity.this,
                        "Please enter your email.", Toast.LENGTH_SHORT).show();
                edit_email.setError("Valid email is required.");
                edit_email.requestFocus();
            } else if (TextUtils.isEmpty(loginPassword)) {
                Toast.makeText(LoginActivity.this,
                        "Please enter your password.", Toast.LENGTH_SHORT).show();
                edit_password.setError("Password is required.");
                edit_password.requestFocus();
            } else {
                // If everything is find, do login
                loginUser(loginEmail, loginPassword);
            }
        });

        // [START FACEBOOK LOGIN]
        auth = FirebaseAuth.getInstance();
        fbLoginButton = findViewById(R.id.btn_login_facebook);
        fbLoginButton.setPermissions("email");
        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                FirebaseUser firebaseUser = auth.getCurrentUser();
                if(firebaseUser == null){
                    showRoleSelectionAlertDialog(loginResult);
                }else{
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }
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
                            Toast.makeText(LoginActivity.this, "Authentication failed. Your email has been registered, please login your account",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
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
                    Toast.makeText(LoginActivity.this, "Seller registered successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, SellerHomePageActivity.class);
                    // Prevent user back to Registration activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(LoginActivity.this, "Failed to write Seller data! because " + task.getException(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to write Seller data - unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void ExtractingCustomerReference(FirebaseUser firebaseUser, Customer newCustomer) {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Customer");
        Toast.makeText(LoginActivity.this, firebaseUser.getUid(), Toast.LENGTH_SHORT).show();
        mFirebaseDatabase.child(firebaseUser.getUid()).setValue(newCustomer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    // Send Verification Email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(LoginActivity.this, "Customer registered successfully!", Toast.LENGTH_SHORT).show();
                    // Open User homepage once successfully register
                    Intent intent = new Intent(LoginActivity.this, CustomerHomePageActivity.class);
                    // Prevent user back to Registration activity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    if (task.getException() != null) {
                        Toast.makeText(LoginActivity.this, "Failed to write Customer data! because " + task.getException(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Failed to write Customer data - unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    // [END FIREBASE REGISTRATION]

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    // [END FACEBOOK LOGIN]

    // [START FIREBASE LOGIN]
    private void loginUser(String loginEmail, String loginPassword) {
        auth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Get instance of current user
                    FirebaseUser firebaseUser = auth.getCurrentUser();
                    // Check if the user is verified before user can access their profile
                    if (firebaseUser.isEmailVerified()) {
                        // Query user role from database
                        String uid = firebaseUser.getUid();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        databaseReference.child("User").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    DataSnapshot dataSnapshot = task.getResult();
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user != null) {
                                        String user_role = user.getRole();
                                        switch (user_role) {
                                            case "customer":
                                                startActivity(new Intent(LoginActivity.this, CustomerHomePageActivity.class));
                                                finish();
                                                break;
                                            case "seller":
                                                startActivity(new Intent(LoginActivity.this, TestPageActivity.class));
                                                finish();
                                                break;
                                            default:
                                                // Handle case where user role is unknown
                                                break;
                                        }
                                    } else {
                                        // Handle case where user object is null
                                    }
                                } else {
                                    // Handle error
                                }
                            }
                        });
                    } else {
                        firebaseUser.sendEmailVerification();
                        auth.signOut();
                        showAlertDialog();
                    }
                } else {
                    // Handle authentication failure
                }
            }
        });
    }
    // [END FIREBASE LOGIN]

    // [START EMAIL VERIFY DIALOG]
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
    // [END EMAIL VERIFY DIALOG]

    // [START SHOW ROLE SELECTION DIALOG]
    private void showRoleSelectionAlertDialog(LoginResult loginResult) {
        final String[] dialog_list = {"Customer", "Seller"};
        final int[] roleIndex = {-1};

        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Please choose a role for registration.");
        builder.setSingleChoiceItems(dialog_list, roleIndex[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                roleIndex[0] = which;
                switch (roleIndex[0]) {
                    case 0:
                        user_role = CUSTOMER;
                        break;
                    case 1:
                        user_role = SELLER;
                        break;
                    default:
                        Toast.makeText(LoginActivity.this, "Please select one of the options", Toast.LENGTH_SHORT).show();
                        user_role = ""; // reset user_role if no valid selection
                        return; // If no valid selection, don't dismiss the dialog or proceed
                }
            }
        });


        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleFacebookAccessToken(loginResult.getAccessToken());
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

        // Show the AlertDialog
        alertDialog.show();

        // Create layout parameters for the AlertDialog
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());

        // Set the width and height for the AlertDialog
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; // This will make the AlertDialog take up the full width of the screen
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; // This will make the height of the AlertDialog wrap to its content

        // Apply the layout parameters to the AlertDialog
        alertDialog.getWindow().setAttributes(layoutParams);
    }

    // [CLOSE SHOW ROLE SELECTION DIALOG]

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = dataSnapshot.getValue(User.class);

                        if (user != null) {
                            String role = user.getRole();
                            if (role.equals("customer")) {
                                startActivity(new Intent(LoginActivity.this, CustomerHomePageActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(LoginActivity.this, SellerHomePageActivity.class));
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Do nothing
                }
            });
            // Check if user is signed in (non-null) and update UI accordingly.
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
    }
}