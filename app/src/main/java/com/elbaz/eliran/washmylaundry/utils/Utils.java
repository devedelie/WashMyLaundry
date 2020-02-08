package com.elbaz.eliran.washmylaundry.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Eliran Elbaz on 08-Feb-20.
 */
public class Utils {

    public static Date getDate(){
        Date currentTime = Calendar.getInstance().getTime();
        return currentTime;
    }
}
