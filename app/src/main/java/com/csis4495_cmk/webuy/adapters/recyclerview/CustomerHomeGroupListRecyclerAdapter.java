package com.csis4495_cmk.webuy.adapters.recyclerview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Seller;
import com.csis4495_cmk.webuy.models.Wishlist;
import com.csis4495_cmk.webuy.viewmodels.CustomerWishlistViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CustomerHomeGroupListRecyclerAdapter extends RecyclerView.Adapter<CustomerHomeGroupListRecyclerAdapter.ViewHolder> {

    Context context;
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    public StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");

    private String groupId;
    private String productId;
    private String groupImage;
    private String groupImageUrl;
    private String groupCategory;
    private int groupType; // in-stock: 0 ; pre-order: 1
    private String groupName;
    private String groupPrice;
    private int soldAmount;
    private int dayLeft;
    private String sellerPic;
    private String sellerName;
    private String sellerId;

    private int groupStatus; // notStarted: 0; started: 1; ended: 2
//    private Long startTimestamp;
//    private Long endTimestamp;
    private Map<String,Group> groupsMap;
    private Map<String,Map<String,Integer>> soldAmountMap;
    private List<Map.Entry<String, Group>> groupsEntryList;
    private onGroupListener mGroupListener;
    private CustomerWishlistViewModel wishListViewModel;
    private LifecycleOwner lifecycleOwner;

    public CustomerHomeGroupListRecyclerAdapter(){

    }

    public CustomerHomeGroupListRecyclerAdapter(Context ct, Map<String,Group> groupsMap, LifecycleOwner lifecycleOwner, CustomerWishlistViewModel wishListViewModel){
        this.context = ct;
        this.groupsMap = groupsMap;
        this.groupsEntryList = new ArrayList<>(groupsMap.entrySet());
        this.lifecycleOwner = lifecycleOwner;
        this.wishListViewModel = wishListViewModel;
    }

    public void setOnGroupListener(onGroupListener mGroupListener) {
        this.mGroupListener = mGroupListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_customer_home_groups_new, parent, false);
        return new CustomerHomeGroupListRecyclerAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        groupId = groupsEntryList.get(position).getKey();

        //image
        productId = groupsEntryList.get(position).getValue().getProductId();
        //want image url here
        groupImage = groupsEntryList.get(position).getValue().getGroupImages().get(0);

        imgRef.child(productId).child(groupImage).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        groupImageUrl = uri.toString();
                        Picasso.get().load(groupImageUrl).into(holder.imvGroupImage);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occur while getting the download URL
                        e.printStackTrace();
                    }
                });

        groupCategory = groupsEntryList.get(position).getValue().getCategory();
        holder.tvGroupCategory.setText(groupCategory);

        groupType = groupsEntryList.get(position).getValue().getGroupType();
        if (groupType == 0) {
            holder.tvGroupType.setText("In-stock");
        } else if (groupType == 1) {
            holder.tvGroupType.setText("Pre-order");
        } else {
            holder.tvGroupType.setText("no type");
        }

        groupName = groupsEntryList.get(position).getValue().getGroupName();
        holder.tvGroupName.setText(groupName);

        groupPrice = groupsEntryList.get(position).getValue().getGroupPrice();
        holder.tvGroupPrice.setText(groupPrice);

        //TODO: soldAmount
        int soldAmount = 0;
        if (soldAmountMap!= null) {
            Log.d("sold", "sold map size: " + soldAmountMap.size());
            if (soldAmountMap.get(groupId) != null) {
                for(String psId: soldAmountMap.get(groupId).keySet()) {
                    soldAmount += soldAmountMap.get(groupId).get(psId);
                    Log.d("sold", "sold amount: " + soldAmount);
                }
            }

            holder.tvSoldAmount.setText(soldAmount + " sold");
        } else {
            Log.d("sold", "sold map null");
        }

        //seller name and pic
        sellerId = groupsEntryList.get(position).getValue().getSellerId();
        Log.d("TestSellId", sellerId);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Seller");
        reference.child(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Seller seller = snapshot.getValue(Seller.class);
                    sellerName = seller.getStoreInfo().getStoreName();
                    holder.tvSellerName.setText(sellerName);

                    sellerPic = seller.getStoreInfo().getStorePic();

                    if (sellerPic != null) {
                        Log.d("Upload img: imageUrl ", sellerPic);
                        Glide.with(context)
                                .load(sellerPic)
                                .circleCrop()
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        holder.imvSellerPic.setImageDrawable(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        holder.imvSellerPic.setImageResource(R.drawable.ic_user_profile);
                                    }
                                });
                    }
                } else {
                    // If the sellerId is not found, handle it accordingly
                    holder.tvSellerName.setText("Seller not found");
                    holder.imvSellerPic.setImageResource(R.drawable.ic_user_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.tvSellerName.setText("Error fetching seller data");
                holder.imvSellerPic.setImageResource(R.drawable.ic_user_profile);
            }
        });

        //group item Clicked
        holder.view.setOnClickListener(v -> {
            mGroupListener.onGroupClicked(groupsEntryList.get(position).getKey(),groupsEntryList.get(position).getValue());
        });

        //TODO: change the status and can filter the status (default show status 1)
        // display the due days if it is preorder
        groupStatus = groupsEntryList.get(position).getValue().getStatus(); // notStarted: 0; started: 1; ended: 2
        if (groupType == 1) {
            Long startTimestamp = groupsEntryList.get(position).getValue().getStartTimestamp();
            Long endTimestamp = groupsEntryList.get(position).getValue().getEndTimestamp();

            long currentTime = System.currentTimeMillis();

            if (startTimestamp != null) {
                //Date startTime = new Date(startTimestamp);

                if (currentTime < startTimestamp) {
                    long tillOpen = startTimestamp - System.currentTimeMillis();
                    CountDownTimer timer = new CountDownTimer(tillOpen, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                            long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                            long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                            String timeTillOpen = String.format(Locale.getDefault(), "%d d %d h %d m", days, hours, minutes);

                            holder.tvTimer.setText("Starts in " + timeTillOpen);
                        }

                        @Override
                        public void onFinish() {
                            //start, change status from 0 to 1
                            //available to buy
                        }
                    }.start();
                }
            }

            if (endTimestamp != null) {
                //Date endTime = new Date(endTimestamp);

                if (endTimestamp > currentTime && currentTime > startTimestamp) { //opening
                    long remainingTime = endTimestamp - currentTime;
                    CountDownTimer timer = new CountDownTimer(remainingTime, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                            long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                            long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                            millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                            String timeRemaining = String.format(Locale.getDefault(), "%d d %d h %d m", days, hours, minutes);

                            holder.tvTimer.setText("Ends in " + timeRemaining);
                        }
                        @Override
                        public void onFinish() {
                            //end, change status from 1 to 2
                            //unavailable to buy
                        }
                    }.start();;
                } else if (currentTime >= endTimestamp) {
                    holder.tvTimer.setText("Expired");
                }
            }
        } else { //groupType == 0 (in-stock)
            holder.tvTimer.setText("");
        }

        // Before doing onClick listener first set it as null
        holder.btnWishList.setOnClickListener(null);

        int clickedPosition = position;

        // Before doing onClick listener first set it as null
        holder.btnWishList.setOnClickListener(null);

        // Get the wishlist from the ViewModel
        ArrayList<Wishlist> wishlists = wishListViewModel.getWishlistObject().getValue();
        boolean isGroupIdInWishlist = false;
        if (wishlists != null) {
            for (Wishlist wishlist : wishlists) {
                if (wishlist.getGroupId().equals(groupId)) {
                    isGroupIdInWishlist = true;
                    break;
                }
            }
        }
        holder.btnWishList.setChecked(isGroupIdInWishlist);
        holder.btnWishList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get current item
                String currentGroupId = groupsEntryList.get(clickedPosition).getKey();
                String currentSellerId = groupsEntryList.get(clickedPosition).getValue().getSellerId();
                String currentProductId = groupsEntryList.get(clickedPosition).getValue().getProductId();

                // Set to wishlist
                Wishlist wishlistItem = new Wishlist();
                wishlistItem.setGroupId(currentGroupId);
                wishlistItem.setSellerId(currentSellerId);
                wishlistItem.setProductId(currentProductId);
                Log.d("Test wishlistItem", "groupId"+ wishlistItem.getGroupId() + " ProductId: "+ wishlistItem.getProductId());

                if (holder.btnWishList.isChecked()) {
                    Toast.makeText(context,"Item added to Wish List",Toast.LENGTH_SHORT).show();
                    wishListViewModel.addToWishlist(wishlistItem, firebaseUser.getUid());
                } else {
                    wishListViewModel.removeFromWishlist(wishlistItem, firebaseUser.getUid());
                    Toast.makeText(context,"Item removed from Wish List",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupsEntryList.size();
    }

    public void updateData(Map<String,Group> groupsMap) {
        this.groupsMap = groupsMap;
        this.groupsEntryList = new ArrayList<>(groupsMap.entrySet());
        notifyDataSetChanged();
    }

    public void updateSoldAmountMap(Map<String,Map<String,Integer>> soldAmountMap) {
        this.soldAmountMap = soldAmountMap;
        notifyDataSetChanged();
    }

    public void reverseData(Map<String, Group> newGroupMap) {
        List<String> groupIds = new ArrayList<>(newGroupMap.keySet());
        List<Group> groupList = new ArrayList<>(newGroupMap.values());
        Collections.reverse(groupIds);
        Collections.reverse(groupList);
        Map<String, Group> reversedMap = new LinkedHashMap<>();
        for (int i = 0; i < groupIds.size(); i++) {
            reversedMap.put(groupIds.get(i), groupList.get(i));
            Log.d("Test PriceRange", "mGroupMap: "+reversedMap.keySet());
        }
        this.groupsMap.putAll(reversedMap);
        this.groupsEntryList.clear();
        this.groupsEntryList.addAll(reversedMap.entrySet());
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        ToggleButton btnWishList;
        ImageView imvGroupImage;
        TextView tvGroupCategory;
        TextView tvGroupType;
        TextView tvGroupName;
        TextView tvGroupPrice;
        TextView tvSoldAmount;
        TextView tvTimer;
        ImageView imvSellerPic;
        TextView tvSellerName;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnWishList = itemView.findViewById(R.id.saveToWishListButton);
            imvGroupImage = itemView.findViewById(R.id.groupImage);
            tvGroupCategory = itemView.findViewById(R.id.groupCategory);
            tvGroupType = itemView.findViewById(R.id.groupType);
            tvGroupName = itemView.findViewById(R.id.groupName);
            tvGroupPrice = itemView.findViewById(R.id.groupPrice);
            tvSoldAmount = itemView.findViewById(R.id.soldAmount);
            tvTimer = itemView.findViewById(R.id.timer);
            imvSellerPic = itemView.findViewById(R.id.seller_picture);
            tvSellerName = itemView.findViewById(R.id.seller_name);
            view = itemView;
        }
    }

    public interface onGroupListener{
        void onGroupClicked(String groupId, Group group);
    }
}
