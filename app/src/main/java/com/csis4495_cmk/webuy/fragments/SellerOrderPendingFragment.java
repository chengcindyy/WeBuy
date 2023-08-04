package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.Group;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.models.Product;
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

public class SellerOrderPendingFragment extends Fragment {

    private RecyclerView rv;

    private TextView tv_no;

    private NavController navController;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference dBRef;

    private DatabaseReference orderRef;

    private String sellerId;

    private List<Order> pendingOrders = new ArrayList<>();

    private List<String> orderIds = new ArrayList<>();

    private int position;


    public SellerOrderPendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_order_pending, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            sellerId = firebaseUser.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        dBRef = firebaseDatabase.getReference();
        orderRef = dBRef.child("Order");

        navController = NavHostFragment.findNavController(SellerOrderPendingFragment.this);

        rv = view.findViewById(R.id.rv_seller_order_list_pending);

        tv_no = view.findViewById(R.id.tv_seller_order_list_no_pending);

        return view;
    }

    private void getOrderData() {
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pendingOrders.clear();
                orderIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    String orderId = dataSnapshot.getKey();
                    if (order.getOrderStatus() == 1 && order.getSellerId().equals(sellerId)) {
                        pendingOrders.add(order);
                        orderIds.add(orderId);
                    }
                }

                if (pendingOrders.isEmpty()) {
                    tv_no.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                } else {
                    tv_no.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}