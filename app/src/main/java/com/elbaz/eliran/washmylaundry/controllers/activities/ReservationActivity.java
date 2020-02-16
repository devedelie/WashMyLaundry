package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.google.gson.Gson;

import static android.content.ContentValues.TAG;
/**
 * Created by Eliran Elbaz on 16-Feb-20.
 */
public class ReservationActivity extends BaseActivity {
    // For Data
    private Provider mProvider = new Provider();
    private String jsonObject = "{'pid' : 'providerName' : 'providerAddress' : 'providerZipCode' : 'phoneNumber' : 'machineType'}";
    private int bagsNumber;
    private boolean isDeliveryChecked, isIroningChecked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Extras
            Intent intent = getIntent();
            if(intent != null){
                bagsNumber = intent.getIntExtra("bagsNumber", 1);
                isDeliveryChecked = intent.getBooleanExtra("isDeliveryChecked", false);
                isIroningChecked = intent.getBooleanExtra("isIroningChecked", false);
                jsonObject = intent.getStringExtra("providerObject");
                mProvider = new Gson().fromJson(jsonObject, Provider.class);
                Log.d(TAG, "onCreate Reservation: 3 " + bagsNumber + " " + isDeliveryChecked + " " + isIroningChecked + " " + jsonObject + " " + mProvider.getMaxBags());
            }
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_reservation;
    }
}
