package com.elbaz.eliran.washmylaundry.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.api.OrdersHelper;
import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.models.Provider;
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
 * Created by Eliran Elbaz on 07-Feb-20.
 */
public class ProviderDataRepository {
    private List<Orders> mOrders;

    private static ProviderDataRepository sInstance;


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

    //-------------------------
    // GET/SET Provider Object
    //-------------------------

    private MutableLiveData<Provider> mProviderData = new MutableLiveData<>();

    public MutableLiveData<Provider> getCurrentProviderData(){
        Log.d(TAG, "getCurrentProviderData:");
        return mProviderData;
    }

    public void setCurrentProviderData() {  // 'Set' behaves as a trigger for the listener
        Log.d(TAG, "setCurrentProviderData:");
        userDataFirestoreListener();
    }


    //-------------------------
    // GET/SET Orders List
    //-------------------------

    private MutableLiveData<List<Orders>> mOrdersList = new MutableLiveData<>();

    public MutableLiveData<List<Orders>> getOrdersList(){
        return mOrdersList;
    }

    public void setOrderList(String pid){
        listenToAllProviderOrders(pid);
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


    //  Orders Listener with provider Id condition
    private void listenToAllProviderOrders(String pid){
        OrdersHelper.getOrdersCollection().whereEqualTo("pid", pid).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                Log.d(TAG, "listenToAllAvailableOrders: END " + mOrders.size() + " " + pid);

            }
        });
    }
}
