package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Constraints;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.elbaz.eliran.washmylaundry.BuildConfig;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.OrdersHelper;
import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.api.UserHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.models.User;
import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;
import com.elbaz.eliran.washmylaundry.repositories.ReservationDataRepository;
import com.elbaz.eliran.washmylaundry.repositories.UserDataRepository;
import com.elbaz.eliran.washmylaundry.utils.Utils;
import com.elbaz.eliran.washmylaundry.viewmodel.UserViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.elbaz.eliran.washmylaundry.models.Constants.IMAGE_URL_PART1;
import static com.elbaz.eliran.washmylaundry.models.Constants.IMAGE_URL_PART2;
import static com.elbaz.eliran.washmylaundry.models.Constants.IMAGE_URL_PART3;

/**
 * Created by Eliran Elbaz on 16-Feb-20.
 */
public class ReservationActivity extends BaseActivity {
    @BindView(R.id.reservation_activity_provider_name) TextView providerName;
    @BindView(R.id.order_activity_static_map_image) ImageView staticMapImage;
    @BindView(R.id.reservation_activity_user_address) TextView userDeliveryAddress;
    @BindView(R.id.reservation_activity_laundry_bags_amount) TextView laundryBagsAmount;
    @BindView(R.id.reservation_activity_laundry_bags_price) TextView laundryBagsPrice;
    @BindView(R.id.reservation_activity_subtotal_price) TextView subtotalPrice;
    @BindView(R.id.reservation_activity_laundry_fee_price) TextView feePrice;
    @BindView(R.id.reservation_activity_ironing_price) TextView ironingPrice;
    @BindView(R.id.reservation_activity_delivery_price) TextView deliveryPrice;
    @BindView(R.id.reservation_activity_total_price) TextView totalPrice;
    @BindView(R.id.reservation_ironing_container) LinearLayout ironingContainer;
    @BindView(R.id.reservation_delivery_container) LinearLayout deliveryContainer;

    // For Data
    private Provider mProvider = new Provider();
    private String jsonObject = "{'pid' : 'providerName' : 'providerAddress' : 'providerZipCode' : 'phoneNumber' : 'machineType' : 'pricePerKg' : 'maxBags' : 'ordersList'}";
    private int bagsNumber;
    private boolean isDeliveryChecked, isIroningChecked;
    private UserViewModel mUserViewModel;
    private User mUser = new User();
    // Data For Invoice
    double fee = 3.5, ironing = 8.5, na=0, delivery=3.5, bagsTotalPrice=0, total=0;
    private List<String> userOrdersList = new ArrayList<>();
    private List<String> providerOrdersList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get Extras
            Intent intent = getIntent();
            if(intent != null){
                bagsNumber = intent.getIntExtra("bagsNumber", 1);
                isDeliveryChecked = intent.getBooleanExtra("isDeliveryChecked", false);
                isIroningChecked = intent.getBooleanExtra("isIroningChecked", false);
                jsonObject = intent.getStringExtra("providerObject");
                mProvider = new Gson().fromJson(jsonObject, Provider.class);
                Log.d(TAG, "onCreate Reservation: 3 " + bagsNumber + " " + isDeliveryChecked + " " + isIroningChecked + " " + jsonObject + " " + mProvider.getMaxBags());
                ReservationDataRepository.getInstance().setProvider(mProvider); // Set in LiveData repository
            }

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getCurrentData();
    }

    private void getCurrentData() {
        UserDataRepository.getInstance().getCurrentUserData().observe(this, this::updateCurrentUserData);
        ReservationDataRepository.getInstance().getProvider().observe(this, new Observer<Provider>() {
            @Override
            public void onChanged(Provider provider) {
                Log.d(TAG, "onChanged: reservation provider");
                mProvider = provider;
            }
        });
    }

    private void updateCurrentUserData(User user) {
        mUser = user;
        Log.d(TAG, "updateCurrentUserData: Reservation" + user.getUsername());
        CurrentUserDataRepository.getInstance().getCurrentUserLatLng().observe(this, this::setUiElements);
    }

    // --------------------
    // Configurations
    // --------------------

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_reservation;
    }


    // --------------------
    // Actions
    // --------------------

    @OnClick(R.id.order_confirmation_btn)
    public void onConfirmationBtnClick(){
        createOrderInFirestore();
    }

    @OnClick(R.id.bottom_sheet_back_button)
    public void onBackButtonClick(){
        finish();
    }


    // --------------------
    // UI
    // --------------------

    private void setUiElements(LatLng latLng) {
        bagsTotalPrice = calculatePriceWithBagsAmount(bagsNumber);

        Log.d(TAG, "setUiElements: Reservation" + latLng.longitude);
        providerName.setText(mProvider.getProviderName());
        // Set static map
        if(Utils.isInternetAvailable(this)) {
            Glide.with(this).load(fabricateURL(latLng)).into(staticMapImage);
        } else {
//            staticMapImage.setImageResource(R.drawable.map_offline);
        }
        userDeliveryAddress.setText(mUser.getUserAddress());
        laundryBagsAmount.setText(getString(R.string.reservation_laundry_bags_amount, String.valueOf(mProvider.getMaxBags())));
        laundryBagsPrice.setText(getString(R.string.laundry_bags_price, String.valueOf(calculatePriceWithBagsAmount(bagsNumber))));
        subtotalPrice.setText(getString(R.string.laundry_subtotal_price, String.valueOf(bagsTotalPrice)));
        feePrice.setText(getString(R.string.laundry_fees_price, String.valueOf(fee)));
        if(isIroningChecked) { ironingContainer.setVisibility(View.VISIBLE); ironingPrice.setText(getString(R.string.laundry_ironing_price, String.valueOf(ironing)));}
        else {ironing = 0;} // Zero value for calculation
        if(isDeliveryChecked) { deliveryContainer.setVisibility(View.VISIBLE); deliveryPrice.setText(getString(R.string.laundry_delivery_price, String.valueOf(delivery)));}
        else {delivery = 0;} // Zero value for calculation

        total = bagsTotalPrice + fee + ironing + delivery; // Total sum
        totalPrice.setText(getString(R.string.laundry_total_price, String.valueOf(total)));
    }

    // --------------------
    // Utils
    // --------------------

    private double calculatePriceWithBagsAmount(int bagsAmount){
        double price = bagsAmount * (mProvider.getPricePerKg() * 5);  // 5Kg per bag X amount
        return price;
    }

    private String fabricateURL(LatLng latLng){
        String url="";
        // Get & Trim property LatLng -> Then create a url for the API
        try{
            String latLngString = (latLng.latitude + "," + latLng.longitude);
            String address = mUser.getUserAddress().replaceAll("[#%@!&*]","");
            url = (IMAGE_URL_PART1 + address +  IMAGE_URL_PART2 + latLngString + IMAGE_URL_PART3 + BuildConfig.GOOGLE_API_KEY);
            Log.d(Constraints.TAG, "fabricateURL: "+ url);
        }catch (Exception e){ }
        return url;
    }

    // --------------------
    // REST REQUEST
    // --------------------

    private void createOrderInFirestore(){
        String uniqueOrderId = Utils.getDateForOrderId() + "&UID=" +  mUser.getUid() + "&PID=" + mProvider.getPid() ; // Creates a unique order ID in Provider's collection
        if(mProvider.getOrdersList() !=null){ // If there are past values in the list, add them into ProviderOrderList
            providerOrdersList = mProvider.getOrdersList();
        }
        if(mUser.getOrdersList() != null){ // If there are past values in the list, add them into UserOrderList
            userOrdersList = mUser.getOrdersList();
        }
        providerOrdersList.add(uniqueOrderId);// add the current order id into ordersList
        userOrdersList.add(uniqueOrderId);// add the current order id into ordersList
        OrdersHelper.createOrderDocument(mUser.getUid(), mProvider.getPid(), uniqueOrderId, mProvider.getProviderName(),
                mProvider.getUrlPicture(), mUser.getUsername(), mUser.getUrlPicture(), mProvider.getPhoneNumber(),
                mUser.getPhoneNumber(), 1, fee, delivery, ironing, total, Utils.getFullDateForOrder().toString(), Utils.getTodayDateFormat())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Update User+Provider documents with the order in a ArrayList<>
                UserHelper.updateOrdersList(mUser.getUid(), userOrdersList).addOnFailureListener(onFailureListener());
                ProviderHelper.updateOrdersList(mProvider.getPid(), providerOrdersList).addOnFailureListener(onFailureListener());

                // End the operation with a message
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.create_reservation_success), Snackbar.LENGTH_LONG);
                snackbar.show();
                finish();
            }
        });
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
