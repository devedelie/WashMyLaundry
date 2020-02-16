package com.elbaz.eliran.washmylaundry.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 12-Feb-20.
 */
public class UserDataRepository {
    private static UserDataRepository sInstance;
    private User mUser;
    private Provider mProvider;
    private List<Provider> mProviderList;


    // Singleton
    public static UserDataRepository getInstance(){
        if(sInstance == null){
            sInstance = new UserDataRepository();
        }
        return sInstance;
    }

    //-------------------------
    // GET/SET User Object
    //-------------------------

    private MutableLiveData<User> mUserMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<User> getCurrentUserData(){
        return mUserMutableLiveData;
    }

    public void setCurrentUserData() {  // 'Set-Method', behaves as a trigger for the listener
        userDataFirestoreListener();
    }

    //-------------------------
    // GET/SET Providers List
    //-------------------------

    private MutableLiveData<List<Provider>> mProvidersListMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Provider>> getProvidersList(){
        return mProvidersListMutableLiveData;
    }

    public void setProviderList(boolean isDelivering){
        if(isDelivering){ listenToAllAvailableProvidersWithDelivery(); }
        else { listenToAllAvailableProviders(); }
    }


    //--------------------------
    // Cloud Firestore Listeners
    //--------------------------

    // User Listener
    private void userDataFirestoreListener(){
        final DocumentReference docRef = UserHelper.getUserDocument(CurrentUserDataRepository.currentUserID);
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
                    mUser = new User();
                    mUser = snapshot.toObject(User.class);
                    if(mUser !=null){
                        mUserMutableLiveData.setValue(mUser); // If a change was detected, set Object in ViewModel
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }


    //  Providers Listener
    private void listenToAllAvailableProviders(){
        ProviderHelper.getProvidersCollection().whereEqualTo("isAvailable", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                // -- Data received
                mProviderList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    mProviderList.add(doc.toObject(Provider.class));
                }

                mProvidersListMutableLiveData.setValue(mProviderList); // Set list in LiveData
                Log.d(TAG, "listenToAllAvailableProviders: END ");

            }
        });
    }

    //  Providers Listener with delivery condition
    private void listenToAllAvailableProvidersWithDelivery(){
        ProviderHelper.getProvidersCollection().whereEqualTo("isAvailable", true).whereEqualTo("isDelivering", true).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                // -- Data received
                mProviderList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    mProviderList.add(doc.toObject(Provider.class));
                }

                mProvidersListMutableLiveData.setValue(mProviderList); // Set list in LiveData
                Log.d(TAG, "listenToAllAvailableProviders: END ");

            }
        });
    }



    //  Get Providers
//    private void getAllAvailableProviders(){
//        ProviderHelper.getProvidersCollection().whereEqualTo("isAvailable", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    mProvider = new Provider();
//                    mProviderList = new ArrayList<>();
//                    for (DocumentSnapshot document : task.getResult()) {
//                        mProviderList.add(document.toObject(Provider.class));
//                    }
//
//                    mProvidersListMutableLiveData.setValue(mProviderList); // Set list in LiveData
//
//                } else {
//                    Log.d(TAG, "Error getting documents: ", task.getException());
//                }
//
//            }
//        });
//
//    }
}
