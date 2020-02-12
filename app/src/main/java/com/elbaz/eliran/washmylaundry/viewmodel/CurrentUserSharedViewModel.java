package com.elbaz.eliran.washmylaundry.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class CurrentUserSharedViewModel extends ViewModel {
    // REPOSITORIES
    private CurrentUserDataRepository mCurrentUserDataRepository;
    // Data


    public void init(){
        if(mCurrentUserID != null){
            return;
        }
        mCurrentUserDataRepository = CurrentUserDataRepository.getInstance();
        mCurrentUserID = mCurrentUserDataRepository.getCurrentUserId();
    }

    //------------------
    // UserId (Firebase)
    //------------------
    private MutableLiveData<String> mCurrentUserID;

    public LiveData<String> getCurrentUserId(){
        return mCurrentUserDataRepository.getCurrentUserId();
    }

    public void setCurrentUserId(){
        mCurrentUserDataRepository.setCurrentUserId();
    }


}