package com.elbaz.eliran.washmylaundry.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.models.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 12-Feb-20.
 */
public class UserDataRepository {
    private static UserDataRepository sInstance;
    private User mUser;


    // Singleton
    public static UserDataRepository getInstance(){
        if(sInstance == null){
            sInstance = new UserDataRepository();
        }
        return sInstance;
    }

    //-------------------------
    // GET/SET Provider Object
    //-------------------------

    private MutableLiveData<User> mUserMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<User> getCurrentUserData(){
        return mUserMutableLiveData;
    }

    public void setCurrentUserData() {  // 'Set-Method', behaves as a trigger for the listener
        userDataFirestoreListener();
    }


    //--------------------------
    // Cloud Firestore Listeners
    //--------------------------
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
}
