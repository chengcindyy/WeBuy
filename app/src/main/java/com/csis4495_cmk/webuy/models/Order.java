package com.csis4495_cmk.webuy.models;

import java.util.Map;

public class Order {
    private String customerId;
    private String sellerId;
    private Map<String, Map<String, OrderItemInfo>> groupsAndItemsMap; //groupId, p___ + productId + ( s___ + styleId ), orderItemInfo
    //    String styleId = key.split("s___")[1];
    //    String productId = key.split("s___")[0].split("p___")[1];
    //    p___thisIsAProductIds___thisIsAStyleId
    private double orderTotalPrice;
    //= itemsTotal + gstTotal + pstTotal + deliveryFee
    private double checkoutItemsTotal;
    //= {style price * qty}
    private double gstTotal;
    private double pstTotal;
    private long orderedTimestamp;
    private double deliveryFee;
    private String address;
    private String country;
    private String province;
    private String city;
    private String postalCode;
    private String paymentType;
    private int orderStatus;
    private String note;
    private String receiverName;
    private String phone;
    private String email;
    private String shippingMethod;

    public String getNote() {
        return note;
    }
/*
    -1: canceled
    0: pending (when customer make a order)
    1: paid (when seller confirm the payment
    2: allocated (when seller all items in the order are allocated
    3: processing (when seller sent the package
    4: received (when customer received the package
     */

    public Order() {
    }

    public Order(String customerId, String sellerId, Map<String, Map<String, OrderItemInfo>> groupsAndItemsMap,
                 double orderTotalPrice, double checkoutItemsTotal, double gstTotal, double pstTotal,
                 long orderedTimestamp, double deliveryFee, String address, String country,
                 String province, String city, String postalCode, String paymentType, int orderStatus,
                 String note, String receiverName, String phone, String email, String shippingMethod) {
        this.customerId = customerId;
        this.sellerId = sellerId;
        this.groupsAndItemsMap = groupsAndItemsMap;
        this.orderTotalPrice = orderTotalPrice;
        this.checkoutItemsTotal = checkoutItemsTotal;
        this.gstTotal = gstTotal;
        this.pstTotal = pstTotal;
        this.orderedTimestamp = orderedTimestamp;
        this.deliveryFee = deliveryFee;
        this.address = address;
        this.country = country;
        this.province = province;
        this.city = city;
        this.postalCode = postalCode;
        this.paymentType = paymentType;
        this.orderStatus = orderStatus;
        this.note = note;
        this.receiverName = receiverName;
        this.phone = phone;
        this.email = email;
        this.shippingMethod = shippingMethod;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public double getOrderTotalPrice() {
        return orderTotalPrice;
    }


    public double getDeliveryFee() {
        return deliveryFee;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public long getOrderedTimestamp() {
        return orderedTimestamp;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public Map<String, Map<String, OrderItemInfo>> getGroupsAndItemsMap() {
        return groupsAndItemsMap;
    }

    public double getCheckoutItemsTotal() {
        return checkoutItemsTotal;
    }

    public double getGstTotal() {
        return gstTotal;
    }

    public double getPstTotal() {
        return pstTotal;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public static class OrderItemInfo {
        public OrderItemInfo(){

        };

        int orderAmount;
        boolean isAllocated; //true: if the item is allocated by seller

        public OrderItemInfo(int orderAmount, boolean isAllocated) {
            this.orderAmount = orderAmount;
            this.isAllocated = isAllocated;
        }

        public void setAllocated(boolean allocated) {
            isAllocated = allocated;
        }

        public int getOrderAmount() {
            return orderAmount;
        }

        public boolean isAllocated() {
            return isAllocated;
        }
    }
}