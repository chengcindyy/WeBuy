package com.csis4495_cmk.webuy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.csis4495_cmk.webuy.models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class TestFragment extends Fragment {

    private NavController navController;
    private Button btnProductList;
    private Button btnTempOrder;
    private Button btnCheckout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set navigation controller, and if you want to navigate to other fragment can call this to navigate
        navController = NavHostFragment.findNavController(TestFragment.this);

        btnCheckout = view.findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate(R.id.action_testFragment_to_customerCheckoutFragment);
            }
        });

        btnTempOrder = view.findViewById(R.id.btn_temp_order);


        btnTempOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Temp Order", Toast.LENGTH_SHORT).show();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Order");
                DatabaseReference orderRef = reference.push();


//                String customerId = "UElcwADSG6ddV4jveqWDR98PIAC3";  //Karl
                String customerId ="0sO8iW7NRfSMOWB6TXIs0fEYOR33";  //Cindy
                Map<String, Map<String, Integer>> groupsAndItemsMap = new HashMap<>();
                Map<String, Integer> innerStyleMap = new HashMap<>();
                int orderStatus = 0;
                double itemsTotal = 120.00;
                double gstTotal = 6.00;
                double pstTotal = 8.40;
                double totalPrice = 134.4;
                double deliveryFee = 10.00;

                String orderDate = "30/07/2023";

                String address = "1000 Rich Road";
                String country = "CA";
                String province = "British Columbia";
                String city = "Richmond";
                String postalCode = "V8A 6E1";
                String paymentType = "E-transfer";

                String GROUPID = "-NadxHxDbWJEhGyfTk-R";
                String productId = "NaPq7lbRgjlIUOr4GeQ";
                String style_Pink = "777e8183-95ef-4221-b07b-7f1f4ba71cc2";
                String style_Yellow = "205f73e9-300a-43e5-bbf7-b7a7c61d2f71";

                String keyPink = "p___" + productId + "s___" + style_Pink;
                String keyYellow = "p___" + productId + "s___" + style_Yellow;

//                innerStyleMap.put(keyPink, 4);
                innerStyleMap.put(keyYellow, 6);
                groupsAndItemsMap.put(GROUPID, innerStyleMap);

                Order order = new Order(customerId, groupsAndItemsMap, totalPrice, itemsTotal, gstTotal, pstTotal, orderDate, deliveryFee,
                        address, country, province, city, postalCode, paymentType, orderStatus);

                orderRef.setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("New Order", "Order Upload Successfully");
                        } else {
                            Log.d("New Order", "Order Upload Failed");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT);
                    }
                });
                String orderId = orderRef.getKey();

                Toast.makeText(getContext(), "new order: " + orderId, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
