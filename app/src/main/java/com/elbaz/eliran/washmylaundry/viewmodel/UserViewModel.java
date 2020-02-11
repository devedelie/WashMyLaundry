package com.elbaz.eliran.washmylaundry.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.elbaz.eliran.washmylaundry.models.User;

/**
 * Created by Eliran Elbaz on 11-Feb-20.
 */
public class UserViewModel extends ViewModel {
    // Data
    private MutableLiveData<User> mUserMutableLiveData = new MutableLiveData<>();

}
