package com.elbaz.eliran.washmylaundry.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class Utils {

    public static Date getDate(){
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }

    public static boolean isInternetAvailable(final Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG, "isInternetAvailable: " + activeNetworkInfo);
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
