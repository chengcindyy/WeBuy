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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomePageNewFragment extends Fragment implements CustomerHomeGroupListRecyclerAdapter.onGroupListener{

    private FirebaseDatabase firebaseInstance = FirebaseDatabase.getInstance();
    private DatabaseReference allGroupRef = firebaseInstance.getReference("Group");
    private RecyclerView recyclerView;
    private List<Group> groupList;

    private ArrayList<String> filter;

    public CustomerHomePageNewFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootview = inflater.inflate(R.layout.fragment_customer_home_page_new, container, false);
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

                    //time

                    //soldAmount
                    groupList.add(group);
                }
                Log.d("custTest", groupList.size()+"");

                CustomerHomeGroupListRecyclerAdapter adapter = new CustomerHomeGroupListRecyclerAdapter(getContext(), groupList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public void onGroupClicked() {

    }
}