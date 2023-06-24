package com.csis4495_cmk.webuy;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.csis4495_cmk.webuy.activities.MainActivity;
import com.csis4495_cmk.webuy.adapters.SellerAddGroupImagesAdapter;
import com.csis4495_cmk.webuy.adapters.SellerAddGroupStylesAdapter;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class SellerAddGroupFragment extends Fragment {

    private NavController navController;

    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;

    private MaterialButtonToggleGroup tgBtnGp_publish;
    private Button tgBtn_in_stock_publish;
    private Button tgBtn_group_buy_publish;
    private TextInputEditText groupName;
    private TextInputEditText description;
    private TextInputEditText groupPriceRange;
    private int groupType;

    private int tax;
    private String productId;
    private RecyclerView rv_img, rv_style;

    private SellerAddGroupStylesAdapter stylesAdapter;
    private SellerAddGroupImagesAdapter imagesAdapter;

    List<ProductStyle> groupStyles;
    List<Uri> styleImageUris;
    List<Uri> productImageUris;
    private Button btnStart, btnEnd ;
    private Date startTime, endTime;

    private AppCompatButton btnCancel, btnPublish;

    private AutoCompleteTextView groupCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_add_group, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages");
        //get passed bundle
        if (getArguments() != null) {
            productId = getArguments().getString("new_group_productId");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set navigation controller, how to navigate simply call -> controller.navigate(destination id)
        navController = NavHostFragment.findNavController(SellerAddGroupFragment.this);

        tgBtnGp_publish = view.findViewById(R.id.tgBtnGp_publish);
        tgBtn_in_stock_publish = view.findViewById(R.id.tgBtn_in_stock_publish);
        tgBtn_group_buy_publish = view.findViewById(R.id.tgBtn_group_buy_publish);

        groupName = view.findViewById(R.id.edit_groupName_publish);
        description = view.findViewById(R.id.edit_groupDes_publish);
        groupPriceRange =view.findViewById(R.id.edit_groupPriceRange_publish);

        btnStart = view.findViewById(R.id.btn_start_group_publish);
        btnEnd = view.findViewById(R.id.btn_end_group_publish);

        //Recycler view of product images and styles
        rv_img = view.findViewById(R.id.rv_groupImg_publish);
        rv_style = view.findViewById(R.id.rv_groupStyle_publish);

        //Group category
        groupCategory = view.findViewById(R.id.edit_group_category_publish);
        ArrayAdapter<CharSequence> productCatAdapter = ArrayAdapter.createFromResource(getContext(), R.array.arr_product_category, android.R.layout.simple_list_item_1);
        groupCategory.setAdapter(productCatAdapter);
        groupCategory.setOnItemClickListener((parent, view2, position, id) -> {});

        //set default group type
        tgBtnGp_publish.check(tgBtn_in_stock_publish.getId());
        groupType = 0;
        btnStart.setEnabled(false);
        btnEnd.setEnabled(false);
        //set group type
        tgBtnGp_publish.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            switch (checkedId){
                case R.id.tgBtn_in_stock_publish:
                    if(isChecked){
                        groupType = 0;
                        btnStart.setEnabled(false);
                        Toast.makeText(getContext(), "Group Type "+Integer.toString(groupType),Toast.LENGTH_SHORT).show();

                        btnStart.setText("Group Start");
                        btnEnd.setEnabled(false);
                        btnStart.setText("Group End");
                    }
                    break;
                case R.id.tgBtn_group_buy_publish:
                    if(isChecked){
                        groupType = 1;
                        btnStart.setEnabled(true);
                        btnEnd.setEnabled(true);
                        Toast.makeText(getContext(), "Group Type "+Integer.toString(groupType),Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    groupType = 0;
                    btnStart.setEnabled(false);
                    btnEnd.setEnabled(false);
            }
        });

        //set group start and end time
        startTime = new Date();
        endTime = new Date();
        btnStart.setOnClickListener(v -> {
            openDateDialog(startTime, updatedstartTime -> {
                btnStart.setText("From " + startTime);
            });
        });
        btnEnd.setOnClickListener(v -> {
            openDateDialog(endTime, updatedstartTime -> {
                validateEndTime();
                btnEnd.setText("To "+ endTime);
            });
        });

        btnCancel = view.findViewById(R.id.btn_cancel_publish);
        btnPublish = view.findViewById(R.id.btn_publish_publish);

        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        //Load data from database to fragment
        RecyclerView.LayoutManager stylelayoutManager = new LinearLayoutManager(getContext());
        rv_style.setLayoutManager(stylelayoutManager);
//        rv_style.setHasFixedSize(true);
        stylesAdapter = new SellerAddGroupStylesAdapter();
        rv_style.setAdapter(stylesAdapter);
        getProdcutData();
        getProductStyle();
        //set delete style image button
        stylesAdapter.setOnImgBtnDeleteStyleListener(new SellerAddGroupStylesAdapter.onImgBtnDeleteStyleListener() {
            @Override
            public void onDeleteClick(int position) {
                groupStyles.remove(position);
                stylesAdapter.updateStyleData(groupStyles);
            }
        });

    }

    private void openDateDialog(Date date, OnDateTimeSetListener listener) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                            (view1, hourOfDay, minute1) -> {
                                Calendar dateTimeCalendar = Calendar.getInstance();
                                dateTimeCalendar.set(year1, monthOfYear, dayOfMonth, hourOfDay, minute1);
                                date.setTime(dateTimeCalendar.getTimeInMillis());
                                if (listener != null) {
                                    listener.onDateTimeSet(date);
                                }
                            }, hour, minute, false);
                    timePickerDialog.show();
                }, year, month, day);
        datePickerDialog.show();
    }

    public void validateEndTime(){
        if (startTime.compareTo(endTime) >= 0 ){
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Invalid End Time!");
            builder.setMessage("Group end time must be after start time");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    openDateDialog(endTime, updatedstartTime -> {
                        btnEnd.setText("To " + endTime);
                        validateEndTime();
                    });
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public interface OnDateTimeSetListener {
        void onDateTimeSet(Date dateTime);
    }

    private void getProdcutData(){
        DatabaseReference productReference = databaseReference.child("Product").child(productId);
        productReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                       String prodcutNameText = dataSnapshot.child("productName").getValue(String.class);
                        String prodcutDesText = dataSnapshot.child("description").getValue(String.class);
                        String prodcutCategoryText = dataSnapshot.child("category").getValue(String.class);
                        String productPriceText = dataSnapshot.child("productPrice").getValue(String.class);
                        tax = dataSnapshot.child("tax").getValue(Integer.class);
                        // Set the retrieved values in your EditText fields if the views are available
                        if (prodcutNameText != null) {
                            groupName.setText(prodcutNameText);
                        }
                        if (prodcutDesText != null) {
                            description.setText(prodcutDesText);
                        }
                        if (prodcutCategoryText != null) {
                            groupCategory.setText(prodcutCategoryText,false);
                        }
                        if(productPriceText !=null){
                            groupPriceRange.setText(productPriceText);
                        }
                        Toast.makeText(getContext(), "Tax "+Integer.toString(tax),Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Cannot retrieve data from database");
                    Toast.makeText(getContext(), "Cannot retrieve data from database", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getProductStyle(){
        groupStyles = new ArrayList<>();
        databaseReference.child("Product").child(productId).child("productStyles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupStyles.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String name = snapshot.child("styleName").getValue(String.class);
                    String img = snapshot.child("stylePic").getValue(String.class);
                    Double price = snapshot.child("stylePrice").getValue(Double.class);
                    ProductStyle ps = new ProductStyle(name, price, img);
                    groupStyles.add(ps);
                }
                stylesAdapter.updateStyleData(groupStyles);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Cannot retrieve data from database", error.toException());
            }
        });
    }

}