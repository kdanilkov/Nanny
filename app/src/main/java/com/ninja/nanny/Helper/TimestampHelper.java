package com.ninja.nanny.Helper;

import java.util.Calendar;

/**
 * Created by nikolay on 17.03.17.
 */

public class TimestampHelper {
    public static int getDayOfMonthFromTimestamp(long timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timestamp);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
}
