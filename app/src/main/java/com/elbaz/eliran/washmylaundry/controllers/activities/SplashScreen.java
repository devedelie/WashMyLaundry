package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.elbaz.eliran.washmylaundry.BuildConfig;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.models.User;
import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;
import com.elbaz.eliran.washmylaundry.viewmodel.CurrentUserSharedViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;

public class SplashScreen extends BaseActivity implements EasyPermissions.PermissionCallbacks {
    private static final String PERMS_FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_PERMISSION_CODE = 100;
    public static Boolean mLocationPermissionGranted = false;
    public static final Integer PERMISSIONS_REQUEST_ENABLE_GPS = 9002;

//    public static Location deviceLocation; // Used for distance calculation on other fragments.
//    private String deviceLocationVariable;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    public static CurrentUserSharedViewModel mCurrentUserSharedViewModel;
    public static String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        this.configureViewModel();
        // Verify all permissions and setups
        this.verifyPlacesSDK();
        this.isGpsEnabled();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Verify Network connectivity
        if(!isNetworkAvailable()){
            displayMobileDataSettingsDialog(this, this);}
        // Then, avoid login-screen if the user is already authenticated (onResume is being called also when Firebase Auth-UI is being closed)
        else if (isCurrentUserLogged() && mLocationPermissionGranted){
            mCurrentUserSharedViewModel.setCurrentUserId(); // To set currentUserID in ViewModel - Then Continue
            getDeviceLocation(); // Get Device location
        }else if (!isCurrentUserLogged() && mLocationPermissionGranted){
            intentActivity(MainActivity.class); // Go to Login screen if logged Off
        }

    }

    //----------------------------
    // CONFIGURATION
    //----------------------------

    private void configureViewModel() {
        mCurrentUserSharedViewModel = new ViewModelProvider(this).get(CurrentUserSharedViewModel.class);
        mCurrentUserSharedViewModel.init();
        mCurrentUserSharedViewModel.getCurrentUserId().observe(this, this::updateCurrentUserId);
    }

    private void updateCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_splash_screen;
    }

    //----------------------------
    // Permissions & device setup
    //----------------------------

    public void verifyPlacesSDK(){
        // Verify OR Initialize "Places SDK" on the device
        if (!Places.isInitialized()) {
            // Initialize Places.
            Places.initialize(getApplicationContext(), BuildConfig.GOOGLE_API_KEY);
            // Create a new Places client instance.
            PlacesClient placesClient = Places.createClient(this);
        }
    }

    public void isGpsEnabled(){
        final LocationManager manager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }else {
            askPermission();
        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.popup_title_permission_gps_access))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.popup_title_permission_gps_enable_btn), new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                        askPermission();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Method to ask the user for location authorization (with EasyPermissions support)
     */
    private void askPermission() {
        if (!EasyPermissions.hasPermissions(this, PERMS_FINE )) {
            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_location_access), RC_PERMISSION_CODE, PERMS_FINE);
            return;
        }else{
            mLocationPermissionGranted = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        mLocationPermissionGranted = true;
    }
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        mLocationPermissionGranted = false;
        askPermission();
    }

    //-----------------End Of User's Permissions -------------------------

    // Get Device location
    private void getDeviceLocation(){
        try{
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                CurrentUserDataRepository.getInstance().setCurrentUserLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
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



    // --------------------
    // REST REQUEST
    // --------------------

    private void checkUserMode() { // Provider OR User ?
        UserHelper.getUser(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User currentUser = documentSnapshot.toObject(User.class);
                if(currentUser!=null) launchActivity(currentUser.getIsProvider());
            }
        }).addOnFailureListener(onFailureListener());
        // If above fail, the account is Provider
        ProviderHelper.getProvider(this.getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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
