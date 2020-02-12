package com.elbaz.eliran.washmylaundry.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elbaz.eliran.washmylaundry.models.User;
import com.elbaz.eliran.washmylaundry.repositories.UserDataRepository;

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

}
