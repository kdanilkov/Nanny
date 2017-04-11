package com.moneynanny.nanny.Utils;

import android.content.Context;
import android.util.Log;

import com.moneynanny.nanny.Model.Bank;
import com.moneynanny.nanny.Model.Transaction;
import com.moneynanny.nanny.R;

import org.json.JSONArray;

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

    public static int calculateSumFromJson(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.demobank_rankbank);
        JSONArray transactions = SmsTransactionFiller.getTransactionsJSONArray(is);

        TransactionLogger logger = new TransactionLogger();
        int sum = 0;
        for (int i = transactions.length() - 1; i >= 0; i--) {
            try {
                Transaction trans = new Transaction(transactions.getJSONObject(i));
                if (trans.getMode() == 1) {
                    sum += trans.getAmountChange();
                    logger.addTransaction(trans.getAmountChange());
                } else {
                    sum -= trans.getAmountChange();
                    logger.addTransaction(-trans.getAmountChange());
                }
            } catch (Exception e) {
                Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
            }
        }
        int i = 5;
        sum += 5;
        sum -= i;
        return sum;
    }
}
