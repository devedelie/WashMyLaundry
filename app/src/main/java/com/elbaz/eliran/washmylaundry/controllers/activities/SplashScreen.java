package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class SplashScreen extends BaseActivity {

    public static Location deviceLocation; // Used for distance calculation on other fragments.
    private String deviceLocationVariable;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean currentUserMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDeviceLocation();
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_splash_screen;
    }

    // Get Device location
    private void getDeviceLocation(){
        try{
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                deviceLocation = location; // Set device location variable for distance calculation
                                // create a location string for retrofit (LatLng toString())
                                deviceLocationVariable = new LatLng(location.getLatitude(), location.getLongitude()).toString(); // set a global location variable for other use
                                deviceLocationVariable = deviceLocationVariable.replaceAll("[()]", "");
                                deviceLocationVariable = deviceLocationVariable.replaceAll("[lat/lng:]", "");
                                Log.d(TAG, "SplashScreen onSuccess: " + deviceLocation + " " + deviceLocationVariable);
                                checkUserMode();
                            }else{
                                Log.d(TAG, "onComplete: current location is null");
                            }
                        }
                    });
        }catch (SecurityException e){
            Log.e(TAG, "Failed to get device location: ", e);
            Toast.makeText(this, R.string.no_location_found, Toast.LENGTH_LONG).show();
        }
    }


    private void checkUserMode() { // Provider OR User ?
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if(currentUser!=null) launchActivity(currentUser.getIsProvider());
            }
        }).addOnFailureListener(onFailureListener());
    }

    private void launchActivity(boolean userMode) {
        if(userMode){
            intentActivity(MainProviderActivity.class);
        } else {
            intentActivity(MainUserActivity.class);
        }
    }

    private void intentActivity(Class classname){
        Intent intent = new Intent(this, classname);
        startActivity(intent);
    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
            }
        };
    }
}
