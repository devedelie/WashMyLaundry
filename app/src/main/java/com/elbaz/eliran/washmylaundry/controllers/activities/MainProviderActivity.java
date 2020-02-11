package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets.EditProviderBottomSheet;
import com.elbaz.eliran.washmylaundry.models.User;
import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;
import com.elbaz.eliran.washmylaundry.viewmodel.ProviderViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class MainProviderActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.main_provider_activity_drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.drawer_main_provider_activity) NavigationView navigationView;
    @BindView(R.id.id_details_profile_image) ImageView providerImage;
    @BindView(R.id.id_details_username_text) TextView usernameText;
    @BindView(R.id.id_details_email_text) TextView emailText;
    @BindView(R.id.id_details_phone_text) TextView phoneText;
    @BindView(R.id.id_details_address_text) TextView addressText;
    @BindView(R.id.id_details_status_text) TextView statusText;
    @BindView(R.id.provider_availability_on_off_text) TextView availabilityOnOffText;
    @BindView(R.id.provider_delivery_on_off_text) TextView deliveryOnOffText;
    @BindView(R.id.provider_ironing_on_off_text) TextView ironingOnOffText;
    @BindView(R.id.provider_availability_img) ImageView statusIndicator;
    @BindView(R.id.provider_status_switch) Switch statusSwitch;
    @BindView(R.id.provider_delivery_switch) Switch deliverySwitch;
    @BindView(R.id.provider_ironing_switch) Switch ironingSwitch;
    @BindView(R.id.provider_max_weight_picker_text) EditText maxWeightEditText;
    @BindView(R.id.provider_price_picker_text) EditText priceEditText;
    // Data
    private ProviderViewModel mProviderViewModel;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        configureDrawerLayoutAndNavigationView();
        configureSwitches();
        userDataFirestoreListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configureViewModel();
    }


    private void configureViewModel() {
        mProviderViewModel = new ViewModelProvider(this).get(ProviderViewModel.class);
//        mProviderViewModel.init(); // To retrieve the data from the repository
        mProviderViewModel.getCurrentProviderData().observe(this, this::updateUiWithData);
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main_provider;
    }

    //-------------------
    // CONFIGURATIONS
    //-------------------

    // Navigation drawer config
    protected void configureDrawerLayoutAndNavigationView(){
        // Configure drawer layout
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Configure NavigationView & set item selection listener
        View headerView = this.navigationView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.navigation_header_name);
        TextView userEmail = headerView.findViewById(R.id.navigation_header_email);
        ImageView userImage = headerView.findViewById(R.id.navigation_header_image);
        // set user name and email
        userName.setText(this.getCurrentUser().getDisplayName());
        userEmail.setText(this.getCurrentUser().getEmail());
        // Set Image
        if (this.getCurrentUser() != null) {
            if (this.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(this.getCurrentUser().getPhotoUrl())
                        .apply(RequestOptions.circleCropTransform())
                        .into(userImage);
            }
        }
        // Set listener
        navigationView.setNavigationItemSelectedListener(this);
    }

        private void configureSwitches() {
            // ----- Availability Switch -------//
            statusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){ availabilityOnOffText.setText(getString(R.string.provider_availability_on));}
                    else{ availabilityOnOffText.setText(getString(R.string.provider_availability_off)); }
                    UserHelper.updateProviderAvailabilityStatus(CurrentUserDataRepository.currentUserID, isChecked);
                }
            });
            // ----- Delivery Switch -------//
            deliverySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(deliverySwitch.isChecked()){ deliveryOnOffText.setText(getString(R.string.provider_delivery_on));}
                    else{ deliveryOnOffText.setText(getString(R.string.provider_delivery_off)); }
                    UserHelper.updateProviderDeliveryStatus(CurrentUserDataRepository.currentUserID, isChecked);
                }
            });
            // ----- Ironing Switch -------//
            ironingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(ironingSwitch.isChecked()){ ironingOnOffText.setText(getString(R.string.provider_ironing_on));}
                    else{ ironingOnOffText.setText(getString(R.string.provider_ironing_off)); }
                    UserHelper.updateProviderIroningStatus(CurrentUserDataRepository.currentUserID, isChecked);
                }
            });
    }

    //--------------------------
    // Cloud Firestore Listeners
    //--------------------------
    private void userDataFirestoreListener(){
        final DocumentReference docRef = UserHelper.getUserDocument(CurrentUserDataRepository.currentUserID);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                // -- Data received
                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: " + snapshot.getData());
                    mUser = new User();
                    mUser = snapshot.toObject(User.class);
                    if(mUser !=null){
                        mProviderViewModel.setCurrentProviderData(mUser); // If a change was detected, set Object in ViewModel
                    }

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }


    //-------------------
    // ACTIONS
    //-------------------

    @OnClick(R.id.provider_max_weight_picker_text)
    public void onWeightTextClick(){
        pricePickerDialog(getString(R.string.provider_max_weight_dialog), Integer.parseInt(maxWeightEditText.getText().toString()), 5, 25);
    }

    @OnClick(R.id.provider_price_picker_text)
    public void onPriceTextClick(){
        pricePickerDialog(getString(R.string.provider_price_rate_dialog), Integer.parseInt(priceEditText.getText().toString()), 3, 15);
    }

    // Drawer item selection
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int order = menuItem.getOrder();
        Log.d(TAG, "Test onNavigationItemSelected: "+ order);
        switch (order){
            case 0:
                 // Your orders
                break;
            case 1:
                 // settings action
                break;
            case 2:
                signOutUserFromFirebase(); // logout action
                break;
        }
        this.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @OnClick(R.id.edit_provider_icon)
    public void onEditClick(){
        EditProviderBottomSheet.newInstance("userObject", new Gson().toJson(mUser)).show(getSupportFragmentManager(), "editProvider");
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void updateProviderMaxWeight(int maxWeight){
        UserHelper.updateProviderWeightPerService(CurrentUserDataRepository.currentUserID, maxWeight).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Max Weight Saved");
            }
        });
    }

    private void updateProviderPrice(int pricePerKg){
        UserHelper.updateProviderPricePerKg(CurrentUserDataRepository.currentUserID, pricePerKg).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Price Saved");
            }
        });
    }

    //-------------------
    // UI
    //-------------------

    private void updateUiWithData(User user) {
        // Set provider Image //
        if (this.getCurrentUser().getPhotoUrl() != null) {
            Glide.with(this)
                    .load(this.getCurrentUser().getPhotoUrl())
                    .apply(RequestOptions.centerCropTransform())
                    .into(providerImage);
        }
        usernameText.setText(user.getUsername());
        emailText.setText(getCurrentUser().getEmail());
        // Phone //
        if(user.getPhoneNumber() == 0){ phoneText.setText(R.string.provider_phone_not_available);}
        else{phoneText.setText(String.valueOf(user.getPhoneNumber()));}
        // Address //
        if(user.getUserAddress() == null){addressText.setText(R.string.provider_address_not_available);}
        else{addressText.setText(user.getUserAddress());}
        // Status //
        if(user.getIsAvailable()) {statusText.setText(R.string.provider_status_available); statusIndicator.setImageResource(R.drawable.ic_button_icon_green); statusSwitch.setChecked(true);}
        else{statusText.setText(R.string.provider_status_not_available); statusIndicator.setImageResource(R.drawable.ic_button_icon_red); statusSwitch.setChecked(false);}
        // Delivery //
        if(user.getIsDelivering()) {deliveryOnOffText.setText(R.string.provider_delivery_on);  deliverySwitch.setChecked(true);}
        else{deliveryOnOffText.setText(R.string.provider_delivery_off); deliverySwitch.setChecked(false);}
        // Ironing //
        if(user.getIsIroning()) {ironingOnOffText.setText(R.string.provider_ironing_on);  ironingSwitch.setChecked(true);}
        else{ironingOnOffText.setText(R.string.provider_ironing_off); ironingSwitch.setChecked(false);}
        // Max Weight //
        maxWeightEditText.setText(String.valueOf(user.getMaxWeightKg()));
        // Price //
        priceEditText.setText(String.valueOf(user.getPricePerKg()));

    }


    public void pricePickerDialog(String title, int displayCurrentValue, int minValue, int maxValue) {
        final Dialog dialog = new Dialog(MainProviderActivity.this);
        dialog.setContentView(R.layout.number_picker_dialog);
        Button saveBtn = (Button) dialog.findViewById(R.id.number_picker_save);
        TextView dialogTitle = (TextView) dialog.findViewById(R.id.picker_dialog_title);
        dialogTitle.setText(title);
        final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(maxValue); // max value depends on the calling method
        np.setMinValue(minValue);   // min value depends on the calling method
        np.setValue(displayCurrentValue);
        np.setWrapSelectorWheel(false);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Save value in Firestore
                if(title.equals(getString(R.string.provider_price_rate_dialog))) updateProviderPrice(np.getValue());
                if(title.equals(getString(R.string.provider_max_weight_dialog))) updateProviderMaxWeight(np.getValue());
                dialog.dismiss();
            }
        });
        dialog.show();
    }


}


