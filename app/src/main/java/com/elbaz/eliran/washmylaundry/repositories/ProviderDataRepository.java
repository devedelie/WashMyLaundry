package com.elbaz.eliran.washmylaundry.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 07-Feb-20.
 */
public class ProviderDataRepository {

    private static ProviderDataRepository sInstance;

    private MutableLiveData<User> mProviderData = new MutableLiveData<>();

    // Singleton
    public static ProviderDataRepository getInstance(){
        if(sInstance == null){
            sInstance = new ProviderDataRepository();
        }
        return sInstance;
    }


    // --- GET ---

//    public MutableLiveData<User> getCurrentProviderData(){
//        // Rest Request - fetch data from Firestore and set in MutableLiveData
//        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                User currentUser = documentSnapshot.toObject(User.class);
//                if(currentUser!=null) mUser = currentUser;
////                callback.onCallback(currentUser);
//                Log.d(TAG, "onSuccess: xxxTest1" + mUser.getUserAddress());
//            }
//        }).addOnFailureListener(onFailureListener());
//
//        mProviderData.setValue(mUser);
//        Log.d(TAG, "onSuccess: xxxTest2");
//        return mProviderData;
//    }

    //    public interface FirebaseDataCallback {
//        void onCallback(User user);
//    }


    public MutableLiveData<User> getCurrentProviderData(){
        return mProviderData;
    }



    // --- SET ---

    public void setCurrentProviderData(User user) {
        mProviderData.setValue(user);
    }



    // --------------------
    // REST REQUESTS
    // --------------------

//    private void fetchProviderData(FirebaseDataCallback callback){
//
//    }

    @Nullable
    private FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Failed to fetch user data in ProviderDataRepository");
            }
        };
    }
}
