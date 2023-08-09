package com.csis4495_cmk.webuy.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csis4495_cmk.webuy.R;
import com.csis4495_cmk.webuy.models.CartItem;
import com.csis4495_cmk.webuy.models.Inventory;
import com.csis4495_cmk.webuy.models.Order;
import com.csis4495_cmk.webuy.viewmodels.CustomerCartItemsViewModel;
import com.csis4495_cmk.webuy.adapters.recyclerview.CustomerCheckoutShoppingDetailsAdapter;
import com.csis4495_cmk.webuy.models.Delivery;
import com.csis4495_cmk.webuy.viewmodels.CustomerCheckoutDataViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerCheckoutFragment extends Fragment {

    private int fromWhere;
    private final int CART = 1;
    private final int DIRECTLY_BUY = 0;
    private final int OPENING = 1;
    private final String HOME_DELIVERY = "Home delivery";
    private final String PICK_UP = "Store pickup";
    final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
    final DatabaseReference sellersRef = FirebaseDatabase.getInstance().getReference("Seller");
    final DatabaseReference customerRef = FirebaseDatabase.getInstance().getReference("Customer");
    final DatabaseReference groupRef  = FirebaseDatabase.getInstance().getReference("Group");
    final DatabaseReference inventoryRef  = FirebaseDatabase.getInstance().getReference("Inventory");
    private CustomerCheckoutDataViewModel model;
    private CustomerCartItemsViewModel cartItemsViewModel;
    private ArrayList<CartItem> shoppingItems = new ArrayList<>();
    private ArrayList<CustomerCartItemsViewModel.CartItemInfo> cartItemInfos = new ArrayList<>();
    private Map<String, Map<String, Order.OrderItemInfo >> groupsAndItemsMap = new HashMap<>();
            //groupId, p___ + productId + (s___ + styleId), orderItemInfo(int orderAmount, boolean isAllocated)
    private String sellerId;
    private String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private RecyclerView rvShoppingItemList;
    private TextView txvStoreName, txvShipmentMethod, txvReceiverPhone, txvAddress,
                     txvPayment, txvCheckoutAmount, txvShipmentAmount, txvGstAmount, txvPstAmount, txvOrderTotalPrice;
    private EditText customerNote;
    private Button btnOrder;
    private CustomerCheckoutShoppingDetailsAdapter adapter;
    private double checkoutTotal = 0;
    private double gstTotal = 0;
    private double pstTotal = 0;
    private double orderTotal = 0;
    private Double shipmentAmount = null;

    public CustomerCheckoutFragment(){}
    public CustomerCheckoutFragment(int fromWhere) {
        this.fromWhere = fromWhere;
    }

    public static CustomerCheckoutFragment newInstance(int fromWhere){
        CustomerCheckoutFragment fragment = new CustomerCheckoutFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_checkout, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialized viewModel
        model = new ViewModelProvider(requireActivity()).get(CustomerCheckoutDataViewModel.class);
        cartItemsViewModel = new ViewModelProvider(requireActivity()).get(CustomerCartItemsViewModel.class);
        //set shoppingItems and cartInfos
        cartItemsViewModel.getSellerItemsMapLiveData().observe(getViewLifecycleOwner(), new Observer<Map<String, ArrayList<CartItem>>>() {
            @Override
            public void onChanged(Map<String, ArrayList<CartItem>> stringArrayListMap) {
                //clear data
                shoppingItems.clear();
                cartItemInfos.clear();
                groupsAndItemsMap.clear();
                checkoutTotal = 0;
                gstTotal = 0;
                pstTotal = 0;

                for (String sellerId: stringArrayListMap.keySet()) {
                    for (CartItem cartItem: stringArrayListMap.get(sellerId)) {
                        if (cartItem.getChecked()) {
                            shoppingItems.add(cartItem);
                        }
                    }
                    Log.d("place order", "shoppingList size: "+ shoppingItems.size());
                }

                Map<String, ArrayList<String>> groupItemIdsMap = new HashMap<>();
                Map<String, Order.OrderItemInfo> itemIdInfoMap = new HashMap<>();
                //groupId, p___ + productId + (s___ + styleId), orderItemInfo(int orderAmount, boolean isAllocated)

                cartItemsViewModel.getCartItemsInfoMapLiveData().observe(getViewLifecycleOwner(), cartItemCartItemInfoMap -> {
                    for (CartItem cartItem: shoppingItems) {
                        CustomerCartItemsViewModel.CartItemInfo cartItemInfo = cartItemCartItemInfoMap.get(cartItem);
                        cartItemInfos.add(cartItemInfo);
                        String groupId = cartItem.getGroupId();
                        String styleId = cartItem.getStyleId();

                        String itemId = "p___" + cartItem.getProductId() + (styleId==null? "": "s___" +styleId);
                        Log.d("place order", "item id: "+ itemId);

                        groupItemIdsMap.computeIfAbsent(groupId, k -> new ArrayList<>()).add(itemId);
                        //groupItemIdMap.put(groupId,itemId); //not duplicated
                        itemIdInfoMap.put(itemId,new Order.OrderItemInfo(cartItem.getAmount(),false));
                        //calculate total
                        int amount = cartItem.getAmount();
                        double price = Double.parseDouble(cartItemInfo.getGroupPrice().split("CA\\$ ")[1]);
                        double subTotal = price * amount;

                        checkoutTotal += subTotal;
                        switch (cartItemInfo.getProductTax()) {
                            case 0:
                                break;
                            case 1:
                                gstTotal += 0.05 * subTotal;
                                break;
                            case 2:
                                gstTotal += 0.05 * subTotal;
                                pstTotal += 0.07 * subTotal;
                                break;
                        }
                    }

                    for (Map.Entry<String, ArrayList<String>> entry : groupItemIdsMap.entrySet()) {
                        Log.d("shopping", "items under the group: " +groupItemIdsMap.entrySet());
                        String groupId = entry.getKey();
                        ArrayList<String> groupItemIds = entry.getValue();
                        for (String itemId: groupItemIds) {
                            Order.OrderItemInfo itemInfo = itemIdInfoMap.get(itemId);
                            if (itemInfo != null) {
                                groupsAndItemsMap.computeIfAbsent(groupId, k -> new HashMap<>()).put(itemId, itemInfo);
                            }
                        }

                    }

                    Log.d("shopping", "groupAndItems size:" + groupsAndItemsMap.size());
                    for (String groupId: groupsAndItemsMap.keySet()) {
                        Log.d("shopping", "items size: "+ groupsAndItemsMap.get(groupId).size());
                    }
                    Log.d("shopping", "Item size:"+shoppingItems.size()+ " Info size:"+ cartItemInfos.size());
                    Log.d("shopping", "Item id set: "+ groupItemIdsMap.keySet());
                    //orderTotal = checkoutTotal + gstTotal + pstTotal + shipmentAmount;

                    checkoutTotal = Double.parseDouble(String.format("%.2f", checkoutTotal));
                    gstTotal = Double.parseDouble(String.format("%.2f", gstTotal));
                    pstTotal = Double.parseDouble(String.format("%.2f", pstTotal));
                    //orderTotal = Double.parseDouble(String.format("%.2f", orderTotal));

                    txvCheckoutAmount.setText("CA$ "+ checkoutTotal);
                    txvShipmentAmount.setText("-");
                    txvGstAmount.setText("CA$ "+ gstTotal);
                    txvPstAmount.setText("CA$ "+ pstTotal);
                    txvOrderTotalPrice.setText("-");

                    //set recyclerview
                    rvShoppingItemList.setLayoutManager(new LinearLayoutManager(getContext()));
                    adapter = new CustomerCheckoutShoppingDetailsAdapter(getContext(),shoppingItems,cartItemInfos);
                    rvShoppingItemList.setAdapter(adapter);

                });
                // set seller information
                setSellerInfo();
            }
        });

        // RecyclerView: shopping items
        rvShoppingItemList = view.findViewById(R.id.recyclerView_shopping_info);
        // TextViews
        txvStoreName = view.findViewById(R.id.txv_store_name);
        txvShipmentMethod = view.findViewById(R.id.txv_shipment_method);
        setShipmentClickListener(txvShipmentMethod);
        txvReceiverPhone = view.findViewById(R.id.txv_shipment_method_name_phone);
        txvAddress = view.findViewById(R.id.txv_shipment_method_address);
        txvPayment = view.findViewById(R.id.txv_payment);
        setPaymentClickListener(txvPayment);
        txvCheckoutAmount = view.findViewById(R.id.txv_checkout_total_amount);
        txvShipmentAmount = view.findViewById(R.id.txv_checkout_shipment_amount);
        txvGstAmount = view.findViewById(R.id.txv_checkout_gst_amount);
        txvPstAmount = view.findViewById(R.id.txv_checkout_pst_amount);
        txvOrderTotalPrice = view.findViewById(R.id.txv_checkout_order_total_amount);
        // Edittext
        customerNote = view.findViewById(R.id.edt_customer_notes);
        // Btn Order
        btnOrder = view.findViewById(R.id.btn_place_order);

        btnOrder.setOnClickListener(v -> {
            if(isOrderPlaced()) {
                //redirect to successful page
                NavController navController = Navigation.findNavController(view);
                navController.navigate(R.id.action_customerCheckoutFragment_to_customerOrderReceivedFragment);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        navController.popBackStack(); // Close the current fragment
//                    }
//                }, 7000);

            }


        });

    }

    private boolean isOrderPlaced() {
        Log.d("place order", "place order clicked");
        boolean validated = false;
        CustomerCheckoutDataViewModel.ShipmentInfo shipmentInfo = model.getShipmentInfoObject().getValue();
        String selectedPayment = model.getSelectedPayment().getValue();

        if (shipmentInfo == null) {
            txvShipmentMethod.setHintTextColor(ContextCompat.getColor(getContext(), R.color.delete_red));
            txvAddress.setHintTextColor(ContextCompat.getColor(getContext(), R.color.delete_red));
            txvReceiverPhone.setHintTextColor(ContextCompat.getColor(getContext(), R.color.delete_red));
            if (selectedPayment == null) {
                txvPayment.setHintTextColor(ContextCompat.getColor(getContext(), R.color.delete_red));
            }
            Toast.makeText(getContext(), "Please provide all the required information.", Toast.LENGTH_SHORT).show();
        } else {
            if (selectedPayment == null) {
                txvPayment.setHintTextColor(ContextCompat.getColor(getContext(), R.color.delete_red));
                Toast.makeText(getContext(), "Please provide all the required information.", Toast.LENGTH_SHORT).show();
            } else {
                String address = shipmentInfo.getAddress().trim(),
                        receiverName = shipmentInfo.getReceiver().trim(),
                        phone = shipmentInfo.getPhone().trim(),
                        shippingMethod = shipmentInfo.getMethod(),
                        note = customerNote.getText().toString().trim();
                int orderStatus = 0; //pending
                Double deliveryFee = shipmentInfo.getShippingFee();
                //check validity
                if (TextUtils.isEmpty(shippingMethod)) {
                    Log.d("place order", "shipment method not selected");
                    txvShipmentMethod.setHintTextColor(ContextCompat.getColor(getContext(), R.color.delete_red));
                } else if (TextUtils.isEmpty(address) || address == null) {
                    Log.d("place order", "address empty");
                    txvAddress.setHintTextColor(ContextCompat.getColor(getContext(), R.color.delete_red));
                } else if (TextUtils.isEmpty(receiverName) || receiverName == null ||
                        TextUtils.isEmpty(phone) || phone == null) {
                    Log.d("place order", "receiver info empty");
                    txvReceiverPhone.setHintTextColor(ContextCompat.getColor(getContext(), R.color.delete_red));
                } else {
                    String shipmentMethod = txvShipmentMethod.getText().toString();

                    if (shipmentMethod.equals(HOME_DELIVERY)) {
                        String country = shipmentInfo.getCountry().trim(),
                                province = shipmentInfo.getProvince().trim(),
                                city = shipmentInfo.getCity().trim(),
                                postalCode = shipmentInfo.getPostalCode().trim(),
                                email = shipmentInfo.getEmail().trim();
                        if (TextUtils.isEmpty(country) || TextUtils.isEmpty(province) ||
                                TextUtils.isEmpty(city) || TextUtils.isEmpty(postalCode) ||
                                TextUtils.isEmpty(email) ) {
                            Log.d("place order", "info empty");
                        } else {
                            Log.d("place order", "HD all of data is validated.");

                            //check group status and amount
                            if (isGroupStatusAndAmountValid()) {
                                Log.d("place order", "all groups are valid.");
                                //new order
                                Order order = new Order(customerId, sellerId, groupsAndItemsMap,
                                        orderTotal, checkoutTotal, gstTotal, pstTotal,
                                        System.currentTimeMillis(), deliveryFee, address, country,
                                        province,  city,  postalCode,  selectedPayment, orderStatus ,
                                        note, receiverName, phone, email, shippingMethod);

                                orderRef.push().setValue(order);
                                Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();

                                Map<String, Map<String, Order.OrderItemInfo>> orderMap = new HashMap<>(groupsAndItemsMap);

                                //inventory ordered ++
                                addOrderedAmountToInventory(orderMap);

                                //group amount --
                                deductGroupAmount(orderMap);

                                //items remove from cart
                                removeItemsFromCart();

                                validated = true;
                            }

                        }

                    } else if (shipmentMethod.equals(PICK_UP)) {
                        Log.d("place order", "PK all of data is valid.");

                        //check group status and amount
                        if (isGroupStatusAndAmountValid()) {
                            Log.d("place order", "all groups are valid.");
                            Log.d("place order","check size bf order: " + groupsAndItemsMap.size());
                            //new order
                            Order order = new Order(customerId, sellerId, groupsAndItemsMap,
                                    orderTotal, checkoutTotal, gstTotal, pstTotal,
                                    System.currentTimeMillis(), deliveryFee, address, null,
                                    null,  null,  null,  selectedPayment, orderStatus ,
                                    note, receiverName, phone, null, shippingMethod);

                            orderRef.push().setValue(order);
                            Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_SHORT).show();

                            Map<String, Map<String, Order.OrderItemInfo>> orderMap = new HashMap<>(groupsAndItemsMap);

                            //inventory ordered ++
                            addOrderedAmountToInventory(orderMap);

                            //group amount --
                            deductGroupAmount(orderMap);

                            //items remove from cart
                            removeItemsFromCart();

                            validated = true;

                        }

                    }

                }

            }

        }

        return validated;

    }


    private void deductGroupAmount(Map<String, Map<String, Order.OrderItemInfo>> orderMap) {
        final ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("place order", "group listening: "+ orderMap.keySet().size());

                for (String groupId : orderMap.keySet()) {
                    for (DataSnapshot singleGroup: snapshot.getChildren()) {
                        if (singleGroup.getKey().equals(groupId)) {
                            for(String productId_styleId: orderMap.get(groupId).keySet()) {
                                Log.d("!!!",productId_styleId);
                                String groupQtyMapId = "";
                                if(productId_styleId.contains("s___")) {
                                    groupQtyMapId = "s___" + productId_styleId.split("s___")[1];
                                } else {
                                    groupQtyMapId = productId_styleId;
                                }
                                int orderAmount = orderMap.get(groupId).get(productId_styleId).getOrderAmount();
                                int groupAmount = singleGroup.child("groupQtyMap").child(groupQtyMapId).getValue(Integer.class);

                                Log.d("place order","order amount: "+ orderAmount+ ";group amount: "+groupAmount);
                                groupAmount -= orderAmount;
                                singleGroup.child("groupQtyMap").child(groupQtyMapId).getRef().setValue(groupAmount);
                                Log.d("place order","value set, new group amount: "+groupAmount);
                            }
                        }
                    }
                }
                // Remove the listener after processing data
                groupRef.removeEventListener(this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        groupRef.addValueEventListener(listener);
    }

    private void addOrderedAmountToInventory(Map<String, Map<String, Order.OrderItemInfo>> orderMap) {
        Log.d("place order", "inventory method");
        Log.d("place order", "order_groups: " + orderMap.keySet().size());
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (String groupId : orderMap.keySet()) {
                    Log.d("place order", "groupId in order: " + groupId);

                    if  (orderMap.get(groupId) == null) {
                        continue;
                    }

                    for (DataSnapshot invSnapshot : snapshot.getChildren()) {
                        String invGroupId = invSnapshot.child("groupId").getValue(String.class);
                        Log.d("place order", "invGroupId: " + invGroupId);

                        if (invGroupId != null && invGroupId.equals(groupId)) {
                            if (orderMap.get(groupId) != null) { //np way is null
                                Log.d("place order!", ""+orderMap.get(groupId).keySet().size());
                                for (String productId_styleId : orderMap.get(groupId).keySet()) {
                                    int orderAmount = orderMap.get(groupId).get(productId_styleId).getOrderAmount();
                                    Log.d("place order", "order amount: " + orderAmount);
                                    if (invSnapshot.child("styleId").exists()) {
                                        String productId = productId_styleId.split("s___")[0].split("p___")[1];
                                        String styleId = productId_styleId.split("s___")[1];
                                        Log.d("place order", "pid: " + productId);
                                        Log.d("place order", "sid: " + styleId);
                                        String invProductId = invSnapshot.child("productId").getValue(String.class);
                                        String invStyleId = invSnapshot.child("styleId").getValue(String.class);
                                        Log.d("place order", "inv_pid: " + invProductId);
                                        Log.d("place order", "inv_sid: " + invStyleId);

                                        if (invProductId != null && invProductId.equals(productId) && invStyleId != null && invStyleId.equals(styleId)) {
                                            updateInventoryOrderedAmount(inventoryRef, invSnapshot.getKey(), orderAmount);
                                            Log.d("place order", "w/ styleId, inventory++");
                                        }
                                    } else { // No styleId
                                        String productId = productId_styleId.split("p___")[1];
                                        Log.d("place order", "pid: " + productId);
                                        String invProductId = invSnapshot.child("productId").getValue(String.class);
                                        Log.d("place order", "inv_pid: " + invProductId);

                                        if (invProductId != null && invProductId.equals(productId)) {
                                            inventoryRef.removeEventListener(this);
                                            updateInventoryOrderedAmount(inventoryRef, invSnapshot.getKey(), orderAmount);
                                            Log.d("place order", "w/o styleId, inventory++");
                                        }
                                    }
                                }

                            }

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateInventoryOrderedAmount(DatabaseReference inventoryRef, String inventoryKey, int orderAmount) {
        inventoryRef.child(inventoryKey).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentOrdered = mutableData.child("ordered").getValue(Integer.class);
                if (currentOrdered == null) {
                    currentOrdered = 0;
                }
                mutableData.child("ordered").setValue(currentOrdered + orderAmount);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                if (committed && databaseError == null) {
                    // Transaction was successful
                    Log.d("place order", "onCompleted, inventory++");
                } else {
                    // Transaction failed
                }
            }
        });
    }

    private boolean isGroupStatusAndAmountValid() {
        final int[] invalidCount = {0};
        outerLoop:
        for(String groupId: groupsAndItemsMap.keySet()) {
            groupRef.child(groupId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int status = snapshot.child("status").getValue(Integer.class);
                    int groupType = snapshot.child("groupType").getValue(Integer.class);
                    long endTimestamp = snapshot.child("endTimestamp").getValue(Long.class);

                    //status
                    if (status != OPENING) {
                        Toast.makeText(getContext(), "Some group in your order are not opening," +
                                " please reselect your items to checkout.", Toast.LENGTH_SHORT).show();
                        invalidCount[0]++;

                    } else if (groupType == 1 && System.currentTimeMillis() > endTimestamp) { // OPENING but time expired
                        Toast.makeText(getContext(), "Some group in your order are expired," +
                                " please reselect your items to checkout.", Toast.LENGTH_SHORT).show();
                        invalidCount[0]++;
                    }

                    //amount
                    if (groupsAndItemsMap.get(groupId) != null) {
                        Log.d("place order", "test again size: "+ groupsAndItemsMap.size());
                        for (String productId_styleId: groupsAndItemsMap.get(groupId).keySet()) {
                            //p___ + productId + (s___ + styleId)
                            String groupQtyMapId;
                            if(productId_styleId.contains("s___")) {
                                groupQtyMapId = "s___" + productId_styleId.split("s___")[1];
                            } else {
                                groupQtyMapId = productId_styleId;
                            }
                            Log.d("place order", "test again item id: "+ productId_styleId);
                            int orderAmount = groupsAndItemsMap.get(groupId).get(productId_styleId).getOrderAmount();
                            int itemGroupAmount = snapshot.child("groupQtyMap").child(groupQtyMapId).getValue(Integer.class);
                            Log.d("place order", "group amount on db: " +itemGroupAmount);
                            if (orderAmount > itemGroupAmount) {
                                Toast.makeText(getContext(), "Some items in your order do not have sufficient quantity for " +
                                                "placing an order. Please review and reselect your items before proceeding to checkout.",
                                        Toast.LENGTH_SHORT).show();
                                invalidCount[0]++;
                            }
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return invalidCount[0] == 0;
    }
    private void removeItemsFromCart() {
        //firebase
        customerRef.child(customerId).child("Cart").child(sellerId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("place order", "shopping size: " + shoppingItems.size());

                //update view model
                //1.
                Map<String, ArrayList<CartItem>> sellerItemsMap = cartItemsViewModel.getSellerItemsMapLiveData().getValue();
                ArrayList<CartItem> updatedCartItems = sellerItemsMap.get(sellerId);
                if (updatedCartItems != null) {
                    updatedCartItems.removeAll(shoppingItems);
                    if (updatedCartItems.size() == 0) {
                        sellerItemsMap.remove(sellerId);
                    } else {
                        sellerItemsMap.put(sellerId, updatedCartItems);
                        Log.d("place order", "sellerItemsMap updated: " + sellerItemsMap.size());
                    }
                    cartItemsViewModel.setSellerItemsMapLiveData(sellerItemsMap);

                    //2.
                    if (sellerItemsMap.get(sellerId) == null) {
                        Map<String, Boolean> sellerAllItemsCheckedMap = cartItemsViewModel.getSellerAllItemsCheckedMap().getValue();
                        sellerAllItemsCheckedMap.remove(sellerId);
                        cartItemsViewModel.setSellerAllItemsCheckedMapLiveData(sellerAllItemsCheckedMap);
                    }
                    //3.
                    Map<CartItem, CustomerCartItemsViewModel.CartItemInfo> cartItemInfoMap = cartItemsViewModel.getCartItemsInfoMapLiveData().getValue();
                    for (CartItem c: shoppingItems) {
                        cartItemInfoMap.remove(c);
                    }
                    cartItemsViewModel.setCartItemsInfoMapLiveData(cartItemInfoMap);
                }

                for (CartItem cartItem: shoppingItems) {
                    Log.d("place order","cart groupId: "+cartItem.getGroupId());
                    Log.d("place order","cart productId: "+cartItem.getProductId());
                    for(DataSnapshot itemSnapshot: snapshot.getChildren()) {
                        Log.d("place order","db groupId: "+itemSnapshot.child("groupId").getValue(String.class));
                        Log.d("place order","db productId: "+itemSnapshot.child("productId").getValue(String.class));
                        if ( itemSnapshot.child("groupId").getValue(String.class).equals(cartItem.getGroupId()) &&
                             itemSnapshot.child("productId").getValue(String.class).equals(cartItem.getProductId()) ) {
                            if (itemSnapshot.child("styleId").exists()) {
                                if (itemSnapshot.child("styleId").getValue(String.class).equals(cartItem.getStyleId())) {

                                    customerRef.child(customerId).child("Cart").child(sellerId).removeEventListener(this);
                                    itemSnapshot.getRef().removeValue();
                                    customerRef.child(customerId).child("Cart").child(sellerId).addValueEventListener(this);
                                    Log.d("place order", "w/ style delete from firebase");
                                    break;
                                }
                            } else {
                                customerRef.child(customerId).child("Cart").child(sellerId).removeEventListener(this);
                                itemSnapshot.getRef().removeValue();
                                customerRef.child(customerId).child("Cart").child(sellerId).addValueEventListener(this);
                                Log.d("place order", "w/o style delete from firebase");
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("place order", "not from firebase");
            }
        });
    }

//                //TODO: delete orderItems from cart if from cart
//                if(fromWhere == CART) {
//
//                } else if (fromWhere == DIRECTLY_BUY) {
//
//                }
//                //TODO: no need to delete if from direct checkout
//
//                Toast.makeText(getContext(),"Your order has been placed",Toast.LENGTH_SHORT).show();


    private void setSellerInfo () {

        if(shoppingItems != null && !shoppingItems.isEmpty()) {
            sellerId = shoppingItems.get(0).getSellerId();
            sellersRef.child(sellerId).child("storeInfo").child("storeName").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // set seller store name
                    txvStoreName.setText(snapshot.getValue(String.class));
                    //return false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    txvStoreName.setText("Store N/A");
                }
            });
        }

    }

    private void setShipmentClickListener(TextView txvShipment) {
        txvShipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String selectedProductSellerId = sellerId;

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String sellerId = childSnapshot.getKey();
                            if (selectedProductSellerId.equals(sellerId)) {
                                DataSnapshot storeInfoSnapshot = childSnapshot.child("storeInfo");
                                DataSnapshot deliveryInfoListSnapshot = storeInfoSnapshot.child("deliveryInfoList");

                                HashMap<String, Delivery> deliveryInfoListMap = new HashMap<>();
                                for (DataSnapshot deliverySnapshot : deliveryInfoListSnapshot.getChildren()) {
                                    String key = deliverySnapshot.getKey();
                                    Delivery delivery = deliverySnapshot.getValue(Delivery.class);
                                    deliveryInfoListMap.put(key, delivery);
                                }

                                Log.d("Test deliveryHashMap", "In Sheet's Size: "+deliveryInfoListMap.size());
                                model.deliveryMethods(deliveryInfoListMap);
                                break;
                            }
                        }


                        // Create BottomSheet
                        CustomerCheckoutDeliveryMethodFragment bottomSheetDialog = new CustomerCheckoutDeliveryMethodFragment(checkoutTotal);
                        bottomSheetDialog.show(getParentFragmentManager(), "Delivery BottomSheet Show");

                        // Set text for deliveryInfo
                        model.getShipmentInfoObject().observe(getViewLifecycleOwner(), new Observer<CustomerCheckoutDataViewModel.ShipmentInfo>() {
                            @Override
                            public void onChanged(CustomerCheckoutDataViewModel.ShipmentInfo shipmentInfo) {
                                if (shipmentInfo != null){
                                    txvShipment.setText(shipmentInfo.getMethod());
                                    setTxvShipment(txvReceiverPhone, shipmentInfo.getReceiver()+" "+shipmentInfo.getPhone());
                                    setTxvShipment(txvAddress,shipmentInfo.getAddress());
                                    //set selected shipment fee
                                    shipmentAmount = shipmentInfo.getShippingFee();
                                    if (shipmentAmount != null) {
                                        txvShipmentAmount.setText("CA$ "+ Double.parseDouble(String.format("%.2f", shipmentAmount)));
                                        orderTotal = checkoutTotal + gstTotal + pstTotal + shipmentAmount;
                                        txvOrderTotalPrice.setText("CA$ " + Double.parseDouble(String.format("%.2f", orderTotal)));
                                    }

                                }
                            }
                        });
                        //return false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    private void setTxvShipment(TextView txv, String str) {
        if (str.trim().isEmpty()) {
            txv.setText(null);
        } else {
            txv.setText(str);
        }
    }
    private void setPaymentClickListener(TextView txvPayment) {
        txvPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sellersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<String> acceptedPaymentTypes;

                        String selectedProductSellerId = sellerId;

                        for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                            String sellerId = childSnapshot.getKey();
                            if (selectedProductSellerId.equals(sellerId)) {
                                DataSnapshot storeInfoSnapshot = childSnapshot.child("storeInfo");
                                acceptedPaymentTypes = (ArrayList<String>) storeInfoSnapshot.child("acceptedPaymentTypes").getValue();
                                model.payments(acceptedPaymentTypes);
                                break;
                            }
                        }

                        // Create BottomSheet
                        CustomerCheckoutPaymentSelectorFragment bottomSheetDialog = new CustomerCheckoutPaymentSelectorFragment();
                        bottomSheetDialog.show(getParentFragmentManager(), "Payment BottomSheet Show");

                        // Set text for payment
                        model.getSelectedPayment().observe(getViewLifecycleOwner(), new Observer<String>() {
                            @Override
                            public void onChanged(String payment) {
                                if(payment != null){
                                    txvPayment.setText(payment);
                                }
                            }
                        });


                        //return false;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}