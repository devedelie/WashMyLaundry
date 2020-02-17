package com.elbaz.eliran.washmylaundry.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.models.User;
import com.elbaz.eliran.washmylaundry.repositories.UserDataRepository;

import java.util.List;

/**
 * Created by Eliran Elbaz on 12-Feb-20.
 */
public class UserViewModel extends ViewModel {
    // REPOSITORIES
    private UserDataRepository mUserDataRepository = UserDataRepository.getInstance();

    public void init(){
        if(mUserMutableLiveData != null){
            return;
        }
        mUserDataRepository = UserDataRepository.getInstance();
        mUserMutableLiveData = mUserDataRepository.getCurrentUserData();
    }


    //-------------------------
    // GET/SET User Object
    //-------------------------

    private MutableLiveData<User> mUserMutableLiveData = new MutableLiveData<>();

    public LiveData<User> getCurrentUserData(){
//
        return mUserDataRepository.getCurrentUserData();
    }

    public void setCurrentUserData(){
//
        mUserDataRepository.setCurrentUserData();
    }


    //-------------------------
    // GET/SET Providers List
    //-------------------------

    private MutableLiveData<List<Provider>> mProvidersListMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<List<Provider>> getProvidersList(){
        return mUserDataRepository.getProvidersList();
    }

    public void setProviderList(String isDelivering){
        mUserDataRepository.setProviderList(isDelivering);
    }

}
