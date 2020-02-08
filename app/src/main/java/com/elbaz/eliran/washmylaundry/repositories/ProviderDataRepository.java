package com.elbaz.eliran.washmylaundry.repositories;

import androidx.lifecycle.MutableLiveData;

import com.elbaz.eliran.washmylaundry.models.User;

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


    //    public interface FirebaseDataCallback {
//        void onCallback(User user);
//    }

    // --- GET ---

    public MutableLiveData<User> getCurrentProviderData(){
        return mProviderData;
    }



    // --- SET ---

    public void setCurrentProviderData(User user) {
        mProviderData.setValue(user);
    }


}
