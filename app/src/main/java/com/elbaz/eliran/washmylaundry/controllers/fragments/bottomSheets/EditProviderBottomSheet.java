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
import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.base.BaseBottomSheet;
import com.elbaz.eliran.washmylaundry.models.Provider;
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
 * Created by Eliran Elbaz on 06-Feb-20.
 */
public class EditProviderBottomSheet extends BaseBottomSheet {
    @BindView(R.id.bottom_sheet_top_bar_title) TextView topTitle;
    @BindView(R.id.edit_provider_address_text) EditText addressEditText;
    @BindView(R.id.edit_provider_zip_text) EditText zipEditText;
    @BindView(R.id.edit_provider_phone_text) EditText phoneEditText;
    @BindView(R.id.edit_provider_machine_type_text) EditText machineEditText;

    // For DATA
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    private List<Place.Field> mFields = Arrays.asList(Place.Field.ID, Place.Field.ADDRESS,Place.Field.LAT_LNG); // Set the fields to specify which types of place data to return after the user has made a selection.
    private double addressLat;
    private double addressLng;
    private Provider mProvider = new Provider();
    private String jsonObject = "{'providerName' : 'providerAddress' : 'providerZipCode' : 'phoneNumber' : 'machineType'}";

    public static EditProviderBottomSheet newInstance(String key, String providerJson) {
        Log.d(TAG, "newInstance BottomSheetEditProvider: " + key + " " +providerJson);
        EditProviderBottomSheet editProviderBottomSheet;
        editProviderBottomSheet = new EditProviderBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString(key, providerJson);
        editProviderBottomSheet.setArguments(bundle);
        return editProviderBottomSheet ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        // Convert JSON to User object
        jsonObject = bundle.getString("providerObject");
        mProvider = new Gson().fromJson(jsonObject, Provider.class);

        this.configureAddressViewType();
        this.setUiElements();

        return view;
    }


    // --------------------
    // CONFIGURATIONS
    // --------------------
    @Override
    protected int getFragmentLayout() { return R.layout.bottom_sheet_edit_provider; }

    @Override
    protected int setTitle() { return R.string.edit_bs_title; }

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

    @OnClick(R.id.bottom_sheet_edit_provider_layout)
    public void onScreenSpaceClick(View v){
        // Close keyboard
        hideKeyboard(v);
    }

    @OnClick(R.id.edit_provider_save_btn)
    public void onAddressSaveButton(){
        if(addressEditText!=null) ProviderHelper.updateProviderAddress(getCurrentUser().getUid(), addressEditText.getText().toString());
        if(addressLat != 0) ProviderHelper.updateProviderServiceLatCoordinates(getCurrentUser().getUid(), addressLat);
        if(addressLat != 0) ProviderHelper.updateProviderServiceLngCoordinates(getCurrentUser().getUid(), addressLng);
        if(zipEditText!=null && !zipEditText.getText().toString().isEmpty()) ProviderHelper.updateProviderZipCode(getCurrentUser().getUid(), Integer.valueOf(zipEditText.getText().toString()));
        if(phoneEditText!=null && !phoneEditText.getText().toString().isEmpty()) ProviderHelper.updateProviderPhone(getCurrentUser().getUid(), Integer.valueOf(phoneEditText.getText().toString()));
        if(machineEditText!=null)ProviderHelper.updateProviderMachineType(getCurrentUser().getUid(), machineEditText.getText().toString());
        dismiss();
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

    //---------------
    // UI
    //--------------

    private void setUiElements() {
        topTitle.setText(setTitle());
        if(mProvider.getProviderAddress() != null && !mProvider.getProviderAddress().isEmpty()) addressEditText.setText(mProvider.getProviderAddress());
        if(mProvider.getUserZipCode() != 0) zipEditText.setText(String.valueOf(mProvider.getUserZipCode()));
        if(mProvider.getPhoneNumber()!= 0 ) phoneEditText.setText(String.valueOf(mProvider.getPhoneNumber()));
        if(mProvider.getMachineType() != null && !mProvider.getMachineType().isEmpty()) machineEditText.setText(mProvider.getMachineType());
    }

}
