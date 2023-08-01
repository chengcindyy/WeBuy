package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.SellerGroupDetailImageRecyclerAdapter;
import com.csis4495_cmk.webuy.adapters.SellerGroupDetailOrderListViewPagerAdapter;
import com.csis4495_cmk.webuy.adapters.SellerGroupDetailStyleListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.ProductStyle;
import com.csis4495_cmk.webuy.viewmodels.SharedGroupInventoryListViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SellerGroupDetailFragment extends Fragment {

   private TextView gName, gDes, gPrice, gStart, gEnd, gCountdown, gQty, gInventory, gOrdered, gAllocated, gToAllocate;
   private RecyclerView imgRecyclerView, styleRecyclerView;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference dBRef;

    private DatabaseReference groupRef;

    private DatabaseReference inventoryRef;

    private NavController navController;

    private SellerGroupDetailImageRecyclerAdapter imgAdapter;

    private SellerGroupDetailStyleListRecyclerAdapter styleAdapter;
    private String groupId;

    private String productId;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private List<Inventory> inventoryList;

    private Map<String, String> inventoryIdMap;

    private SellerGroupDetailOrderListViewPagerAdapter viewPagerAdapter;

    SimpleDateFormat simpleDateFormat;

    private Group group;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_group_detail, container, false);

        //Get passed bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            groupId = bundle.getString("detail_groupId");
            }

        inventoryList = new ArrayList<>();
        inventoryIdMap = new HashMap<>();

        simpleDateFormat = new SimpleDateFormat("HH:mm yyyy-MM-dd");

        navController = NavHostFragment.findNavController(SellerGroupDetailFragment.this);

        styleAdapter = new SellerGroupDetailStyleListRecyclerAdapter();
        imgAdapter = new SellerGroupDetailImageRecyclerAdapter();

        firebaseDatabase = FirebaseDatabase.getInstance();
        dBRef = firebaseDatabase.getReference();
        groupRef = dBRef.child("Group");
        inventoryRef = dBRef.child("Inventory");

        gName = view.findViewById(R.id.group_detail_name);
        gDes = view.findViewById(R.id.group_detail_des);
        gPrice = view.findViewById(R.id.group_detail_price);
        gStart = view.findViewById(R.id.group_detail_start);
        gEnd = view.findViewById(R.id.group_detail_end);
        gCountdown = view.findViewById(R.id.group_detail_countdown);
        gQty = view.findViewById(R.id.group_detail_qty);
        gInventory = view.findViewById(R.id.group_detail_inventory);
        gOrdered = view.findViewById(R.id.group_detail_ordered);
        gAllocated = view.findViewById(R.id.group_detail_allocated);
        gToAllocate = view.findViewById(R.id.group_detail_toAllocate);

        imgRecyclerView = view.findViewById(R.id.rv_group_detail_img);

        styleRecyclerView = view.findViewById(R.id.rv_group_detail_style);

        tabLayout = view.findViewById(R.id.group_detail_order_tab_layout);
        viewPager = view.findViewById(R.id.group_detail_order_view_pager);

        viewPagerAdapter = new SellerGroupDetailOrderListViewPagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });


        getGroupDetail();

        getInventoryData();

        return view;
    }

    private void getGroupDetail() {
        groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Group gp = snapshot.getValue(Group.class);
                if(gp != null){
                    group = gp;
                    productId = gp.getProductId();
                    gName.setText(gp.getGroupName());
                    gDes.setText(gp.getDescription());
                    gPrice.setText(gp.getGroupPrice());

                    if(gp.getGroupType() == 1){

                        long currentTime = System.currentTimeMillis();
                        Long startTimestamp = gp.getStartTimestamp();
                        Long endTimestamp = gp.getEndTimestamp();

                        if((currentTime > endTimestamp && endTimestamp != 0 ) || gp.getStatus() == 2){
                            gCountdown.setVisibility(View.VISIBLE);
                            gCountdown.setText("Group Closed");
                        }

                        gStart.setVisibility(View.VISIBLE);
                        gStart.setText("From " + simpleDateFormat.format(new Date(startTimestamp)));

                        gEnd.setVisibility(View.VISIBLE);
                        gEnd.setText("To " + simpleDateFormat.format(new Date(endTimestamp)));

                        if (currentTime < startTimestamp && gp.getStatus() != 2 ) {
                            long tillOpen = startTimestamp - System.currentTimeMillis();
                            CountDownTimer timer = new CountDownTimer(tillOpen, 60000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                                    millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                                    long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                                    String timeTillOpen = String.format(Locale.getDefault(), "%dD %dH %dM", days, hours, minutes);

                                    gCountdown.setText("Opens in " + timeTillOpen);
                                }

                                @Override
                                public void onFinish() {
                                    DatabaseReference selectedGp = groupRef.child(groupId);
                                    selectedGp.child("status").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Log.d(TAG, "Group status is updated: 1");

                                            }else{
                                                Log.d(TAG, "Group status update error", task.getException());
                                            }
                                        }
                                    });
                                }

                            }.start();;
                        }

                        if (endTimestamp > currentTime && currentTime >= startTimestamp && gp.getStatus() != 2) {
                            long remainingTime = endTimestamp - currentTime;
                            CountDownTimer timer = new CountDownTimer(remainingTime, 60000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    long days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished);
                                    millisUntilFinished -= TimeUnit.DAYS.toMillis(days);

                                    long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                                    millisUntilFinished -= TimeUnit.HOURS.toMillis(hours);

                                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                                    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes);

                                    String timeRemaining = String.format(Locale.getDefault(), "%dD %dH %dM", days, hours, minutes);

                                    gCountdown.setText("Ends in " + timeRemaining);
                                }

                                @Override
                                public void onFinish() {
                                    DatabaseReference selectedGp = groupRef.child(groupId);
                                    selectedGp.child("status").setValue(2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Log.d(TAG, "Group status is updated: 2");
                                                gCountdown.setText("Group Closed");
                                                gCountdown.setVisibility(View.VISIBLE);
                                            }else{
                                                Log.d(TAG, "Group status update error", task.getException());

                                            }
                                        }
                                    });

                                }
                            }.start();;
                        }
                    }
                    else{
                        gStart.setVisibility(View.GONE);
                        gEnd.setVisibility(View.GONE);
                        gCountdown.setVisibility(View.GONE);
                        if( gp.getStatus() == 2){
                            gCountdown.setVisibility(View.VISIBLE);
                            gCountdown.setText("Group Closed");
                        }
                    }
                }

                List<String> imageUrls = gp.getGroupImages();
                imgAdapter.setImgUrls(imageUrls);
                imgAdapter.setProductId(productId);
                imgAdapter.notifyDataSetChanged();

                imgRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                imgRecyclerView.setAdapter(imgAdapter);

                Map<String, Integer> qtyMap = gp.getGroupQtyMap();

                if(gp.getGroupStyles() != null){
                    List<ProductStyle>styles = gp.getGroupStyles();
                    Log.d(TAG, "onDataChange: " + styles);
                    styleAdapter.setStyles(styles);
                    styleAdapter.setQtyMap(qtyMap);
                    styleAdapter.setProductId(productId);
                    styleAdapter.notifyDataSetChanged();
                    styleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    styleRecyclerView.setAdapter(styleAdapter);

                    gQty.setVisibility(View.GONE);
                    gInventory.setVisibility(View.GONE);
                    gOrdered.setVisibility(View.GONE);
                    gAllocated.setVisibility(View.GONE);
                    gToAllocate.setVisibility(View.GONE);
                }else{
                    Integer qty = qtyMap.get("p___"+gp.getProductId());
                    gQty.setVisibility(View.VISIBLE);
                    gOrdered.setVisibility(View.VISIBLE);
                    gInventory.setVisibility(View.VISIBLE);
                    gAllocated.setVisibility(View.VISIBLE);
                    gToAllocate.setVisibility(View.VISIBLE);
                    gQty.setText("Quantity: " + Integer.toString(qty));
                    styleRecyclerView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getInventoryData(){
        inventoryList.clear();
        inventoryIdMap.clear();
        inventoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Inventory i = dataSnapshot.getValue(Inventory.class);
                    if(i.getProductId().equals(productId)){
                        inventoryList.add(i);
                        inventoryIdMap.put(dataSnapshot.getKey(), i.getProductStyleKey());
                    }
                }
                if(inventoryList != null){
                    Log.d(TAG, "Inventory list" + inventoryList);
                    if(inventoryList.size() == 1 && inventoryList.get(0).getProductStyleKey().equals(productId)){
                        gInventory.setText("Inventory: " + Integer.toString(inventoryList.get(0).getInStock()));
                        gOrdered.setText("Ordered: " + Integer.toString(inventoryList.get(0).getOrdered()));
                        gAllocated.setText("Allocated: " + Integer.toString(inventoryList.get(0).getAllocated()));
                        gToAllocate.setText("To Allocate: "+ Integer.toString(inventoryList.get(0).getToAllocated()));
                    }
                    else{
                        styleAdapter.setInventoryList(inventoryList);
                        styleAdapter.notifyDataSetChanged();
                    }
                    SharedGroupInventoryListViewModel listViewModel = new ViewModelProvider(requireActivity()).get(SharedGroupInventoryListViewModel.class);
                    listViewModel.setInventoryList(inventoryList);
                    listViewModel.setGroupId(groupId);
                    listViewModel.setGroup(group);
                    listViewModel.setinventoryIdMap(inventoryIdMap);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}