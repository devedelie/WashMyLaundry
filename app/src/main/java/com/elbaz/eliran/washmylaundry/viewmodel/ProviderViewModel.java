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

    public void init(){
        if(mProviderMutableLiveData != null){
            return;
        }
        mProviderDataRepository = ProviderDataRepository.getInstance();
        mProviderMutableLiveData = mProviderDataRepository.getCurrentProviderData();
    }

    //-------------------------
    // GET/SET Provider Object
    //-------------------------

    private MutableLiveData<Provider> mProviderMutableLiveData = new MutableLiveData<>();

    public LiveData<Provider> getCurrentProviderData(){
//        return mProviderMutableLiveData;
        return mProviderDataRepository.getCurrentProviderData();
    }

    public void setCurrentProviderData(){
//        this.mProviderMutableLiveData.setValue(provider);
        mProviderDataRepository.setCurrentProviderData();
    }

}
