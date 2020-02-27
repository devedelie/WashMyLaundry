package com.elbaz.eliran.washmylaundry.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elbaz.eliran.washmylaundry.models.Message;
import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;

import java.util.List;

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

    //--------------------------
    // GET/SET UserId (Firebase)
    //--------------------------
    private MutableLiveData<String> mCurrentUserID;

    public LiveData<String> getCurrentUserId(){
        return mCurrentUserDataRepository.getCurrentUserId();
    }

    public void setCurrentUserId(){
        mCurrentUserDataRepository.setCurrentUserId();
    }

    //-------------------------
    // GET/SET User Chat
    //-------------------------

    private MutableLiveData<List<Message>> mMessagesList = new MutableLiveData<>();

    public LiveData<List<Message>> getMessagesList(){
        return mCurrentUserDataRepository.getMessageList();
    }

    public void setMessagesList(String chatChannel){
        mCurrentUserDataRepository.setMessagesList(chatChannel);
    }


}
