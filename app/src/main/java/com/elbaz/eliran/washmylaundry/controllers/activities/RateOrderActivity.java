package com.elbaz.eliran.washmylaundry.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.api.OrdersHelper;
import com.elbaz.eliran.washmylaundry.api.ProviderHelper;
import com.elbaz.eliran.washmylaundry.base.BaseActivity;
import com.elbaz.eliran.washmylaundry.models.Orders;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 23-Feb-20.
 */
public class RateOrderActivity extends BaseActivity {
    @BindView(R.id.rate_order_user_name) TextView userName;
    @BindView(R.id.rate_order_provider_name) TextView providerName;
    @BindView(R.id.rate_order_review_text) EditText reviewText;
    @BindView(R.id.rate_order_provider_image) ImageView providerImage;
    @BindView(R.id.provider_stars_1) ImageButton providerStars1;
    @BindView(R.id.provider_stars_2) ImageButton providerStars2;
    @BindView(R.id.provider_stars_3) ImageButton providerStars3;
    @BindView(R.id.provider_stars_4) ImageButton providerStars4;
    @BindView(R.id.provider_stars_5) ImageButton providerStars5;
    @BindView(R.id.rating_submit_btn) Button submitBtn;

    private double starsRate;
    private Orders mOrders = new Orders();
    private String jsonObject = "{'providerName' : 'userAddress' : 'clientName' : 'reservationDateFormatted' : 'phoneNumber' : 'orderStatus' : 'deliveryPrice' : 'ironingPrice' : 'finalPrice' : 'taxAdded' : 'providerRating' }";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent != null) { //Null Checking
            // Convert JSON to Order object
            jsonObject = intent.getStringExtra("orderObject");
            mOrders = new Gson().fromJson(jsonObject, Orders.class);
            Log.d(TAG, "onCreate Chat: " + mOrders.getProviderName());
        }

        this.updateUI();
    }

    // -----------------
    // CONFIGURATIONS
    // -----------------
    @Override
    public int getActivityLayout() {
        return R.layout.activity_rate_order;
    }

    // -----------------
    // ACTIONS
    // -----------------

    @OnClick(R.id.bottom_sheet_back_button)
    public void onBackButtonClick(){
        finish();
    }

    @OnClick(R.id.provider_stars_1)
    public void onStar1Click(){
        setStarState(providerStars1, true);
        setStarState(providerStars2, false);
        setStarState(providerStars3, false);
        setStarState(providerStars4, false);
        setStarState(providerStars5, false);
        starsRate =1;
    }

    @OnClick(R.id.provider_stars_2)
    public void onStar2Click(){
        setStarState(providerStars1, true);
        setStarState(providerStars2, true);
        setStarState(providerStars3, false);
        setStarState(providerStars4, false);
        setStarState(providerStars5, false);
        starsRate =2;
    }

    @OnClick(R.id.provider_stars_3)
    public void onStar3Click(){
        setStarState(providerStars1, true);
        setStarState(providerStars2, true);
        setStarState(providerStars3, true);
        setStarState(providerStars4, false);
        setStarState(providerStars5, false);
        starsRate =3;
    }

    @OnClick(R.id.provider_stars_4)
    public void onStar4Click(){
        setStarState(providerStars1, true);
        setStarState(providerStars2, true);
        setStarState(providerStars3, true);
        setStarState(providerStars4, true);
        setStarState(providerStars5, false);
        starsRate =4;
    }

    @OnClick(R.id.provider_stars_5)
    public void onStar5Click(){
        setStarState(providerStars1, true);
        setStarState(providerStars2, true);
        setStarState(providerStars3, true);
        setStarState(providerStars4, true);
        setStarState(providerStars5, true);
        starsRate =5;
    }

    @OnClick(R.id.rating_submit_btn)
    public void onSubmitClick(){
        String review = reviewText.getText().toString();
        // Register/Update data into existing Document
        DocumentReference updateProviderDocument = ProviderHelper.getProviderDocument(mOrders.getPid());
        updateProviderDocument.update("providerRating", FieldValue.increment(starsRate),"reviewsList", FieldValue.arrayUnion(review)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                DocumentReference updaeOrderDocument = OrdersHelper.getOrderDocument(mOrders.getUniqueOrderId());
                updaeOrderDocument.update("orderIsRated" , true);
                Log.d(TAG, "onSuccess: Submit review");
                finish();
            }
        });
    }

    // -----------------
    // UI
    // -----------------

    private void updateUI() {
        try {
            // Set provider Image //
            if (mOrders.getProviderImageUrl() != null) {
                Glide.with(this)
                        .load(mOrders.getProviderImageUrl())
                        .apply(RequestOptions.centerCropTransform())
                        .into(providerImage);
            }else {
                providerImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_anon_user_48dp));
            }

            userName.setText(getString(R.string.hi_user, mOrders.getClientName()));
            providerName.setText(getString(R.string.tell_us_how_was_your_order_with, mOrders.getProviderName()));
        }catch (Exception e){
            Log.e(TAG, "updateUI: ", e);
        }

    }

    private void setStarState(ImageButton imageButton, boolean isClicked){
        if(isClicked){
            imageButton.setImageResource(R.drawable.ic_rating_star_accent_24dp);
        }else {
            imageButton.setImageResource(R.drawable.ic_rating_star_border_accent_24dp);
        }


    }
}
