package com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.controllers.activities.ReservationActivity;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.models.User;
import com.elbaz.eliran.washmylaundry.repositories.UserDataRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 14-Feb-20.
 */
public class UserPreOrderBottomSheet extends BottomSheetDialogFragment {
    @BindView(R.id.id_details_profile_image) ImageView providerImage;
    @BindView(R.id.id_details_username_text) TextView providerName;
    @BindView(R.id.id_details_services_counter_text) TextView providerServiceCounter;
    @BindView(R.id.id_details_services_rate_text) TextView providerServiceRate;
    @BindView(R.id.pre_order_about_me_text) TextView providerDescription;
    @BindView(R.id.pre_order_plus_minus_number) TextView bagsNumber;
    @BindView(R.id.pre_order_max_bags_text) TextView bagsNumberText;
    @BindView(R.id.pre_order_ironing_switch) CheckBox ironingCheckBox;
    @BindView(R.id.pre_order_delivery_switch) CheckBox deliveryCheckBox;

    // For Data
    private Provider mProvider = new Provider();
    private String jsonObject = "{'pid' : 'providerName' : 'providerAddress' : 'providerZipCode' : 'phoneNumber' : 'machineType'}";
    // Data for Order
    private int bagsNumberInt =1; // Default bags number for order
    private boolean isIroningChecked, isDeliveryChecked;
    private User mUser = new User();

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
        this.configureCheckBoxes();
        this.setUiElements();
        UserDataRepository.getInstance().getCurrentUserData().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                mUser = user;
            }
        });

        return view;
    }

    // --------------------
    // Configurations
    // --------------------
    private void configureCheckBoxes() {
        // ----- Ironing Switch -------//
        ironingCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(ironingCheckBox.isChecked()){ isIroningChecked = true; }
                else{ isIroningChecked = false; }
            }
        });
        // ----- Delivery Switch -------//
        deliveryCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(deliveryCheckBox.isChecked()){ isDeliveryChecked = true; }
                else{ isDeliveryChecked = false; }
            }
        });
    }
    // --------------------
    // Actions
    // --------------------
    @OnClick(R.id.pre_order_plus_btn)
    public void onPlusBagsClick(){
        if(bagsNumberInt < mProvider.getMaxBags()){
            bagsNumberInt++;
            bagsNumber.setText(String.valueOf(bagsNumberInt));
        }
    }

    @OnClick(R.id.pre_order_minus_btn)
    public void onMinusBagsClick(){
        if(bagsNumberInt > 1){
            bagsNumberInt--;
            bagsNumber.setText(String.valueOf(bagsNumberInt));
        }
    }

    @OnClick(R.id.edit_provider_continue_btn)
    public void onContinueBtnClick (){
        if(mUser.getUserAddress() != null && !mUser.getUserAddress().isEmpty()){
            Intent intent = new Intent(getActivity().getApplicationContext(), ReservationActivity.class);
            intent.putExtra("isDeliveryChecked", isDeliveryChecked);
            intent.putExtra("isIroningChecked", isIroningChecked);
            intent.putExtra("bagsNumber", bagsNumberInt);
            intent.putExtra("providerObject", new Gson().toJson(mProvider));
            startActivity(intent);
        }else{
            Log.d(TAG, "onContinueBtnClick: "); // invoke error dialog to set address before reservation
        }
    }
    // --------------------
    // UI
    // --------------------
    private void setUiElements() {
        // Set provider Image //
        if (mProvider.getUrlPicture() != null) {
            Glide.with(this)
                    .load(mProvider.getUrlPicture())
                    .apply(RequestOptions.centerCropTransform())
                    .into(providerImage);
        }

        providerName.setText(mProvider.getProviderName());
        providerServiceCounter.setText(getString(R.string.pre_order_provider_service_count,String.valueOf(mProvider.getServicesCount())));
        providerServiceRate.setText(getString(R.string.id_details_euro_kg, String.valueOf(mProvider.getPricePerKg())));
        providerDescription.setText(mProvider.getProviderDescription());
        bagsNumberText.setText(getString(R.string.max_bags_15kg, String.valueOf(mProvider.getMaxBags())));
        bagsNumber.setText(String.valueOf(bagsNumberInt));
    }

    protected int getFragmentLayout() { return R.layout.bottom_sheet_user_pre_order; }
}
