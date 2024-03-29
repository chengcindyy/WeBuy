package com.csis4495_cmk.webuy.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.adapters.recyclerview.CustomerProfileWishlistItemsAdapter;
import com.csis4495_cmk.webuy.dialog.CustomerOrderStatusDialog;
import com.csis4495_cmk.webuy.models.Customer;
import com.csis4495_cmk.webuy.models.User;
import com.csis4495_cmk.webuy.models.Wishlist;
import com.csis4495_cmk.webuy.viewmodels.CustomerWishlistViewModel;
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
import com.skydoves.expandablelayout.OnExpandListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class CustomerProfileFragment extends Fragment {

    private TextView labelUsername;
    private Button logoutButton, btnUpdate;
    private ImageView imgUserProfile;
    private ImageButton btnChat, btnViewPending, btnViewConfirmed, btnViewProcessing, btnViewShipped, btnViewCanceled;
    private ExpandableLayout expProfile, expOrderStatus, expWishList, expMoreFunctions;
    private DatePickerDialog picker;
    private TextInputLayout editName, editEmail, editPhone, editAddress, editCity, editPostalCode;
    private EditText editBirthday;
    private AutoCompleteTextView editProvince;
    private AlertDialog alertDialog;
    Map<String, Wishlist> _FAVORITE;
    RecyclerView recyclerView;
    CustomerProfileWishlistItemsAdapter customerProfileWishlistItemsAdapter;
    List<Wishlist> wishlistDisplayList;
    CustomerWishlistViewModel wishListViewModel;
    String _USERNAME, _NAME, _EMAIL, _PHONE, _ADDRESS, _CITY, _PROVINCE, _POSTCODE, _PIC, _DOB;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    DatabaseReference reference;
    StorageReference storage = FirebaseStorage.getInstance().getReference();
    StorageReference imageRef;
    private NavController navController;

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

        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(CustomerProfileFragment.this);

        // References
        editName = view.findViewById(R.id.edit_name);
        editEmail = view.findViewById(R.id.edit_email);
        editPhone = view.findViewById(R.id.edit_phone);
        editAddress = view.findViewById(R.id.edit_address);
        editCity = view.findViewById(R.id.edit_city);
        editPostalCode = view.findViewById(R.id.edit_postal_code);
        imgUserProfile = view.findViewById(R.id.img_user_profile);
        editBirthday = view.findViewById(R.id.edit_birthday);
        SetBirthdayOnClickListener(editBirthday);
        editProvince = view.findViewById(R.id.autoComplete_province);
        setProvinceAutoCompleteAdapter(editProvince);
        labelUsername = view.findViewById(R.id.txv_username);
        imgUserProfile.setOnClickListener(view1 -> showUploadPicSelectionDialog());

        if (firebaseUser != null){
            // Notify user if they have not verified email
//            checkIfEmailVerified(firebaseUser);
            showCustomerProfileTitle(firebaseUser);
            showCustomerProfileMoreInfo(firebaseUser);

        } else {
            Toast.makeText(getContext(), "Loading user problem!", Toast.LENGTH_LONG).show();
        }

        // Expendable layout
        expOrderStatus = view.findViewById(R.id.expandableLayout_order);
        expProfile = view.findViewById(R.id.expandableLayout_profile);
        expWishList = view.findViewById(R.id.expandableLayout_saves);
        expMoreFunctions = view.findViewById(R.id.expandableLayout_more_function);

        // Set parent layout title and icon
        List<Pair<ExpandableLayout, Pair<Integer, String>>> list = new ArrayList<>();
        list.add(new Pair<>(expOrderStatus, new Pair<>(R.drawable.baseline_list_24, "View Order")));
        list.add(new Pair<>(expProfile, new Pair<>(R.drawable.baseline_edit_note_24, "Edit Profile")));
        list.add(new Pair<>(expWishList, new Pair<>(R.drawable.baseline_favorite_border_24, "Your Wish List")));
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

        // Set order status
        btnViewPending = view.findViewById(R.id.btn_view_pending);
        btnViewConfirmed = view.findViewById(R.id.btn_view_confirmed);
        btnViewProcessing = view.findViewById(R.id.btn_view_processed);
        btnViewShipped = view.findViewById(R.id.btn_view_shipped);
        btnViewCanceled = view.findViewById(R.id.btn_view_canceled);
        SetOrderStatusOnClickListener(btnViewPending, 0);
        SetOrderStatusOnClickListener(btnViewConfirmed, 1);
        SetOrderStatusOnClickListener(btnViewProcessing, 3);
        SetOrderStatusOnClickListener(btnViewShipped, 4);
        SetOrderStatusOnClickListener(btnViewCanceled, -1);

        // Get data from viewModel
        wishListViewModel = new ViewModelProvider(requireActivity()).get(CustomerWishlistViewModel.class);

        // Set wishlist recyclerView
        wishlistDisplayList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView_profile_wishlist);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        customerProfileWishlistItemsAdapter = new CustomerProfileWishlistItemsAdapter(getContext(), wishListViewModel, wishlistDisplayList);
        recyclerView.setAdapter(customerProfileWishlistItemsAdapter);
        wishListViewModel.getWishlistObject().observe(getViewLifecycleOwner(), new Observer<ArrayList<Wishlist>>() {
            @Override
            public void onChanged(ArrayList<Wishlist> wishlists) {
                wishlistDisplayList.clear();
                wishlistDisplayList.addAll(wishlists);
                customerProfileWishlistItemsAdapter.notifyDataSetChanged();
            }
        });
        setExpandableLayoutTitleOnClickListener(expWishList);

        // Setting
        btnChat = view.findViewById(R.id.btn_chat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate( R.id.action_customerProfileFragment_to_sellerCustomerSupportFragment);
            }
        });

        // Update profile
        btnUpdate = view.findViewById(R.id.btn_update);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateCustomerProfile(firebaseUser);
            }
        });

        // Logout
        logoutButton = view.findViewById(R.id.btn_logout);
        SetLogoutOnClickListener(logoutButton);
    }

    private void setExpandableLayoutTitleOnClickListener(ExpandableLayout expWishList) {
        expWishList.setOnExpandListener(new OnExpandListener() {
            @Override
            public void onExpand(boolean isExpand) {
                if(isExpand){
                    recyclerView.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateCustomerProfile(FirebaseUser firebaseUser) {
        // Obtain the entered data
        _NAME = editName.getEditText().getText().toString();
        _PHONE = editPhone.getEditText().getText().toString();
        _ADDRESS = editAddress.getEditText().getText().toString();
        _CITY = editCity.getEditText().getText().toString();
        _PROVINCE = editProvince.getText().toString();
        _POSTCODE = editPostalCode.getEditText().getText().toString();
        // TODO: get watchlist recycler view
//        _FAVORITE = customer.getWatchList();
        _DOB = editBirthday.getText().toString();

        Customer customer = new Customer(_NAME,_PHONE,_ADDRESS,_CITY,_PROVINCE,_POSTCODE,_DOB);

        String userId = firebaseUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Customer");
        reference.child(userId).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    // Setting new fields
                    UserProfileChangeRequest updatableProfileField = new UserProfileChangeRequest.Builder().setDisplayName(_USERNAME).build();
                    firebaseUser.updateProfile(updatableProfileField);
                    Toast.makeText(getContext(), "Data update successfully", Toast.LENGTH_SHORT).show();
//
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e){
                        Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

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

    private void SetOrderStatusOnClickListener(final ImageButton imageButton, int status) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrderStatusDialog("viewConfirmed", status);
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

    private void SetBirthdayOnClickListener(EditText edit_birthday) {
        edit_birthday.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                int day, month, year;
                if (_DOB == null || _DOB.isEmpty()) {
                    // Get current date if _DOB is null or empty
                    Calendar c = Calendar.getInstance();
                    year = c.get(Calendar.YEAR);
                    month = c.get(Calendar.MONTH);
                    day = c.get(Calendar.DAY_OF_MONTH);
                } else {
                    // Extracting to dd,mm,yyyy by /
                    String textSADoB[] = _DOB.split("/");
                    day = Integer.parseInt(textSADoB[0]);
                    month = Integer.parseInt(textSADoB[1]) - 1;
                    year = Integer.parseInt(textSADoB[2]);
                }
                //Date Picker Dialog
                picker = new DatePickerDialog(requireContext(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        _DOB = dayOfMonth + "/" + (month + 1) + "/" + year;
                        edit_birthday.setText(_DOB);
                    }
                }, year, month, day);
                picker.show();
                // To prevent the keyboard from appearing when you click the EditText
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
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

    private void openOrderStatusDialog(String tab, int status) {
        CustomerOrderStatusDialog dialog = new CustomerOrderStatusDialog(status);
        dialog.show(getParentFragmentManager(), tab);
    }

    private void showCustomerProfileTitle(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customer");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                if(user != null) {
                    _USERNAME = firebaseUser.getDisplayName();
                    labelUsername.setText(_USERNAME);
                }else{
                    Toast.makeText(requireActivity(), "something went wrong! " +
                            "Show profile was canceled", Toast.LENGTH_LONG).show();
                }
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "something went wrong! " +
                        "Show profile was canceled", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showCustomerProfileMoreInfo(FirebaseUser firebaseUser) {
        String uid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Customer");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Customer customer = snapshot.getValue(Customer.class);
                if(customer != null) {

                    _NAME = customer.getName();
                    _PHONE = customer.getPhone();
                    _ADDRESS = customer.getAddress();
                    _CITY = customer.getCity();
                    _PROVINCE = customer.getProvince();
                    _POSTCODE = customer.getPostalCode();
                    _PIC = customer.getProfilePic();
                    _DOB = customer.getBirth();
                    _EMAIL = firebaseUser.getDisplayName();

                    editName.getEditText().setText(_NAME);
                    editPhone.getEditText().setText(_PHONE);
                    editEmail.getEditText().setText(_EMAIL);
                    editAddress.getEditText().setText(_ADDRESS);
                    editCity.getEditText().setText(_CITY);
                    editProvince.setText(_PROVINCE);
                    editPostalCode.getEditText().setText(_POSTCODE);
                    editBirthday.setText(_DOB);

                    Map<String, Wishlist> wishlistMap = customer.getWishlistMap();
                    _FAVORITE = wishlistMap;
                }else{
                    Toast.makeText(requireActivity(), "something went wrong! " +
                            "Show profile was canceled", Toast.LENGTH_LONG).show();
                }
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireActivity(), "something went wrong! " +
                        "Show profile was canceled", Toast.LENGTH_LONG).show();
            }
        });
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

        int heightDp = 230;
        int widthDp = 210;
        float density = getResources().getDisplayMetrics().density;
        int heightPixel = (int) (heightDp * density);
        int widthPixel = (int) (widthDp * density);

        layoutParams.height = heightPixel;
        layoutParams.width = widthPixel;

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setAttributes(layoutParams);
        }
    }

    private void createFireStoragePath(Uri uri) {
        StorageReference storageRef = storage.child("ProfileImages");
        imageRef = storageRef.child("customerImages");
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
                                    .into(imgUserProfile);

                            UserProfileChangeRequest request = new UserProfileChangeRequest.
                                    Builder().setPhotoUri(uri).build();
                            firebaseUser.updateProfile(request);

                            // Write the download URL to Firebase Database
                            reference.child("Customer").child(uid).child("profilePic").setValue(uri.toString());
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

    @Override
    public void onStart() {
        super.onStart();
        // Set User profile picture (After uploaded)
        Uri uri = firebaseUser.getPhotoUrl();
        if (uri != null){
            Glide.with(getContext())
                    .load(uri.toString())
                    .circleCrop() // or .transform(new CircleCrop())
                    .into(imgUserProfile);
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

