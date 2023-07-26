package com.csis4495_cmk.webuy.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.csis4495_cmk.webuy.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;

public class SellerInventoryStockManagementFragment extends BottomSheetDialogFragment{

    private static final String ARG_INVENTORY_ID = "inventoryId";
    private static final String ARG_STYLE_NAME = "styleName";
    private static final String ARG_IN_STOCK = "inStock";
    private static final String ARG_ORDERED = "ordered";
    private static final String ARG_TO_ORDER = "toOrder";
    onStockButtonClickListener stockListener;
    private TextView inventoryName, inventoryQty ,inventoryRequired, inventoryNeeded;
    private Button btnStockIn, btnStockOut;
    private TextInputLayout etStockInValue, etStockOutValue;


    private String inventoryId, styleName;
    private int inStock, ordered, toOrder;

    public SellerInventoryStockManagementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param inventoryId inventoryId
     * @param styleName Style/product's name
     * @param inStock Current inventory amount
     * @param ordered Current the product's ordered amount.
     * @param toOrder The number of the product needs to be ordered. (inStock - ordered)
     * @return A new instance of fragment sellerInventoryStockInFragment.
     */
    public static SellerInventoryStockManagementFragment newInstance(String inventoryId, String styleName, int inStock, int ordered, int toOrder) {
        SellerInventoryStockManagementFragment fragment = new SellerInventoryStockManagementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_INVENTORY_ID, inventoryId);
        args.putString(ARG_STYLE_NAME, styleName);
        args.putInt(ARG_IN_STOCK, inStock);
        args.putInt(ARG_ORDERED, ordered);
        args.putInt(ARG_TO_ORDER, toOrder);
        fragment.setArguments(args);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnStockButtonClickListener(onStockButtonClickListener listener) {
        this.stockListener = listener;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            inventoryId = getArguments().getString(ARG_INVENTORY_ID);
            styleName = getArguments().getString(ARG_STYLE_NAME);
            inStock = getArguments().getInt(ARG_IN_STOCK);
            ordered = getArguments().getInt(ARG_ORDERED);
            toOrder = getArguments().getInt(ARG_TO_ORDER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_inventory_stock_management, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TextView
        inventoryName = view.findViewById(R.id.txv_inventory_name_value);
        inventoryQty = view.findViewById(R.id.txv_qty_value);
        inventoryRequired = view.findViewById(R.id.txv_to_order_value);
        inventoryNeeded = view.findViewById(R.id.txv_shortfall_value);
        // Button
        btnStockIn = view.findViewById(R.id.btn_stock_in);
        btnStockOut = view.findViewById(R.id.btn_stock_out);
        // EditText
        etStockInValue = view.findViewById(R.id.et_stock_in_value);
        etStockOutValue = view.findViewById(R.id.et_stock_out_value);

        // Set default text
        inventoryName.setText(styleName);
        inventoryQty.setText(String.valueOf(inStock));
        inventoryRequired.setText(String.valueOf(ordered));
        inventoryNeeded.setText(String.valueOf(toOrder));

        // Se Button Click listener
        btnStockIn.setOnClickListener(view1 -> {
            String _NUM = etStockInValue.getEditText().getText().toString();

            if(TextUtils.isEmpty(_NUM)){
                Toast.makeText(getContext(),
                        "Please enter a value", Toast.LENGTH_SHORT).show();
                etStockInValue.setError("a number is required");
                etStockInValue.requestFocus();
            } else {
                int stockInAmount = Integer.parseInt(_NUM);
                stockListener.onStockInButtonClicked(inventoryId, stockInAmount);
                Toast.makeText(getContext(), "Inventory updated", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });

        btnStockOut.setOnClickListener(view12 -> {
            String _NUM = etStockOutValue.getEditText().getText().toString();

            if(TextUtils.isEmpty(_NUM)){
                Toast.makeText(getContext(),
                        "Please enter a value", Toast.LENGTH_SHORT).show();
                etStockOutValue.setError("a number is required");
                etStockOutValue.requestFocus();
            } else {
                int stockOutAmount = Integer.parseInt(_NUM);
                stockListener.onStockOutButtonClicked(inventoryId, stockOutAmount);
                Toast.makeText(getContext(), "Inventory updated", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }

    public interface onStockButtonClickListener{
        void onStockInButtonClicked(String inventoryId, int stockIn);
        void onStockOutButtonClicked(String inventoryId, int stockOut);
    }
}