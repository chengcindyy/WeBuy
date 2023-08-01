package com.csis4495_cmk.webuy.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.viewmodels.CartItemsViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class CustomerCartItemsWithSameSellerAdapter extends RecyclerView.Adapter<CustomerCartItemsWithSameSellerAdapter.ViewHolder> {

    Context context;

    onSingleCartItemListener onSingleCartItemListener;
    CartItemsViewModel viewModel;
    LifecycleOwner lifecycleOwner;
    String sellerId;
    Map<String, ArrayList<CartItem>> sellerItemsMap;
    ArrayList<CartItem> cartItems;
    Map<String, Boolean> sellerAllItemsCheckedMap;
    Map<CartItem, CartItemsViewModel.CartItemInfo> cartItemsInfoMap;

    private final int IN_STOCK = 0;
    private final int PRE_ORDER = 1;
    private final int OPENING = 1;

    public CustomerCartItemsWithSameSellerAdapter(Context context, String sellerId, CartItemsViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.sellerId = sellerId;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        viewModel.getSellerItemsMapLiveData().observe(lifecycleOwner, new Observer<Map<String, ArrayList<CartItem>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<CartItem>> stringArrayListMap) {
                sellerItemsMap = stringArrayListMap;
                cartItems = stringArrayListMap.get(sellerId);

            }
        });
        viewModel.getSellerAllItemsCheckedMap().observe(lifecycleOwner, new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> stringBooleanMap) {
                sellerAllItemsCheckedMap = stringBooleanMap;
            }
        });

        viewModel.getCartItemsInfoMapLiveData().observe(lifecycleOwner, new Observer<Map<CartItem, CartItemsViewModel.CartItemInfo>>() {
            @Override
            public void onChanged(Map<CartItem, CartItemsViewModel.CartItemInfo> cartItemCartItemInfoMap) {
                cartItemsInfoMap = cartItemCartItemInfoMap;
            }
        });

        Log.d("TestMap", cartItems.size()+"");
        Log.d("GGG", cartItemsInfoMap.size()+"");
    }

    public void setOnSingleCartItemListener(onSingleCartItemListener onSingleCartItemListener) {
        this.onSingleCartItemListener = onSingleCartItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_customer_cart_item, parent, false);
        return new CustomerCartItemsWithSameSellerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CartItem cartItem = cartItems.get(position);
        //set CartItemsInfoMap in ViewModel
        CartItemsViewModel.CartItemInfo cartItemInfo = cartItemsInfoMap.get(cartItem);
        Log.d("GGG", cartItemsInfoMap.size() +" after set");

        if (cartItemInfo != null) {
            if (cartItemInfo.getGroupName().equals("Group N/A")) { //group is deleted in firebase
                holder.view.setAlpha(0.5f);
                holder.view.setOnClickListener(v->{
                    Toast.makeText(context, "The group is no longer available", Toast.LENGTH_SHORT).show();
                });
                holder.cbxSingleItem.setChecked(false);
                holder.cbxSingleItem.setEnabled(false);
                holder.btnIncreaseAmount.setEnabled(false);
                holder.btnDecreaseAmount.setEnabled(false);
                holder.tvGroupName.setTextColor(ContextCompat.getColor(context,R.color.light_grey));
                holder.tvPrice.setTextColor(ContextCompat.getColor(context,R.color.light_grey));
                holder.tvGroupStyle.setTextColor(ContextCompat.getColor(context,R.color.light_grey));

            } else {
                Log.d("TestGroup", "group is not null");
                holder.view.setOnClickListener(v->{
                    // TODO: open the group detail
                });

                if (cartItemInfo.getStyleName() == null) {
                    holder.tvGroupStyle.setVisibility(View.GONE);
                    Log.d("TestGroup","no style");
                } else { // with style
                    holder.tvGroupStyle.setVisibility(View.VISIBLE);
                }

                Picasso.get().load(cartItemInfo.getGroupPicUrl()).into(holder.imvGroupPic);

                //pre-order group expired
                if (cartItemInfo.getGroupType() == PRE_ORDER) {
                    if (cartItemInfo.getGroupEndTimestamp() < System.currentTimeMillis()) {
                        holder.view.setAlpha(0.5f);
                        holder.view.setOnClickListener(v -> {
                            Toast.makeText(context, "The group is no longer available", Toast.LENGTH_SHORT).show();
                        });
                        holder.cbxSingleItem.setChecked(false);
                        holder.cbxSingleItem.setEnabled(false);
                        holder.btnIncreaseAmount.setEnabled(false);
                        holder.btnDecreaseAmount.setEnabled(false);
                        holder.tvGroupName.setTextColor(ContextCompat.getColor(context, R.color.light_grey));
                        holder.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.light_grey));
                        holder.tvGroupStyle.setTextColor(ContextCompat.getColor(context, R.color.light_grey));
                    }
                }
                // group not opening
                if (cartItemInfo.getGroupStatus() != OPENING) {
                    holder.view.setAlpha(0.5f);
                    holder.view.setOnClickListener(v -> {
                        Toast.makeText(context, "The group is no longer available", Toast.LENGTH_SHORT).show();
                    });
                    holder.cbxSingleItem.setChecked(false);
                    holder.cbxSingleItem.setEnabled(false);
                    holder.btnIncreaseAmount.setEnabled(false);
                    holder.btnDecreaseAmount.setEnabled(false);
                    holder.tvGroupName.setTextColor(ContextCompat.getColor(context, R.color.light_grey));
                    holder.tvPrice.setTextColor(ContextCompat.getColor(context, R.color.light_grey));
                    holder.tvGroupStyle.setTextColor(ContextCompat.getColor(context, R.color.light_grey));
                }
            }

            holder.tvGroupName.setText(cartItemInfo.getGroupName());
            holder.tvPrice.setText(cartItemInfo.getGroupPrice());
            holder.tvGroupStyle.setText(cartItemInfo.getStyleName());

            int orderAmount = cartItem.getAmount();
            holder.etOrderAmount.setText(String.valueOf(orderAmount));
            if(orderAmount > cartItemInfo.getInventoryAmount()) {
                holder.etOrderAmount.setError("Amount must be greater than 0 and less than "+cartItemInfo.getInventoryAmount());
                holder.etOrderAmount.requestFocus();
            }

            //etOrderAmount TextWatcher
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
                            int editAmount = Integer.parseInt(inputText);
                            if (cartItemInfo.getInventoryAmount() == -1) {
                                if(editAmount <= 0) {
                                    holder.etOrderAmount.setError("Amount must be greater than 0");
                                } else {
                                    holder.etOrderAmount.setError(null);
                                    //save to db
                                    cartItems.get(holder.getBindingAdapterPosition()).setAmount(editAmount);
                                    sellerItemsMap.put(sellerId, cartItems);
                                    viewModel.setSellerItemsMapLiveData(sellerItemsMap);
                                }
                            } else {
                                if (editAmount <= 0 || editAmount > cartItemInfo.getInventoryAmount() ) {
                                    // Show an error or notify the user that the amount is invalid
                                    holder.etOrderAmount.setError("Amount must be greater than 0 and less than "+cartItemInfo.getInventoryAmount());
                                } else {
                                    // The amount is valid, do something with it
                                    holder.etOrderAmount.setError(null);
                                    //save to db
                                    cartItems.get(holder.getBindingAdapterPosition()).setAmount(editAmount);
                                    sellerItemsMap.put(sellerId, cartItems);
                                    viewModel.setSellerItemsMapLiveData(sellerItemsMap);
                                }
                            }

                        } catch (NumberFormatException e) {
                            // Handle the case when the input cannot be parsed as an integer
                            holder.etOrderAmount.setError("Invalid input");
                        }
                    } else {
                        // Handle the case when the input is empty
                        holder.etOrderAmount.setError("Amount is required");
                    }
                }
            };
            holder.etOrderAmount.addTextChangedListener(watcher);

            //increase and decrease the amount
            holder.btnDecreaseAmount.setOnClickListener(v -> {
                int editAmount = Integer.parseInt(String.valueOf(holder.etOrderAmount.getText()));
                if (editAmount > 1) {
                    editAmount--;
                    if (editAmount < cartItemInfo.getInventoryAmount() ) {
                        holder.btnIncreaseAmount.setEnabled(true);
                    }
                    //holder.etOrderAmount.setText((String.valueOf(editAmount)));
                } else {
                    showDeleteDialog(holder);
                }
                cartItems.get(holder.getBindingAdapterPosition()).setAmount(editAmount);
                sellerItemsMap.put(sellerId, cartItems);
                viewModel.setSellerItemsMapLiveData(sellerItemsMap);
            });

            holder.btnIncreaseAmount.setOnClickListener(v -> {
                int editAmount = Integer.parseInt(String.valueOf(holder.etOrderAmount.getText()));
                if(editAmount >= cartItemInfo.getInventoryAmount() ) {
                    holder.btnIncreaseAmount.setEnabled(false);
                    Toast.makeText(context,"Only "+ cartItemInfo.getInventoryAmount() + " left, " +
                            "cannot order more than " + cartItemInfo.getInventoryAmount(), Toast.LENGTH_SHORT).show();
                } else {
                    if (editAmount == cartItemInfo.getInventoryAmount() -1) {
                        holder.btnIncreaseAmount.setEnabled(false);
                        Toast.makeText(context,"Only "+ cartItemInfo.getInventoryAmount() + " left, " +
                                "cannot order more than " + cartItemInfo.getInventoryAmount(), Toast.LENGTH_SHORT).show();
                    }
                    editAmount++;
                    Log.d("HHH","increased");
                    //holder.etOrderAmount.setText(String.valueOf(editAmount));
                    holder.btnDecreaseAmount.setEnabled(true);
                }
                cartItems.get(holder.getBindingAdapterPosition()).setAmount(editAmount);
                sellerItemsMap.put(sellerId, cartItems);
                viewModel.setSellerItemsMapLiveData(sellerItemsMap);
            });

            //delete cart item
            holder.btnDeleteCartItem.setOnClickListener(v->{
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                popupMenu.getMenuInflater().inflate(R.menu.cart_item_popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        showDeleteDialog(holder);
                        return true;
                    }
                });

                popupMenu.show();
            });

            //set checked
            boolean checked = cartItem.getChecked();
            Log.d("TestAllCheck", cartItem.getGroupId() +" InnerSetChecked "+ checked);
            if (holder.cbxSingleItem.isEnabled()) {
                //holder.view.setOnClickListener(v -> {});
                holder.cbxSingleItem.setChecked(checked);

                holder.cbxSingleItem.setOnCheckedChangeListener((buttonView, isChecked) -> {

                    cartItem.setChecked(isChecked);
                    sellerItemsMap.put(sellerId, cartItems);
                    viewModel.setSellerItemsMapLiveData(sellerItemsMap);
//                    cartItemsInfoMap.put(cartItem,cartItemInfo);
//                    viewModel.setCartItemsInfoMapLiveData(cartItemsInfoMap);

                    //check if check all
                    boolean allChecked = true;
                    for (CartItem c: cartItems) {
                        if (!c.getChecked()) {
                            allChecked = false;
                        }
                    }
                    //set seller all checked
                    Log.d("TestAllCheck", "inner checked map size "+ sellerAllItemsCheckedMap.size());
                    Log.d("TestAllCheck", sellerId +" is all checked "+ allChecked);
                    sellerAllItemsCheckedMap.put(sellerId, allChecked);
                    Log.d("TestAllCheck", "inner checked map size "+ sellerAllItemsCheckedMap.size());
                    viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
                    onSingleCartItemListener.onAllItemsChecked(allChecked);

                });
            }


        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbxSingleItem;
        ImageView imvGroupPic;
        TextView tvGroupName;
        TextView tvGroupStyle;
        TextView tvPrice;
        EditText etOrderAmount;
        ImageButton btnDecreaseAmount;
        ImageButton btnIncreaseAmount;

        ImageButton btnDeleteCartItem;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbxSingleItem = itemView.findViewById(R.id.checkbox_single_group);
            imvGroupPic = itemView.findViewById(R.id.imv_cart_item_group_pic);
            tvGroupName = itemView.findViewById(R.id.tv_cart_item_group_name);
            tvGroupStyle = itemView.findViewById(R.id.tv_cart_item_group_style);
            tvPrice = itemView.findViewById(R.id.tv_cart_item_group_price);
            etOrderAmount = itemView.findViewById(R.id.et_cart_item_order_amount);
            btnDecreaseAmount = itemView.findViewById(R.id.btn_cart_item_decrease_amount);
            btnIncreaseAmount = itemView.findViewById(R.id.btn_cart_item_increase_amount);
            btnDeleteCartItem = itemView.findViewById(R.id.imvbtn_more_cart_item);
            view = itemView;
        }
    }
    public interface onSingleCartItemListener{
        void onAllItemsChecked(Boolean isAllChecked);
    }

    private void showDeleteDialog(CustomerCartItemsWithSameSellerAdapter.ViewHolder holder) {
        //dialog - confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete the item");
        builder.setMessage("Are you sure you want to delete the item from the cart?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                CartItem cartItemToRemove = cartItems.get(holder.getBindingAdapterPosition());
                cartItems.remove(holder.getBindingAdapterPosition());
                Log.d("HHH", "delete cartItems size: "+ cartItems.size());
                if (cartItems.isEmpty()) {
                    sellerItemsMap.remove(sellerId);
                    Log.d("HHH", "inner removed");
                    sellerAllItemsCheckedMap.remove(sellerId);
                    viewModel.setSellerItemsMapLiveData(sellerItemsMap);
                    viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
                } else {
                    sellerItemsMap.put(sellerId, cartItems);
                    viewModel.setSellerItemsMapLiveData(sellerItemsMap);

                    // outer seller checked status might change
                    //check if check all
                    boolean allChecked = true;
                    for (CartItem c: cartItems) {
                        if (!c.getChecked()) {
                            allChecked = false;
                        }
                    }
                    //set seller all checked
                    sellerAllItemsCheckedMap.put(sellerId, allChecked);
                    viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
                    onSingleCartItemListener.onAllItemsChecked(allChecked);
                    Log.d("HHH", "delete map size: "+ sellerItemsMap.size());
                }

                //remove from info map
                cartItemsInfoMap.remove(cartItemToRemove);
                viewModel.setCartItemsInfoMapLiveData(cartItemsInfoMap);

                Log.d("HHH", "inner map: "+sellerItemsMap.size());
                Log.d("HHH", "outer map: "+sellerAllItemsCheckedMap.size());

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
