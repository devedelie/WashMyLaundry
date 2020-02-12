package com.elbaz.eliran.washmylaundry.repositories;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class CurrentUserDataRepository {
    private static CurrentUserDataRepository sInstance;

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



    // --------------------
    // REST REQUEST
    // --------------------

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }



}
