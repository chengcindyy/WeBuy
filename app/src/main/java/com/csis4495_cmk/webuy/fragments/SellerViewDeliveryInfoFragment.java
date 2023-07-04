package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cottacush.android.currencyedittext.CurrencyEditText;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.SellerDeliveryRecyclerAdapter;
import com.csis4495_cmk.webuy.adapters.SellerProductListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Delivery;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.skydoves.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellerViewDeliveryInfoFragment extends Fragment {

    private ExpandableLayout expDeliveryInfo, expCreateDelivery;
    private TextInputLayout editCustomName;
    private CurrencyEditText editSpendOver, editShippingFee;
    private AutoCompleteTextView editDeliveryMethods;
    private RecyclerView mRecyclerView;
    private Button btnCreate;
    private List<String> keysList;
    SellerDeliveryRecyclerAdapter adapter;
    private HashMap<String, Delivery> deliveryHashMap;
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
        editDeliveryMethods = view.findViewById(R.id.dropMenu_delivery_methods);
        setDeliveryAutoCompleteAdapter(editDeliveryMethods);
        editCustomName = view.findViewById(R.id.edit_delivery_name);
        editSpendOver = view.findViewById(R.id.edit_shipping_fee_range_from);
        editShippingFee = view.findViewById(R.id.edit_shipping_fee_range_fee);
        // Setup ExpandableLayout
        expDeliveryInfo = view.findViewById(R.id.expandableLayout_store_delivery_info);
        expCreateDelivery = view.findViewById(R.id.expandableLayout_seller_add_delivery_method);
        setupExpandableLayout(expDeliveryInfo, R.drawable.baseline_info_24, "Delivery information");
        setupExpandableLayout(expCreateDelivery, R.drawable.baseline_playlist_add_24, "Create a new method");
        // Lists
        deliveryHashMap = new HashMap<>();
        keysList = new ArrayList<>();
        // Set ListView
        mRecyclerView = view.findViewById(R.id.recyclerView_delivery_info);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SellerDeliveryRecyclerAdapter(getContext(), deliveryHashMap, keysList);
        mRecyclerView.setAdapter(adapter);
        expDeliveryInfo.requestLayout();

        showDeliveryInfo();




        // Button
        btnCreate = view.findViewById(R.id.btn_create_delivery_info);
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewDeliveryInfoListView();
            }
        });


    }

    private void showDeliveryInfo() {
        DatabaseReference deliveryRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("deliveryInfoList");
        deliveryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliveryHashMap.clear();
                keysList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Delivery delivery = snapshot.getValue(Delivery.class);
                    String key = snapshot.getKey();

                    if (delivery != null && key != null) {
                        deliveryHashMap.put(key, delivery);
                        keysList.add(key);
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error here
            }
        });
    }

    private void createNewDeliveryInfoListView() {
        String _METHOD, _NAME;
        Double _FROM, _FEE;
        // Obtain the entered data
        _METHOD = editDeliveryMethods.getText().toString();
        _NAME = editCustomName.getEditText().getText().toString();
        _FROM = editSpendOver.getNumericValue();
        _FEE = editShippingFee.getNumericValue();

        Delivery newDeliveryInfo = new Delivery(_METHOD,_NAME,_FROM,_FEE);

        DatabaseReference deliveryRef = reference.child(firebaseUser.getUid()).child("storeInfo");
        String key = deliveryRef.child("deliveryInfoList").push().getKey();
        if (key != null) {
            deliveryRef.child("deliveryInfoList").child(key).setValue(newDeliveryInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Data update successfully", Toast.LENGTH_SHORT).show();
                        deliveryHashMap.put(key, newDeliveryInfo);
                        keysList.add(key);
                        Log.d("Test map", deliveryHashMap.get(key).getDisplayName());
                        Log.d("Test map", keysList.get(0));
                        adapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(adapter);
                        expDeliveryInfo.requestLayout();

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
        // States (Canada)
        String[] states = new String[]{"Face to Face/Store Pick Up", "Home delivery", "Cash on Delivery (COD)", "Designated Area Self-Pickup"};
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_dropdown_item, states);
        editDeliveryMethods.setAdapter(stateAdapter);
        editDeliveryMethods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupExpandableLayoutClickListener(ExpandableLayout expandableLayout) {
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
}