package com.csis4495_cmk.webuy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.dialog.CustomerOrderStatusDialog;
import com.csis4495_cmk.webuy.models.Customer;
import com.csis4495_cmk.webuy.models.Seller;
import com.csis4495_cmk.webuy.models.User;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.expandablelayout.ExpandableAnimation;
import com.skydoves.expandablelayout.ExpandableLayout;

public class CustomerProfileFragment extends Fragment {

    private TextView labelUsername;
    private Button logoutButton, btnTest;
    private ImageView imgUserProfile;
    private ImageButton btnSetting, btnViewAll, btnViewPending, btnViewPayment, btnViewPackaging, btnViewShipped, btnViewDelivered;
    private ExpandableLayout expProfile, expOrderStatus, expWishList, expMoreFunctions;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    DatabaseReference reference;
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    StorageReference imageRef;

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

        if (firebaseUser != null){
            // Notify user if they have not verified email
            checkIfEmailVerified(firebaseUser);
            showUserProfile(firebaseUser);
        } else {
            Toast.makeText(requireActivity(), "displayUserProfileInfo:failure", Toast.LENGTH_LONG).show();
        }

        // Firebase instance
        auth = FirebaseAuth.getInstance();

        imgUserProfile = view.findViewById(R.id.img_user_profile);
        imgUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUploadPicSelectionDialog();
            }
        });

        // Expendable layout
        expOrderStatus = view.findViewById(R.id.expandableLayout_order);
        expProfile = view.findViewById(R.id.expandableLayout_profile);
        expWishList = view.findViewById(R.id.expandableLayout_saves);
        expMoreFunctions = view.findViewById(R.id.expandableLayout_more_function);

        setupExpandableLayoutClickListener(expOrderStatus);
        setupExpandableLayoutClickListener(expProfile);
        setupExpandableLayoutClickListener(expWishList);
        setupExpandableLayoutClickListener(expMoreFunctions);

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


    private void setupExpandableLayoutClickListener(final ExpandableLayout expandableLayout) {
        expandableLayout.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandableLayout.isExpanded()) {
                    expandableLayout.collapse();
                } else {
                    expandableLayout.expand();
                }
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

    private void showUploadPicSelectionDialog() {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Inflate the layout for the dialog
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_image, null);
        builder.setView(dialogView);

        // Get references to dialog views
        ImageButton UploadFromDrive = dialogView.findViewById(R.id.btn_upload_image);
        ImageButton UploadFromCloud = dialogView.findViewById(R.id.btn_select_default_image);
        ImageButton btnCancel = dialogView.findViewById(R.id.btn_cancel);

        UploadFromDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("image/*");
            }
        });

        UploadFromCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (alertDialog.getWindow() != null) {
            layoutParams.copyFrom(alertDialog.getWindow().getAttributes());
        }

        int heightDp = 250;
        float density = getResources().getDisplayMetrics().density;
        int heightPixel = (int) (heightDp * density);

        layoutParams.height = heightPixel;

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setAttributes(layoutParams);
        }
    }

    private void CreatePathBaseOnUserRole(Uri uri) {
        String uid = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("User").child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                String userRole = user.getRole();
                StorageReference storageRef = storage.child("ProfileImages");

                if(userRole.equals("Customer")){
                    imageRef = storageRef.child("customerImages");
                }else{
                    imageRef = storageRef.child("sellerImages");
                }

                // Start the upload here
                uploadImage(uri);
                PassDataToFirebase(userRole);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "checkingCurrentUserRole(): canceled", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage(Uri uri) {
        // Check that the imageRef is not null
        if (imageRef != null) {
            String uid = firebaseUser.getUid();
            StorageReference fileRef = imageRef.child(uid + ".jpg");

            UploadTask uploadTask = fileRef.putFile(uri);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("Test Upload Profile Img: ", "ActivityResultLauncher: onFailure()");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("Test Upload Profile Img: ", "ActivityResultLauncher: onSuccess()");
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.d("Test Upload Profile Img: ", "Uri: "+uri.toString());
                            Glide.with(getContext())
                                    .load(uri.toString())
                                    .circleCrop() // or .transform(new CircleCrop())
                                    .into(imgUserProfile);

                        }
                    });
                }
            });

        } else {
            Toast.makeText(getContext(), "Image uploaded failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void PassDataToFirebase(String userRole) {
        String uid = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference(userRole).child(uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Customer customer = snapshot.getValue(Customer.class);
                Seller seller = snapshot.getValue(Seller.class);
                if(customer != null){

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    CreatePathBaseOnUserRole(uri);
                }
            });


    }