package com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 14-Feb-20.
 */
public class UserPreOrderBottomSheet extends BottomSheetDialogFragment {
    @BindView(R.id.id_details_profile_image) ImageView providerImage;
    @BindView(R.id.id_details_username_text) TextView providerName;

    // For Data
    private Provider mProvider = new Provider();
    private String jsonObject = "{'pid' : 'providerName' : 'providerAddress' : 'providerZipCode' : 'phoneNumber' : 'machineType'}";

    public static UserPreOrderBottomSheet newInstance(String key, String providerJson) {
        Log.d(TAG, "newInstance BottomSheetEditProvider: " + key + " " +providerJson);
        UserPreOrderBottomSheet userPreOrderBottomSheet;
        userPreOrderBottomSheet = new UserPreOrderBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString(key, providerJson);
        userPreOrderBottomSheet.setArguments(bundle);
        return userPreOrderBottomSheet ;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(this.getFragmentLayout(), container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        // Convert JSON to User object
        jsonObject = bundle.getString("providerObject");
        mProvider = new Gson().fromJson(jsonObject, Provider.class);
        Log.d(TAG, "onCreateView: BTMS " + mProvider.getProviderName());
//
//        this.configureAddressViewType();
        this.setUiElements();

        return view;
    }

    private void setUiElements() {
        // Set provider Image //
        if (mProvider.getUrlPicture() != null) {
            Glide.with(this)
                    .load(mProvider.getUrlPicture())
                    .apply(RequestOptions.centerCropTransform())
                    .into(providerImage);
        }
        // Set provider Name //
        providerName.setText(mProvider.getProviderName());
    }

    protected int getFragmentLayout() { return R.layout.bottom_sheet_user_pre_order; }
}
