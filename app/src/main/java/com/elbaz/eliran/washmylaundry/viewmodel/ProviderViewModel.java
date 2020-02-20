package com.elbaz.eliran.washmylaundry.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elbaz.eliran.washmylaundry.models.Orders;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.repositories.ProviderDataRepository;

import java.util.List;

/**
 * Created by Eliran Elbaz on 07-Feb-20.
 */
public class ProviderViewModel extends ViewModel {
    // REPOSITORIES
    private ProviderDataRepository mProviderDataRepository = ProviderDataRepository.getInstance();

    //-------------------------
    // GET/SET Provider Object
    //-------------------------

    public void initProvider(){
        if(mProviderMutableLiveData != null){
            return;
        }
        mProviderDataRepository = ProviderDataRepository.getInstance();
        mProviderMutableLiveData = mProviderDataRepository.getCurrentProviderData();
    }

    private MutableLiveData<Provider> mProviderMutableLiveData = new MutableLiveData<>();

    public LiveData<Provider> getCurrentProviderData(){
//        return mProviderMutableLiveData;
        return mProviderDataRepository.getCurrentProviderData();
    }

    public void setCurrentProviderData(){
//        this.mProviderMutableLiveData.setValue(provider);
        mProviderDataRepository.setCurrentProviderData();
    }

    //-------------------------
    // GET/SET Orders List
    //-------------------------


    public MutableLiveData<List<Orders>> getOrdersList(){
        return mProviderDataRepository.getOrdersList();
    }

    public void setOrderList(String pid){
        mProviderDataRepository.setOrderList(pid);
    }


}
