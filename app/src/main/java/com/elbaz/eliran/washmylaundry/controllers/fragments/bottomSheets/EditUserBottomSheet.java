package com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.elbaz.eliran.washmylaundry.BuildConfig;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseBottomSheet;
import com.elbaz.eliran.washmylaundry.models.User;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 12-Feb-20.
 */
public class EditUserBottomSheet extends BaseBottomSheet {
    @BindView(R.id.bottom_sheet_top_bar_title) TextView topTitle;
    @BindView(R.id.edit_user_full_name_text) EditText fullNameEditText;
    @BindView(R.id.edit_user_address_text) EditText addressEditText;
    @BindView(R.id.edit_user_zip_text) EditText zipEditText;
    @BindView(R.id.edit_user_phone_text) EditText phoneEditText;

    // For DATA
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private List<Place.Field> mFields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG); // Set the fields to specify which types of place data to return after the user has made a selection.
    private double addressLat;
    private double addressLng;
    private User mUser = new User();
    private String jsonObject = "{'username' : 'userAddress' : 'userZipCode' : 'phoneNumber'}";

    public static EditUserBottomSheet newInstance(String key, String userJson) {
        Log.d(TAG, "newInstance BottomSheetEditProvider: " + key + " " +userJson);
        EditUserBottomSheet editUserBottomSheet;
        editUserBottomSheet = new EditUserBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString(key, userJson);
        editUserBottomSheet.setArguments(bundle);
        return editUserBottomSheet ;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        // Convert JSON to User object
        jsonObject = bundle.getString("userObject");
        mUser = new Gson().fromJson(jsonObject, User.class);

        this.configureAddressViewType();
        this.setUiElements();

        return view;
    }

    //-------------------
    // CONFIGURATIONS
    //-------------------

    @Override
    protected int getFragmentLayout() {
        return R.layout.bottom_sheet_edit_user;
    }

    @Override
    protected int setTitle() {
        return R.string.edit_bs_title;
    }

    private void configureAddressViewType() {
//        addressEditText.setClickable(true);
        addressEditText.setFocusable(false);
//        addressEditText.setInputType(InputType.TYPE_NULL);
        addressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                    launchAutocompleteSearchBar(); // If Internet is active - then invoke Google Address-Autocomplete
                }else {
//                    alertDialogAddress(); // If Internet is inactive - invoke warning dialog box
                }
            }
        });
    }

    // --------------------
    // ACTIONS
    // --------------------

    @OnClick(R.id.bottom_sheet_back_button)
    public void onBackButtonClick(){
        dismiss();
    }

    @OnClick(R.id.bottom_sheet_edit_user_layout)
    public void onScreenSpaceClick(View v){
        // Close keyboard
        hideKeyboard(v);
    }

    private void launchAutocompleteSearchBar(){
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(), BuildConfig.GOOGLE_API_KEY, Locale.FRANCE);
        }
        // Bias results to Paris region (use 'bounds' variable in below filter)
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(48.832304, 2.239726),
                new LatLng(48.900962, 2.42124));

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, mFields)
                .setTypeFilter(TypeFilter.ADDRESS)
                .setCountry("Fr")
                .setLocationBias(bounds)
                .build(getActivity().getApplicationContext());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    // onActivityResult for Search Auto-Complete
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Manage the action to be taken
                Place place = Autocomplete.getPlaceFromIntent(data);
                addressEditText.getText().clear();
                addressEditText.setText(place.getAddress());
                addressLat = place.getLatLng().latitude;
                addressLng = place.getLatLng().longitude;
                Log.d(TAG, "onActivityResult: " + addressLat + " , " + addressLng);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(ContentValues.TAG, "onActivityResult Error: " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }


    //-------------------
    // UI
    //-------------------

    private void setUiElements() {
        topTitle.setText(setTitle());
        if(mUser.getUserAddress() != null && !mUser.getUserAddress().isEmpty()) addressEditText.setText(mUser.getUserAddress());
        if(mUser.getUserZipCode() != 0) zipEditText.setText(String.valueOf(mUser.getUserZipCode()));
        if(mUser.getPhoneNumber()!= 0 ) phoneEditText.setText(String.valueOf(mUser.getPhoneNumber()));
        if(mUser.getUsername() != null && !mUser.getUsername().isEmpty()) fullNameEditText.setText(mUser.getUsername());
    }
}
