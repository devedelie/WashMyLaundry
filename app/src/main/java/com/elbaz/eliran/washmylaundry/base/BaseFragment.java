package com.elbaz.eliran.washmylaundry.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.models.User;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

/**
 * Created by Eliran Elbaz on 11-Feb-20.
 */
public abstract class BaseFragment extends Fragment {
    private User mUser;

    public BaseFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
//        userDataFirestoreListener();
        return view;
    }

    protected abstract int getFragmentLayout();

    //--------------------------
    // Cloud Firestore Listeners
    //--------------------------
//    private void userDataFirestoreListener(){
//        final DocumentReference docRef = UserHelper.getUserDocument(CurrentUserDataRepository.currentUserID);
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w(TAG, "Listen failed.", e);
//                    return;
//                }
//                // -- Data received
//                if (snapshot != null && snapshot.exists()) {
//                    Log.d(TAG, "Current data: " + snapshot.getData());
//                    mUser = new User();
//                    mUser = snapshot.toObject(User.class);
//                    if(mUser !=null){
//                        CurrentUserDataRepository.getInstance().setCurrentUserData(mUser); // If a change was detected, set Object in ViewModel
//                    }
//
//                } else {
//                    Log.d(TAG, "Current data: null");
//                }
//            }
//        });
//    }

    // --------------------
    // UTILS
    // --------------------

    @Nullable
    public FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    // -------------
    // AlertDialogs
    // -------------

    public void alertDialogAction(String title, String message){
        new MaterialAlertDialogBuilder(getContext().getApplicationContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Ok and close the alert

                    }
                })
                // A null listener allows the button to dismiss the dialog and take no further action.
//                .setNegativeButton(android.R.string.no, null)
                .setIcon(R.drawable.ic_help_black_24dp)
                .show();
    }
}
