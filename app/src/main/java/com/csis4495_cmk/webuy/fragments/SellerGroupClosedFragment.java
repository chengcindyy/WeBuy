package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.adapters.recyclerview.SellerGroupListRecyclerAdapter;
import com.csis4495_cmk.webuy.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SellerGroupClosedFragment extends Fragment {

    private NavController navController;

    private RecyclerView mRecyclerView;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference dBRef;

    private DatabaseReference groupRef;

    private SellerGroupListRecyclerAdapter groupListRecyclerAdapter;

    private String sellerId;

    private List<Group> closedGroups = new ArrayList<>();

    private List<String> groupIds = new ArrayList<>();

    private TextView tv_group_list_no_closed;

    private int position;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_group_closed, container, false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            sellerId = firebaseUser.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        dBRef = firebaseDatabase.getReference();
        groupRef = dBRef.child("Group");

        navController = NavHostFragment.findNavController(SellerGroupClosedFragment.this);

        mRecyclerView = view.findViewById(R.id.rv_group_list_closed);

        tv_group_list_no_closed = view.findViewById(R.id.tv_group_list_no_closed);

        groupListRecyclerAdapter = new SellerGroupListRecyclerAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(groupListRecyclerAdapter);

        getGroupsData();

        OnRecyclerItemSwipeActionHelper();

        groupListRecyclerAdapter.setOnItemClickListener(new SellerGroupListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String groupId = groupIds.get(position);
                Toast.makeText(getContext(), "groupId: "+groupId, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putString("detail_groupId", groupId);

                SellerGroupDetailFragment sellerGroupDetailFragment = new SellerGroupDetailFragment();
                sellerGroupDetailFragment.setArguments(bundle);

                Navigation.findNavController(view).navigate(R.id.action_sellerGroupList_to_sellerGroupDetailFragment, bundle);


            }
        });


        return view;

    }

    private void OnRecyclerItemSwipeActionHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String productId = "";
                position = viewHolder.getLayoutPosition();
                // Swiped item left: close the group (set status to 2)
                if (direction == ItemTouchHelper.RIGHT) {
                    Group selectedGroup = closedGroups.get(position);
                    productId = selectedGroup.getProductId();
                    Bundle bundle = new Bundle();
                    bundle.putString("new_group_productId", productId);

                    SellerAddGroupFragment sellerAddGroupFragment = new SellerAddGroupFragment();
                    sellerAddGroupFragment.setArguments(bundle);
                    Navigation.findNavController(viewHolder.itemView).navigate(R.id.action_sellerGroupList_to_sellerAddGroupFragment, bundle);
                    groupListRecyclerAdapter.setGroups(closedGroups);
                    groupListRecyclerAdapter.setGroupIds(groupIds);
                    groupListRecyclerAdapter.notifyDataSetChanged();
                }

            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
//                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete_red))
//                        .addSwipeLeftActionIcon(R.drawable.baseline_close_48)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.android_green))
                        .addSwipeRightActionIcon(R.drawable.baseline_autorenew_448)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    private void getGroupsData(){
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                closedGroups.clear();
                groupIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Group gp = dataSnapshot.getValue(Group.class);
                    long currentTime = System.currentTimeMillis();
                    String groupId = dataSnapshot.getKey();

                    if(gp.getSellerId().equals(sellerId)){
                        //Get seller's not closed groups data and update status
                        if(gp.getStatus() != 2){
                            if(gp.getGroupType() == 1){
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
                                DatabaseReference currentGp = groupRef.child(groupId);
                                currentGp.child("status").setValue(gp.getStatus()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Group status is updated: " + Integer.toString(gp.getStatus()));
                                        } else {
                                            Log.d(TAG, "Group status update error", task.getException());
                                        }
                                    }
                                });
                            }
                        }
                        if(gp.getStatus() == 2){
                            closedGroups.add(gp);
                            groupIds.add(groupId);
                        }
                    }
                }

                if (closedGroups.isEmpty()) {
                    tv_group_list_no_closed.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                }else{
                    tv_group_list_no_closed.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);

                    groupListRecyclerAdapter.setContext(getContext());
                    groupListRecyclerAdapter.setGroups(closedGroups);
                    groupListRecyclerAdapter.setGroupIds(groupIds);
                    groupListRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}