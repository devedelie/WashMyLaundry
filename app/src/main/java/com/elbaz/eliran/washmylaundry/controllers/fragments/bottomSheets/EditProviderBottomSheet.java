package com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.elbaz.eliran.washmylaundry.BuildConfig;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.base.BaseBottomSheet;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 06-Feb-20.
 */
public class EditProviderBottomSheet extends BaseBottomSheet {
    @BindView(R.id.edit_provider_address_text) EditText addressEditText;
    @BindView(R.id.edit_provider_zip_text) EditText zipEditText;
    @BindView(R.id.edit_provider_phone_text) EditText phoneEditText;
    @BindView(R.id.edit_provider_machine_type_text) EditText machineEditText;

    // For DATA
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private List<Place.Field> mFields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG); // Set the fields to specify which types of place data to return after the user has made a selection.
    private LatLng addressLatLng;

    public static EditProviderBottomSheet newInstance() {
        EditProviderBottomSheet editProviderBottomSheet;
        editProviderBottomSheet = new EditProviderBottomSheet();
        Bundle bundle = new Bundle();
//        bundle.putString(MARKER_ID, markerID);
//        bundle.putInt(MARKER_INDEX, index);
        editProviderBottomSheet.setArguments(bundle);
        return editProviderBottomSheet ;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.configureAddressViewType();
    }

    // --------------------
    // CONFIGURATIONS
    // --------------------
    @Override
    protected int getFragmentLayout() { return R.layout.bottom_sheet_edit_provider; }

    @Override
    protected int setTitle() { return R.string.edit_bs_title; }

    private void configureAddressViewType() {
        addressEditText.setClickable(true);
        addressEditText.setFocusable(false);
        addressEditText.setInputType(InputType.TYPE_NULL);
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

    @OnClick(R.id.edit_provider_address_v_button)
    public void onAddressSaveButton(){ if(addressEditText!=null)UserHelper.updateUserAddress(getCurrentUser().getUid(), addressEditText.getText().toString()); }

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
                addressEditText.setText(place.getAddress());
                addressLatLng = place.getLatLng();
                Log.d(TAG, "onActivityResult: " + addressLatLng);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(ContentValues.TAG, "onActivityResult Error: " + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}
