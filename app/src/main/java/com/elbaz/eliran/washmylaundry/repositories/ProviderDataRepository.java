package com.elbaz.eliran.washmylaundry.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 07-Feb-20.
 */
public class ProviderDataRepository {

    private static ProviderDataRepository sInstance;

    private MutableLiveData<Provider> mProviderData = new MutableLiveData<>();
    private Provider mProvider;

    // Singleton
    public static ProviderDataRepository getInstance(){
        if(sInstance == null){
            sInstance = new ProviderDataRepository();
        }
        return sInstance;
    }


    //    public interface FirebaseDataCallback {
//        void onCallback(User user);
//    }

    // --- GET ---

    public MutableLiveData<Provider> getCurrentProviderData(){
        Log.d(TAG, "setCurrentProviderData: 1");
        return mProviderData;
    }



    // --- SET ---

    public void setCurrentProviderData() {  // 'Set' behaves as a trigger for the listener
        Log.d(TAG, "setCurrentProviderData: 1");
        userDataFirestoreListener();
    }



    //--------------------------
    // Cloud Firestore Listeners
    //--------------------------
    private void userDataFirestoreListener(){
        final DocumentReference docRef = ProviderHelper.getProviderDocument(CurrentUserDataRepository.currentUserID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                // -- Data received
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    mProvider = new Provider();
                    mProvider = snapshot.toObject(Provider.class);
                    if(mProvider !=null){
                        mProviderData.setValue(mProvider); // If a change was detected, set Object in ViewModel
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }
}
