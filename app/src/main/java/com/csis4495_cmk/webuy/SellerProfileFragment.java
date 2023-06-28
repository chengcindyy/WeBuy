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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.models.Seller;
import com.csis4495_cmk.webuy.models.Store;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

public class SellerProfileFragment extends Fragment {

    private Button logoutButton, btnTest, btnUpdate;
    private ImageView imgStoreImage;
    private ExpandableLayout expStoreInfo, expDeliveryInfo, expPaymentInfo, expMoreFunctions;
    private TextInputLayout editStoreId, editStoreName, editEmail, editBusinessNumber, editAddress, editCity, editPostalCode;
    private AutoCompleteTextView editProvince, editCountry;
    private AlertDialog alertDialog;
    String _USERNAME, _NAME, _EMAIL, _PHONE, _STORE,_ADDRESS, _CITY, _PROVINCE, _POSTCODE, _PIC, _COUNTRY;
    List<Store> storeInfo;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    String uid = firebaseUser.getUid();
    DatabaseReference reference;
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    StorageReference imageRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_profile, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // References
        editStoreId = view.findViewById(R.id.edit_store_id);
        editStoreName = view.findViewById(R.id.edit_store_name);
        editEmail = view.findViewById(R.id.edit_email);
        editBusinessNumber = view.findViewById(R.id.edit_Business_number);
        editAddress = view.findViewById(R.id.edit_address);
        editCity = view.findViewById(R.id.edit_city);
        editPostalCode = view.findViewById(R.id.edit_postal_code);
        editProvince = view.findViewById(R.id.autoComplete_province);
        setProvinceAutoCompleteAdapter(editProvince);
        editCountry = view.findViewById(R.id.autoComplete_country);
        setCountryAutoCompleteAdapter(editCountry);
        //        imgStoreImage = view.findViewById(R.id.img_user_profile);
//        imgUserProfile.setOnClickListener(view1 -> showUploadPicSelectionDialog());

        if (firebaseUser != null){
            // Notify user if they have not verified email
            checkIfEmailVerified(firebaseUser);
            showSellerProfileInfo(firebaseUser);

        } else {
            Toast.makeText(getContext(), "Loading user problem!", Toast.LENGTH_LONG).show();
        }

        // Expendable layout
        expDeliveryInfo = view.findViewById(R.id.expandableLayout_order);
        expStoreInfo = view.findViewById(R.id.expandableLayout_profile);
        expPaymentInfo = view.findViewById(R.id.expandableLayout_saves);
        expMoreFunctions = view.findViewById(R.id.expandableLayout_more_function);

        // Set parent layout title and icon
        List<Pair<ExpandableLayout, Pair<Integer, String>>> list = new ArrayList<>();
        list.add(new Pair<>(expStoreInfo, new Pair<>(R.drawable.baseline_edit_note_24, "Store information")));
        list.add(new Pair<>(expDeliveryInfo, new Pair<>(R.drawable.baseline_local_shipping_24, "Delivery Setting")));
        list.add(new Pair<>(expPaymentInfo, new Pair<>(R.drawable.baseline_payment_24, "Payment Setting")));
        list.add(new Pair<>(expMoreFunctions, new Pair<>(R.drawable.baseline_more_horiz_24, "More Functions")));

        for (Pair<ExpandableLayout, Pair<Integer, String>> pair : list) {
            ExpandableLayout expandableLayout = pair.first;
            View parentLayout = expandableLayout.getParentLayout();
            ImageView expandableIcon = parentLayout.findViewById(R.id.txv_expandable_layout_icon);
            TextView expandableTxv = parentLayout.findViewById(R.id.txv_expandable_layout_title);

            // Set icon & title
            expandableIcon.setImageResource(pair.second.first);
            expandableTxv.setText(pair.second.second);

            // Set onClick listener
            setupExpandableLayoutClickListener(expandableLayout);
        }

        // Update profile
//        btnUpdate = view.findViewById(R.id.btn_update);
//        btnUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                updateSellerProfile(firebaseUser);
//            }
//        });

        // For Testing
        btnTest = view.findViewById(R.id.btn_test_page);
        btnTest.setOnClickListener(view12 -> Navigation.findNavController(view12).navigate(R.id.action_to_testPageFragment));// End Testing

        // Logout
        logoutButton = view.findViewById(R.id.btn_logout);
        SetLogoutOnClickListener(logoutButton);
    }



    private void showSellerProfileInfo(FirebaseUser firebaseUser) {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Seller");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Seller seller = snapshot.getValue(Seller.class);
                if(seller != null) {

                    _NAME = seller.getName();
                    _PHONE = seller.getPhone();
                    _EMAIL = firebaseUser.getDisplayName();


//                    _ADDRESS = seller.getAddress();
//                    _CITY = seller.getCity();
//                    _PROVINCE = seller.getProvince();
//                    _COUNTRY = seller.getCountry();
//                    _POSTCODE = seller.getPostalCode();

                    editStoreName.getEditText().setText(_NAME);
                    editBusinessNumber.getEditText().setText(_PHONE);
                    editEmail.getEditText().setText(_EMAIL);
//                    editAddress.getEditText().setText(_ADDRESS);
//                    editCity.getEditText().setText(_CITY);
//                    editProvince.setText(_PROVINCE);
//                    editCountry.setText(_COUNTRY);
//                    editPostalCode.getEditText().setText(_POSTCODE);

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
//    private void updateSellerProfile(FirebaseUser firebaseUser) {
//        // Obtain the entered data
//        _NAME = editStoreName.getEditText().getText().toString();
//        _PHONE = editBusinessNumber.getEditText().getText().toString();
//        _EMAIL = editEmail.getEditText().toString();
//
//        Seller seller = new Seller(_NAME,_PHONE,_EMAIL);
//
//        String userId = firebaseUser.getUid();
//        reference = FirebaseDatabase.getInstance().getReference("Seller");
//        reference.child(userId).setValue(seller).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//                    // Setting new fields
//                    UserProfileChangeRequest updatableProfileField = new UserProfileChangeRequest.Builder().setDisplayName(_USERNAME).build();
//                    firebaseUser.updateProfile(updatableProfileField);
//                    Toast.makeText(getContext(), "Data update successfully", Toast.LENGTH_SHORT).show();
////
//                } else {
//                    try {
//                        throw task.getException();
//                    } catch (Exception e){
//                        Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                }
//            }
//        });
//    }

    private void SetLogoutOnClickListener(Button logoutButton) {
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

    private void setProvinceAutoCompleteAdapter(AutoCompleteTextView editProvince) {
        // States (Canada)
        String[] states = new String[]{"Alberta", "British Columbia", "Manitoba", "New Brunswick",
                "Newfoundland and Labrador", "Northwest Territories", "Nova Scotia", "Nunavut",
                "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan", "Yukon"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, states);
        editProvince.setAdapter(stateAdapter);
        editProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCountryAutoCompleteAdapter(AutoCompleteTextView editCountry) {
        // Countries
        String[] countries = new String[]{"Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe", "Palestine"};
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, countries);
        editCountry.setAdapter(countryAdapter);
        editCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
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
        ImageButton UploadImage = dialogView.findViewById(R.id.btn_upload_image);
        ImageButton btnCancel = dialogView.findViewById(R.id.btn_cancel);

        UploadImage.setOnClickListener(view -> mGetContent.launch("image/*"));

        //Create AlertDialog
        alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();

        btnCancel.setOnClickListener(v -> alertDialog.dismiss());

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

    private void createFireStoragePath(Uri uri) {
        StorageReference storageRef = storage.child("ProfileImages");
        imageRef = storageRef.child("sellerImages");
        uploadImageToFireStorage(uri);
    }

    private void uploadImageToFireStorage(Uri uri) {
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
                            reference = FirebaseDatabase.getInstance().getReference();
                            Log.d("Test Upload Profile Img: ", "Uri: "+uri.toString());
                            Glide.with(getContext())
                                    .load(uri.toString())
                                    .circleCrop() // or .transform(new CircleCrop())
                                    .into(imgStoreImage);

                            UserProfileChangeRequest request = new UserProfileChangeRequest.
                                    Builder().setPhotoUri(uri).build();
                            firebaseUser.updateProfile(request);

                            // Write the download URL to Firebase Database
                            reference.child("Seller").child(uid).child("profilePic").setValue(uri.toString());
                            if (alertDialog != null && alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                        }
                    });
                    Toast.makeText(getContext(), "Image upload successfully", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getContext(), "Image uploaded failed.", Toast.LENGTH_SHORT).show();
        }
    }



    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    createFireStoragePath(uri);
                }
            });
}
