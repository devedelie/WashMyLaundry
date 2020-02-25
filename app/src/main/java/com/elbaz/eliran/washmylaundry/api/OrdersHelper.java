package com.elbaz.eliran.washmylaundry.api;

import com.elbaz.eliran.washmylaundry.models.Orders;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class OrdersHelper {
    private static final String ORDERS_COLLECTION = "orders";
//    private static final String PERSONAL_ORDERS_COLLECTION = "personalOrders";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getOrdersCollection(){
        return FirebaseFirestore.getInstance().collection(ORDERS_COLLECTION);
    }

    public static DocumentReference getOrderDocument(String uniqueId){
        return OrdersHelper.getOrdersCollection().document(uniqueId);
    }

    // --- CREATE ---

    // Create an order by USER for a PROVIDER
    public static Task<Void> createOrderDocument(String uid, String pid, String uniqueOrderId, String providerName, String providerImageUrl, String clientName, String clientImageUrl, int providerPhone, int clientPhone, int orderStatus, double taxAdded, double deliveryPrice, double ironingPrice, double finalPrice, String reservationDate, String reservationDateFormatted, String clientAddress, int orderBagsAmount, double subtotalPrice) {
        // Create Order object
        Orders orderDocumentToCreate = new Orders(uid, pid, uniqueOrderId, providerName, providerImageUrl, clientName, clientImageUrl, providerPhone, clientPhone, orderStatus, taxAdded, deliveryPrice, ironingPrice, finalPrice, reservationDate, reservationDateFormatted, clientAddress, orderBagsAmount, subtotalPrice);

        // Add a new Order Document to Firestore
        return OrdersHelper.getOrdersCollection()
                .document(uniqueOrderId)
                .set(orderDocumentToCreate);
    }



    // --- UPDATE ---

    public static Task<Void> updateOrderState(String uniqueOrderId, int orderStatus){
        return OrdersHelper.getOrdersCollection().document(uniqueOrderId).update("orderStatus", orderStatus);
    }


    // --- DELETE ---

    public static Task<Void> deleteOrderFromOrdersList(String uniqueOrderId){

        return OrdersHelper.getOrdersCollection()
                .document(uniqueOrderId)
                .delete();
    }



}
