package com.elbaz.eliran.washmylaundry.api;

import com.elbaz.eliran.washmylaundry.models.Provider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Eliran Elbaz on 04-Feb-20.
 */
public class ProviderHelper {
    private static final String COLLECTION_NAME = "providers";

    //*******************************************
    // To be used in the future, on 2 apps version
    //*******************************************

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getProvidersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createProvider (String pid, String providerName, String urlPicture, boolean isProvider) {
        // Create Provider object
        Provider providerToCreate = new Provider(pid, providerName, urlPicture, isProvider);
        // Add a new Provider Document to Firestore
        return ProviderHelper.getProvidersCollection()
                .document(pid) // Setting uID for Document
                .set(providerToCreate); // Setting object for Document
    }

    // --- GET (Read) ---

    public static Task<DocumentSnapshot> getProvider(String uid){
        return ProviderHelper.getProvidersCollection().document(uid).get();
    }
}
