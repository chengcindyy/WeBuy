package com.csis4495_cmk.webuy.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.SellerGroupListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SellerGroupOpeningFragment extends Fragment {

    private NavController navController;

    private RecyclerView mRecyclerView;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference dBRef;

    private DatabaseReference groupRef;



    private ValueEventListener valueEventListener;

    private SellerGroupListRecyclerAdapter groupListRecyclerAdapter;

    private String sellerId;

   private List<Group> openingGroups = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_group_opening, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            sellerId = firebaseUser.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        dBRef = firebaseDatabase.getReference();
        groupRef = dBRef.child("Group");

        navController = NavHostFragment.findNavController(SellerGroupOpeningFragment.this);

        mRecyclerView = view.findViewById(R.id.rv_group_list_opening);

        groupListRecyclerAdapter = new SellerGroupListRecyclerAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(groupListRecyclerAdapter);

        getDatabaseData();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    private void getDatabaseData(){
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Group gp = dataSnapshot.getValue(Group.class);
                    if(gp.getSellerId().equals(sellerId)){
                        // Ensure the qtyMap is not null
                        Map<String, Integer> groupQtyMap = gp.getGroupQtyMap();
                        if (groupQtyMap == null) {
                            groupQtyMap = new HashMap<>();
                            gp.setGroupQtyMap(groupQtyMap);
                        }


                        if(gp.getGroupType()==1){
                            long currentTime = System.currentTimeMillis();
                            if (currentTime < gp.getStartTimestamp()) {
                                // Group is not yet opened
                                gp.setStatus(0);
                            } else if (currentTime > gp.getEndTimestamp()) {
                                // Group is closed
                                gp.setStatus(2);
                            } else {
                                // Group is opening
                                gp.setStatus(1);

                            }

                            String groupId = dataSnapshot.getKey();
                            DatabaseReference currentGp = groupRef.child(groupId);
                            currentGp.child("status").setValue(gp.getStatus());
                        }

                        if(gp.getStatus()==1){
                            openingGroups.add(gp);

                        }
                    }
                }
                groupListRecyclerAdapter.setContext(getContext());
                groupListRecyclerAdapter.setGroups(openingGroups);
                groupListRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}