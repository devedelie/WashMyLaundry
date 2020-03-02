package com.elbaz.eliran.washmylaundry.models;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Created by Eliran Elbaz on 04-Feb-20.
 */
public class User {


    private String uid, username, userAddress, district;
    @Nullable
    private String urlPicture, userEmail;
    private boolean isProvider;
    private int userZipCode, phoneNumber;
    private double userLatCoordinates, userLngCoordinates;
    private List<String> ordersList;

    public User(){ }

    public User(String uid, String username, @Nullable String urlPicture, boolean isProvider, @Nullable String userEmail) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isProvider = isProvider;
        this.userEmail = userEmail;
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

    public double getUserLatCoordinates() { return userLatCoordinates;}

    public void setUserLatCoordinates(double userLatCoordinates) { this.userLatCoordinates = userLatCoordinates; }

    public double getUserLngCoordinates() { return userLngCoordinates; }

    public void setUserLngCoordinates(double userLngCoordinates) { this.userLngCoordinates = userLngCoordinates; }

    public String getDistrict() { return district; }

    public void setDistrict(@Nullable String district) { this.district = district; }

    public int getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(int phoneNumber) { this.phoneNumber = phoneNumber; }

    public List<String> getOrdersList() { return ordersList; }

    public void setOrdersList(List<String> ordersList) { this.ordersList = ordersList; }

    @Nullable
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(@Nullable String userEmail) {
        this.userEmail = userEmail;
    }
}
