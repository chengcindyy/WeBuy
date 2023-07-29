package com.csis4495_cmk.webuy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CustomerCartItemsWithSameSellerAdapter extends RecyclerView.Adapter<CustomerCartItemsWithSameSellerAdapter.ViewHolder> {

    Context context;
    ArrayList<CartItem> cartItems;
    DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Group");

    public CustomerCartItemsWithSameSellerAdapter(Context context, ArrayList<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        Log.d("TestMap", cartItems.size()+"");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_customer_cart_item, parent, false);
        return new CustomerCartItemsWithSameSellerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckBox checkBox;
        ImageView imvGroupPic;
        TextView tvGroupName;
        TextView tvGroupStyle;
        TextView tvPrice;
        EditText etAmount;
        Button btnDecreaseAmount;
        Button btnIncreaseAmount;

        String groupId = cartItems.get(position).getGroupId();
        String productId = cartItems.get(position).getProductId();
        String styleId = cartItems.get(position).getStyleId();

        int amount = cartItems.get(position).getAmount();
        holder.etOrderAmount.setText(String.valueOf(amount));


        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group group = snapshot.getValue(Group.class);
                holder.tvGroupName.setText(group.getGroupName());
                String groupPic = null;
                String groupPrice = "CA$";
                String styleName = null;

                if (styleId == null) {
                    groupPic = group.getGroupImages().get(0);
                    groupPrice = group.getGroupPrice();
                    holder.tvGroupStyle.setVisibility(View.GONE);
                } else {
                    holder.tvGroupStyle.setVisibility(View.VISIBLE);
                    for(DataSnapshot styleShot: snapshot.child("groupStyles").getChildren()){
                        if (styleShot.child("styleId").getValue(String.class) == styleId) {
                            groupPic = styleShot.child("stylePicName").getValue(String.class);
                            groupPrice = "CA$ " + styleShot.child("stylePrice").getValue(Double.class);
                            //stlyeName
                        }
                    }
                }

                holder.tvPrice.setText(groupPrice);
                //set imv
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //holder.tvGroupName.setText("test");
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;
        ImageView imvGroupPic;
        TextView tvGroupName;
        TextView tvGroupStyle;
        TextView tvPrice;
        EditText etOrderAmount;
        Button btnDecreaseAmount;
        Button btnIncreaseAmount;

        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox_single_group);
            imvGroupPic = itemView.findViewById(R.id.imv_cart_item_group_pic);
            tvGroupName = itemView.findViewById(R.id.tv_cart_item_group_name);
            tvGroupStyle = itemView.findViewById(R.id.tv_cart_item_group_style);
            tvPrice = itemView.findViewById(R.id.tv_cart_item_group_price);
            etOrderAmount = itemView.findViewById(R.id.et_cart_item_order_amount);
            btnDecreaseAmount = itemView.findViewById(R.id.btn_cart_item_decrease_amount);
            btnIncreaseAmount = itemView.findViewById(R.id.btn_cart_item_increase_amount);
            view = itemView;
        }
    }
}
