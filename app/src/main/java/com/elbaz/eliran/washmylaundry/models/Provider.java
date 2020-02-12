package com.elbaz.eliran.washmylaundry.models;

import javax.annotation.Nullable;

/**
 * Created by Eliran Elbaz on 04-Feb-20.
 */
public class Provider {
    private String pid, providerName, providerAddress, district, machineType;
    @Nullable
    private String urlPicture;
    private boolean isProvider, isAvailable, isDelivering, isIroning;
    private int userZipCode, phoneNumber, maxWeightKg, pricePerKg;
    private double providerLatCoordinates, providerLngCoordinates;

    public Provider(){ }

    public Provider(String pid, String providerName, @Nullable String urlPicture, boolean isProvider) {
        this.pid = pid;
        this.providerName = providerName;
        this.urlPicture = urlPicture;
        this.isProvider = isProvider;
        this.maxWeightKg = 7;
        this.pricePerKg = 5;
    }

    public String getPid() { return pid; }

    public void setPid(String pid) { this.pid = pid; }

    public String getProviderName() { return providerName; }

    public void setProviderName(String providerName) { this.providerName = providerName; }

    @Nullable
    public String getUrlPicture() { return urlPicture; }

    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    public boolean getIsProvider() { return isProvider; }

    public void setIsProvider(boolean provider) { isProvider = provider; }

    public String getProviderAddress() { return providerAddress; }

    public void setProviderAddress(@Nullable String providerAddress) { this.providerAddress = providerAddress; }

    public int getUserZipCode() { return userZipCode; }

    public void setUserZipCode(int userZipCode) { this.userZipCode = userZipCode; }

    public double getProviderLatCoordinates() { return providerLatCoordinates;}

    public void setProviderLatCoordinates(double providerLatCoordinates) { this.providerLatCoordinates = providerLatCoordinates; }

    public double getProviderLngCoordinates() { return providerLngCoordinates; }

    public void setProviderLngCoordinates(double providerLngCoordinates) { this.providerLngCoordinates = providerLngCoordinates; }

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

    public int getPricePerKg() { return pricePerKg; }

    public void setPricePerKg(int pricePerKg) { this.pricePerKg = pricePerKg; }

    public boolean getIsDelivering() { return isDelivering; }

    public void setIsDelivering(boolean delivering) { isDelivering = delivering; }

    public boolean getIsIroning() { return isIroning; }

    public void setIsIroning(boolean ironing) { isIroning = ironing; }
}


