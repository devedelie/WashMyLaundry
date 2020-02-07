package com.elbaz.eliran.washmylaundry.api;

import com.elbaz.eliran.washmylaundry.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Eliran Elbaz on 04-Feb-20.
 */
public class UserHelper {
    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, boolean isProvider) {
        // Create User object
        User userToCreate = new User(uid, username, urlPicture, isProvider);
        // Add a new User Document to Firestore
        return UserHelper.getUsersCollection()
                .document(uid) // Setting uID for Document
                .set(userToCreate); // Setting object for Document
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }


    // --- SET ---

    public static Task<Void> updateUserAddress(String uid, String address){
        return UserHelper.getUsersCollection().document(uid).update("userAddress", address);
    }

    public static Task<Void> updateUserZipCode(String uid, int zipCode){
        return UserHelper.getUsersCollection().document(uid).update("userZipCode", zipCode);
    }

    public static Task<Void> updateUserPhone(String uid, int phone){
        return UserHelper.getUsersCollection().document(uid).update("phoneNumber", phone);
    }



    // -- PROVIDERS Methods only-------------------------------------------------------

    // --- SET ---

    public static Task<Void> updateUserMachineType(String uid, String machineType){
        return UserHelper.getUsersCollection().document(uid).update("machineType", machineType);
    }
}
