package com.ninja.nanny.Utils;

import android.content.Context;

import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.R;

import java.io.InputStream;

/**
 * Created by nikolay on 03.04.17.
 */

public class Tester {
    private static final Bank endb = new Bank("Account 1", 0, 0, 1, 0);;
    private static final Bank cdb = new Bank("Account 1", 1, 0, 1, 0);;
    private static final Bank adcb = new Bank("Account 1", 2, 0, 1, 0);;
    private static final Bank rak = new Bank("Account 1", 3, 0, 1, 0);;


    public static void fillSmsEndb(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.demobank_endb);
        SmsTransactionFiller.fillSmsFromDemoJSON(endb, is);
    }

    public static void fillSmsCdb(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.demobank_cdb);
        SmsTransactionFiller.fillSmsFromDemoJSON(cdb, is);
    }

    public static void fillSmsAdcb(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.demobank_adcb);
        SmsTransactionFiller.fillSmsFromDemoJSON(adcb, is);
    }

    public static void fillSmsRak(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.demobank_rankbank);
        SmsTransactionFiller.fillSmsFromDemoJSON(rak, is);
    }
}
