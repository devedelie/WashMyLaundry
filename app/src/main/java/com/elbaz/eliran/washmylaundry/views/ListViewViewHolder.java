package com.elbaz.eliran.washmylaundry.views;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 18-Feb-20.
 */
public class ListViewViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.list_view_recyclerView_provider_name) TextView providerName;
    @BindView(R.id.list_view_recyclerView_address) TextView providerAddress;
    @BindView(R.id.list_view__recyclerView_rate) TextView providerRate;
    @BindView(R.id.list_view__recyclerView_max_bags) TextView providerMaxBags;
    @BindView(R.id.list_view_recyclerViewList_stars_1) ImageView starImage1;
    @BindView(R.id.list_view_recyclerViewList_stars_2) ImageView starImage2;
    @BindView(R.id.list_view_recyclerViewList_stars_3) ImageView starImage3;
    @BindView(R.id.list_view_recyclerViewList_stars_4) ImageView starImage4;
    @BindView(R.id.list_view_recyclerViewList_stars_5) ImageView starImage5;
    @BindView(R.id.list_view_recyclerViewList_image) ImageView providerImage;

    public ListViewViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void updateProvidersList(Provider provider, RequestManager glide, Resources resources){
        try{
            // Set ImageView
            if(provider.getUrlPicture() != null && !provider.getUrlPicture().isEmpty()){
                glide.load(provider.getUrlPicture()).apply(RequestOptions.centerCropTransform()).into(providerImage);
            }else {
                providerImage.setImageDrawable(resources.getDrawable(R.drawable.ic_anon_user_48dp));
            }
            // Set TextViews
            providerName.setText(provider.getProviderName());
            providerAddress.setText(provider.getProviderAddress());
            providerRate.setText(String.valueOf(provider.getPricePerKg()));
            providerMaxBags.setText(String.valueOf(provider.getMaxBags()));
            calculateStarRating(provider);
        }catch (Exception e){
            Log.d(TAG, "updateProvidersList: ERROR");
        }
    }


    private void calculateStarRating(Provider provider){
        try{
            double ratingValue = Utils.rating(provider.getProviderRating()); // round the value of rating
            if (ratingValue < 0.5 ) {
                starImage1.setVisibility(View.INVISIBLE);
                starImage2.setVisibility(View.INVISIBLE);
                starImage3.setVisibility(View.INVISIBLE);
                starImage4.setVisibility(View.INVISIBLE);
                starImage5.setVisibility(View.INVISIBLE);
            }else if(ratingValue > 0.5 && ratingValue < 1.5){
                starImage1.setVisibility(View.VISIBLE);
                starImage2.setVisibility(View.INVISIBLE);
                starImage3.setVisibility(View.INVISIBLE);
                starImage4.setVisibility(View.INVISIBLE);
                starImage5.setVisibility(View.INVISIBLE);
            }else if(ratingValue > 1.5 && ratingValue < 2.5){
                starImage1.setVisibility(View.VISIBLE);
                starImage2.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.INVISIBLE);
                starImage4.setVisibility(View.INVISIBLE);
                starImage5.setVisibility(View.INVISIBLE);
            }else if(ratingValue > 2.5 && ratingValue < 3.5){
                starImage1.setVisibility(View.VISIBLE);
                starImage2.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.INVISIBLE);
                starImage3.setVisibility(View.INVISIBLE);
            }else if(ratingValue > 3.5 && ratingValue < 4.5){
                starImage1.setVisibility(View.VISIBLE);
                starImage2.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.INVISIBLE);
            }else if(ratingValue > 4.5 && ratingValue <= 5){
                starImage1.setVisibility(View.VISIBLE);
                starImage2.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.VISIBLE);
                starImage3.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            Log.d(TAG, "calculateStarRating: "+ e);
        }
    }
}
