package com.elbaz.eliran.washmylaundry.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.models.User;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 17-Feb-20.
 */
public class ReservationDataRepository {
    private static ReservationDataRepository sInstance;
    private User mUser;
    private Provider mProvider;

    // Singleton
    public static ReservationDataRepository getInstance(){
        if(sInstance == null){
            sInstance = new ReservationDataRepository();
        }
        return sInstance;
    }

    //-------------------------
    // GET/SET User Object
    //-------------------------

    private MutableLiveData<User> mUserMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<User> getCurrentUserData(){
        Log.d(TAG, "getCurrentUserData: " + mUserMutableLiveData);
        return mUserMutableLiveData;
    }

    public void setCurrentUserData(User user) {  // 'Set-Method', behaves as a trigger for the listener
        mUserMutableLiveData.setValue(user);
    }
    //-------------------------
    // GET/SET Providers List
    //-------------------------

    private MutableLiveData<Provider> mProviderMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Provider> getProvider(){
        return mProviderMutableLiveData;
    }

    public void setProvider(Provider provider){
        mProviderMutableLiveData.setValue(provider);
    }
}
