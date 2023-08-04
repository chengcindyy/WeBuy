package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Store;
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
import java.util.Arrays;
import java.util.List;

public class SellerProfileSetPaymentFragment extends Fragment {

    private ExpandableLayout expPaymentInfo, expBankInfo;
    private CheckBox checkBoxEmt, checkBoxCashDelivery, checkBoxCashStore, checkBoxCard;
    private ArrayList<String> acceptedPaymentTypes;
    private Button btnSavePayment, btnSaveBank;
    private TextInputLayout edtAccount, edtAccountHolder;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser firebaseUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference("Seller");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_payment_setting, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // References
        // ExpandableLayout
        expPaymentInfo = view.findViewById(R.id.expandableLayout_payment_info);
        expBankInfo = view.findViewById(R.id.expandableLayout_seller_bank_info);
        setupExpandableLayout(expPaymentInfo, R.drawable.baseline_info_24, "Accepted Payment types");
        setupExpandableLayout(expBankInfo, R.drawable.baseline_playlist_add_24, "Bank account information");
        // EditText
        edtAccount = view.findViewById(R.id.edit_account);
        edtAccountHolder = view.findViewById(R.id.edit_account_name);
        // CheckBox
        checkBoxEmt = view.findViewById(R.id.checkbox_child_1);
        checkBoxCashDelivery = view.findViewById(R.id.checkbox_child_2);
        checkBoxCashStore  = view.findViewById(R.id.checkbox_child_3);
        checkBoxCard = view.findViewById(R.id.checkbox_child_4);
        // Array List
        acceptedPaymentTypes = new ArrayList<>();
        // Button
        btnSavePayment = view.findViewById(R.id.btn_save_payment);
        setOnSavePaymentButtonClickListener(btnSavePayment);
        showPaymentsData();
        btnSaveBank = view.findViewById(R.id.btn_save_bank);
        setOnSaveBankButtonClickedListener(btnSaveBank);
        showBackData();

    }

    private void showBackData() {
        DatabaseReference storeRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("bankInfo");
        storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Store bankInfo = snapshot.getValue(Store.class);
                if (bankInfo != null){
                    String _ACCOUNT = bankInfo.getBankAccount();
                    String _HOLDER = bankInfo.getBankAccountHolder();

                    edtAccount.getEditText().setText(_ACCOUNT);
                    edtAccountHolder.getEditText().setText(_HOLDER);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnSaveBankButtonClickedListener(Button button) {
        button.setOnClickListener(view -> {
            String _ACCOUNT, _HOLDER;
            _ACCOUNT = edtAccount.getEditText().getText().toString();
            _HOLDER = edtAccountHolder.getEditText().getText().toString();

            DatabaseReference storeRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("bankInfo");
            Store newBankInfo = new Store(_ACCOUNT, _HOLDER);

            storeRef.setValue(newBankInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getContext(), "Data update successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e){
                            Toast.makeText(getContext(), "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        });
    }


    private void showPaymentsData() {
        DatabaseReference storeRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("acceptedPaymentTypes");
        storeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    String paymentType = snapshot1.getValue().toString();
                    List<CheckBox> checkBoxes = Arrays.asList(checkBoxEmt, checkBoxCashDelivery, checkBoxCashStore, checkBoxCard);
                    for (CheckBox checkBox: checkBoxes) {
                        if(paymentType.equals(checkBox.getText().toString()))
                            checkBox.setChecked(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setOnSavePaymentButtonClickListener(Button button) {
        List<CheckBox> checkBoxes = Arrays.asList(checkBoxEmt, checkBoxCashDelivery, checkBoxCashStore, checkBoxCard);
        button.setOnClickListener(view -> {
            // Clean list first
            acceptedPaymentTypes.clear();

            for (CheckBox checkBox: checkBoxes) {
                if (checkBox.isChecked())
                    acceptedPaymentTypes.add(checkBox.getText().toString());
            }
            // Send to firebase
            DatabaseReference storeRef = reference.child(firebaseUser.getUid()).child("storeInfo").child("acceptedPaymentTypes");
            storeRef.setValue(acceptedPaymentTypes).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Data update successfully", Toast.LENGTH_SHORT).show();
                }
            });
        });
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