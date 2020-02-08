package com.elbaz.eliran.washmylaundry.repositories;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class CurrentUserDataRepository {

    private static CurrentUserDataRepository sInstance;

    private MutableLiveData<String> mCurrentUserID = new MutableLiveData<>();

    // A static variable to be used as a reference instead of calling FirebaseAuth.getInstance().getCurrentUser();  multiple times
    public static String currentUserID;

    // Singleton
    public static CurrentUserDataRepository getInstance(){
        if(sInstance == null){
            sInstance = new CurrentUserDataRepository();
        }
        return sInstance;
    }


    // --- GET ---

    public MutableLiveData<String> getCurrentUserId(){
        return mCurrentUserID;
    }


    // --- SET ---

    public void setCurrentUserId(){
        if(isCurrentUserLogged()){
            this.mCurrentUserID.setValue(getCurrentUser().getUid());
            currentUserID = getCurrentUser().getUid();
        }

    }

    // --------------------
    // REST REQUEST
    // --------------------

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

}