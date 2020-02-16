package com.elbaz.eliran.washmylaundry.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.controllers.activities.MainActivity;
import com.elbaz.eliran.washmylaundry.controllers.activities.SplashScreen;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 27-Jan-20.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final int SIGN_OUT_TASK = 10;

    // --------------------
    // LIFE CYCLE
    // --------------------

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(this.getFragmentLayout());
        ButterKnife.bind(this); //Configure Butterknife
    }

    public abstract int getFragmentLayout();

    // --------------------
    // UTILS
    // --------------------

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    @Nullable
    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }


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

    // --------------------
    // NETWORK CONNECTIVITY
    // --------------------

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG, "isNetworkAvailable: " + activeNetworkInfo);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public AlertDialog displayMobileDataSettingsDialog(final Activity activity, final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("No Internet");
        builder.setMessage("Please enable network access");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
                Intent intent = new Intent(context, SplashScreen.class);
                context.startActivity(intent);
                dialog.cancel();
            }
        });
        builder.show();

        return builder.create();
    }

    // -----------------
    // LOGOUT ACTION
    // -----------------

    public void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(SIGN_OUT_TASK));
    }

    public void signOutUserFromFirebaseOnly(){
        AuthUI.getInstance()
                .signOut(this);
    }

    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        clearActivityBackStack();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void clearActivityBackStack(){
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    // -------------
    // AlertDialogs
    // -------------

    public void alertDialogAction(String title, String message){
        new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Ok and close the alert

                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_dialog_alert_dark)
                .show();
    }

    public void alertDialogInformation(String title, String message){
        new MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Ok and close the alert

                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
//                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_dialog_alert_dark)
                .show();
    }
}

