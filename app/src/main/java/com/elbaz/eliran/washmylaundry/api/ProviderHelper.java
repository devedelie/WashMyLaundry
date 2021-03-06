package com.elbaz.eliran.washmylaundry.api;

import com.elbaz.eliran.washmylaundry.models.Provider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Created by Eliran Elbaz on 04-Feb-20.
 */
public class ProviderHelper {
    private static final String COLLECTION_NAME = "providers";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getProvidersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createProvider (String pid, String providerName, String urlPicture, boolean isProvider, String providerEmail) {
        // Create Provider object
        Provider providerToCreate = new Provider(pid, providerName, urlPicture, isProvider, providerEmail);
        // Add a new Provider Document to Firestore
        return ProviderHelper.getProvidersCollection()
                .document(pid) // Setting uID for Document
                .set(providerToCreate); // Setting object for Document
    }

    // --- GET (Read) ---

    public static Task<DocumentSnapshot> getProvider(String uid){
        return ProviderHelper.getProvidersCollection().document(uid).get();
    }

    public static DocumentReference getProviderDocument(String uid){
        return ProviderHelper.getProvidersCollection().document(uid);
    }

    // --- Update ---

    public static Task<Void> updateOrdersList(String uid, List<String> orderId){
        return ProviderHelper.getProvidersCollection().document(uid).update("ordersList", orderId);
    }

    public static Task<Void> updateProviderServiceCount(String uid){
        return ProviderHelper.getProvidersCollection().document(uid).update("servicesCount", FieldValue.increment(1));
    }

    public static Task<Void> updateProviderAvailabilityStatus(String uid, boolean status){
        return ProviderHelper.getProvidersCollection().document(uid).update("isAvailable", status);
    }

    public static Task<Void> updateProviderDeliveryStatus(String uid, boolean status){
        return ProviderHelper.getProvidersCollection().document(uid).update("isDelivering", status);
    }

    public static Task<Void> updateProviderIroningStatus(String uid, boolean status){
        return ProviderHelper.getProvidersCollection().document(uid).update("isIroning", status);
    }

    public static Task<Void> updateProviderMaxBagsPerService(String uid, int maxWeightPerService){
        return ProviderHelper.getProvidersCollection().document(uid).update("maxBags", maxWeightPerService);
    }

    public static Task<Void> updateProviderPricePerKg(String uid, int pricePerKg){
        return ProviderHelper.getProvidersCollection().document(uid).update("pricePerKg", pricePerKg);
    }

    public static Task<Void> updateAndDeleteOrderFromProviderOrdersList(String pid, String uniqueOrderId){
        return ProviderHelper.getProvidersCollection().document(pid).update("ordersList",  FieldValue.arrayRemove(uniqueOrderId));
    }





}
