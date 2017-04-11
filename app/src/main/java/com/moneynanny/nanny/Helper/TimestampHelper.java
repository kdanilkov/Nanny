package com.moneynanny.nanny.Helper;

import android.util.Log;

import com.moneynanny.nanny.Utils.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by nikolay on 17.03.17.
 */

public class TimestampHelper {
    public static int getDayOfMonthFromTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static long getTimeStampFromString(String completed_time) {
        long timestamp = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = format.parse(completed_time);
            timestamp = date.getTime();
        } catch (ParseException e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
        return timestamp;
    }
}
