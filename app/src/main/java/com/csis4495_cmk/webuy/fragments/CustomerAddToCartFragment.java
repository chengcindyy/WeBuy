package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.CustomerGroupStyleAdapter;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerAddToCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerAddToCartFragment extends BottomSheetDialogFragment
        implements CustomerGroupStyleAdapter.onStyleItemListener{

    private final static String ARG_GROUPID = "groupId";
    private final static String ARG_GROUPJSON = "groupJson";
    private final static String ARG_STYLEPOS = "stylePosition";
    private final static String ARG_IMGURLS = "imgUrls";
    private String groupId;
    private String groupJson;
    private Group group;
    private int preSelectedStylePosition = -1;
    private ProductStyle selectedStyle;
    private ArrayList<String> imgUrls;

    TextView tvGroupPrice;
    ImageView imvGroupPic;


    public CustomerAddToCartFragment() {
        // Required empty public constructor
    }

    public static CustomerAddToCartFragment newInstance(String groupId, String groupJson, ArrayList<String> imgUrls) {
        CustomerAddToCartFragment fragment = new CustomerAddToCartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUPID, groupId);
        args.putString(ARG_GROUPJSON, groupJson);
        args.putStringArrayList(ARG_IMGURLS, imgUrls);
        fragment.setArguments(args);
        return fragment;
    }

    public static CustomerAddToCartFragment newInstance(String groupId, String groupJson, int preSelectedStylePosition, ArrayList<String> imgUrls) {
        CustomerAddToCartFragment fragment = new CustomerAddToCartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GROUPID, groupId);
        args.putString(ARG_GROUPJSON, groupJson);
        args.putInt(ARG_STYLEPOS, preSelectedStylePosition);
        args.putStringArrayList(ARG_IMGURLS, imgUrls);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            groupId = getArguments().getString(ARG_GROUPID);
            groupJson = getArguments().getString(ARG_GROUPJSON);
            preSelectedStylePosition = getArguments().getInt(ARG_STYLEPOS);
            Log.d("testPre", preSelectedStylePosition+"");

            // Convert the JSON string back to a Group object
            Gson gson = new Gson();
            group = gson.fromJson(groupJson, Group.class);

            if(preSelectedStylePosition != -1) {
                selectedStyle = group.getGroupStyles().get(preSelectedStylePosition);
            }
            imgUrls = getArguments().getStringArrayList(ARG_IMGURLS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_add_to_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imvGroupPic = view.findViewById(R.id.imv_add_to_cart_group_pic);
        TextView tvGroupName = view.findViewById(R.id.tv_add_to_cart_group_name);
        tvGroupPrice = view.findViewById(R.id.tv_add_to_cart_group_price);
        TextView tvInventoryAmount = view.findViewById(R.id.tv_add_to_cart_inventory_amount);
        RecyclerView rvGroupStyle = view.findViewById(R.id.rv_add_to_cart_group_style);
        TextView tvGroupStyle = view.findViewById(R.id.tv_add_to_cart_group_style);
        Button btnDecrease = view.findViewById(R.id.btn_decrease_amount);
        Button btnIncrease = view.findViewById(R.id.btn_increase_amount);
        EditText etOrderAmount = view.findViewById(R.id.edi_add_to_cart_order_amount);
        Button btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        Button btnDirectCheckout = view.findViewById(R.id.btn_direct_checkout);

        //set the group

        //set styles
        if (group.getGroupStyles() != null) {
            tvGroupStyle.setVisibility(View.VISIBLE);
            CustomerGroupStyleAdapter styleAdapter = new CustomerGroupStyleAdapter(getContext(), group.getProductId(), group.getGroupStyles(), preSelectedStylePosition);
            rvGroupStyle.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvGroupStyle.setAdapter(styleAdapter);
            styleAdapter.setmOnStyleItemListener(CustomerAddToCartFragment.this);
        } else {
            tvGroupStyle.setVisibility(View.GONE);
        }

        //imageView changed according to the style, set shape of the img
        if(preSelectedStylePosition != -1) { // style is preselected
            Picasso.get().load(imgUrls.get(group.getGroupImages().size() + preSelectedStylePosition)).into(imvGroupPic);
            tvGroupPrice.setText("CA$ "+ selectedStyle.getStylePrice());  // price changed according to the style
        } else {
            Picasso.get().load(imgUrls.get(0)).into(imvGroupPic);
            tvGroupPrice.setText(group.getGroupPrice());
        }

        tvGroupName.setText(group.getGroupName());

        //TODO: get product inventory amount (amount left(toSell - ordered))
        int inventoryAmount = 5;
        tvInventoryAmount.setText(inventoryAmount + " left");

        //amount adjustment
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString().trim();
                if (!inputText.isEmpty()) {
                    try {
                        int amount = Integer.parseInt(inputText);
                        if (amount <= 0 || amount > inventoryAmount) {
                            // Show an error or notify the user that the amount is invalid
                            etOrderAmount.setError("Amount must be greater than 0 and less than "+inventoryAmount);
                        } else {
                            // The amount is valid, do something with it
                            etOrderAmount.setError(null);
                        }
                    } catch (NumberFormatException e) {
                        // Handle the case when the input cannot be parsed as an integer
                        etOrderAmount.setError("Invalid input");
                    }
                } else {
                    // Handle the case when the input is empty
                    etOrderAmount.setError("Amount is required");
                }
            }
        };
        etOrderAmount.addTextChangedListener(watcher);

        btnDecrease.setOnClickListener(v -> {
            int orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            Log.d("testAmount", orderAmount +"");
            if(orderAmount > 1) {
                orderAmount--;
                if (orderAmount < inventoryAmount) {
                    btnIncrease.setEnabled(true);
                    //etOrderAmount.setError(null);
                }
//                orderAmount--;

                Log.d("testAmount", orderAmount +"--");
                etOrderAmount.setText(String.valueOf(orderAmount));

                if(orderAmount == 1) {
                    btnDecrease.setEnabled(false);
                }
            }
        });

        btnIncrease.setOnClickListener(v -> {
            int orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            if(orderAmount >= inventoryAmount) {
                btnIncrease.setEnabled(false);
            } else {
                if (orderAmount == inventoryAmount-1) {
                    btnIncrease.setEnabled(false);
                }
                orderAmount++;
                etOrderAmount.setText(String.valueOf(orderAmount));
                btnDecrease.setEnabled(true);
            }
        });

        //direct checkout
        //add to cart
        btnAddToCart.setOnClickListener(v -> {
            int orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            //check amount
            if (orderAmount < 0 || orderAmount > inventoryAmount) {
                etOrderAmount.setError("Amount must be greater than 0 and less than " + inventoryAmount);
            } else {
                //check if style selected if having styles
                if (group.getGroupStyles() != null) {
                    if (selectedStyle == null) {
                        Toast.makeText(getContext(), "Please select a style", Toast.LENGTH_SHORT).show();
                    } else {
                        CartItem item = new CartItem(groupId, group.getSellerId(), group.getProductId(), selectedStyle.getStyleId(), orderAmount);
                        //upload item with style to customerRef
                        uploadNewCartItem(item);
                    }
                } else {
                    CartItem item = new CartItem(groupId, group.getSellerId(), group.getProductId(), orderAmount);
                    //upload item without style to customerRef
                    uploadNewCartItem(item);
                }

                //show item added and pop back to the previous page
                //cart small badges added
            }

        });

    }

    private void uploadNewCartItem(CartItem item) {
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerId);
        customerRef.child("Cart").push().setValue(item);
    }

    @Override
    public void onStyleClicked(int stylePosition, ProductStyle style) {
        //Toast.makeText(getContext(),"styleId: "+ style.getStyleId(), Toast.LENGTH_SHORT).show();
        tvGroupPrice.setText("CA$ "+ style.getStylePrice());
        selectedStyle = style;
        //set img
        int imgPosition = group.getGroupImages().size() + stylePosition;
        Picasso.get().load(imgUrls.get(imgPosition)).into(imvGroupPic);
    }
}