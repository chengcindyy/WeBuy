package com.csis4495_cmk.webuy.fragments;

import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.csis4495_cmk.webuy.adapters.SellerGroupListRecyclerAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class SellerGroupClosingSoonFragment extends Fragment {

    private NavController navController;

    private RecyclerView mRecyclerView;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference dBRef;

    private DatabaseReference groupRef;

    private SellerGroupListRecyclerAdapter groupListRecyclerAdapter;

    private String sellerId;

    private List<Group> closingSoonGroups = new ArrayList<>();

    private List<String> groupIds = new ArrayList<>();

    private TextView tv_group_list_no_closing_soon;

    private int position;

    private Map<Group, String>groupandIdMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_group_closing_soon, container, false);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            sellerId = firebaseUser.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        dBRef = firebaseDatabase.getReference();
        groupRef = dBRef.child("Group");

        navController = NavHostFragment.findNavController(SellerGroupClosingSoonFragment.this);

        mRecyclerView = view.findViewById(R.id.rv_group_list_closing_soon);

        tv_group_list_no_closing_soon = view.findViewById(R.id.tv_group_list_no_closing_soon);

        groupListRecyclerAdapter = new SellerGroupListRecyclerAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(groupListRecyclerAdapter);

        getGroupsData();

        OnRecyclerItemSwipeActionHelper();

        groupListRecyclerAdapter.setOnItemClickListener(new SellerGroupListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Toast.makeText(getContext(), "groupId: "+groupIds.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        groupListRecyclerAdapter.setOnItemClickListener(new SellerGroupListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String groupId = groupIds.get(position);
                Group gp = closingSoonGroups.get(position);
                String productId = gp.getProductId();
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

    private void getGroupsData() {
        groupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                closingSoonGroups.clear();
                groupIds.clear();
                groupandIdMap.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Group gp = dataSnapshot.getValue(Group.class);
                    long currentTime = System.currentTimeMillis();
                    String groupId = dataSnapshot.getKey();

                    //Get seller's not closed groups data and update status
                    if (gp.getSellerId().equals(sellerId) && gp.getStatus() != 2) {
                        if (gp.getGroupType() == 1) {
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

                        long remainingTime = gp.getEndTimestamp() - currentTime;
                        long remainingInDays = TimeUnit.MILLISECONDS.toDays(remainingTime);
                        if (remainingInDays < 5 && gp.getStatus() == 1 && gp.getEndTimestamp() != 0) {
                            //Add the closing group
                            closingSoonGroups.add(gp);
                            //Map the group and groupId
                            groupandIdMap.put(gp, groupId);
                        }
                    }
                }

                if (closingSoonGroups.isEmpty()) {
                    tv_group_list_no_closing_soon.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.GONE);
                } else {
                    tv_group_list_no_closing_soon.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                    groupListRecyclerAdapter.setContext(getContext());

                    //Sort group's endtime
                    Collections.sort(closingSoonGroups, new Comparator<Group>() {
                        @Override
                        public int compare(Group o1, Group o2) {
                            return Long.compare(o1.getEndTimestamp(), o2.getEndTimestamp());
                        }
                    });
                    //get the groupId after sorting
                    for(Group g :closingSoonGroups ){
                        if(groupandIdMap.containsKey(g)){
                            groupIds.add(groupandIdMap.get(g));
                        }
                    }

                    groupListRecyclerAdapter.setGroups(closingSoonGroups);
                    groupListRecyclerAdapter.setGroupIds(groupIds);
                    groupListRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void OnRecyclerItemSwipeActionHelper() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String groupId = "";
                String productId = "";
                position = viewHolder.getLayoutPosition();
                // Swiped item left: close the group (set status to 2)

                if (direction == ItemTouchHelper.LEFT) {
                    groupId = groupIds.get(position);
                    showConfirmToCloseDialog(groupId, position);
                }

                if (direction == ItemTouchHelper.RIGHT) {
                    groupId = groupIds.get(position);
                    Group selectedGroup = closingSoonGroups.get(position);
                    productId = selectedGroup.getProductId();
                    Bundle bundle = new Bundle();
                    bundle.putString("edit_group_productId", productId);
                    bundle.putString("edit_group_groupId", groupId);

                    SellerAddGroupFragment sellerAddGroupFragment = new SellerAddGroupFragment();
                    sellerAddGroupFragment.setArguments(bundle);
                    Navigation.findNavController(viewHolder.itemView).navigate(R.id.action_sellerGroupList_to_sellerAddGroupFragment, bundle);
                    groupListRecyclerAdapter.setGroups(closingSoonGroups);
                    groupListRecyclerAdapter.setGroupIds(groupIds);
                    groupListRecyclerAdapter.notifyDataSetChanged();
                }

            }


            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.delete_red))
                        .addSwipeLeftActionIcon(R.drawable.baseline_close_48)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.android_green))
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_48)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void showConfirmToCloseDialog(String groupId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Close this group");
        builder.setMessage("Are you sure you want to close this group?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DatabaseReference selectedGp = groupRef.child(groupId);
                selectedGp.child("status").setValue(2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Group status is updated: 2");

                        } else {
                            Log.d(TAG, "Group status update error", task.getException());
                        }
                    }
                });
                closingSoonGroups.remove(position);
                groupIds.remove(position);
                groupListRecyclerAdapter.setGroups(closingSoonGroups);
                groupListRecyclerAdapter.setGroupIds(groupIds);
                groupListRecyclerAdapter.notifyDataSetChanged();

            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                groupListRecyclerAdapter.setGroups(closingSoonGroups);
                groupListRecyclerAdapter.setGroupIds(groupIds);
                groupListRecyclerAdapter.notifyDataSetChanged();
            }
        });
        //Create AlertDialog
        AlertDialog alertDialog = builder.create();
        //Show AlertDialog
        alertDialog.show();
    }

}