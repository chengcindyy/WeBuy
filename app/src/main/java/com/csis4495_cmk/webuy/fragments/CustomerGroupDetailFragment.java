package com.csis4495_cmk.webuy.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.viewpager.CustomerGroupDetailImageViewPagerAdapter;
import com.csis4495_cmk.webuy.adapters.recyclerview.CustomerGroupStyleAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.csis4495_cmk.webuy.models.Seller;
import com.csis4495_cmk.webuy.models.Wishlist;
import com.csis4495_cmk.webuy.viewmodels.CustomerWishlistViewModel;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class CustomerGroupDetailFragment extends Fragment
                                        implements CustomerGroupStyleAdapter.onStyleItemListener {
    ViewPager2 viewPagerGroupImg;
    CustomerGroupDetailImageViewPagerAdapter viewPagerAdapter;
    String groupId;
    Group group;
    String groupJson;
    ProductStyle selectedStyle;

    List<ProductStyle> groupStyles;
    ArrayList<String> groupImgUrls;
    List<Group> wishList;
    ArrayList<String> groupAndStylesImgUrls;
    StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");
    DatabaseReference groupsRef  = FirebaseDatabase.getInstance().getReference("Group");
    RecyclerView rvGroupStyle;
    TextView tvGroupName, tvGroupPrice, tvCategory, tvType,
            tvSoldAmount, tvInventoryAmount,
            tvGroupDescription, tvGroupStartTime, tvGroupEndTime, tvTimer,
            tvSellerName, tvGroupStyle;
    ImageView imvSellerPic;
    Button fabAddToCart;
    ToggleButton btnSaveToList;
    ImageButton btnShare;

    CustomerGroupStyleAdapter styleAdapter;
    int selectedStylePosition = -1;
    CustomerWishlistViewModel wishListViewModel;
    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    Map<String,Integer> psIdSoldAmountMap = new HashMap<>();

    public ShareContent shareContent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_group_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPagerGroupImg = view.findViewById(R.id.viewPagerGroupImage);
        tvGroupStyle = view.findViewById(R.id.tv_group_detail_group_style);
        rvGroupStyle = view.findViewById(R.id.rv_group_style);
        tvGroupName = view.findViewById(R.id.tv_detail_group_name);
        tvGroupPrice = view.findViewById(R.id.tv_detail_group_price);
        tvCategory = view.findViewById(R.id.tv_detail_group_category);
        tvType = view.findViewById(R.id.tv_detail_group_type);
        tvSoldAmount = view.findViewById(R.id.tv_detail_sold_amount);
        tvInventoryAmount = view.findViewById(R.id.tv_detail_inventory_amount);
        tvGroupDescription = view.findViewById(R.id.tv_detail_group_description);
        tvGroupStartTime = view.findViewById(R.id.tv_detail_group_start_time);
        tvGroupEndTime = view.findViewById(R.id.tv_detail_group_end_time);
        tvTimer = view.findViewById(R.id.tv_detail_group_timer);
        tvSellerName = view.findViewById(R.id.tv_detail_seller_name);
        imvSellerPic = view.findViewById(R.id.imv_detail_store_pic);
        fabAddToCart = view.findViewById(R.id.fab_add_to_cart);
        btnSaveToList = view.findViewById(R.id.btn_detail_save_to_list);
        btnShare =view.findViewById(R.id.btn_detail_share_to_social_media);

         //Share to social media
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupId = getArguments().getString("groupId");
                String groupJson = getArguments().getString("group");
                Group group = new Gson().fromJson(groupJson, Group.class);
                if (getArguments() != null) {
                    Log.d("Test groupId", groupId + "!");
                    String groupTitle = group.getGroupName();
                    String deepLink = "webuy://product/view?groupId=" + groupId + "&group=" + group;
                    if (group.getGroupStyles() != null) {
                        deepLink += "&styleId=" + group.getGroupStyles();
                    }
                    String shareMessage = "Hi, I want to share our " + groupTitle + " with you! " + deepLink;

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "Share to: "));

//                    ShareLinkContent content = new ShareLinkContent.Builder()
//                            .setContentUrl(Uri.parse(deepLink))
//                            .setQuote("See this amazing product: " + groupTitle)
//                            .build();
//
//                    ShareDialog shareDialog = new ShareDialog(getActivity());
//                    shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

                }
            }
        });


        // Get data from viewModel
        wishListViewModel = new ViewModelProvider(requireActivity()).get(CustomerWishlistViewModel.class);
        wishListViewModel.getWishlistObject().observe(getViewLifecycleOwner(), new Observer<ArrayList<Wishlist>>() {
            @Override
            public void onChanged(ArrayList<Wishlist> wishlists) {
                boolean isGroupIdInWishlist = false;
                if (wishlists != null) {
                    for (Wishlist wishlist : wishlists) {
                        if (wishlist.getGroupId().equals(groupId)) {
                            isGroupIdInWishlist = true;
                            break;
                        }
                    }
                }
                btnSaveToList.setChecked(isGroupIdInWishlist);
            }
        });
        btnSaveToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current item
                String currentGroupId = groupId;
                String currentSellerId = group.getSellerId();
                String currentProductId = group.getProductId();

                // Set to wishlist
                Wishlist wishlistItem = new Wishlist();
                wishlistItem.setGroupId(currentGroupId);
                wishlistItem.setSellerId(currentSellerId);
                wishlistItem.setProductId(currentProductId);
                Log.d("Test wishlistItem", "groupId"+ wishlistItem.getGroupId() + " ProductId: "+ wishlistItem.getProductId());

                if (btnSaveToList.isChecked()) {
                    Toast.makeText(getContext(),"Item added to Wish List",Toast.LENGTH_SHORT).show();
                    wishListViewModel.addToWishlist(wishlistItem, firebaseUser.getUid());
                } else {
                    wishListViewModel.removeFromWishlist(wishlistItem, firebaseUser.getUid());
                    Toast.makeText(getContext(),"Item removed from Wish List",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //get the groupId from Bundle passed
        Bundle bundle = getArguments();
        if (bundle != null) {
            groupId = bundle.getString("groupId");
            groupJson = bundle.getString("group");
            Gson gson = new Gson();
            group = gson.fromJson(groupJson, Group.class);
        }
        //set recycler view for group style
        groupStyles = group.getGroupStyles();
        if (groupStyles != null && groupStyles.size() != 0) {
            tvGroupStyle.setVisibility(View.VISIBLE);
            styleAdapter = new CustomerGroupStyleAdapter(getContext(), group.getProductId(), groupStyles);
            rvGroupStyle.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            rvGroupStyle.setAdapter(styleAdapter);
            styleAdapter.setmOnStyleItemListener(CustomerGroupDetailFragment.this);

        } else {
            tvGroupStyle.setVisibility(View.GONE);
        }

        //set images
        List<Task<Uri>> tasks = new ArrayList<>();
        for(String groupImage: group.getGroupImages()) {
            tasks.add(imgRef.child(group.getProductId()).child(groupImage).getDownloadUrl());
        }
        Tasks.whenAllSuccess(tasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> objects) {
                groupImgUrls = new ArrayList<>();
                for (Object object: objects) {
                    Log.d("Test url", object.toString());
                    groupImgUrls.add(object.toString());
                }

                if(groupStyles != null && groupStyles.size() != 0) {
                    //set images with style pic
                    StorageReference imgRef = FirebaseStorage.getInstance().getReference("ProductImage");

                    List<Task<Uri>> stylePicsTasks = new ArrayList<>();
                    for (ProductStyle style: groupStyles) {
                        stylePicsTasks.add(imgRef.child(group.getProductId()).child(style.getStylePicName()).getDownloadUrl());
                    }
                    Tasks.whenAllSuccess(stylePicsTasks).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                        @Override
                        public void onSuccess(List<Object> objects) {
                            groupAndStylesImgUrls = new ArrayList<>(groupImgUrls);
                            for (Object object: objects) {
                                Log.d("Test url style", object.toString());
                                groupAndStylesImgUrls.add(object.toString());
                            }
                            viewPagerGroupImg.setAdapter(
                                    new CustomerGroupDetailImageViewPagerAdapter(getContext(), groupAndStylesImgUrls));
                        }
                    });

                } else {
                    //set group images
                    groupAndStylesImgUrls = new ArrayList<>(groupImgUrls);
                    viewPagerAdapter = new CustomerGroupDetailImageViewPagerAdapter(getContext(),groupAndStylesImgUrls);
                    viewPagerGroupImg.setAdapter(viewPagerAdapter);
                }

            }
        });

        //TODO:sold amount (ordered)
        DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference("Inventory");
        //p___ productId + s___ styleId  ordered
        inventoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                psIdSoldAmountMap.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot invSnapshot: snapshot.getChildren()) {
                        if ( groupId.equals(invSnapshot.child("groupId").getValue(String.class)) ) {
                            String psId;
                            if (invSnapshot.child("styleId").exists()) {
                                psId = "s___" + invSnapshot.child("styleId").getValue(String.class);
                            } else {
                                psId = "p___" + invSnapshot.child("productId").getValue(String.class);
                            }
                            int soldAmount = invSnapshot.child("ordered").getValue(Integer.class);

                            psIdSoldAmountMap.put(psId,soldAmount);
                        }
                    }
                } else {
                    Log.d("sold", "sold map null");
                }
                if (psIdSoldAmountMap!= null) {
                    Log.d("sold", "sold map size: " + psIdSoldAmountMap.size());
                    if (group.getGroupStyles() == null) {
                        int soldAmount = psIdSoldAmountMap.get("p___"+group.getProductId());
                        tvSoldAmount.setText(soldAmount + " sold");
                        Log.d("sold", "sold amount: " + soldAmount);
                    } else {
                        //not selected
                        tvSoldAmount.setText("");
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //TODO:amount left
        int inventoryAmount;
        if (group.getGroupStyles() == null) {
            inventoryAmount = group.getGroupQtyMap().get("p___" + group.getProductId());
            if (inventoryAmount == -1) {
                tvInventoryAmount.setText("unlimited amount");
            } else {
                tvInventoryAmount.setText(inventoryAmount + " left");
            }

        } else {
            //not selected
            tvInventoryAmount.setText("");
        }


        //set other data
        tvGroupName.setText(group.getGroupName());
        tvGroupPrice.setText(group.getGroupPrice());
        tvCategory.setText(group.getCategory());
        if(group.getGroupType() == 0) {
            tvType.setText("in-stock");
            tvGroupStartTime.setVisibility(View.GONE);
            tvGroupEndTime.setVisibility(View.GONE);
            tvTimer.setVisibility(View.GONE);
        } else if (group.getGroupType() == 1) {
            tvType.setText("pre-order");
            Long startTimestamp = group.getStartTimestamp();
            Long endTimestamp = group.getEndTimestamp();
            long currentTime = System.currentTimeMillis();

            if (startTimestamp != null) {
                Date startTime = new Date(startTimestamp);
                tvGroupStartTime.setText("From " + startTime);

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

                            String timeTillOpen = String.format(Locale.getDefault(), "%d D %d H %d M", days, hours, minutes);

                            tvTimer.setText("Opens in " + timeTillOpen);
                        }

                        @Override
                        public void onFinish() {
                            //change status from 0 to 1
                            if(group.getStatus() == 0) {
                                groupsRef.child(groupId).child("status").setValue(1);
                                //available to buy
                            }
                        }
                    }.start();;

                }
            } else {
                tvGroupStartTime.setVisibility(View.GONE);
                tvTimer.setVisibility(View.GONE);
            }

            if (endTimestamp != null) {
                Date endTime = new Date(endTimestamp);
                tvGroupEndTime.setText("To " + endTime);

                if (endTimestamp > currentTime && currentTime > startTimestamp) {
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

                            String timeRemaining = String.format(Locale.getDefault(), "%d D %d H %d M", days, hours, minutes);

                            tvTimer.setText("Ends in " + timeRemaining);
                        }

                        @Override
                        public void onFinish() {
                            //change status from 1 to 2
                            if(group.getStatus() == 1) {
                                groupsRef.child(groupId).child("status").setValue(2);
                                //unavailable to buy
                            }
                            //unavailable to buy
                        }
                    }.start();;

                } else if (currentTime >= endTimestamp) {
                    tvTimer.setText("Expired");
                }

            } else {
                tvGroupEndTime.setVisibility(View.GONE);
                tvTimer.setVisibility(View.GONE);
            }
        } else {
            tvType.setText("unknown type");
            tvGroupStartTime.setVisibility(View.GONE);
            tvGroupEndTime.setVisibility(View.GONE);
            tvTimer.setVisibility(View.GONE);
        }
        tvGroupDescription.setText(group.getDescription());

        //seller name and pic
        String sellerId = group.getSellerId();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Seller");
        reference.child(sellerId).addValueEventListener(new ValueEventListener() {  // change to addValueEventListener
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Seller seller = snapshot.getValue(Seller.class);
                    String sellerName = seller.getStoreInfo().getStoreName();
                    tvSellerName.setText(sellerName);

                    String sellerPic = seller.getStoreInfo().getStorePic();

                    if (sellerPic != null) {
                        Log.d("Upload img: imageUrl ", sellerPic);
                        Glide.with(getContext())
                                .load(sellerPic)
                                .circleCrop()
                                .into(new CustomTarget<Drawable>() {
                                    @Override
                                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                        imvSellerPic.setImageDrawable(resource);
                                    }

                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) {
                                        imvSellerPic.setImageResource(R.drawable.ic_user_profile);
                                    }
                                });
                    }
                } else {
                    tvSellerName.setText("Seller not found");
                    imvSellerPic.setImageResource(R.drawable.ic_user_profile);
                }
                //eturn false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvSellerName.setText("Error fetching seller data");
                imvSellerPic.setImageResource(R.drawable.ic_user_profile);
            }
        });

        //btnAddToCart click or buy directly (bottom popup frag)
        fabAddToCart.setOnClickListener(v -> {
            if (group.getStatus() == 0 ) {
                Toast.makeText(getContext(), "The group has not started", Toast.LENGTH_SHORT).show();
            } else if (group.getStatus() == 2 ) {
                Toast.makeText(getContext(), "The group is expired", Toast.LENGTH_SHORT).show();
            } else { //group.getStatus() == 1
                //if (selectedStyle != null) {
                    //pass style to AddToCart
                    CustomerAddToCartFragment customerAddToCartFragment = CustomerAddToCartFragment.newInstance(groupId, groupJson, selectedStylePosition, groupAndStylesImgUrls);
                    // Get the fragmentManager
                    FragmentManager fragmentManager = getParentFragmentManager();
                    // Show the addStyleFragment
                    customerAddToCartFragment.show(fragmentManager, "AddToCart Frag show");
//                } else {
//                    CustomerAddToCartFragment customerAddToCartFragment = CustomerAddToCartFragment.newInstance(groupId, groupJson, -1, groupAndStylesImgUrls);
//                    // Get the fragmentManager
//                    FragmentManager fragmentManager = getParentFragmentManager();
//                    // Show the addStyleFragment
//                    customerAddToCartFragment.show(fragmentManager, "AddToCart Frag show");
//                }
            }
        });

    }

    @Override
    public void onStyleClicked(int stylePosition, ProductStyle style) {
        //Toast.makeText(getContext(),"styleId: "+ style.getStyleId(), Toast.LENGTH_SHORT).show();
        tvGroupPrice.setText("CA$ "+ style.getStylePrice());
        selectedStyle = style;
        selectedStylePosition = stylePosition;
        String styleId = selectedStyle.getStyleId();
        //set img
        viewPagerGroupImg.setCurrentItem(groupImgUrls.size()+stylePosition,true);
        //set inventory amount
        int inventoryAmount = group.getGroupQtyMap().get("s___" + styleId);
        if (inventoryAmount == -1) {
            tvInventoryAmount.setText("unlimited amount");
        } else {
            tvInventoryAmount.setText(inventoryAmount + " left");
        }
        //TODO: sold Amount
        Log.d("ssss",styleId);
        Log.d("ssss!",psIdSoldAmountMap.keySet().size() + "");
        for (String psId: psIdSoldAmountMap.keySet()) {
            Log.d("ssss!",psId);
        }
        if (psIdSoldAmountMap.get("s___" + styleId) != null) {
            int soldAmount = psIdSoldAmountMap.get("s___" + styleId);

        } else {
            tvSoldAmount.setText("0 sold");
        }

    }
}