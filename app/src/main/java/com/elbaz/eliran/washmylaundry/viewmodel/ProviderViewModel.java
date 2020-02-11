package com.elbaz.eliran.washmylaundry.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elbaz.eliran.washmylaundry.models.User;

/**
 * Created by Eliran Elbaz on 07-Feb-20.
 */
public class ProviderViewModel extends ViewModel {

    // REPOSITORIES
//    private ProviderDataRepository mProviderDataRepository;

    // Data
    private MutableLiveData<User> mUserMutableLiveData = new MutableLiveData<>();

//    public void init(){
//        if(mUserMutableLiveData != null){
//            return;
//        }
//        mProviderDataRepository = ProviderDataRepository.getInstance();
//        mUserMutableLiveData = mProviderDataRepository.getCurrentProviderData();
//    }


    public LiveData<User> getCurrentProviderData(){
        return mUserMutableLiveData;
//        return mProviderDataRepository.getCurrentProviderData();
    }

    public void setCurrentProviderData(User user){
        this.mUserMutableLiveData.setValue(user);
//        mProviderDataRepository.setCurrentProviderData(user);
    }

}
