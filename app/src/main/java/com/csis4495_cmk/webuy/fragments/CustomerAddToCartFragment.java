package com.csis4495_cmk.webuy.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
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
import com.csis4495_cmk.webuy.adapters.recyclerview.CustomerGroupStyleAdapter;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.csis4495_cmk.webuy.viewmodels.CustomerCartItemsViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerAddToCartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerAddToCartFragment extends BottomSheetDialogFragment
        implements CustomerGroupStyleAdapter.onStyleItemListener{

    private final int DIRECTLY_BUY = 0;
    private NavController navController;
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

    TextView tvGroupPrice, tvInventoryAmount;
    EditText etOrderAmount;
    ImageView imvGroupPic;
    Button btnDecrease, btnIncrease;

    int inventoryAmount;
    int orderAmount;

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

        //set navigation controller
        navController = NavHostFragment.findNavController(CustomerAddToCartFragment.this);

        imvGroupPic = view.findViewById(R.id.imv_add_to_cart_group_pic);
        TextView tvGroupName = view.findViewById(R.id.tv_add_to_cart_group_name);
        tvGroupPrice = view.findViewById(R.id.tv_add_to_cart_group_price);
        tvInventoryAmount = view.findViewById(R.id.tv_add_to_cart_inventory_amount);
        RecyclerView rvGroupStyle = view.findViewById(R.id.rv_add_to_cart_group_style);
        TextView tvGroupStyle = view.findViewById(R.id.tv_add_to_cart_group_style);
        btnDecrease = view.findViewById(R.id.btn_decrease_amount);
        btnIncrease = view.findViewById(R.id.btn_increase_amount);
        etOrderAmount = view.findViewById(R.id.edi_add_to_cart_order_amount);
        Button btnAddToCart = view.findViewById(R.id.btn_add_to_cart);
        Button btnDirectCheckout = view.findViewById(R.id.btn_direct_checkout);

        //set the group
        orderAmount =Integer.parseInt(etOrderAmount.getText().toString());

        //set styles
        if (group.getGroupStyles() != null) {
            tvGroupStyle.setVisibility(View.VISIBLE);
            CustomerGroupStyleAdapter styleAdapter = new CustomerGroupStyleAdapter(getContext(), group.getProductId(), group.getGroupStyles(), preSelectedStylePosition);
            rvGroupStyle.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvGroupStyle.setAdapter(styleAdapter);
            styleAdapter.setmOnStyleItemListener(CustomerAddToCartFragment.this);

            //TODO: get product inventory amount (amount left(toSell - ordered))
            if(preSelectedStylePosition != -1) { //preselected
            inventoryAmount = group.getGroupQtyMap().get("s___" + selectedStyle.getStyleId());
            if (inventoryAmount == -1) {
                tvInventoryAmount.setText("unlimited amount");
            } else {
                tvInventoryAmount.setText(inventoryAmount + " left");
            }

            } else {
                tvInventoryAmount.setText("");
                inventoryAmount = -1;
            }

        } else { //no style
            tvGroupStyle.setVisibility(View.GONE);
            //TODO: get product inventory amount (amount left(toSell - ordered))
            inventoryAmount = group.getGroupQtyMap().get("p___"+group.getProductId());
            if (inventoryAmount == -1) {
                tvInventoryAmount.setText("unlimited amount");
            } else {
                tvInventoryAmount.setText(inventoryAmount + " left");
            }

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
                        if (inventoryAmount == -1) {
                            if(amount <= 0) {
                                etOrderAmount.setError("Amount must be greater than 0");
                            } else {
                                etOrderAmount.setError(null);
                            }
                        } else {
                            if (amount <= 0 || amount > inventoryAmount) {
                                // Show an error or notify the user that the amount is invalid
                                etOrderAmount.setError("Amount must be greater than 0 and less than "+inventoryAmount);
                            } else {
                                // The amount is valid, do something with it
                                etOrderAmount.setError(null);
                            }
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
            orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            Log.d("testAmount", orderAmount +"");
            if(orderAmount > 1) {
                orderAmount--;
                if (orderAmount < inventoryAmount || inventoryAmount == -1) {
                    btnIncrease.setEnabled(true);
                }

                Log.d("testAmount", orderAmount +"--");
                etOrderAmount.setText(String.valueOf(orderAmount));

                if(orderAmount == 1) {
                    btnDecrease.setEnabled(false);
                }
            }
        });

        btnIncrease.setOnClickListener(v -> {
            orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            if(inventoryAmount == -1) {
                btnIncrease.setEnabled(true);
                orderAmount++;
                etOrderAmount.setText(String.valueOf(orderAmount));
            } else {
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
            }

        });
        btnDirectCheckout.setVisibility(View.GONE);
        //TODO: direct checkout
        btnDirectCheckout.setOnClickListener(v -> {
            orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            //check style
            if (group.getGroupStyles() != null) {
                if (selectedStyle == null) {
                    Toast.makeText(getContext(), "Please select a style", Toast.LENGTH_SHORT).show();
                } else {
                    //checkAmountValidity(inventoryAmount,getContext());
                }
            } else {
                //checkAmountValidity(inventoryAmount,getContext());
                Log.d("addToCart","with style");
            }

            CustomerCheckoutFragment.newInstance(DIRECTLY_BUY);
            navController.navigate(R.id.customerCheckoutFragment);

        });
        //add to cart
        btnAddToCart.setOnClickListener(v -> {
            orderAmount = Integer.parseInt(String.valueOf(etOrderAmount.getText()));
            //check style
            if (group.getGroupStyles() != null) {
                if (selectedStyle == null) {
                    Toast.makeText(getContext(), "Please select a style", Toast.LENGTH_SHORT).show();
                } else {
                    checkAmountValidity(inventoryAmount,getContext());
                    Log.d("addToCart","with style: " + selectedStyle.getStyleName());
                }
            } else {
                checkAmountValidity(inventoryAmount,getContext());
                Log.d("addToCart","without style");
            }

        });

    }

    private void checkAmountValidity(int inventoryAmount, Context context) {
        //check amount
        if (inventoryAmount == -1) { //unlimited
            Log.d("addToCart","unlimited item");
            if (orderAmount <= 0) {
                etOrderAmount.setError("Amount must be greater than 0");
            } else {
                //upload
                CartItem item;
                if (selectedStyle == null) {
                    item = new CartItem(groupId, group.getSellerId(), group.getProductId(), orderAmount);
                } else {
                    item = new CartItem(groupId, group.getSellerId(), group.getProductId(), selectedStyle.getStyleId(), orderAmount);
                }

                // check if item existing
                checkItemInCart(item, new OnCheckItemInCartListener() {
                    @Override
                    public void onItemExists(boolean exists) {
                        if (exists) {
                            // Item exists in the cart
                            Log.d("addToCart","exists");
                            // show dialog
                            showRedirectToCartDialog(context,getDialog());
                            // Do something
                        } else { // Item does not exist in the cart
                            Log.d("addToCart","not exist, upload");
                            //upload item with style to customerRef
                            uploadNewCartItem(item);
                            //show item added and pop back to the previous page
                            Toast.makeText(context,"Item Added to the Cart!", Toast.LENGTH_SHORT).show();
                            onDismiss(getDialog());
                            //TODO:cart small badges added
                        }
                    }
                });
            }

        } else {
            Log.d("addToCart","limited item");
            if (orderAmount <= 0 || orderAmount > inventoryAmount) {
                Log.d("addToCart","error amount");
                etOrderAmount.setError("Amount must be greater than 0 and less than " + inventoryAmount);
            } else {
                //upload
                CartItem item;
                if (selectedStyle == null) {
                    Log.d("addToCart","to upload, w/o style");
                    item = new CartItem(groupId, group.getSellerId(), group.getProductId(), orderAmount);
                } else {
                    Log.d("addToCart","to upload, w/ style");
                    item = new CartItem(groupId, group.getSellerId(), group.getProductId(), selectedStyle.getStyleId(), orderAmount);
                }

                // check if item existed
                checkItemInCart(item, exists -> {
                    if (exists) {
                        // Item exists in the cart
                        Log.d("addToCart","exists");
                        // show dialog
                        showRedirectToCartDialog(context,getDialog());
                    } else { // Item does not exist in the cart
                        Log.d("addToCart","not exist, upload");
                        //upload item with style to customerRef
                        uploadNewCartItem(item);
                        //show item added and pop back to the previous page
                        Toast.makeText(context,"Item Added to the Cart!", Toast.LENGTH_SHORT).show();
                        onDismiss(getDialog());
                        //TODO:cart small badges added
                    }
                });
            }
        }
    }

    private void showRedirectToCartDialog(Context context, Dialog addToCartDialog) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder .setTitle("Go to shopping cart")
                .setMessage("The item already existed, you want to check at your cart?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the addToCartDialog
                        onDismiss(addToCartDialog);
                        // Navigate to customerCartFragment
                        navController.navigate(R.id.action_customerAddToCartFragment_to_customerCartFragment);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onDismiss(addToCartDialog);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void checkItemInCart(CartItem item, OnCheckItemInCartListener listener) {
        CustomerCartItemsViewModel viewModel = new ViewModelProvider(requireActivity()).get(CustomerCartItemsViewModel.class);
        viewModel.getSellerItemsMapLiveData().observe(requireActivity(), new Observer<Map<String, ArrayList<CartItem>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<CartItem>> stringArrayListMap) {
                if (stringArrayListMap.containsKey(item.getSellerId())) {
                    ArrayList<CartItem> cartItems = stringArrayListMap.get(item.getSellerId());
                    Log.d("addToCart",cartItems.size()+" seller items");
                    for (CartItem existedCartItem : cartItems) {
                        if (item.equals(existedCartItem)) {
                            viewModel.getSellerItemsMapLiveData().removeObserver(this);
                            listener.onItemExists(true);
                            return;
                        }
                    }
                } else {
                    Log.d("addToCart","new item");
                }
                viewModel.getSellerItemsMapLiveData().removeObserver(this);
                listener.onItemExists(false); //if two exists, will cause loop (add more than one)// need to test null to exist
            }
        });
    }

    // Interface to provide the callback result
    interface OnCheckItemInCartListener {
        void onItemExists(boolean exists);
    }

    private void uploadNewCartItem(CartItem item) {
        // upload to firebase
        String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerId);
        customerRef.child("Cart").child(item.getSellerId()).push().setValue(item);
    }

    @Override
    public void onStyleClicked(int stylePosition, ProductStyle style) {
        //Toast.makeText(getContext(),"styleId: "+ style.getStyleId(), Toast.LENGTH_SHORT).show();
        tvGroupPrice.setText("CA$ "+ style.getStylePrice());
        selectedStyle = style;

        //set img
        int imgPosition = group.getGroupImages().size() + stylePosition;
        Picasso.get().load(imgUrls.get(imgPosition)).into(imvGroupPic);

        //set inventory amount
        inventoryAmount = group.getGroupQtyMap().get("s___" + selectedStyle.getStyleId());
        if (inventoryAmount == -1) {
            tvInventoryAmount.setText("unlimited amount");
        } else {
            tvInventoryAmount.setText(inventoryAmount + " left");
        }

        Log.d("TestCom", orderAmount + " " +inventoryAmount);
        if(inventoryAmount == -1) {
            btnIncrease.setEnabled(true);
            etOrderAmount.setError(null);
        } else {
            if (orderAmount < inventoryAmount) {
                btnIncrease.setEnabled(true);
                etOrderAmount.setError(null);
            } else if (orderAmount == inventoryAmount) {
                btnIncrease.setEnabled(false);
                etOrderAmount.setError(null);
            } else {
                btnIncrease.setEnabled(false);
                etOrderAmount.setError("Amount must be greater than 0 and less than "+inventoryAmount);
            }
        }

    }
}