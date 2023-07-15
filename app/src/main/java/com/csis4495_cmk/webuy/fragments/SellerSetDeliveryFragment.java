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

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SellerSetDeliveryFragment extends Fragment {

    private ExpandableLayout expDeliveryInfo, expCreateDelivery;
    private TextInputLayout editCustomName;
    private CurrencyEditText editSpendOver, editShippingFee;
    private AutoCompleteTextView editDeliveryMethods;
    private RecyclerView mRecyclerView;
    private Button btnCreate;
    private List<String> keysList;
    private SellerDeliveryRecyclerAdapter adapter;
    private HashMap<String, Delivery> deliveryHashMap;
    private String methodId = "";
    private Delivery deliveryInfo;
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
        // Text fields
        editDeliveryMethods = view.findViewById(R.id.dropMenu_delivery_methods);
        setDeliveryAutoCompleteAdapter(editDeliveryMethods);
        editCustomName = view.findViewById(R.id.edit_delivery_name);
        editSpendOver = view.findViewById(R.id.edit_shipping_fee_range_from);
        editShippingFee = view.findViewById(R.id.edit_shipping_fee_range_fee);
        // Lists
        deliveryHashMap = new HashMap<>();
        keysList = new ArrayList<>();
        // Set RecyclerView
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
                createOrUpdateNewDeliveryInfoRecyclerView();
            }
        });
        // Call swipe helper
        OnRecyclerItemSwipeActionHelper();
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

                // Swiped item right: open edit page
                } else if (direction == ItemTouchHelper.RIGHT) {
                    methodId = keysList.get(position);
                    Bundle bundle = new Bundle();
                    bundle.putString("methodId", methodId);
                    SellerAddProductFragment sellerAddProductFragment = new SellerAddProductFragment();
                    sellerAddProductFragment.setArguments(bundle);
                    showDeliveryData();

                }
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

    private void showDeliveryData() {
        DatabaseReference deliveryRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("deliveryInfoList");
        deliveryRef.child(methodId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                deliveryInfo = snapshot.getValue(Delivery.class);
                if(deliveryInfo != null){
                    if(deliveryInfo != null){
                        String _METHOD = deliveryInfo.getDeliveredMethod();
                        String _NAME = deliveryInfo.getDisplayName();
                        Double _FROM = deliveryInfo.getFrom();
                        Double _FEE = deliveryInfo.getFee();

                        editDeliveryMethods.setText(_METHOD);
                        editCustomName.getEditText().setText(_NAME);
                        editSpendOver.setText(String.valueOf(_FROM));
                        editShippingFee.setText(String.valueOf(_FEE));

                        btnCreate.setText("Update");
                    } else {
                        Toast.makeText(getContext(), "No delivery information available.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e("MethodId", "Snapshot to Delivery conversion failed.");
                }
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
        builder.setTitle("Remove this product");
        builder.setMessage("Are you sure you want to remove this product?");
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
                // Do nothing
            }
        });
    }

    private void createOrUpdateNewDeliveryInfoRecyclerView() {
        String _METHOD, _NAME;
        Double _FROM, _FEE;
        // Obtain the entered data
        _METHOD = editDeliveryMethods.getText().toString();
        _NAME = editCustomName.getEditText().getText().toString();
        _FROM = editSpendOver.getNumericValue();
        _FEE = editShippingFee.getNumericValue();

        Delivery newDeliveryInfo = new Delivery(_METHOD,_NAME,_FROM,_FEE);

        DatabaseReference deliveryRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("deliveryInfoList");

        if(methodId.isEmpty()){
            methodId = deliveryRef.push().getKey();
        }

        // Update or create new delivery info
        if (methodId != null) {
            deliveryRef.child(methodId).setValue(newDeliveryInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Data update successfully", Toast.LENGTH_SHORT).show();
                        deliveryHashMap.put(methodId, newDeliveryInfo);
                        if (!keysList.contains(methodId)) {
                            keysList.add(methodId);
                        }
                        Log.d("Test map", deliveryHashMap.get(methodId).getDisplayName());
                        Log.d("Test map", keysList.get(0));
                        adapter.notifyDataSetChanged();
                        mRecyclerView.setAdapter(adapter);
                        expDeliveryInfo.requestLayout();
                        methodId = "";
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
        String[] states = new String[]{"[Store Pick Up] ", "[Home delivery] ", "[Cash on Delivery] ", "[Designated Area Self-Pickup] "};
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