package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeGroupsFragment extends Fragment implements CustomerHomeGroupListRecyclerAdapter.onGroupListener{

    private FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
    private DatabaseReference allGroupRef = firebaseInstance.getReference("Group");
    private RecyclerView recyclerView;
    private List<Group> groupList;

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
        final View rootview = inflater.inflate(R.layout.viewpager_customer_home_groups, container, false);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupList = new ArrayList<>();

        recyclerView = view.findViewById(R.id.rv_cust_home_group);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        Log.d("custTest", groupList.size()+"");

        allGroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupList.clear();
                for (DataSnapshot groupSnapshot: snapshot.getChildren()) {
                    Group group = groupSnapshot.getValue(Group.class);
                    Log.d("custTest", group.getGroupName()+"");

                    //type

                    //status

                    //category
                    Log.d("custTest", "Category: "+category);
                    if(category!= null) {
                        if (group.getCategory().equals(category)) {
                            groupList.add(group);
                        }
                    } else {
                        groupList.add(group);
                    }

                    //time

                    //soldAmount


                }
                Log.d("custTest", groupList.size()+"");

                CustomerHomeGroupListRecyclerAdapter adapter = new CustomerHomeGroupListRecyclerAdapter(getContext(), groupList);
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
    public void onGroupClicked() {

    }
}