package com.elbaz.eliran.washmylaundry.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.api.OrdersHelper;
import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.models.Orders;
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
    private List<Orders> mOrders;


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

    public void setProviderList(String isDelivering){
        listenToAllAvailableProvidersWithDelivery(isDelivering);
    }

    //-------------------------
    // GET/SET Orders List
    //-------------------------

    private MutableLiveData<List<Orders>> mOrdersList = new MutableLiveData<>();

    public MutableLiveData<List<Orders>> getOrdersList(){
        return mOrdersList;
    }

    public void setOrderList(String uid){
        listenToAllUserOrders(uid);
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


    //  Providers Listener with delivery condition
    private void listenToAllAvailableProvidersWithDelivery(String isDelivering){
        ProviderHelper.getProvidersCollection().whereEqualTo("isAvailable", true).whereEqualTo(isDelivering, true).addSnapshotListener(new EventListener<QuerySnapshot>() {
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


    //  Orders Listener with user Id condition
    private void listenToAllUserOrders(String uid){
        OrdersHelper.getOrdersCollection().whereEqualTo("uid", uid).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                // -- Data received
                mOrders = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    mOrders.add(doc.toObject(Orders.class));
                }

                mOrdersList.setValue(mOrders); // Set list in LiveData
                Log.d(TAG, "listenToAllAvailableOrders: END " + mOrders.size());

            }
        });
    }

}
