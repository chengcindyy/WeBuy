package com.csis4495_cmk.webuy;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.models.User;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CustomerProfileFragment extends Fragment {

    private TextView labelUsername, labelRole;
    private Button logoutButton, btnTest;
    private ImageButton btnSetting, btnViewAll, btnViewPending, btnViewPayment, btnViewPackaging, btnViewShipped, btnViewDelivered;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // For Testing
        btnTest = view.findViewById(R.id.btn_test_page);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_to_testPageFragment);
            }
        });// End Testing

        // show profile info
        labelUsername = view.findViewById(R.id.txv_username);
        labelRole = view.findViewById(R.id.txv_member_role);

        if (firebaseUser != null){
            // Notify user if they have not verified email
            checkIfEmailVerified(firebaseUser);
            showUserProfile(firebaseUser);
        } else {
            Toast.makeText(requireActivity(), "displayUserProfileInfo:failure", Toast.LENGTH_LONG).show();
        }

        // Firebase instance
        auth = FirebaseAuth.getInstance();

        // Display order status
        btnViewAll = view.findViewById(R.id.btn_view_all_order);
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderStatusDialog("viewAll");
            }
        });

        btnViewPending = view.findViewById(R.id.btn_view_pending);
        btnViewPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderStatusDialog("viewPending");
            }
        });

        btnViewPayment = view.findViewById(R.id.btn_view_confirmed);
        btnViewPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderStatusDialog("viewConfirmed");
            }
        });

        btnViewPackaging = view.findViewById(R.id.btn_view_processed);
        btnViewPackaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderStatusDialog("viewProcessed");
            }
        });

        btnViewShipped = view.findViewById(R.id.btn_view_shipped);
        btnViewShipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderStatusDialog("viewShipped");
            }
        });

        btnViewDelivered = view.findViewById(R.id.btn_view_canceled);
        btnViewDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderStatusDialog("viewCanceled");
            }
        });

        // Setting
        btnSetting = view.findViewById(R.id.btn_setting);
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Logout
        logoutButton = view.findViewById(R.id.btn_logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                LoginManager.getInstance().logOut();
                Toast.makeText(requireActivity(),"Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireActivity(), MainActivity.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

    }

    private void openOrderStatusDialog(String tab) {
        CustomerOrderStatusDialog dialog = new CustomerOrderStatusDialog();
        dialog.show(getParentFragmentManager(), tab);
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
                    Toast.makeText(requireActivity(), "something went wrong! " +
                            "Show profile was canceled", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "something went wrong! " +
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
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
            Toast.makeText(requireActivity(),"Logged out successfully, please login again", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(requireActivity(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish(); // if you want to finish the current activity
        });

        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }


}