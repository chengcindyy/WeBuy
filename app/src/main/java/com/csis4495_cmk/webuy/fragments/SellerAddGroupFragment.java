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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerAddGroupImagesAdapter;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerAddGroupStylesAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.Product;
import com.csis4495_cmk.webuy.models.ProductStyle;

import com.csis4495_cmk.webuy.viewmodels.SharedEditStyleViewModel;

import com.csis4495_cmk.webuy.viewmodels.SharedICheckInventoryViewModel;

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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


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
    private TextInputEditText groupCategory;
    private boolean isNewGroup = true;
    private Group editGroup;
    private SimpleDateFormat simpleDateFormat;

    private Double minPrice, maxPrice;

    private Button btnCheckInventory, btnEditNewStyle;

    private Map<String, Integer> inventoryMap;

    private Map<String, String> inventoryNameMap;

    private SharedICheckInventoryViewModel inventoryViewModel;
    private List<ProductStyle> newEditGroupStyles;

    private SharedEditStyleViewModel styleViewModel;

    private boolean isRestockChecked = false;

    private Map<String, Integer> selectedRestockInventory;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inventoryViewModel = new ViewModelProvider(getActivity()).get(SharedICheckInventoryViewModel.class);
        inventoryViewModel.getselectedInventory().observe(this, item -> {
            if (item != null) {
                selectedRestockInventory = item;
            }
        });

        inventoryViewModel.getIsRestockChecked().observe(this, aBoolean -> {
            if (aBoolean != null) {
                isRestockChecked = aBoolean;
            }
        });

        if (!isNewGroup) {
            btnCheckInventory.setEnabled(false);
            btnCheckInventory.setVisibility(View.GONE);
            isRestockChecked = true;
            styleViewModel = new ViewModelProvider(getActivity()).get(SharedEditStyleViewModel.class);
            styleViewModel.getSelectedStyle().observe(this, item -> {
                Log.d(TAG, "Get selected new style from child fragment: " + item);
                if (item != null) {
                    for (String key : item.keySet()) {
                        groupQtyMap.put(key, null);
                        String id = key.substring(4);
                        for (ProductStyle allStyle : newEditGroupStyles) {
                            if (allStyle.getStyleId().equals(id)) {
                                groupStyles.add(allStyle);
                                groupQtyMap.put("s___" + allStyle.getStyleId(), null);
                            }
                        }
                        stylesAdapter.setStyles(groupStyles);
                        stylesAdapter.setGroupQtyMap(groupQtyMap);
                        stylesAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

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
                Log.d("Test bundle productId", "productId: " + productId);
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

        groupStyles = new ArrayList<>();
        imgPaths = new ArrayList<>();
        groupQtyMap = new HashMap<>();

        inventoryNameMap = new HashMap<>();
        inventoryMap = new HashMap<>();

        newEditGroupStyles = new ArrayList<>();

        if (isNewGroup) {
            Log.d(TAG, "isNewGroup:" + isNewGroup + " productId" + productId);
            getProductData();
            getProductStyle();
        } else {
            getEditGroupData();
        }

        simpleDateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd", Locale.CANADA);

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

        btnCheckInventory = view.findViewById(R.id.btn_check_inventory_publish);

        btnEditNewStyle = view.findViewById(R.id.btn_group_edit_new_group_style);

        if (isNewGroup) {
            //Set default group type as in stock
            tgBtnGp_publish.check(tgBtn_in_stock_publish.getId());
            tgBtn_in_stock_publish.setClickable(false);
            groupType = 0;
            groupStatus = OPENING;
            btnStart.setEnabled(false);
            btnEnd.setEnabled(false);
            //To change group type
            tgBtnGp_publish.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
                switch (checkedId) {
                    case R.id.tgBtn_in_stock_publish:
                        if (isChecked) {
                            groupType = 0;
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
                        groupStatus = OPENING;
                        startTime = null;
                        endTime = null;
                        btnStart.setText("Group Start");
                        btnEnd.setText("Group End");
                        btnStart.setEnabled(false);
                        btnEnd.setEnabled(false);
                        tgBtn_group_buy_publish.setClickable(true);
                }
//
            });

            //Set delete style button listener
            stylesAdapter.setOnImgBtnDeleteStyleListener(new SellerAddGroupStylesAdapter.OnImgBtnDeleteStyleListener() {
                @Override
                public void onDeleteClick(int position) {
                    groupQtyMap.remove("s___" + groupStyles.get(position).getStyleId());
                    groupStyles.remove(position);
                    stylesAdapter.updateStyleData2(productId, groupStyles);
                    stylesAdapter.notifyDataSetChanged();
                    comparePriceRange();
                }
            });
            btnEditNewStyle.setVisibility(View.GONE);
        }

        btnCheckInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inventoryMap.clear();
                inventoryNameMap.clear();

                DatabaseReference productReference = databaseReference.child("Product").child(productId);
                productReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    Integer pInStock;
                    Integer sInstock;
                    String pName;
                    String sName;

                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        Product p = new Product();
                        if (dataSnapshot.exists()) {
                            p = dataSnapshot.getValue(Product.class);
                            if (dataSnapshot.child("inStock").exists()) {
                                try {
                                    pInStock = dataSnapshot.child("inStock").getValue(Integer.class);
                                    pName = dataSnapshot.child("productName").getValue(String.class);
                                    if (pInStock != 0) {
                                        inventoryMap.put("p___" + productId, pInStock);
                                        inventoryNameMap.put("p___" + productId, pName);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, "loading product inventory error: " + e);
                                }
                            }
                            if (dataSnapshot.child("productStyles").exists()) {
                                Log.d(TAG, "styles exists");

                                for (DataSnapshot ds : dataSnapshot.child("productStyles").getChildren()) {
                                    String styleId = ds.child("styleId").getValue(String.class);
                                    sName = ds.child("styleName").getValue(String.class);
                                    try {
                                        sInstock = ds.child("inStock").getValue(Integer.class);
                                        Log.d(TAG, "loading style inventory: " + sInstock);
                                        if (sInstock != 0) {
                                            inventoryMap.put("s___" + styleId, sInstock);
                                            inventoryNameMap.put("s___" + styleId, sName);
                                        }
                                    } catch (Exception e) {
                                        Log.d(TAG, "loading style inventory error: " + e);
                                    }
                                }
                            }
                        }
                        Log.d(TAG, "check inventoryNameMap: " + inventoryNameMap);
                        Log.d(TAG, "onClick: inventory" + inventoryMap);
                        Log.d(TAG, "onClick: qty" + groupQtyMap);

                        //match inventory and name with current group product/style offers
                        if (p.getProductStyles() != null && groupQtyMap.size() > 0) {
                            Set<String> qtyKey = new HashSet<>(groupQtyMap.keySet());
                            inventoryMap.keySet().retainAll(qtyKey);
                            inventoryNameMap.keySet().retainAll(qtyKey);
                        } else if (p.getProductStyles() != null && groupQtyMap.size() == 0) {
                            inventoryMap.clear();
                            inventoryNameMap.clear();
                            if (pInStock != 0) {
                                inventoryMap.put("p___" + productId, pInStock);
                                inventoryNameMap.put("p___" + productId, pName);
                            }
                        }
                        if (inventoryMap.isEmpty() || inventoryMap == null) {
                            Toast.makeText(getContext(), "There is no inventory to restock", Toast.LENGTH_SHORT).show();
                            isRestockChecked = true;
                        } else {
                            CheckInventoryFragment fragment = CheckInventoryFragment.newInstance(inventoryMap, inventoryNameMap);
                            fragment.show(getActivity().getSupportFragmentManager(), "tag");
                        }

                    }
                });
            }
        });

        btnEditNewStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEditGroupStyles.clear();
                DatabaseReference productReference = databaseReference.child("Product").child(productId);
                productReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        DataSnapshot dataSnapshot = task.getResult();
                        Product p = new Product();
                        if (dataSnapshot.exists()) {
                            p = dataSnapshot.getValue(Product.class);
                            newEditGroupStyles = p.getProductStyles();
                            List<ProductStyle> duplicates = new ArrayList<>();
                            for (ProductStyle allStyle : newEditGroupStyles) {
                                String allId = allStyle.getStyleId();
                                for (ProductStyle existing : groupStyles) {
                                    if (allId.equals(existing.getStyleId())) {
                                        duplicates.add(allStyle);
                                    }
                                }
                            }
                            newEditGroupStyles.removeAll(duplicates);
                            Log.d(TAG, "existing style" + groupStyles);
                            Log.d(TAG, "style can be added" + newEditGroupStyles);
                            GroupAddNewStyleFragment fragment = GroupAddNewStyleFragment.newInstance(productId, newEditGroupStyles);
                            fragment.show(getActivity().getSupportFragmentManager(), "tag");
                        }
                    }
                });
            }
        });

        //Set group startTime and endTime
        btnStart.setOnClickListener(v -> {
            startTime = new Date();
            openDateDialog(startTime, updatedstartTime -> {
                validateStartime();
                btnStart.setText("From " + simpleDateFormat.format(startTime));
            }, "Setup start time");
        });
        btnEnd.setOnClickListener(v -> {
            endTime = new Date();
            openDateDialog(endTime, updatedstartTime -> {
                validateEndTime();
                btnEnd.setText("To " + simpleDateFormat.format(endTime));
            }, "Setup end time");
        });

        btnPublish.setOnClickListener(v -> {
            onSubmitNewGroup();
        });

        stylesAdapter.setOnStyleChangedListner(new SellerAddGroupStylesAdapter.OnStyleChangedListner() {
            @Override
            public void onStyleChangeQty(int position, ProductStyle style, int qty) {
                groupQtyMap.put("s___" + groupStyles.get(position).getStyleId(), qty);
                groupStyles.set(position, style);
                Log.d(TAG, "onStyleChangeQty: " + groupQtyMap);

                comparePriceRange();
            }

            @Override
            public void onStyleChange(int position, ProductStyle style) {
                String oldKey = "s___" + groupStyles.get(position).getStyleId();
                Integer qty = groupQtyMap.get(oldKey);

                if (groupQtyMap.containsKey(oldKey)) {
                    groupQtyMap.remove(oldKey);
                    groupQtyMap.put("s___" + style.getStyleId(), qty);
                }
                groupStyles.set(position, style);
                comparePriceRange();
            }
        });
        //cancel publish and back to previous page
        btnCancel.setOnClickListener(v -> {
            //go back to the fragment you came from
            Navigation.findNavController(v).popBackStack();
        });
    }


    private void comparePriceRange() {
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
                minPrice = minStylePrice;
                maxPrice = maxStylePrice;
                Log.d(TAG, "minPrice " + minPrice + " maxPrice: " + maxPrice);
            } else {
                groupPriceRange.setText("CA$ " + minStylePrice + "~" + maxStylePrice);
                minPrice = minStylePrice;
                maxPrice = maxStylePrice;
                Log.d(TAG, "minPrice " + minPrice + " maxPrice: " + maxPrice);
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

            minPrice = groupStyles.get(0).getStylePrice();
            maxPrice = groupStyles.get(0).getStylePrice();
            Log.d(TAG, "minPrice " + minPrice + " maxPrice: " + maxPrice);
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

    private void getEditGroupData() {
        imgPaths.clear();
        groupStyles.clear();
        groupQtyMap.clear();
        DatabaseReference editGroupRef = groupRef.child(groupId);
        editGroupRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    editGroup = task.getResult().getValue(Group.class);
                    if (editGroup != null) {
                        tax = editGroup.getTax();
                        groupType = editGroup.getGroupType();
                        minPrice = editGroup.getMinPrice();
                        maxPrice = editGroup.getMaxPrice();
                        List<ProductStyle> edGroupStyles = editGroup.getGroupStyles();
                        Log.d(TAG, "Get edit group data: minPrice " + minPrice + " maxPrice: " + maxPrice);
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

                            startTime = new Date(editGroup.getStartTimestamp());

                            endTime = new Date(editGroup.getEndTimestamp());

                            btnStart.setText("From " + simpleDateFormat.format(editGroup.getStartTimestamp()));
                            btnEnd.setText("To " + simpleDateFormat.format(editGroup.getEndTimestamp()));

                            long currentTimeStamp = System.currentTimeMillis();
                            if (currentTimeStamp > editGroup.getStartTimestamp()) {
                                btnStart.setEnabled(false);
                            } else {
                                btnStart.setEnabled(true);
                            }
                            btnEnd.setEnabled(true);
                        }
                        groupName.setText(editGroup.getGroupName());
                        description.setText(editGroup.getDescription());
                        groupCategory.setText(editGroup.getCategory());
                        groupCategory.setEnabled(false);
                        //Get Group Style data
                        groupQtyMap = editGroup.getGroupQtyMap();
                        if (editGroup.getGroupStyles() != null) {
                            //get group styles data
                            groupPriceRange.setVisibility(View.VISIBLE);
                            groupPriceRange.setText(editGroup.getGroupPrice());
                            groupPriceCurrency.setVisibility(View.GONE);
                            group_no_style_qty.setVisibility(View.GONE);
                            groupStyles = editGroup.getGroupStyles();
                            stylesAdapter.setStyles(groupStyles);
                            stylesAdapter.setProductId(productId);
                            stylesAdapter.setNewGroup(false);
                            stylesAdapter.setGroupQtyMap(groupQtyMap);

                        } else {
                            groupPriceRange.setVisibility(View.GONE);
                            groupPriceCurrency.setVisibility(View.VISIBLE);
                            groupPriceCurrency.setText(editGroup.getGroupPrice());
                            group_no_style_qty.setVisibility(View.VISIBLE);
                            group_no_style_qty.setText(Integer.toString(editGroup.getGroupQtyMap().get("p___" + productId)));

                            btnEditNewStyle.setVisibility(View.GONE);
                        }

                        DatabaseReference productReference = databaseReference.child("Product").child(productId);
                        productReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Product p = snapshot.getValue(Product.class);

                                if (p.getProductStyles() != null) {
                                    if (edGroupStyles == null) {
                                        btnEditNewStyle.setVisibility(View.GONE);
                                    }
                                    if (edGroupStyles != null && p.getProductStyles().size() == edGroupStyles.size()) {
                                        btnEditNewStyle.setVisibility(View.GONE);
                                    }
                                } else {
                                    btnEditNewStyle.setVisibility(View.GONE);
                                }
                                GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {
                                };
                                List<String> loadedProductImages = snapshot.child("productImages").getValue(t);
                                if (loadedProductImages != null) {
                                    imgPaths = loadedProductImages;
                                    Log.d(TAG, "All product images path: " + imgPaths);
                                    editGroup.setGroupImages(imgPaths);
                                    Log.d(TAG, "Group images path: " + imgPaths);
                                    imagesAdapter.updateGroupImgPaths2(productId, imgPaths);
                                }


                                //Set delete group image button listener
                                if (imgPaths.size() > 1) {
                                    imagesAdapter.setOnGroupDeleteImgListener(new SellerAddGroupImagesAdapter.onGroupDeleteImgListener() {
                                        @Override
                                        public void onDeleteClick(int position) {
                                            imgPaths.remove(position);
                                            imagesAdapter.updateGroupImgPaths2(productId, imgPaths);
                                            editGroup.setGroupImages(imgPaths);
                                            Log.d(TAG, "On change group image: " + editGroup.getGroupImages());
                                        }
                                    });
                                } else {
                                    imagesAdapter.setOnGroupDeleteImgListener(new SellerAddGroupImagesAdapter.onGroupDeleteImgListener() {
                                        @Override
                                        public void onDeleteClick(int position) { // do nothing here
                                        }
                                    });
                                }
                                imagesAdapter.updateGroupImgPaths2(productId, imgPaths);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                } else {
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

        String gPriceCurrency = groupPriceCurrency.getText().toString();
        String gPriceRange = groupPriceRange.getText().toString();
        String gNoStyleQty = group_no_style_qty.getText().toString().trim();

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

        if (groupStyles.size() == 0) {
            if (TextUtils.isEmpty(gPriceCurrency)) {
                isComplete = false;
                Toast.makeText(getContext(),
                        "Please enter the group price.", Toast.LENGTH_SHORT).show();
                groupPriceCurrency.setError("Group price is required.");
                groupPriceCurrency.requestFocus();
            } else if (gPriceCurrency.substring(4).isEmpty()) {
                isComplete = false;
                Toast.makeText(getContext(),
                        "Please enter the group price.", Toast.LENGTH_SHORT).show();
                groupPriceCurrency.setError("Group price is required.");
                groupPriceCurrency.requestFocus();
            } else if (Double.parseDouble(gPriceCurrency.substring(4)) <= 0) {
                isComplete = false;
                Toast.makeText(getContext(),
                        "The group price must be greater than 0", Toast.LENGTH_SHORT).show();
                groupPriceCurrency.setError("Group price is required.");
                groupPriceCurrency.requestFocus();
            }
        }

        newGroup.setSellerId(sellerId);
        newGroup.setProductId(productId);
        newGroup.setGroupName(gName);
        newGroup.setDescription(gDescription);
        newGroup.setCategory(gCategory);

        if (!isNewGroup) {
            editGroup.setGroupName(gName);
            newGroup.setDescription(gDescription);
        }

        if (groupStyles.size() == 0) {
            newGroup.setGroupPrice(gPriceCurrency);
            minPrice = Double.parseDouble(gPriceCurrency.substring(4));
            maxPrice = Double.parseDouble(gPriceCurrency.substring(4));
            Log.d(TAG, "minPrice " + minPrice + " maxPrice: " + maxPrice);
            newGroup.setMinPrice(minPrice);
            newGroup.setMaxPrice(maxPrice);
            if (!isNewGroup) {
                editGroup.setGroupPrice(gPriceCurrency);
                editGroup.setMinPrice(minPrice);
                editGroup.setMaxPrice(maxPrice);
            }
        } else {
            newGroup.setGroupPrice(gPriceRange);
            newGroup.setMinPrice(minPrice);
            newGroup.setMaxPrice(maxPrice);
            if (!isNewGroup) {
                editGroup.setGroupPrice(gPriceRange);
                editGroup.setMinPrice(minPrice);
                editGroup.setMaxPrice(maxPrice);
            }
        }

        newGroup.setGroupType(groupType);
        newGroup.setGroupImages(imgPaths);

        if (!isNewGroup) {
            editGroup.setGroupImages(imgPaths);
        }

        newGroup.setTax(tax);

        if (groupType == 0) {
            //groupType = in stock
            startTime = null;
            endTime = null;
            //Group started, status = OPENING (1)
            groupStatus = OPENING;
            newGroup.setStatus(groupStatus);
        } else {
            //groupType = pre order
            if (btnStart.getText().equals("Group Start")) {
                isComplete = false;
                startTime = new Date();
                openDateDialog(startTime, updatedstartTime -> {
                    validateStartime();
                    btnStart.setText("From " + simpleDateFormat.format(startTime));
                }, "Setup start time");
            } else if (btnEnd.getText().equals("Group End")) {
                isComplete = false;
                endTime = new Date();
                openDateDialog(endTime, updatedstartTime -> {
                    validateEndTime();
                    btnEnd.setText("To " + simpleDateFormat.format(endTime));
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

                if (!isNewGroup) {
                    editGroup.setStartTimestamp(startTime.getTime());
                    editGroup.setEndTimestamp(endTime.getTime());
                    editGroup.setStatus(groupStatus);
                }
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

                if (!isNewGroup) {
                    editGroup.setStartTimestamp(startTime.getTime());
                    editGroup.setEndTimestamp(endTime.getTime());
                    editGroup.setStatus(groupStatus);
                }

            }
        }

        if (groupStyles.size() == 0) {
            groupQtyMap.clear();
            if (TextUtils.isEmpty(gNoStyleQty)) {
                isComplete = false;
                group_no_style_qty.setError("Please input the group quantity");
                group_no_style_qty.requestFocus();
            }

            try {
                no_qty = Integer.valueOf(gNoStyleQty);
                if (no_qty == 0 && isNewGroup) {
                    isComplete = false;
                    group_no_style_qty.setError("The quantity must be greater than 0");
                    group_no_style_qty.setText("");
                    group_no_style_qty.requestFocus();
                } else if (no_qty < -1) {
                    isComplete = false;
                    group_no_style_qty.setError("The minimum quantity is -1 for unlimited preorder group");
                    group_no_style_qty.setText("");
                    group_no_style_qty.requestFocus();
                }

                if (groupType == 0) {
                    if (no_qty == -1) {
                        isComplete = false;
                        group_no_style_qty.setError("The quantity for in stock group must be greater than 0");
                    }
                }
                groupQtyMap.put("p___" + productId, no_qty);

            } catch (Exception e) {
                Log.d(TAG, "onSubmitNewGroup: error" + e);
            }
        }

        if (groupQtyMap != null) {
            for (Map.Entry<String, Integer> entry : groupQtyMap.entrySet()) {

                //check if the style qty is missing
                if (entry.getValue() == null) {
                    isComplete = false;
                    // trigger alert
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("No Quantity");
                    builder.setMessage("Please input the quantity for the style(s)");
//                builder.setMessage("Please input the quantity for " + entry.getKey());
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                }
                if (groupType == 0 && isNewGroup) {
                    if (entry.getValue() < 1) {
                        isComplete = false;
                        // trigger alert
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Invalid Quantity");
                        builder.setMessage("The quantity for in stock group must be greater than 0");
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

        if (groupStyles.size() > 0) {
            newGroup.setGroupStyles(groupStyles);
            if (!isNewGroup) {
                editGroup.setGroupStyles(groupStyles);
            }
        }


        if (!groupQtyMap.isEmpty()) {
            newGroup.setGroupQtyMap(groupQtyMap);
            if (!isNewGroup) {
                editGroup.setGroupStyles(groupStyles);
            }
        }

        if (isRestockChecked == false) {
            isComplete = false;
            Toast.makeText(getActivity(), "Please restock inventory first", Toast.LENGTH_SHORT).show();
        }

        if (isNewGroup) {
            uploadGroup(newGroup, isComplete);
        } else {
            updateGroup(editGroup, isComplete, groupId);
        }
    }

    private void restockInventory(boolean isRestockChecked) {
        if (isRestockChecked) {
            DatabaseReference productRef = firebaseDatabase.getReference("Product").child(productId);
            DatabaseReference inventoryRef = firebaseDatabase.getReference("Inventory");
            DatabaseReference productStyleRef = productRef.child("productStyles");

            if (selectedRestockInventory != null) {
                for (String key : selectedRestockInventory.keySet()) {
                    String styleId;
                    String split = "s___";
                    if (key.startsWith("p___")) {
                        productRef.child("inStock").setValue(0);
                        Log.d(TAG, "Restock: update product instock: " + productId + " 0");
                        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Integer toAdd = selectedRestockInventory.get(key);
                                    Inventory inventory = ds.getValue(Inventory.class);
                                    if (inventory.getProductStyleKey().equals(productId)) {
                                        Integer newInStock = inventory.getInStock() + toAdd;
                                        ds.child("inStock").getRef().setValue(newInStock);
                                        Log.d(TAG, "Restock: update inventory instock: " + ds.getKey() + " " + Integer.toString(newInStock));
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else {
                        Integer toAdd = selectedRestockInventory.get(key);
                        styleId = key.split(split)[1];
                        productStyleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    String sid = ds.child("styleId").getValue(String.class);
                                    if (sid.equals(styleId)) {
                                        ds.child("inStock").getRef().setValue(0);
                                        Log.d(TAG, "Restock: update product instock: " + sid + " 0");
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Inventory inventory = ds.getValue(Inventory.class);
                                    if (inventory.getProductStyleKey().equals(productId + "_" + styleId)) {
                                        Integer newInStock = inventory.getInStock() + toAdd;
                                        ds.child("inStock").getRef().setValue(newInStock);
                                        Log.d(TAG, "Restock: update inventory instock: " + ds.getKey() + " " + Integer.toString(newInStock));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }
            }


        }
    }

    private void updateGroup(Group group, boolean isComplete, String groupId) {
        if (isComplete) {
            groupRef.child(groupId).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("New Group Upload", "Group Upload Successfully");
                    } else {
                        Log.d("New Group Upload", "Group Upload Failed");
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

    private void uploadGroup(Group group, boolean isComplete) {
        if (isNewGroup) { //it is a new group
            groupId = groupRef.push().getKey(); //Group -> groupId -> newGroup
            Log.d(TAG, "new group: " + groupId);
        }
        if (isComplete) {
            groupRef.child(groupId).setValue(group).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("New Group Upload", "Group Upload Successfully");
                    } else {
                        Log.d("New Group Upload", "Group Upload Failed");
                    }
                    restockInventory(isRestockChecked);
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
                        btnEnd.setText("To " + simpleDateFormat.format(endTime));
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
                        btnStart.setText("From " + simpleDateFormat.format(startTime));
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
        if (productId == null) {
            Log.e(TAG, "Product ID is null");
            return;
        }

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
                groupQtyMap.clear();
                if (!dataSnapshot.exists()) {
                    Log.d(TAG, "There are no product styles");
                    groupQtyMap.put("p___" + productId, null);
                    Log.d(TAG, "qtyMap: " + groupQtyMap);
                }
                groupStyles.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("styleName").getValue(String.class);
                    String img = snapshot.child("stylePicName").getValue(String.class);
                    Double price = snapshot.child("stylePrice").getValue(Double.class);
                    String styleId = snapshot.child("styleId").getValue(String.class);
                    ProductStyle ps = new ProductStyle(name, price, img, styleId);
                    groupStyles.add(ps);
                    groupQtyMap.put("s___" + ps.getStyleId(), null);
//                    Toast.makeText(getContext(), ps.getStyleName() + "qty: " +  groupQtyMap.get(ps) , Toast.LENGTH_SHORT ).show();
                }
                comparePriceRange();
                stylesAdapter.updateStyleData2(productId, groupStyles);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "Cannot retrieve data from database", error.toException());
            }
        });
    }


}