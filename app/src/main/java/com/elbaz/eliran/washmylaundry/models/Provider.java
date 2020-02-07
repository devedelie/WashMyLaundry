package com.elbaz.eliran.washmylaundry.models;

import com.google.android.gms.maps.model.LatLng;

import javax.annotation.Nullable;

/**
 * Created by Eliran Elbaz on 04-Feb-20.
 */
public class Provider {
    private String pid;
    private String providerName;
    @Nullable
    private String urlPicture, providerAddress;
    private boolean isProvider, isProviderAvailable;
    private int providerZipCode;
    private LatLng providerCoordinates;

    //*******************************************
    // To be used in the future, on 2 apps version
    //*******************************************

    public Provider(){ }

    public Provider(String pid, String providerName, @Nullable String urlPicture, boolean isProvider) {
        this.pid = pid;
        this.providerName = providerName;
        this.urlPicture = urlPicture;
        this.providerAddress = "";
        this.isProvider = isProvider;
        this.isProviderAvailable = false;
        this.providerZipCode = 0;
        this.providerCoordinates = null;
    }

    public String getPid() { return pid; }

    public void setPid(String pid) { this.pid = pid; }

    public String getProviderName() { return providerName; }

    public void setProviderName(String providerName) { this.providerName = providerName; }

    @Nullable
    public String getUrlPicture() { return urlPicture; }

    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    @Nullable
    public String getProviderAddress() { return providerAddress; }

    public void setProviderAddress(@Nullable String providerAddress) { this.providerAddress = providerAddress; }

    public boolean getIsProvider() { return isProvider; }

    public void setIsProvider(boolean provider) { isProvider = provider; }

    public boolean getIsProviderAvailable() { return isProviderAvailable; }

    public void setIsProviderAvailable(boolean providerAvailable) { isProviderAvailable = providerAvailable; }

    public int getProviderZipCode() { return providerZipCode; }

    public void setProviderZipCode(int providerZipCode) { this.providerZipCode = providerZipCode; }

    public LatLng getProviderCoordinates() { return providerCoordinates; }

    public void setProviderCoordinates(LatLng providerCoordinates) { this.providerCoordinates = providerCoordinates; }
}


