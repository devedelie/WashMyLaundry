package com.elbaz.eliran.washmylaundry.models;

import com.google.android.gms.maps.model.LatLng;

import javax.annotation.Nullable;

/**
 * Created by Eliran Elbaz on 04-Feb-20.
 */
public class User {


    private String uid, username, userAddress, district, machineType;
    @Nullable
    private String urlPicture;
    private boolean isProvider, isAvailable;
    private int userZipCode, phoneNumber, maxWeightKg;
    private LatLng userCoordinates;

    public User(){ }

    public User(String uid, String username, @Nullable String urlPicture, boolean isProvider) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isProvider = isProvider;
    }

    public String getUid() { return uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    @Nullable
    public String getUrlPicture() { return urlPicture; }

    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    public boolean getIsProvider() { return isProvider; }

    public void setIsProvider(boolean provider) { isProvider = provider; }

    public String getUserAddress() { return userAddress; }

    public void setUserAddress(@Nullable String userAddress) { this.userAddress = userAddress; }

    public int getUserZipCode() { return userZipCode; }

    public void setUserZipCode(int userZipCode) { this.userZipCode = userZipCode; }

    public LatLng getUserCoordinates() { return userCoordinates; }

    public void setUserCoordinates(LatLng userCoordinates) { this.userCoordinates = userCoordinates; }

    public String getDistrict() { return district; }

    public void setDistrict(@Nullable String district) { this.district = district; }

    public boolean getIsAvailable() { return isAvailable; }

    public void setIsAvailable(boolean available) { isAvailable = available; }

    public int getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }

    public int getMaxWeightKg() { return maxWeightKg; }

    public void setMaxWeightKg(int maxWeightKg) { this.maxWeightKg = maxWeightKg; }

    public String getMachineType() { return machineType; }

    public void setMachineType(String machineType) { this.machineType = machineType; }
}
