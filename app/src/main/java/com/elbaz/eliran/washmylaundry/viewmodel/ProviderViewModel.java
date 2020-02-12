package com.elbaz.eliran.washmylaundry.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.repositories.ProviderDataRepository;

/**
 * Created by Eliran Elbaz on 07-Feb-20.
 */
public class ProviderViewModel extends ViewModel {

    // REPOSITORIES
    private ProviderDataRepository mProviderDataRepository = ProviderDataRepository.getInstance();

    // Data
    private MutableLiveData<Provider> mUserMutableLiveData = new MutableLiveData<>();

    public void init(){
        if(mUserMutableLiveData != null){
            return;
        }
        mProviderDataRepository = ProviderDataRepository.getInstance();
        mUserMutableLiveData = mProviderDataRepository.getCurrentProviderData();
    }


    public LiveData<Provider> getCurrentProviderData(){
//        return mUserMutableLiveData;
        return mProviderDataRepository.getCurrentProviderData();
    }

    public void setCurrentProviderData(){
//        this.mUserMutableLiveData.setValue(provider);
        mProviderDataRepository.setCurrentProviderData();
    }

}
