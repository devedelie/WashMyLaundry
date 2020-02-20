package com.elbaz.eliran.washmylaundry.api;

import com.elbaz.eliran.washmylaundry.models.Orders;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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

    // --- GET ---

    // Get current user orders list from all providers
//    public static Task<CollectionReference> getUserOrdersList(String uid){
//        return OrdersHelper.getOrdersCollection()
//                .document()
//                .collection(PERSONAL_ORDERS_COLLECTION)
//                .whereEqualTo("uid", uid)
//                .get();
//    }


}
