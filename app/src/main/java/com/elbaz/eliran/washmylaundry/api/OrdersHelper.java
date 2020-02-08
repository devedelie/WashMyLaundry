package com.elbaz.eliran.washmylaundry.api;

import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.utils.Utils;
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
    public static Task<Void> createProviderOrdersDocument(String uid, String pid, String details) {
        // Create Order object
        Orders orderDocumentToCreate = new Orders(uid);
        String uniqueOrderID = uid + "AND" + pid + Utils.getDate(); // Creates a unique order ID in Provider's collection
        // Add a new User Document to Firestore
        return OrdersHelper.getOrdersCollection()
                .document(uid) // Setting uID for Document
                .collection(PERSONAL_ORDERS_COLLECTION)
                .document(uniqueOrderID)
                .set(orderDocumentToCreate);
    }
}
