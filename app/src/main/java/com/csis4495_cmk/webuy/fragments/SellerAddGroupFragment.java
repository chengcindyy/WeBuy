package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.SellerAddGroupImagesAdapter;
import com.csis4495_cmk.webuy.adapters.SellerAddGroupStylesAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SellerAddGroupFragment extends Fragment {

    private NavController navController;

    private static final int NOT_YET_OPENED = 0;
    private static final int OPENING = 1;
    private static final int CLOSED = 2;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private DatabaseReference groupRef;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private MaterialButtonToggleGroup tgBtnGp_publish;
    private Button tgBtn_in_stock_publish;
    private Button tgBtn_group_buy_publish;
    private TextInputEditText groupName;
    private TextInputEditText description;
    private TextInputEditText group_no_style_qty;
    private TextInputLayout editLayout_groupPriceRange_publish;
    private TextInputLayout editLayout_groupPriceCurrency_publish;
    private TextInputEditText groupPriceRange;
    private CurrencyEditText groupPriceCurrency;
    private TextView publishTitle;

    private int groupType;

    private String groupId;

    private int no_qty;

    private String sellerId;
    private int tax;

    private int groupStatus;
    private String productId;
    private RecyclerView rv_img, rv_style;

    private SellerAddGroupStylesAdapter stylesAdapter;

    private SellerAddGroupImagesAdapter imagesAdapter;

    private List<ProductStyle> groupStyles;
    private List<String> imgPaths;

    private Map<String, Integer> groupQtyMap;

    private Button btnStart, btnEnd;
    private Date startTime, endTime;

    private AppCompatButton btnCancel, btnPublish;

    private Date currentTime;

    //    private AutoCompleteTextView groupCategory;
    private TextInputEditText groupCategory;

    private boolean isNewGroup = true;

    private Group editGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_add_group, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        groupRef = firebaseDatabase.getReference("Group");

        publishTitle = view.findViewById(R.id.tv_group_publish_title);
        //Get passed bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey("new_group_productId")) {
                isNewGroup = true;
                productId = bundle.getString("new_group_productId");
                publishTitle.setText("Publish a new product group");
            } else if (bundle.containsKey("edit_group_productId")) {
                isNewGroup = false;
                productId = bundle.getString("edit_group_productId");
                groupId = bundle.getString("edit_group_groupId");
                publishTitle.setText("Edit a group");
            }

        }

        if (firebaseUser != null) {
            sellerId = firebaseUser.getUid();
            // Now you can use the userId as needed
        }

        rv_img = view.findViewById(R.id.rv_groupImg_publish);
        rv_style = view.findViewById(R.id.rv_groupStyle_publish);

        //Load product images and product styles data from database to fragment
        LinearLayoutManager styleLayoutManager = new LinearLayoutManager(getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        rv_style.setLayoutManager(styleLayoutManager);
        rv_img.setLayoutManager(gridLayoutManager);
//      rv_style.setHasFixedSize(true);
        stylesAdapter = new SellerAddGroupStylesAdapter(getContext());
        rv_style.setAdapter(stylesAdapter);
        imagesAdapter = new SellerAddGroupImagesAdapter();
        rv_img.setAdapter(imagesAdapter);

//        productImageUris = new ArrayList<>();
        groupStyles = new ArrayList<>();
        imgPaths = new ArrayList<>();
        groupQtyMap = new HashMap<>();

        if (isNewGroup) {
            getProductData();

            getProductStyle();
        } else {
            getEditGroupData();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentTime = new Date();
        //Set navigation controller, how to navigate simply call -> controller.navigate(destination id)
        navController = NavHostFragment.findNavController(SellerAddGroupFragment.this);

        tgBtnGp_publish = view.findViewById(R.id.tgBtnGp_publish);
        tgBtn_in_stock_publish = view.findViewById(R.id.tgBtn_in_stock_publish);
        tgBtn_group_buy_publish = view.findViewById(R.id.tgBtn_group_buy_publish);

        groupName = view.findViewById(R.id.edit_groupName_publish);
        description = view.findViewById(R.id.edit_groupDes_publish);

        editLayout_groupPriceRange_publish = view.findViewById(R.id.editLayout_groupPriceRange_publish);
        groupPriceRange = view.findViewById(R.id.edit_groupPriceRange_publish);

        editLayout_groupPriceCurrency_publish = view.findViewById(R.id.editLayout_groupPriceCurrency_publish);
        groupPriceCurrency = view.findViewById(R.id.edit_groupPriceCurrecny_publish);

        group_no_style_qty = view.findViewById(R.id.edit_groupQty_publish);

        btnStart = view.findViewById(R.id.btn_start_group_publish);

        btnEnd = view.findViewById(R.id.btn_end_group_publish);

        groupCategory = view.findViewById(R.id.edit_group_category_publish);

        btnCancel = view.findViewById(R.id.btn_cancel_publish);

        btnPublish = view.findViewById(R.id.btn_publish_publish);

        if (isNewGroup) {
            //Set default group type as in stock
            tgBtnGp_publish.check(tgBtn_in_stock_publish.getId());
            tgBtn_in_stock_publish.setClickable(false);
            groupType = 0;
//        groupStatus = 1;
            groupStatus = OPENING;
            btnStart.setEnabled(false);
            btnEnd.setEnabled(false);
            //To change group type
            tgBtnGp_publish.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                switch (checkedId) {
                    case R.id.tgBtn_in_stock_publish:
                        if (isChecked) {
                            groupType = 0;
//                        groupStatus = 1;
                            groupStatus = OPENING;
                            startTime = null;
                            endTime = null;
//                        Toast.makeText(getContext(), "Group Type "+Integer.toString(groupType) + "Start and End Time: " + startTime + ": " + endTime,Toast.LENGTH_SHORT).show();
                            btnStart.setText("Group Start");
                            btnEnd.setText("Group End");
                            btnStart.setEnabled(false);
                            btnEnd.setEnabled(false);
                            tgBtn_group_buy_publish.setClickable(true);
                            tgBtn_in_stock_publish.setClickable(false);
                        }
                        break;
                    case R.id.tgBtn_group_buy_publish:
                        if (isChecked) {
                            groupType = 1;
                            startTime = new Date();
                            endTime = new Date();
                            btnStart.setEnabled(true);
                            btnEnd.setEnabled(true);
//                        Toast.makeText(getContext(), "Group Type "+Integer.toString(groupType),Toast.LENGTH_SHORT).show();
                            tgBtn_group_buy_publish.setClickable(false);
                            tgBtn_in_stock_publish.setClickable(true);
                        }
                        break;
                    default:
                        groupType = 0;
//                    groupStatus = 1;
                        groupStatus = OPENING;
                        startTime = null;
                        endTime = null;
                        btnStart.setText("Group Start");
                        btnEnd.setText("Group End");
                        btnStart.setEnabled(false);
                        btnEnd.setEnabled(false);
                        tgBtn_group_buy_publish.setClickable(true);
                }
            });

            //Set group startTime and endTime
            btnStart.setOnClickListener(v -> {
                startTime = new Date();
                openDateDialog(startTime, updatedstartTime -> {
                    validateStartime();
                    btnStart.setText("From " + startTime);
                }, "Setup start time");
            });
            btnEnd.setOnClickListener(v -> {
                endTime = new Date();
                openDateDialog(endTime, updatedstartTime -> {
                    validateEndTime();
                    btnEnd.setText("To " + endTime);
                }, "Setup end time");
            });

            btnPublish.setOnClickListener(v -> {
                onSubmitNewGroup();
            });

            //Set delete style button listener
            stylesAdapter.setOnImgBtnDeleteStyleListener(new SellerAddGroupStylesAdapter.OnImgBtnDeleteStyleListener() {
                @Override
                public void onDeleteClick(int position) {
                    groupQtyMap.remove(groupStyles.get(position).getStyleName());
                    groupStyles.remove(position);
                    stylesAdapter.updateStyleData2(productId, groupStyles);

                    if (groupStyles.size() > 0) {
                        groupPriceCurrency.setVisibility(View.GONE);
                        groupPriceCurrency.setEnabled(false);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.GONE);

                        editLayout_groupPriceRange_publish.setVisibility(View.VISIBLE);
                        groupPriceRange.setVisibility(View.VISIBLE);
                        groupPriceRange.setEnabled(false);

                        double minStylePrice = Double.MAX_VALUE;
                        double maxStylePrice = Double.MIN_VALUE;

                        for (ProductStyle ps : groupStyles) {
                            double stylePrice = ps.getStylePrice();
                            if (stylePrice < minStylePrice) {
                                minStylePrice = stylePrice;
                            }
                            if (stylePrice > maxStylePrice) {
                                maxStylePrice = stylePrice;
                            }
                        }

                        if (minStylePrice == maxStylePrice) {
                            groupPriceRange.setText("CA$ " + minStylePrice);
                            Log.d("Price Range", "min " + minStylePrice + " max: " + maxStylePrice);

                        } else {
                            groupPriceRange.setText("CA$ " + minStylePrice + "~" + maxStylePrice);
                            Log.d("Price Range", "min " + minStylePrice + " max: " + maxStylePrice);
                        }

                    } else if (groupStyles.size() == 1) {
                        editLayout_groupPriceRange_publish.setVisibility(View.GONE);
                        groupPriceRange.setVisibility(View.GONE);
                        groupPriceRange.setEnabled(false);

                        groupPriceCurrency.setVisibility(View.VISIBLE);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.VISIBLE);
                        groupPriceCurrency.setEnabled(false);
                        groupPriceCurrency.setText(Double.toString(groupStyles.get(0).getStylePrice()));
                        groupPriceCurrency.findFocus();
                    } else if (groupStyles.size() == 0) {
                        editLayout_groupPriceRange_publish.setVisibility(View.GONE);
                        groupPriceRange.setVisibility(View.GONE);
                        groupPriceRange.setEnabled(false);

                        groupPriceCurrency.setVisibility(View.VISIBLE);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.VISIBLE);
                        groupPriceCurrency.setEnabled(true);
                        groupPriceCurrency.findFocus();

                        group_no_style_qty.setVisibility(View.VISIBLE);
                        group_no_style_qty.findFocus();
                    }
                }
            });

            //Set edit style price and quantity listener
            stylesAdapter.setOnStyleChangedListner(new SellerAddGroupStylesAdapter.OnStyleChangedListner() {

                @Override
                public void onStyleChange2(int position, ProductStyle style, int qty) {
                    groupQtyMap.put(groupStyles.get(position).getStyleName(), qty);

//                Toast.makeText(getContext(), groupStyles.get(position).getStyleName() + "qty: " +  groupQtyMap.get(groupStyles.get(position).getStyleName()) , Toast.LENGTH_SHORT ).show();

                    groupStyles.set(position, style);

                    if (groupStyles.size() > 0) {
                        groupPriceCurrency.setVisibility(View.GONE);
                        groupPriceCurrency.setEnabled(false);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.GONE);

                        editLayout_groupPriceRange_publish.setVisibility(View.VISIBLE);
                        groupPriceRange.setVisibility(View.VISIBLE);
                        groupPriceRange.setEnabled(false);

                        double minStylePrice = Double.MAX_VALUE;
                        double maxStylePrice = Double.MIN_VALUE;

                        for (ProductStyle ps : groupStyles) {
                            double stylePrice = ps.getStylePrice();
                            if (stylePrice < minStylePrice) {
                                minStylePrice = stylePrice;
                            }
                            if (stylePrice > maxStylePrice) {
                                maxStylePrice = stylePrice;
                            }
                        }

                        if (minStylePrice == maxStylePrice) {
                            groupPriceRange.setText("CA$ " + minStylePrice);
                            Log.d("Price Range", "min " + minStylePrice + " max: " + maxStylePrice);

                        } else {
                            groupPriceRange.setText("CA$ " + minStylePrice + "~" + maxStylePrice);
                            Log.d("Price Range", "min " + minStylePrice + " max: " + maxStylePrice);
                        }
                    } else if (groupStyles.size() == 1) {
                        editLayout_groupPriceRange_publish.setVisibility(View.GONE);
                        groupPriceRange.setVisibility(View.GONE);
                        groupPriceRange.setEnabled(false);

                        groupPriceCurrency.setVisibility(View.VISIBLE);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.VISIBLE);
                        groupPriceCurrency.setEnabled(false);
                        groupPriceCurrency.setText(Double.toString(groupStyles.get(0).getStylePrice()));
                        groupPriceCurrency.findFocus();
                    } else if (groupStyles.size() == 0) {
                        editLayout_groupPriceRange_publish.setVisibility(View.GONE);
                        groupPriceRange.setVisibility(View.GONE);
                        groupPriceRange.setEnabled(false);

                        groupPriceCurrency.setVisibility(View.VISIBLE);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.VISIBLE);
                        groupPriceCurrency.setEnabled(true);
                        groupPriceCurrency.findFocus();

                        group_no_style_qty.setVisibility(View.VISIBLE);
                        group_no_style_qty.findFocus();
                    }
                }

                @Override
                public void onStyleChange(int position, ProductStyle style) {
                    String oldKey = groupStyles.get(position).getStyleName();
                    Integer qty = groupQtyMap.get(oldKey);

                    if (groupQtyMap.containsKey(oldKey)) {
                        groupQtyMap.remove(oldKey);
                        groupQtyMap.put(style.getStyleName(), qty);
                    }

                    groupStyles.set(position, style);

                    if (groupStyles.size() > 0) {
                        groupPriceCurrency.setVisibility(View.GONE);
                        groupPriceCurrency.setEnabled(false);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.GONE);

                        editLayout_groupPriceRange_publish.setVisibility(View.VISIBLE);
                        groupPriceRange.setVisibility(View.VISIBLE);
                        groupPriceRange.setEnabled(false);

                        double minStylePrice = Double.MAX_VALUE;
                        double maxStylePrice = Double.MIN_VALUE;

                        for (ProductStyle ps : groupStyles) {
                            double stylePrice = ps.getStylePrice();
                            if (stylePrice < minStylePrice) {
                                minStylePrice = stylePrice;
                            }
                            if (stylePrice > maxStylePrice) {
                                maxStylePrice = stylePrice;
                            }
                        }

                        if (minStylePrice == maxStylePrice) {
                            groupPriceRange.setText("CA$ " + minStylePrice);
                            Log.d("Price Range", "min " + minStylePrice + " max: " + maxStylePrice);

                        } else {
                            groupPriceRange.setText("CA$ " + minStylePrice + "~" + maxStylePrice);
                            Log.d("Price Range", "min " + minStylePrice + " max: " + maxStylePrice);
                        }


                    } else if (groupStyles.size() == 1) {
                        editLayout_groupPriceRange_publish.setVisibility(View.GONE);
                        groupPriceRange.setVisibility(View.GONE);
                        groupPriceRange.setEnabled(false);

                        groupPriceCurrency.setVisibility(View.VISIBLE);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.VISIBLE);
                        groupPriceCurrency.setEnabled(false);
                        groupPriceCurrency.setText(Double.toString(groupStyles.get(0).getStylePrice()));
                        groupPriceCurrency.findFocus();
                    } else if (groupStyles.size() == 0) {
                        editLayout_groupPriceRange_publish.setVisibility(View.GONE);
                        groupPriceRange.setVisibility(View.GONE);
                        groupPriceRange.setEnabled(false);

                        groupPriceCurrency.setVisibility(View.VISIBLE);
                        editLayout_groupPriceCurrency_publish.setVisibility(View.VISIBLE);
                        groupPriceCurrency.setEnabled(true);
                        groupPriceCurrency.findFocus();

                        group_no_style_qty.setVisibility(View.VISIBLE);
                        group_no_style_qty.findFocus();
                    }

                }
            });
        }

        //cancel publish and back to previous page
        btnCancel.setOnClickListener(v -> {
            //go back to the fragment you came from
            Navigation.findNavController(v).popBackStack();
        });


    }

    private void getEditGroupData() {
        imgPaths.clear();
        DatabaseReference editGroupRef = groupRef.child(groupId);
        editGroupRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    editGroup = task.getResult().getValue(Group.class);
                    if (editGroup != null) {

                            if (editGroup.getGroupType() == 0) {
                                tgBtnGp_publish.check(tgBtn_in_stock_publish.getId());
                                tgBtn_in_stock_publish.setClickable(false);
                                tgBtn_group_buy_publish.setClickable(false);
                                btnStart.setEnabled(false);
                                btnEnd.setEnabled(false);

                            } else {
                                tgBtnGp_publish.check(tgBtn_group_buy_publish.getId());
                                tgBtn_in_stock_publish.setClickable(false);
                                tgBtn_group_buy_publish.setClickable(false);
//
                                long currentTimeStamp = System.currentTimeMillis();
                                if(currentTimeStamp > editGroup.getStartTimestamp()){
                                    btnStart.setEnabled(false);
                                }else{
                                    btnStart.setEnabled(true);
                                }
                                btnEnd.setEnabled(true);

                            }

                        tax = editGroup.getTax();
                        groupName.setText(editGroup.getGroupName());
                        description.setText(editGroup.getDescription());
                        groupCategory.setText(editGroup.getCategory());
                        groupCategory.setEnabled(false);
                        groupPriceRange.setText(editGroup.getGroupPrice());
                        groupPriceCurrency.setText(editGroup.getGroupPrice());
                        imgPaths = editGroup.getGroupImages();
                        imagesAdapter.updateGroupImgPaths2(productId, imgPaths);
                        imagesAdapter.setOnGroupDeleteImgListener(new SellerAddGroupImagesAdapter.onGroupDeleteImgListener() {
                            @Override
                            public void onDeleteClick(int position) {

                            }
                        });
                    }
                }else{
                    Log.d(TAG, "Cannot retrieve data from database");
                    Toast.makeText(getContext(), "Cannot retrieve data from database", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    private void onSubmitNewGroup() {
        Group newGroup = new Group();
        boolean isComplete = true;

        String gName = groupName.getText().toString();
        String gDescription = description.getText().toString();
        String gCategory = groupCategory.getText().toString();
        groupCategory.setEnabled(false);

//        String gPriceCurrency = groupPriceCurrency.getText().toString().substring(4);
        String gPriceCurrency = groupPriceCurrency.getText().toString();
        String gPriceRange = groupPriceRange.getText().toString();

        String gNoStyleQty = group_no_style_qty.getText().toString();

        if (TextUtils.isEmpty(gName)) {
            isComplete = false;
            Toast.makeText(getContext(),
                    "Please enter the group name.", Toast.LENGTH_SHORT).show();
            groupName.setError("Group name is required.");
            groupName.requestFocus();
        }
        if (TextUtils.isEmpty(gDescription)) {
            isComplete = false;
            Toast.makeText(getContext(),
                    "Please enter the group description.", Toast.LENGTH_SHORT).show();
            description.setError("Group description is required.");
            description.requestFocus();
        }

        if (TextUtils.isEmpty(gCategory)) {
            isComplete = false;
            Toast.makeText(getContext(),
                    "Please enter the group category.", Toast.LENGTH_SHORT).show();
            groupCategory.setError("Group category is required.");
            groupCategory.requestFocus();
        }
        if (TextUtils.isEmpty(gPriceCurrency.substring(4)) && groupStyles.size() == 0) {
            isComplete = false;
            Toast.makeText(getContext(),
                    "Please enter the group price.", Toast.LENGTH_SHORT).show();
            groupPriceCurrency.setError("Group price is required.");
            groupPriceCurrency.requestFocus();
        }

        newGroup.setSellerId(sellerId);
        newGroup.setProductId(productId);
        newGroup.setGroupName(gName);
        newGroup.setDescription(gDescription);
        newGroup.setCategory(gCategory);

        if (groupStyles.size() == 0) {
            newGroup.setGroupPrice(gPriceCurrency);
        } else {
            newGroup.setGroupPrice(gPriceRange);
        }

        newGroup.setGroupType(groupType);
        newGroup.setGroupImages(imgPaths);

        newGroup.setTax(tax);

        if (groupType == 0) {
            //groupType = in stock
            startTime = null;
            endTime = null;
            //Group started, status = OPENING (1)
//            groupStatus = 1;
            groupStatus = OPENING;
            newGroup.setStatus(groupStatus);
        } else {
            //groupType = pre order
            if (btnStart.getText().equals("Group Start")) {
                isComplete = false;
                startTime = new Date();
                openDateDialog(startTime, updatedstartTime -> {
                    validateStartime();
                    btnStart.setText("From " + startTime);
                }, "Setup start time");
            } else if (btnEnd.getText().equals("Group End")) {
                isComplete = false;
                endTime = new Date();
                openDateDialog(endTime, updatedstartTime -> {
                    validateEndTime();
                    btnEnd.setText("To " + endTime);
                }, "Setup end time");
            }
            currentTime = new Date();
            if (startTime.compareTo(currentTime) > 0) {
                //Group not started yet, status = NOT_YET_OPENED (0)
//                groupStatus = 0;
                groupStatus = NOT_YET_OPENED;
                newGroup.setStartTimestamp(startTime.getTime());
                newGroup.setEndTimestamp(endTime.getTime());
                newGroup.setStatus(groupStatus);
                Log.d("DEBUG", "Current time: " + currentTime);
                Log.d("DEBUG", "Start time: " + startTime);
                Log.d("DEBUG", "Group Status: " + groupStatus);
            } else if (startTime.compareTo(currentTime) < 0) {
                //Group started, status = 1
//                groupStatus = 1;
                groupStatus = OPENING;
                newGroup.setStartTimestamp(startTime.getTime());
                newGroup.setEndTimestamp(endTime.getTime());
                newGroup.setStatus(groupStatus);
                Log.d("DEBUG", "Current time: " + currentTime);
                Log.d("DEBUG", "Start time: " + startTime);
                Log.d("DEBUG", "Group Status: " + groupStatus);

            }

        }

        for (Map.Entry<String, Integer> entry : groupQtyMap.entrySet()) {
            //check if the style qty is missing
            if (entry.getValue() == null) {
                isComplete = false;
                // trigger alert
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("No Quantity");
                builder.setMessage("Please input the quantity for " + entry.getKey());
//                builder.setMessage("Please input the quantity for " + entry.getKey());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
            if (groupType == 0) {
                if (entry.getValue() < 1) {
                    isComplete = false;
                    // trigger alert
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Invalid Quantity");
                    builder.setMessage("The quantity for in stock item must be at least 1");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                }
            }

        }

        for (int i = 0; i < stylesAdapter.getItemCount(); i++) {
            SellerAddGroupStylesAdapter.ViewHolder viewHolder = (SellerAddGroupStylesAdapter.ViewHolder) rv_style.findViewHolderForAdapterPosition(i);
            if (viewHolder != null && viewHolder.isAnyFieldEmpty()) {
                isComplete = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Empty Field");
                builder.setMessage("Please input all the fields");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            }
        }

//        if (groupStyles.size() != groupQtyMap.size()){
//            Toast.makeText(getContext(), "The style list and the style qty is not matched", Toast.LENGTH_SHORT).show();
//            Log.d("Test","The style list and the style qty is not matched");
//        }

        if (groupStyles.size() > 0) {
            newGroup.setGroupStyles(groupStyles);
        }

        if (groupStyles.size() == 0) {
            groupQtyMap.clear();
            if (TextUtils.isEmpty(gNoStyleQty)) {
                isComplete = false;
//                Toast.makeText(getContext(),
//                        "Please enter the group quantity.", Toast.LENGTH_SHORT).show();
                group_no_style_qty.setError("Group quantity is required.");
                group_no_style_qty.requestFocus();
            }

            try {
                no_qty = Integer.valueOf(gNoStyleQty);
                if (no_qty == 0) {
                    isComplete = false;
                    group_no_style_qty.setError("The quantity cannot be 0,");
                    group_no_style_qty.setText("");
                    group_no_style_qty.requestFocus();
                } else if (no_qty < -1) {
                    isComplete = false;
                    group_no_style_qty.setError("The minimum quantity is -1 for unlimited quantity order");
                    group_no_style_qty.setText("");
                    group_no_style_qty.requestFocus();
                }

                if (groupType == 0) {
                    if (no_qty == -1) {
                        isComplete = false;
                        group_no_style_qty.setError("The quantity for in stock item " + gName + " must be at least 1");
                    }
                }
                groupQtyMap.put(gName, no_qty);

            } catch (Exception e) {
//                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        if (!groupQtyMap.isEmpty()) {
            newGroup.setGroupQtyMap(groupQtyMap);
        }

        UploadGroup(newGroup, isComplete);

    }

    private void UploadGroup(Group group, boolean isComplete) {
        if (groupId == null) { //it is a new product
            groupId = groupRef.push().getKey(); //Product -> productId -> newProduct
            Log.d("Test", "new group: " + groupId);

        }
        if (isComplete) {
            groupRef.child(groupId).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("Test Upload", "Group Upload Successfully");
                    } else {
                        Log.d("Test Upload", "Grooup Upload Failed");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
                }
            });

            Navigation.findNavController(getView()).popBackStack();
        }

    }

    //Date picker
    private void openDateDialog(Date date, OnDateTimeSetListener listener, String title) {
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
        datePickerDialog.setTitle(title);
        datePickerDialog.show();
    }

    //Validate end time later than start sime and current time;
    public void validateEndTime() {
        if (startTime.compareTo(endTime) >= 0 || endTime.compareTo(currentTime) < 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Invalid End Time!");
            builder.setMessage("Group end time must be after start time and current time");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    openDateDialog(endTime, updatedstartTime -> {
                        btnEnd.setText("To " + endTime);
                        validateEndTime();
                    }, "Setup end time");
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

    //Validate start time later than current time;
    public void validateStartime() {
        if (startTime.compareTo(currentTime) < 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Invalid Start Time!");
            builder.setMessage("Group start time must be after current time");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    openDateDialog(startTime, updatedstartTime -> {
                        btnStart.setText("From " + startTime);
                        validateStartime();
                    }, "Setup start time");
                    Log.d("DEBUG", "Current time: " + currentTime);
                    Log.d("DEBUG", "Start time: " + startTime);
                    Log.d("DEBUG", "Group Status: " + groupStatus);
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }


    public interface OnDateTimeSetListener {
        void onDateTimeSet(Date dateTime);
    }

    //Get Product data
    private void getProductData() {
        imgPaths.clear();
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
//                        Toast.makeText(getContext(), productPriceText, Toast.LENGTH_LONG).show();
                        tax = dataSnapshot.child("tax").getValue(Integer.class);
                        if (prodcutNameText != null) {
                            groupName.setText(prodcutNameText);
                        }
                        if (prodcutDesText != null) {
                            description.setText(prodcutDesText);
                        }
                        if (prodcutCategoryText != null) {
                            groupCategory.setText(prodcutCategoryText);
                            groupCategory.setEnabled(false);
                        }
                        if (productPriceText != null) {
                            groupPriceRange.setText(productPriceText);
                            groupPriceCurrency.setText(productPriceText);
                        }

                        DataSnapshot productImgSnapshot = dataSnapshot.child("productImages");
                        for (DataSnapshot img : productImgSnapshot.getChildren()) {
                            String imgPath = img.getValue(String.class);
                            imgPaths.add(imgPath);
                        }

                        //Set delete group image button listener
                        if (imgPaths.size() > 1) {
                            imagesAdapter.setOnGroupDeleteImgListener(new SellerAddGroupImagesAdapter.onGroupDeleteImgListener() {
                                @Override
                                public void onDeleteClick(int position) {
                                    imgPaths.remove(position);
                                    imagesAdapter.updateGroupImgPaths2(productId, imgPaths);
//                                    Toast.makeText(getContext(), "imgPaths size: " + Integer.toString(imgPaths.size()), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            imagesAdapter.setOnGroupDeleteImgListener(new SellerAddGroupImagesAdapter.onGroupDeleteImgListener() {
                                @Override
                                public void onDeleteClick(int position) {

                                }
                            });
                        }
                        imagesAdapter.updateGroupImgPaths2(productId, imgPaths);
                    }
                } else {
                    Log.d(TAG, "Cannot retrieve data from database");
                    Toast.makeText(getContext(), "Cannot retrieve data from database", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Get Product style(s) data
    private void getProductStyle() {
        databaseReference.child("Product").child(productId).child("productStyles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                groupStyles.clear();
                groupQtyMap.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("styleName").getValue(String.class);
                    String img = snapshot.child("stylePic").getValue(String.class);
                    Double price = snapshot.child("stylePrice").getValue(Double.class);
                    String styleId = snapshot.child("styleId").getValue(String.class);
                    ProductStyle ps = new ProductStyle(name, price, img, styleId);
                    groupStyles.add(ps);
                    groupQtyMap.put(ps.getStyleName(), null);

//                    Toast.makeText(getContext(), ps.getStyleName() + "qty: " +  groupQtyMap.get(ps) , Toast.LENGTH_SHORT ).show();
                }

                if (groupStyles.size() > 0) {
                    groupPriceCurrency.setVisibility(View.GONE);
                    groupPriceCurrency.setEnabled(false);
                    editLayout_groupPriceCurrency_publish.setVisibility(View.GONE);

                    editLayout_groupPriceRange_publish.setVisibility(View.VISIBLE);
                    groupPriceRange.setVisibility(View.VISIBLE);
                    groupPriceRange.setEnabled(false);

                    double minStylePrice = Double.MAX_VALUE;
                    double maxStylePrice = Double.MIN_VALUE;

                    for (ProductStyle ps : groupStyles) {
                        double stylePrice = ps.getStylePrice();
                        if (stylePrice < minStylePrice) {
                            minStylePrice = stylePrice;
                        }
                        if (stylePrice > maxStylePrice) {
                            maxStylePrice = stylePrice;
                        }
                    }

                    if (minStylePrice == maxStylePrice) {
                        groupPriceRange.setText("CA$ " + minStylePrice);
                        Log.d("Price Range", "min " + minStylePrice + " max: " + maxStylePrice);

                    } else {
                        groupPriceRange.setText("CA$ " + minStylePrice + "~" + maxStylePrice);
                        Log.d("Price Range", "min " + minStylePrice + " max: " + maxStylePrice);
                    }

                } else if (groupStyles.size() == 1) {
                    editLayout_groupPriceRange_publish.setVisibility(View.GONE);
                    groupPriceRange.setVisibility(View.GONE);
                    groupPriceRange.setEnabled(false);

                    groupPriceCurrency.setVisibility(View.VISIBLE);
                    editLayout_groupPriceCurrency_publish.setVisibility(View.VISIBLE);
                    groupPriceCurrency.setEnabled(false);
                    groupPriceCurrency.setText(Double.toString(groupStyles.get(0).getStylePrice()));
                    groupPriceCurrency.findFocus();

                } else if (groupStyles.size() == 0) {
                    editLayout_groupPriceRange_publish.setVisibility(View.GONE);
                    groupPriceRange.setVisibility(View.GONE);
                    groupPriceRange.setEnabled(false);

                    groupPriceCurrency.setVisibility(View.VISIBLE);
                    editLayout_groupPriceCurrency_publish.setVisibility(View.VISIBLE);
                    groupPriceCurrency.setEnabled(true);
                    groupPriceCurrency.findFocus();

                    group_no_style_qty.setVisibility(View.VISIBLE);
                    group_no_style_qty.findFocus();
                }

                stylesAdapter.updateStyleData2(productId, groupStyles);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Cannot retrieve data from database", error.toException());
            }
        });
    }


}