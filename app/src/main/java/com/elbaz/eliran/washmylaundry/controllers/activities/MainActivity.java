package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.elbaz.eliran.washmylaundry.BuildConfig;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.EasyPermissions;

import static android.content.ContentValues.TAG;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks{
    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 100;
    private static final String PERMS_FINE = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int RC_PERMISSION_CODE = 100;
    public static Boolean mLocationPermissionGranted = false;
    public static final Integer PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static boolean washerSwitchMode;

    @BindView(R.id.main_activity_coordinator_layout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.washer_switch) Switch washerSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Verify all permissions and setups
        this.verifyPlacesSDK();
        this.isGpsEnabled();
    }

    @Override
    public int getFragmentLayout() { return R.layout.activity_main; }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: isLogged? " + isCurrentUserLogged() + "  isNetworkAvailable? "+ isNetworkAvailable() + "  hasLocationPermission? " + mLocationPermissionGranted);
        // Verify Network connectivity
        if(!isNetworkAvailable()){
            displayMobileDataSettingsDialog(this, this);}
        // Then, avoid login-screen if the user is already authenticated (onResume is being called also when Firebase Auth-UI is being closed)
        else if (isCurrentUserLogged() && mLocationPermissionGranted){
            loginModeSwitcher();
        }
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

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.main_activity_button_email)
    public void onClickEmailLoginButton() { this.startSignInActivityWithEmail(); }

    @OnClick(R.id.main_activity_button_gmail)
    public void onClickGmailLoginButton() { this.startSignInActivityWithGmail(); }

    @OnClick(R.id.main_activity_button_facebook)
    public void onClickFacebookLoginButton() { this.startSignInActivityWithFacebook(); }

    // --------------------
    // NAVIGATION
    // --------------------

    // Launch Sign-In Activity with Email- Firebase UI
    private void startSignInActivityWithEmail(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo)
                        .setIsSmartLockEnabled(false) //Disable SmartLock to enable Espresso testing on UI**
                        .build(),
                RC_SIGN_IN);
    }

    // Launch Sign-In Activity with Gmail- Firebase UI
    private void startSignInActivityWithGmail(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.GoogleBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo)
                        .build(),
                RC_SIGN_IN);
    }

    // Launch Sign-In Activity with Facebook- Firebase UI
    private void startSignInActivityWithFacebook(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(
                                Arrays.asList(
                                        new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setLogo(R.drawable.ic_logo)
                        .build(),
                RC_SIGN_IN);
    }

    // --------------------
    // UTILS
    // --------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Handle SignIn Activity response on activity result
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    //  Method that handles response after SignIn Activity close
    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                // CREATE USER IN FIRESTORE
                    this.loginModeSwitcher();
                    showSnackBar(this.coordinatorLayout, getString(R.string.connection_succeed));

            } else { // ERRORS
                if (response == null) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_authentication_canceled));
                } else if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_no_internet));
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    showSnackBar(this.coordinatorLayout, getString(R.string.error_unknown_error));
                }
            }
        }
    }

    // Launching SplashScreen Activity
    private void startSplashScreenActivity(){
        if (mLocationPermissionGranted){
            Intent intent = new Intent(this, SplashScreen.class);
            startActivity(intent);
        }else {
            showSnackBar(this.coordinatorLayout, getString(R.string.need_to_authorise_location_services));
        }
    }

    // --------------------
    // UI
    // --------------------

    // Show Snack Bar with a message
    private void showSnackBar(CoordinatorLayout coordinatorLayout, String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void loginModeSwitcher(){
        if(washerSwitch.isChecked()){
            loginOrcreateProviderInFirestore();
        }else {
            loginOrCreateUserInFirestore();
        }
    }

    //  Http request that create user in firestore
    private void loginOrCreateUserInFirestore(){

        if (this.getCurrentUser() != null){
            // Get user collection whereEqualsTo the current userID (if successful --> user exist in firestore)
            UserHelper.getUsersCollection().whereEqualTo("uid", getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // Put Documents in a DocumentSnapshot List
                                List<DocumentSnapshot> mListOfDocuments = task.getResult().getDocuments();
                                if(mListOfDocuments.size()<=0){
                                    String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null;
                                    String username = getCurrentUser().getDisplayName();
                                    String uid = getCurrentUser().getUid();
                                    boolean isProvider = washerSwitch.isChecked();
                                    Log.d(TAG, "onComplete: User doesn't exist in Firestore " + username + " " + uid + " " + isProvider);
                                    UserHelper.createUser(uid, username, urlPicture, isProvider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startSplashScreenActivity(); // Call SplashScreen only when object was created in Firestore
                                        }
                                    }).addOnFailureListener(onFailureListener());
                                }else {
                                    Log.d(TAG, "onComplete: Continue normally -> User exist in Firestore " +mListOfDocuments.size());
                                    startSplashScreenActivity();
                                }
                            }
                        }
                    });
        }
    }

    //  Http request that create provider in firestore
    private void loginOrcreateProviderInFirestore(){

        if (this.getCurrentUser() != null){
            // Get user collection whereEqualsTo the current userID (if successful --> user exist in firestore)
            ProviderHelper.getProvidersCollection().whereEqualTo("pid", getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                // Put Documents in a DocumentSnapshot List
                                List<DocumentSnapshot> mListOfDocuments = task.getResult().getDocuments();
                                if(mListOfDocuments.size()<=0){
                                    String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null;
                                    String providerName = getCurrentUser().getDisplayName();
                                    String pid = getCurrentUser().getUid();
                                    boolean isProvider = washerSwitch.isChecked();
                                    Log.d(TAG, "onComplete: User doesn't exist in Firestore " + providerName + " " + pid + " " + isProvider);
                                    ProviderHelper.createProvider(pid, providerName, urlPicture, isProvider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            startSplashScreenActivity();
                                        }
                                    }).addOnFailureListener(onFailureListener());
                                }else {
                                    Log.d(TAG, "onComplete: Continue normally -> User exist in Firestore " +mListOfDocuments.size());
                                    startSplashScreenActivity();
                                }
                            }
                        }
                    });
        }
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
