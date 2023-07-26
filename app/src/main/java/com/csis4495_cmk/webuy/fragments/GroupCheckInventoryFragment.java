package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.CheckInventoryRecyclerAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class GroupCheckInventoryFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextView testTV;

    private RecyclerView recyclerView;

    private CheckInventoryRecyclerAdapter adapter;

    private Map<String, Integer> inventoryMap;
    
    private Map<String, String> inventoryNameMap;

    public GroupCheckInventoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InventoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupCheckInventoryFragment newInstance(String param1, String param2) {
        GroupCheckInventoryFragment fragment = new GroupCheckInventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static GroupCheckInventoryFragment newInstance(Map<String, Integer> inventoryMap, Map<String, String> inventoryNameMap){
        GroupCheckInventoryFragment fragment = new GroupCheckInventoryFragment();
        Bundle args = new Bundle();

        for (Map.Entry<String, Integer> entry : inventoryMap.entrySet()) {
            args.putInt("i_"+entry.getKey(), entry.getValue());
        }


        for (Map.Entry<String, String> entry : inventoryNameMap.entrySet()) {
            args.putString("n_"+entry.getKey(), entry.getValue());
        }

        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inventoryMap = new HashMap<>();
        inventoryNameMap = new HashMap<>();
        Bundle args = getArguments();
        if (args != null) {
            for (String key : args.keySet()) {
                String realKey = key.substring(key.indexOf("_") + 1);
                if (key.startsWith("i_")) {
                    inventoryMap.put(realKey, args.getInt(key));
                } else if (key.startsWith("n_")) {
                    inventoryNameMap.put(realKey, args.getString(key));
                }
            }
        }
        Log.d(TAG, "onCreate: inventoryMap" + inventoryMap);
        Log.d(TAG, "onCreate: inventoryNameMap" + inventoryNameMap);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_inventory, container, false);

        testTV = view.findViewById(R.id.tv_inventory_frag);

        recyclerView = view.findViewById(R.id.rv_check_inventory);

        adapter = new CheckInventoryRecyclerAdapter(inventoryMap, inventoryNameMap);

        recyclerView.setAdapter(adapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return  view;
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
        }    }
}