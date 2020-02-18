package com.elbaz.eliran.washmylaundry.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

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
}
