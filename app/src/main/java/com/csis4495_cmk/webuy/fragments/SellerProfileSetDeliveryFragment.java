package com.csis4495_cmk.webuy.fragments;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerDeliveryEditPriceRangeRecyclerAdapter;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerDeliveryItemRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Delivery;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SellerProfileSetDeliveryFragment extends Fragment implements SellerDeliveryEditPriceRangeRecyclerAdapter.OnItemClickListener{
    private ExpandableLayout expDeliveryInfo, expCreateDelivery;
    private TextInputLayout editPickUpLocation;
    private CurrencyEditText editStartingPrice, editShippingFee;
    private AutoCompleteTextView editDeliveryMethods, editCities;
    private TextInputLayout deliveryInputLayout, pickUpInputLayout, fromInputLayout, feeInputLayout;
    private RecyclerView mRecyclerView, childRecyclerView;
    private Button btnCreate, btnAddRange;
    private List<Map.Entry<String, Double>> feeEntries;
    private List<String> keysList;
    private SellerDeliveryItemRecyclerAdapter adapter;
    private SellerDeliveryEditPriceRangeRecyclerAdapter childAdapter;
    private HashMap<String, Delivery> deliveryHashMap;
    private Map<String, Double> feeMap;
    private String methodId = "";
    private Delivery deliveryInfo;
    private SellerDeliveryEditPriceRangeRecyclerAdapter.ViewHolder holder;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("Seller");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_delivery_info, container, false);;
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // References
        // ExpandableLayout
        expDeliveryInfo = view.findViewById(R.id.expandableLayout_store_delivery_info);
        expCreateDelivery = view.findViewById(R.id.expandableLayout_seller_add_delivery_method);
        setupExpandableLayout(expDeliveryInfo, R.drawable.baseline_info_24, "Delivery information");
        setupExpandableLayout(expCreateDelivery, R.drawable.baseline_playlist_add_24, "Create a new method");
        // Text layout
        deliveryInputLayout = view.findViewById(R.id.dropMenu_delivery_city_layout);
        pickUpInputLayout = view.findViewById(R.id.edit_pick_up_location);
        fromInputLayout = view.findViewById(R.id.edit_shipping_fee_range_from_layout);
        feeInputLayout = view.findViewById(R.id.edit_shipping_fee_range_fee_layout);
        // Text fields
        editDeliveryMethods = view.findViewById(R.id.dropMenu_delivery_methods);
        setDeliveryAutoCompleteAdapter(editDeliveryMethods);
        editCities = view.findViewById(R.id.dropMenu_delivery_city);
        setCityAutoCompletedAdapter(editCities);
        editPickUpLocation = view.findViewById(R.id.edit_pick_up_location);
        editStartingPrice = view.findViewById(R.id.edit_shipping_fee_range_from);
        editShippingFee = view.findViewById(R.id.edit_shipping_fee_range_fee);
        // Lists
        deliveryHashMap = new HashMap<>();
        feeMap = new HashMap<>();
        keysList = new ArrayList<>();
        feeEntries = new ArrayList<>();
        // Set parent recyclerView
        mRecyclerView = view.findViewById(R.id.recyclerView_delivery_info);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SellerDeliveryItemRecyclerAdapter(getContext(), deliveryHashMap, keysList);
        mRecyclerView.setAdapter(adapter);
        expDeliveryInfo.requestLayout();
        showDeliveryInfo();
        // Set child recyclerView
        childRecyclerView = view.findViewById(R.id.RecyclerView_price_range);
        childRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        childAdapter = new SellerDeliveryEditPriceRangeRecyclerAdapter(getContext(), feeEntries, this);
        childRecyclerView.setAdapter(childAdapter);
        // Button
        btnCreate = view.findViewById(R.id.btn_create_delivery_info);
        btnCreate.setOnClickListener(view1 -> createOrUpdateNewDeliveryInfoRecyclerView());
        btnAddRange = view.findViewById(R.id.btn_add_range);
        btnAddRange.setOnClickListener(view12 -> {
            feeEntries.add(new AbstractMap.SimpleEntry<>("", 0.0));
            addNewPriceRangeRecyclerView();
            childAdapter.notifyDataSetChanged();
        });
        // Call swipe helper
        OnRecyclerItemSwipeActionHelper();
    }

    private void addNewPriceRangeRecyclerView() {
        if (childAdapter == null) {
            childAdapter = new SellerDeliveryEditPriceRangeRecyclerAdapter(getContext(), feeEntries, this);
            childRecyclerView.setAdapter(childAdapter);
        } else {
            childAdapter.notifyDataSetChanged();
        }
    }

    private void OnRecyclerItemSwipeActionHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position;
                position = viewHolder.getLayoutPosition();

                // Swiped item left: remove item from database
                if (direction == ItemTouchHelper.LEFT) {
                    methodId = keysList.get(position);
                    showConfirmToRemoveDialog(methodId, position);

                // Swiped item right: set data to text fields
                } else if (direction == ItemTouchHelper.RIGHT) {
                    methodId = keysList.get(position);
                    Delivery swipedDelivery = deliveryHashMap.get(methodId);
                    String selectedItem = swipedDelivery.getDeliveredMethod();
                    switch (selectedItem) {
                        case "[Self pick up] ":
                            pickUpInputLayout.setVisibility(View.VISIBLE);
                            deliveryInputLayout.setVisibility(View.GONE);
                            break;

                        case "[Home delivery] ":
                            deliveryInputLayout.setVisibility(View.VISIBLE);
                            pickUpInputLayout.setVisibility(View.GONE);
                            break;
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("methodId", methodId);
                    SellerAddProductFragment sellerAddProductFragment = new SellerAddProductFragment();
                    sellerAddProductFragment.setArguments(bundle);
                    showDeliveryDataToEditTextField();
                }
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete_red))
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.android_green))
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void showDeliveryDataToEditTextField() {
        DatabaseReference deliveryRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("deliveryInfoList");
        deliveryRef.child(methodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String _METHOD, _NAME, _CITY, _FROM = "", _FEE = "";

                deliveryInfo = snapshot.getValue(Delivery.class);
                if(deliveryInfo != null){
                    _METHOD = deliveryInfo.getDeliveredMethod();
                    _NAME = deliveryInfo.getPickUpLocation();
                    _CITY = deliveryInfo.getDeliveryCity();

                    feeEntries.clear();
                    feeMap.clear();
                    int count = 0;
                    for (DataSnapshot feeSnapshot : snapshot.child("feeMap").getChildren()) {
                        feeMap.put(feeSnapshot.getKey(), feeSnapshot.getValue(Double.class));
                        if(!feeMap.isEmpty()){
                            for (Map.Entry<String, Double> entry : feeMap.entrySet()) {
                                String fullKey = entry.getKey();
                                String[] parts = fullKey.split("_");
                                String from = parts[1];
                                Double fee = entry.getValue();
                                if (count == 0) {
                                    _FROM = from;
                                    _FEE = String.valueOf(fee);
                                } else {
                                    Map.Entry<String, Double> newEntry = new AbstractMap.SimpleEntry<>(from, fee);
                                    feeEntries.add(newEntry);
                                }
                                count++;
                            }
                        }
                        feeMap.clear(); // Clear feeMap for the next iteration
                    }
                    childAdapter.notifyDataSetChanged(); // Notify data set changed here
                    editDeliveryMethods.setText(_METHOD);
                    editCities.setText(_CITY);
                    editPickUpLocation.getEditText().setText(_NAME);
                    editStartingPrice.setText(_FROM);
                    editShippingFee.setText(_FEE);

                    btnCreate.setText("Update");
                } else {
                    Log.e("MethodId", "Snapshot to Delivery conversion failed.");
                }
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Do nothing
            }
        });
    }




    private void showConfirmToRemoveDialog(String methodId, int position) {
        //Set up alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Remove this delivery method?");
        builder.setMessage("Are you sure you want to remove this delivery method?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                adapter.removeItem(position);
                adapter.notifyItemRemoved(position);
                reference.child(firebaseUser.getUid()).child("storeInfo").child("deliveryInfoList").child(methodId).removeValue();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                adapter.notifyItemChanged(position);
            }
        });
        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }

    private void showDeliveryInfo() {
        DatabaseReference deliveryRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("deliveryInfoList");
        deliveryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryHashMap.clear();
                keysList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    Delivery delivery = snapshot.getValue(Delivery.class);

                    if (delivery != null && key != null) {
                        deliveryHashMap.put(key, delivery);
                        keysList.add(key);
                    }
                }
                adapter.notifyDataSetChanged();
                //return false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Do nothing
            }
        });
    }

    private void createOrUpdateNewDeliveryInfoRecyclerView() {

        feeMap.clear();

        // Obtain the entered data
        String _METHOD = editDeliveryMethods.getText().toString();
        String _LOCATION = editPickUpLocation.getEditText().getText().toString();
        String _CITY = editCities.getText().toString();
        String _PRICE = String.valueOf((int) editStartingPrice.getNumericValue());
        Double _FEE = editShippingFee.getNumericValue();

        // Validate data
        if (TextUtils.isEmpty(_METHOD)) {
            Toast.makeText(getContext(),
                    "Please select a delivery method", Toast.LENGTH_SHORT).show();
            editDeliveryMethods.setError("Delivery method is required.");
            editDeliveryMethods.requestFocus();
        } else if (!TextUtils.equals(_METHOD, editDeliveryMethods.getText().toString())) {
            Toast.makeText(getContext(),
                    "Entered context does not match, please select again", Toast.LENGTH_SHORT).show();
            editDeliveryMethods.setError("Delivery method is required.");
            editDeliveryMethods.requestFocus();
        } else if (_METHOD.equals("[Self pick up] ") && TextUtils.isEmpty(_LOCATION)) {
            Toast.makeText(getContext(),
                    "Please enter a location", Toast.LENGTH_SHORT).show();
            editPickUpLocation.setError("Location is required.");
            editPickUpLocation.requestFocus();
        } else if (_METHOD.equals("[Home delivery] ") && TextUtils.isEmpty(_CITY)) {
            Toast.makeText(getContext(),
                    "Please select a city", Toast.LENGTH_SHORT).show();
            editCities.setError("City is required.");
            editCities.requestFocus();
        } else if (TextUtils.isEmpty(_PRICE)) {
            Toast.makeText(getContext(),
                    "Please enter the applicable price begin range", Toast.LENGTH_SHORT).show();
            editStartingPrice.setError("price is required.");
            editStartingPrice.requestFocus();
        } else if (_FEE == null || _FEE.equals(Double.NaN)) {
            Toast.makeText(getContext(),
                    "Please a shipping fee", Toast.LENGTH_SHORT).show();
            editShippingFee.setError("price is required.");
            editShippingFee.requestFocus();
        } else {
            feeMap.put("key_"+_PRICE, _FEE);
            for (int i = 0; i < childAdapter.getItemCount(); i++) {
                holder = (SellerDeliveryEditPriceRangeRecyclerAdapter.ViewHolder) childRecyclerView.findViewHolderForAdapterPosition(i);
                String _OVER = holder.getCostOver();
                Double _MOREFEE = holder.getShippingFee();
                feeMap.put("key_"+_OVER, _MOREFEE);
            }

            Delivery newDeliveryInfo = null;

            if (_METHOD.equals("[Self pick up] ")){
                newDeliveryInfo = new Delivery(_METHOD,_LOCATION, feeMap);
            } else if (_METHOD.equals("[Home delivery] ")) {
                newDeliveryInfo = new Delivery(_METHOD, feeMap, _CITY);
            }

            DatabaseReference deliveryRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("deliveryInfoList");

            if(methodId.isEmpty()){
                methodId = deliveryRef.push().getKey();
            }

            // Update or create new delivery info
            if (methodId != null) {
                Delivery finalNewDeliveryInfo = newDeliveryInfo;
                deliveryRef.child(methodId).setValue(newDeliveryInfo).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Data update successfully", Toast.LENGTH_SHORT).show();
                        deliveryHashMap.put(methodId, finalNewDeliveryInfo);
                        if (!keysList.contains(methodId)) {
                            keysList.add(methodId);
                        }
                        adapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(adapter);
                        expDeliveryInfo.requestLayout();
                        methodId = "";

                        // Clear all text fields
                        editDeliveryMethods.setText("");
                        editPickUpLocation.getEditText().setText("");
                        editCities.setText("");
                        editStartingPrice.setText("");
                        editShippingFee.setText("");
                        for (int i = 0; i < childAdapter.getItemCount(); i++) {
                            holder = (SellerDeliveryEditPriceRangeRecyclerAdapter.ViewHolder) childRecyclerView.findViewHolderForAdapterPosition(i);
                            if (holder != null) {
                                holder.clearFields();
                            }
                        }

                        // Change button text back to "CREATE"
                        btnCreate.setText("CREATE");
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }
    }


    private void setupExpandableLayout(ExpandableLayout layout, int res, String Title) {
        List<Pair<ExpandableLayout, Pair<Integer, String>>> list = new ArrayList<>();
        list.add(new Pair<>(layout, new Pair<>(res, Title)));

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
    }

    private void setDeliveryAutoCompleteAdapter(AutoCompleteTextView editDeliveryMethods) {
        String[] deliveryMethods = new String[]{"[Self pick up] ", "[Home delivery] "};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, deliveryMethods);
        editDeliveryMethods.setAdapter(stateAdapter);
        editDeliveryMethods.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = parent.getItemAtPosition(position).toString();
            Toast.makeText(getContext(), selectedItem, Toast.LENGTH_SHORT).show();

            switch (selectedItem) {
                case "[Self pick up] ":
                    pickUpInputLayout.setVisibility(View.VISIBLE);
                    deliveryInputLayout.setVisibility(View.GONE);
                    break;

                case "[Home delivery] ":
                    deliveryInputLayout.setVisibility(View.VISIBLE);
                    pickUpInputLayout.setVisibility(View.GONE);
                    break;
            }
        });
    }



//    "[Cash on Delivery] ", "[Designated Area Self-Pickup]

    private void setCityAutoCompletedAdapter(AutoCompleteTextView editLocations) {
        String[] cities = new String[]{"Anmore", "Belcarra", "Bowen Island", "Burnaby", "Coquitlam", "Delta", "Langley", "Langley Township", "Lions Bay", "Maple Ridge", "New Westminster", "North Vancouver City", "North Vancouver District", "Pitt Meadows", "Port Coquitlam", "Port Moody", "Richmond", "Surrey", "Vancouver", "West Vancouver", "White Rock", "Electoral Area A"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, cities);
        editLocations.setAdapter(stateAdapter);
        editLocations.setOnItemClickListener((parent, view, position, id)
                -> Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show());
    }

    private void setupExpandableLayoutClickListener(ExpandableLayout expandableLayout) {
        expandableLayout.parentLayout.setOnClickListener(v -> {
            if (expandableLayout.isExpanded()) {
                expandableLayout.collapse();
            } else {
                expandableLayout.expand();
            }
        });
    }

    @Override
    public void onRemoveItemButtonClick(int position) {
        childAdapter.removeItem(position);
    }
}