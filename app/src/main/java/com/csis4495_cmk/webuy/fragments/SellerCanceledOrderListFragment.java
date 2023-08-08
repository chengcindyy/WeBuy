package com.csis4495_cmk.webuy.fragments;

        import static android.content.ContentValues.TAG;

        import android.os.Bundle;

        import androidx.annotation.NonNull;
        import androidx.fragment.app.Fragment;
        import androidx.navigation.NavController;
        import androidx.navigation.Navigation;
        import androidx.navigation.fragment.NavHostFragment;
        import androidx.recyclerview.widget.LinearLayoutManager;
        import androidx.recyclerview.widget.RecyclerView;

        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.csis4495_cmk.webuy.R;
        import com.csis4495_cmk.webuy.adapters.recyclerview.SellerOrderListRecyclerAdapter;
        import com.csis4495_cmk.webuy.models.Order;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;
        import java.util.List;


public class SellerCanceledOrderListFragment extends Fragment {

    private RecyclerView rv;

    private TextView tv_no;

    private NavController navController;

    private FirebaseAuth auth = FirebaseAuth.getInstance();

    private FirebaseUser firebaseUser;

    private FirebaseDatabase firebaseDatabase;

    private DatabaseReference dBRef;

    private DatabaseReference orderRef;

    private String sellerId;

    private List<Order> canceledOrders = new ArrayList<>();

    private List<String> orderIds = new ArrayList<>();

    private int position;

    private SellerOrderListRecyclerAdapter adapter;


    public SellerCanceledOrderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_seller_canceled_order_list, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null) {
            sellerId = firebaseUser.getUid();
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        dBRef = firebaseDatabase.getReference();
        orderRef = dBRef.child("Order");

        navController = NavHostFragment.findNavController(SellerCanceledOrderListFragment.this);

        rv = view.findViewById(R.id.rv_seller_order_list_canceled);

        tv_no = view.findViewById(R.id.tv_seller_order_list_no_canceled);

        getOrderData();

        return view;
    }

    private void getOrderData() {
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                canceledOrders.clear();
                orderIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    String orderId = dataSnapshot.getKey();
                    if (order.getOrderStatus() == -1 && order.getSellerId().equals(sellerId)) {
                        canceledOrders.add(order);
                        Log.d(TAG, "onDataChange: allocatedOrders" + canceledOrders);
                        orderIds.add(orderId);
                    }
                }
                if (canceledOrders.isEmpty()) {
                    tv_no.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                } else {
                    tv_no.setVisibility(View.GONE);
                    rv.setVisibility(View.VISIBLE);
                    adapter = new SellerOrderListRecyclerAdapter(canceledOrders, orderIds);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter.setOnItemClickListener(new SellerOrderListRecyclerAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            String orderId = orderIds.get(position);
                            String customerEmail = adapter.getCustomerEmail();
                            Log.d(TAG, "onItemClick: position" + position);
                            Bundle bundle = new Bundle();
                            bundle.putString("detail_orderId", orderId);
                            bundle.putString("customer_email", customerEmail);
                            SellerOrderDetailFragment sellerGroupDetailFragment = new SellerOrderDetailFragment();
                            sellerGroupDetailFragment.setArguments(bundle);
                            Navigation.findNavController(getView()).navigate(R.id.action_sellerOrderListFragment_to_sellerOrderDetailFragment, bundle);

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
