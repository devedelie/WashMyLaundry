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
    private static final String PERSONAL_ORDERS_COLLECTION = "personalOrders";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getOrdersCollection(){
        return FirebaseFirestore.getInstance().collection(ORDERS_COLLECTION);
    }

    // --- CREATE ---

    // Create an order by USER for a PROVIDER
    public static Task<Void> createOrderDocument(String uid, String pid, String uniqueOrderId, double taxAdded, double deliveryPrice, double ironingPrice, double finalPrice, String reservationDate, String reservationDateFormatted) {
        // Create Order object
        Orders orderDocumentToCreate = new Orders(uid, pid, uniqueOrderId, taxAdded, deliveryPrice, ironingPrice, finalPrice, reservationDate, reservationDateFormatted);

        // Add a new Order Document to Firestore
        return OrdersHelper.getOrdersCollection()
                .document(pid) // Setting uID for Document
                .collection(PERSONAL_ORDERS_COLLECTION)
                .document(uniqueOrderId)
                .set(orderDocumentToCreate);
    }
}
