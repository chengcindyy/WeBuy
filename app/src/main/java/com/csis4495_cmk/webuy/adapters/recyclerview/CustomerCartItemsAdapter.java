package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.viewmodels.CustomerCartItemsViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

public class CustomerCartItemsAdapter extends RecyclerView.Adapter<CustomerCartItemsAdapter.ViewHolder>
                                    implements CustomerCartItemsWithSameSellerAdapter.onSingleCartItemListener{

    Context context;
    CustomerCartItemsViewModel viewModel;
    LifecycleOwner lifecycleOwner;
    Map<String, ArrayList<CartItem>> sellerItemsMap;
    Map<String, Boolean> sellerAllItemsCheckedMap;
    ArrayList<String> sellerIds;

    onCartSellerBannerListener mCartSellerBannerListener;

    DatabaseReference sellerRef = FirebaseDatabase.getInstance().getReference("Seller");

    public CustomerCartItemsAdapter(Context context, CustomerCartItemsViewModel viewModel, LifecycleOwner lifecycleOwner) {
        this.context = context;
        this.viewModel = viewModel;
        this.lifecycleOwner = lifecycleOwner;
        viewModel.getSellerItemsMapLiveData().observe(lifecycleOwner, new Observer<Map<String, ArrayList<CartItem>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<CartItem>> stringArrayListMap) {
                sellerItemsMap = stringArrayListMap;
                sellerIds = new ArrayList<>(stringArrayListMap.keySet());
            }
        });
        viewModel.getSellerAllItemsCheckedMap().observe(lifecycleOwner, new Observer<Map<String, Boolean>>() {
            @Override
            public void onChanged(Map<String, Boolean> stringBooleanMap) {
                sellerAllItemsCheckedMap = stringBooleanMap;
            }
        });
    }

    public void setOnCartSellerBannerListener(onCartSellerBannerListener mCartSellerBannerListener) {
        this.mCartSellerBannerListener = mCartSellerBannerListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_customer_cart_seller_banner, parent, false);
        return new CustomerCartItemsAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //set seller
        String sellerId = sellerIds.get(position);
        sellerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String storeName = snapshot.child(sellerId).child("storeInfo").child("storeName").getValue(String.class);
                String storePic = snapshot.child(sellerId).child("storeInfo").child("storePic").getValue(String.class);

                holder.tvSellerName.setText(storeName);
                if (storePic != null && storePic != "") {
                    Picasso.get().load(storePic).into(holder.imvSellerPic);
                } else {
                    holder.imvSellerPic.setImageResource(R.drawable.app_logo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set inner rv
        holder.rvCartItemsWithSameSeller.setLayoutManager(new LinearLayoutManager(context));
        CustomerCartItemsWithSameSellerAdapter customerCartItemsWithSameSellerAdapter =
                new CustomerCartItemsWithSameSellerAdapter(context, sellerId, viewModel, lifecycleOwner);
        holder.rvCartItemsWithSameSeller.setAdapter(customerCartItemsWithSameSellerAdapter);
        customerCartItemsWithSameSellerAdapter.setOnSingleCartItemListener(this);

        //set cbx linked data with inner
        Log.d("HHH", "sellers :: " + sellerAllItemsCheckedMap.size());
        Log.d("HHH", sellerId+ " all checked:: "+ sellerAllItemsCheckedMap.get(sellerId));

        // Remove the listener before setting the state
        holder.cbxAllGroups.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mCartSellerBannerListener.onSellerBannerChecked(sellerAllItemsCheckedMap,sellerItemsMap);
        });

        //setting the check state
        boolean allChecked = false;
        if (sellerAllItemsCheckedMap.get(sellerId) != null) {
            allChecked = sellerAllItemsCheckedMap.get(sellerId);
        }
        holder.cbxAllGroups.setChecked(allChecked);
        // Add the listener back after setting the state
        holder.cbxAllGroups.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                for(CartItem cartItem: sellerItemsMap.get(sellerId)){

                    cartItem.setChecked(isChecked);
                }
                viewModel.setSellerItemsMapLiveData(sellerItemsMap);
                sellerAllItemsCheckedMap.put(sellerId, isChecked);
                Log.d("TestAllCheck", sellerId +" OuterClicked "+ isChecked);
                viewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
                //holder.rvCartItemsWithSameSeller.setAdapter(customerCartItemsWithSameSellerAdapter);
                mCartSellerBannerListener.onSellerBannerChecked(sellerAllItemsCheckedMap,sellerItemsMap);

            }
        });

        //no items
        Log.d("HHH", "no items:::"+sellerItemsMap.get(sellerId).size());

    }

    @Override
    public int getItemCount() {
        return sellerIds.size();
    }

    @Override
    public void onAllItemsChecked(Boolean isAllChecked) {
        //notifyDataSetChanged();
        Log.d("TestAllCheck", "onAllItemsChecked() "+ isAllChecked);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CheckBox cbxAllGroups;
        ImageView imvSellerPic;
        TextView tvSellerName;
        RecyclerView rvCartItemsWithSameSeller;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbxAllGroups = itemView.findViewById(R.id.checkbox_all_groups);
            imvSellerPic = itemView.findViewById(R.id.imv_cart_seller_banner_seller_picture);
            tvSellerName = itemView.findViewById(R.id.tv_cart_seller_banner_seller_name);
            rvCartItemsWithSameSeller = itemView.findViewById(R.id.rv_cust_cart_items_same_seller);
            view = itemView;
        }
    }

    public interface onCartSellerBannerListener {
        void onSellerBannerChecked(Map<String,Boolean> sellerAllItemsCheckedMap,Map<String, ArrayList<CartItem>> sellerItemsMap);
    }

}
