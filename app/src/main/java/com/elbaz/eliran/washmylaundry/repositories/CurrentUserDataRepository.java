package com.elbaz.eliran.washmylaundry.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.api.ChatHelper;
import com.elbaz.eliran.washmylaundry.models.Message;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class CurrentUserDataRepository {
    private static CurrentUserDataRepository sInstance;
    private List<Message> mMessages;
    private ListenerRegistration registration;

    // A static variable to be used as a reference instead of calling FirebaseAuth.getInstance().getCurrentUser();  multiple times
    public static String currentUserID;

    // Singleton
    public static CurrentUserDataRepository getInstance(){
        if(sInstance == null){
            sInstance = new CurrentUserDataRepository();
        }
        return sInstance;
    }

    //------------------
    // UserId (Firebase)
    //------------------

    private MutableLiveData<String> mCurrentUserID = new MutableLiveData<>();


    public MutableLiveData<String> getCurrentUserId(){
        return mCurrentUserID;
    }

    public void setCurrentUserId(){
        if(isCurrentUserLogged()){
            this.mCurrentUserID.setValue(getCurrentUser().getUid());
            currentUserID = getCurrentUser().getUid();
        }
    }

    //----------
    // Location
    //----------

    private MutableLiveData<LatLng> mUserLatLng = new MutableLiveData<>();

    public LiveData<LatLng> getCurrentUserLatLng(){
        return mUserLatLng;
    }

    public void setCurrentUserLatLng(LatLng latLng){
        this.mUserLatLng.setValue(latLng);
    }


    //-----------------------
    // Chat Listeners
    //-----------------------

    private MutableLiveData<List<Message>> mMessagesList = new MutableLiveData<>();

    public LiveData<List<Message>> getMessageList(){
        return mMessagesList;
    }

    public void setMessagesList(String chatChannel){
        listenToChatChannels(chatChannel);
    }

    private void listenToChatChannels(String chatChannel){
        registration = ChatHelper.getChatCollection().document(chatChannel).collection("messages").whereEqualTo("messageReceived", false).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                // -- Data received
                mMessages = new ArrayList<>();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    mMessages.add(doc.toObject(Message.class));
                }

                mMessagesList.setValue(mMessages); // Set list in LiveData
                Log.d(TAG, "listenToChatChannels" );

            }
        });

    }

    public void stopListener(){
        registration.remove();
    }

    // --------------------
    // REST REQUEST
    // --------------------

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }



}
