package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class ProviderLoginActivity extends BaseActivity {
    @BindView(R.id.provider_login_coordinator_layout) CoordinatorLayout coordinatorLayout;
    // Identifier for Sign-In Activity
    private static final int RC_SIGN_IN = 100;

    @Override
    public int getFragmentLayout() { return R.layout.activity_provider_login; }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCurrentUserLogged()){ // (onResume is being called also when Firebase Auth-UI is being closed)
            loginOrCreateProviderInFirestore();
        }
    }

// --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.provider_login_button_email)
    public void onClickEmailLoginButton() { this.startSignInActivityWithEmail(); }

    @OnClick(R.id.provider_login_button_gmail)
    public void onClickGmailLoginButton() { this.startSignInActivityWithGmail(); }

    @OnClick(R.id.provider_login_button_facebook)
    public void onClickFacebookLoginButton() { this.startSignInActivityWithFacebook(); }

    @OnClick(R.id.user_login_btn_container)
    public void onClickProviderLogin() { this.intentActivity(MainActivity.class); finish(); }

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
                this.loginOrCreateProviderInFirestore();
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

    // Launching Activities
    private void intentActivity(Class classname){
        Intent intent = new Intent(this, classname);
        startActivity(intent);
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

    //  Http request that create user in firestore
    private void loginOrCreateProviderInFirestore(){

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
                                    boolean isProvider = true; // Provider Login Screen = true
                                    Log.d(TAG, "onComplete: User doesn't exist in Firestore " + username + " " + uid + " " + isProvider);
                                    // Create a Provider document (inside Users collection)
                                    UserHelper.createUser(uid, username, urlPicture, isProvider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            intentActivity(SplashScreen.class); // Call SplashScreen only when object was created in Firestore
                                        }
                                    }).addOnFailureListener(onFailureListener());
                                }else {
                                    Log.d(TAG, "onComplete: Continue normally -> User exist in Firestore " +mListOfDocuments.size());
                                    intentActivity(SplashScreen.class);
                                }
                            }
                        }
                    });
        }
    }

//    //  Http request that create provider in firestore
//    private void createProviderInFirestore(){
//
//        if (this.getCurrentUser() != null){
//            // Get user collection whereEqualsTo the current userID (if successful --> user exist in firestore)
//            ProviderHelper.getProvidersCollection().whereEqualTo("pid", getCurrentUser().getUid())
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                // Put Documents in a DocumentSnapshot List
//                                List<DocumentSnapshot> mListOfDocuments = task.getResult().getDocuments();
//                                if(mListOfDocuments.size()<=0){
//                                    String urlPicture = (getCurrentUser().getPhotoUrl() != null) ? getCurrentUser().getPhotoUrl().toString() : null;
//                                    String providerName = getCurrentUser().getDisplayName();
//                                    String pid = getCurrentUser().getUid();
//                                    boolean isProvider = true;
//                                    Log.d(TAG, "onComplete: User doesn't exist in Firestore " + providerName + " " + pid + " " + isProvider);
//                                    ProviderHelper.createProvider(pid, providerName, urlPicture, isProvider).addOnFailureListener(onFailureListener());
//                                }else {
//                                    Log.d(TAG, "onComplete: Continue normally -> User exist in Firestore " +mListOfDocuments.size());
//                                }
//                            }
//                        }
//                    });
//        }
//    }

    // --------------------
    // ERROR HANDLER
    // --------------------

    protected OnFailureListener onFailureListener(){
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_unknown_error), Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: ProviderLoginActivity Firebase fail");
            }
        };
    }


}
