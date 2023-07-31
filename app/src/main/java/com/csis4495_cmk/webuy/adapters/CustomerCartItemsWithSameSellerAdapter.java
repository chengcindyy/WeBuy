package com.csis4495_cmk.webuy.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.models.Group;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
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
    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Group");
    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("Customer").child(customerId).child("Cart");

    private final int IN_STOCK = 0;

    public CustomerCartItemsWithSameSellerAdapter(Context context, String sellerId, CartItemsViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.sellerId = sellerId;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        viewModel.getSellerItemsMap().observe(lifecycleOwner, new Observer<Map<String, ArrayList<CartItem>>>() {
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

        Log.d("TestMap", cartItems.size()+"");
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

        String groupId = cartItems.get(position).getGroupId();
        String productId = cartItems.get(position).getProductId();
        String styleId = cartItems.get(position).getStyleId();

        int orderAmount = cartItems.get(position).getAmount();
        holder.etOrderAmount.setText(String.valueOf(orderAmount));
        //TODO: if qty = -1
        final int[] inventoryAmount = {9999};

        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String groupPicName = null;
                String groupPrice = "CA$ N/A";
                String styleName = "Style N/A";
                String groupName = "Group N/A";


                Group group = snapshot.getValue(Group.class);
                if (group == null) {
                    Log.d("TestGroup",groupId+" is null");
                } else {
                    Log.d("TestGroup", groupId+ " is not null");
                    groupName = group.getGroupName();
                    if (styleId == null) {
                        groupPicName = group.getGroupImages().get(0);
                        groupPrice = group.getGroupPrice();
                        holder.tvGroupStyle.setVisibility(View.GONE);
                        Log.d("TestGroup","no style");
                        if (group.getGroupType() == IN_STOCK) {
                            inventoryAmount[0] = group.getGroupQtyMap().get("p___"+productId);
                        }

                    } else { // with style
                        holder.tvGroupStyle.setVisibility(View.VISIBLE);
                        for(DataSnapshot styleShot: snapshot.child("groupStyles").getChildren()){
                            if (styleShot.child("styleId").getValue(String.class).equals(styleId)) {
                                groupPicName = styleShot.child("stylePicName").getValue(String.class);
                                groupPrice = "CA$ " + styleShot.child("stylePrice").getValue(Double.class);
                                styleName = styleShot.child("styleName").getValue(String.class);

                                if (group.getGroupType() == IN_STOCK) {
                                    inventoryAmount[0] = group.getGroupQtyMap().get("s___"+styleId);
                                }
                            }
                        }
                    }
                    StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage").child(productId);
                    imgRef.child(groupPicName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imgUrl = uri.toString();
                            Picasso.get().load(imgUrl).into(holder.imvGroupPic);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //pic not found
                        }
                    });

                    holder.tvGroupStyle.setText(styleName);
                    Log.d("TestGroup", groupPrice);
                }

                holder.tvGroupName.setText(groupName);
                holder.tvPrice.setText(groupPrice);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                        if (inventoryAmount[0] == -1) {
                            if(editAmount <= 0) {
                                holder.etOrderAmount.setError("Amount must be greater than 0");
                            } else {
                                holder.etOrderAmount.setError(null);
                                //save to db
                                cartItems.get(holder.getBindingAdapterPosition()).setAmount(editAmount);
                                cartRef.child(sellerId).setValue(cartItems);
                            }
                        } else {
                            if (editAmount <= 0 || editAmount > inventoryAmount[0]) {
                                // Show an error or notify the user that the amount is invalid
                                holder.etOrderAmount.setError("Amount must be greater than 0 and less than "+inventoryAmount[0]);
                            } else {
                                // The amount is valid, do something with it
                                holder.etOrderAmount.setError(null);
                                //save to db
                                cartItems.get(holder.getBindingAdapterPosition()).setAmount(editAmount);
                                cartRef.child(sellerId).setValue(cartItems);
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
                if (editAmount < inventoryAmount[0]) {
                    holder.btnIncreaseAmount.setEnabled(true);
                }
                holder.etOrderAmount.setText((String.valueOf(editAmount)));

                if(editAmount == 1) {
                    holder.btnDecreaseAmount.setEnabled(false);
                }
            }
            cartItems.get(holder.getBindingAdapterPosition()).setAmount(editAmount);
            cartRef.child(sellerId).setValue(cartItems);
        });

        holder.btnIncreaseAmount.setOnClickListener(v -> {
            int editAmount = Integer.parseInt(String.valueOf(holder.etOrderAmount.getText()));
            if(editAmount >= inventoryAmount[0]) {
                holder.btnIncreaseAmount.setEnabled(false);
            } else {
                if (editAmount == inventoryAmount[0]-1) {
                    holder.btnIncreaseAmount.setEnabled(false);
                }
                editAmount++;
                holder.etOrderAmount.setText(String.valueOf(editAmount));
                holder.btnDecreaseAmount.setEnabled(true);
            }
            cartItems.get(holder.getBindingAdapterPosition()).setAmount(editAmount);
            cartRef.child(sellerId).setValue(cartItems);
        });


        //delete cart item
        holder.btnDeleteCartItem.setOnClickListener(v->{

            //dialog - confirm deletion
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete the item");
            builder.setMessage("Are you sure you want to delete the item from the cart?");
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    cartItems.remove(holder.getBindingAdapterPosition());
                    cartRef.child(sellerId).setValue(cartItems);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        });

        //set checked
        boolean checked = cartItems.get(holder.getBindingAdapterPosition()).getChecked();
        Log.d("TestAllCheck", cartItems.get(holder.getBindingAdapterPosition()).getGroupId() +" InnerSetChecked "+ checked);
        holder.cbxSingleItem.setChecked(checked);

        holder.cbxSingleItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                //set checked
                cartItems.get(holder.getBindingAdapterPosition()).setChecked(true);
            } else {
                //set all unchecked
                cartItems.get(holder.getBindingAdapterPosition()).setChecked(false);
            }
            sellerItemsMap.put(sellerId, cartItems);
            viewModel.setSellerItemsMap(sellerItemsMap);

            //check if check all
            boolean allChecked = true;
            for (CartItem cartItem: cartItems) {
                if (!cartItem.getChecked()) {
                    allChecked = false;
                }
            }
            //set seller all checked
            Log.d("TestAllCheck", "inner checked map size "+ sellerAllItemsCheckedMap.size());
            Log.d("TestAllCheck", sellerId +" is all checked "+ allChecked);
            sellerAllItemsCheckedMap.put(sellerId, allChecked);
            Log.d("TestAllCheck", "inner checked map size "+ sellerAllItemsCheckedMap.size());
            viewModel.setSellerAllItemsCheckedMap(sellerAllItemsCheckedMap);
            onSingleCartItemListener.onAllItemsChecked(allChecked);

        });

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
        Button btnDecreaseAmount;
        Button btnIncreaseAmount;

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
            btnDeleteCartItem = itemView.findViewById(R.id.imvbtn_delete_cart_item);
            view = itemView;
        }
    }
    public interface onSingleCartItemListener{
        void onAllItemsChecked(Boolean isAllChecked);
    }
}
