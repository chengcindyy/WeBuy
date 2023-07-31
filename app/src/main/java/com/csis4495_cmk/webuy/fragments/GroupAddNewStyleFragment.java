package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.GroupEditStyleAdapter;
import com.csis4495_cmk.webuy.models.Product;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.csis4495_cmk.webuy.viewmodels.SharedEditStyleViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupAddNewStyleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupAddNewStyleFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String productId;

    private List<ProductStyle> newEditGroupStyles;

    private Map<String, String> newEditGroupStylesMap;

    private GroupEditStyleAdapter adapter;
    private TextView title;

    private RecyclerView recyclerView;

    private Button btnCancel, btnAdd;

    private SharedEditStyleViewModel model;

    public GroupAddNewStyleFragment() {
        // Required empty public constructor
    }



    // TODO: Rename and change types and number of parameters
    public static GroupAddNewStyleFragment newInstance(String param1, String param2) {
        GroupAddNewStyleFragment fragment = new GroupAddNewStyleFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupAddNewStyleFragment newInstance(String productId, List<ProductStyle> newStyles) {
        GroupAddNewStyleFragment fragment = new GroupAddNewStyleFragment();
        Bundle args = new Bundle();

        for (ProductStyle ps : newStyles) {
            args.putString("s___" + ps.getStyleId(), ps.getStyleName());
        }
        args.putString("productId", productId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        model = new ViewModelProvider(getActivity()).get(SharedEditStyleViewModel.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newEditGroupStyles = new ArrayList<>();
        newEditGroupStylesMap = new HashMap<>();
        Bundle args = getArguments();
        if (args != null) {
            for (String key : args.keySet()) {
                if (key.startsWith("s___")) {
                    newEditGroupStylesMap.put(key, args.getString(key));
                } else {
                    productId = args.getString("productId");
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_add_new_style, container, false);
        title = view.findViewById(R.id.tv_add_group_style_frag);
        recyclerView = view.findViewById(R.id.rv_add_group_style);
        btnCancel = view.findViewById(R.id.btn_add_group_style_cancel);
        btnAdd = view.findViewById(R.id.btn_add_group_style_add);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Boolean> myData = adapter.getToAddMap();
                Log.d(TAG, "onClick: getToAddMap" + myData);
                model.select(myData);
                dismiss();
            }
        });
        getProductStyle();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
            View bottomSheetInternal = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheetInternal != null) {
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheetInternal);
                behavior.setPeekHeight(Resources.getSystem().getDisplayMetrics().heightPixels / 2);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    private void getProductStyle() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference productReference = firebaseDatabase.getReference().child("Product").child(productId);
        productReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                newEditGroupStyles.clear();
                Product p;
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        p = dataSnapshot.getValue(Product.class);
                        for(ProductStyle ps : p.getProductStyles()){
                            String matchingKey = "s___"+ps.getStyleId();
                            for(String key : newEditGroupStylesMap.keySet()){
                                if(matchingKey.equals(key)){
                                    newEditGroupStyles.add(ps);
                                }
                            }
                        }
                        adapter = new GroupEditStyleAdapter(newEditGroupStylesMap, productId, newEditGroupStyles);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    }
                }
            }
        });
    }
}