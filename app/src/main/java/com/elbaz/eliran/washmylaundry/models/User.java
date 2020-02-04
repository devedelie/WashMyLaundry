package com.elbaz.eliran.washmylaundry.models;

import com.google.android.gms.maps.model.LatLng;

import javax.annotation.Nullable;

/**
 * Created by Eliran Elbaz on 04-Feb-20.
 */
public class User {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture, userAddress;
    private boolean isProvider=false, isProviderAvailable;
    private int userZipCode;
    private LatLng userCoordinates;


    public User(){ }

    public User(String uid, String username, @Nullable String urlPicture, boolean isProvider) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isProvider = isProvider;
        this.userAddress = "";
        this.isProviderAvailable = false;
        this.userZipCode = 0;
        this.userCoordinates = null;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    @Nullable
    public String getUrlPicture() { return urlPicture; }

    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    public boolean isProvider() { return isProvider; }

    public void setProvider(boolean provider) { isProvider = provider; }

    @Nullable
    public String getUserAddress() { return userAddress; }

    public void setUserAddress(@Nullable String userAddress) { this.userAddress = userAddress; }

    public boolean isProviderAvailable() { return isProviderAvailable; }

    public void setProviderAvailable(boolean providerAvailable) { isProviderAvailable = providerAvailable; }

    public int getUserZipCode() { return userZipCode; }

    public void setUserZipCode(int userZipCode) { this.userZipCode = userZipCode; }

    public LatLng getUserCoordinates() { return userCoordinates; }

    public void setUserCoordinates(LatLng userCoordinates) { this.userCoordinates = userCoordinates; }
}
