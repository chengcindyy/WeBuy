package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.CustomerHomeGroupListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerHomeGroupsFragment extends Fragment implements CustomerHomeGroupListRecyclerAdapter.onGroupListener{

    private NavController navController;
    private FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
    private DatabaseReference allGroupRef = firebaseInstance.getReference("Group");
    private RecyclerView recyclerView;
    private TextView tvNotFound;
    private Map<String,Group> groupMap;

    private ArrayList<String> filter;

    private String category;
    public CustomerHomeGroupsFragment() {

    }
    public CustomerHomeGroupsFragment(String category) {
        this.category = category;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_customer_home_groups, container, false);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set navigation controller
        navController = NavHostFragment.findNavController(CustomerHomeGroupsFragment.this);
        tvNotFound = view.findViewById(R.id.tv_not_found);

        groupMap = new HashMap<>();

        recyclerView = view.findViewById(R.id.rv_cust_home_group);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        Log.d("custTest", "size: " + groupMap.size());

        allGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupMap.clear();
                for (DataSnapshot groupSnapshot: snapshot.getChildren()) {
                    String groupId = groupSnapshot.getKey();
                    Group group = groupSnapshot.getValue(Group.class);
                    Log.d("custTest", groupId + ": " + group.getGroupName());

                    //type

                    //status - show group not expired
                    int groupStatus = group.getStatus();
                    if (groupStatus != 2 ) { //not expired
                        //category
                        Log.d("custTest", "Category: "+category);
                        if(category!= null) {
                            if (group.getCategory().equals(category)) {
                                groupMap.put(groupId,group);
                            }
                        } else {
                            groupMap.put(groupId,group);
                        }

                    }

                    //time - change group status
                    if (group.getGroupType() == 1) { // pre-order
                        Long startTimestamp = group.getStartTimestamp();
                        Long endTimestamp = group.getEndTimestamp();
                        long currentTime = System.currentTimeMillis();

                        if (groupStatus == 1 && currentTime > endTimestamp) { // ongoing to expire: 1->2
                            allGroupRef.child(groupId).child("status").setValue(2);
                        } else if (groupStatus == 0 && currentTime > startTimestamp) { // not started to ongoing: 0->1
                            allGroupRef.child(groupId).child("status").setValue(1);
                        }
                    }

                    //soldAmount

                }

                Log.d("custTest", "size: "+groupMap.size());
                if (groupMap.size() == 0) {
                    tvNotFound.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    tvNotFound.setVisibility(View.GONE);
                }

                CustomerHomeGroupListRecyclerAdapter adapter = new CustomerHomeGroupListRecyclerAdapter(getContext(), groupMap);
                recyclerView.setAdapter(adapter);
                adapter.setOnGroupListener(CustomerHomeGroupsFragment.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("custTest", error.getMessage());
            }
        });

    }

    @Override
    public void onGroupClicked(String groupId, Group group) {
        Log.d("setTest",groupId+ ": "+ group.getGroupName());
        //pass the groupId to pop up GroupDetailPage
        Bundle bundle = new Bundle();

        bundle.putString("groupId", groupId);

        Gson gson = new Gson();
        String groupJson = gson.toJson(group);
        bundle.putString("group", groupJson);

        CustomerGroupDetailFragment customerGroupDetailFragment = new CustomerGroupDetailFragment();
        customerGroupDetailFragment.setArguments(bundle);

        Log.d("Test view", getView().getRootView().toString());
        navController.navigate(R.id.action_customerHomeFragment_to_customerGroupDetailFragment, bundle);
    }
}