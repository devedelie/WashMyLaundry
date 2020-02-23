package com.elbaz.eliran.washmylaundry.utils;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.elbaz.eliran.washmylaundry.R;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class Utils {

    public static Date getDate(){
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    public static String getDateForOrderId(){
        DateFormat date = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault());
        return date.format(new Date());
    }

    public static Date getFullDateForOrder(){
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    public static String getTodayDateFormat(){
        DateFormat date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return date.format(new Date());
    }

    public static boolean isInternetAvailable(final Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG, "isInternetAvailable: " + activeNetworkInfo);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static double rating (double rating, int listSize ) {
//        double totalRatings=0;
//        for(int i =0; i < rating.size() ; i++){
//            totalRatings = rating.get(i);
//        }
        // Calculate rating average
        double newRating = rating / listSize;
        Log.d(TAG, "rating: " + rating + " / " + listSize + " = " +newRating );
        return newRating;
    }

    public static String getOrderStatus(int orderStatus){
        // Order Levels : 1) Waiting for approval  2) Approved by the provider 3) Order is Ready and waiting for delivery 4) Order delivered
        switch (orderStatus){
            case 1:
                return "Waiting for approval";
            case 2:
                return "Approved and in process";
            case 3:
                return "Order is Ready for delivery";
            case 4:
                return "Order delivered";
        }
        return "";
    }

    public static int getOrderStatusColor(int orderStatus){
        // Order Levels : 1) Waiting for approval  2) Approved by the provider 3) Order is Ready and waiting for delivery 4) Order delivered
        switch (orderStatus){
            case 1:
                return R.color.order_waiting;
            case 2:
                return R.color.order_approved;
            case 3:
                return R.color.order_ready;
            case 4:
                return R.color.order_delivered;
        }
        return R.color.order_waiting;
    }

    public static int calculateDistance(LatLng userLatLng, LatLng providerLatLng){
        Log.d(TAG, "calculateDistance: " + userLatLng.latitude + " " + userLatLng.longitude+ " " + providerLatLng.latitude+ " " + providerLatLng.longitude);
        float[] distance = new float[1];
        try{
            if(providerLatLng.latitude !=0 && providerLatLng.longitude !=0 && userLatLng.latitude !=0 && userLatLng.longitude !=0){
                Location.distanceBetween(userLatLng.latitude, userLatLng.longitude, providerLatLng.latitude, providerLatLng.longitude, distance); }
        }catch (Exception e){
            Log.d(TAG, "calculateDistance Error: " +e);
        }
        Log.d(TAG, "calculateDistance: Distance" + Math.round(distance[0]) );
        return Math.round(distance[0]);
    }
}
