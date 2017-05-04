package com.moneynanny.nanny.Utils;

import android.content.Context;
import android.util.Log;

import com.moneynanny.nanny.Model.Bank;
import com.moneynanny.nanny.Model.Transaction;
import com.moneynanny.nanny.R;

import org.json.JSONArray;
import org.json.JSONObject;

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

    public static void testRegex() {

        String strSms = "25MAR2017 Credit Telegraphic Transfer to Amr-HSBC AED 20,000.00+ Your available balance is AED 20,667.64";
        String strPattern = "(.*) Credit (.*) AED (\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?)\\+ Your available balance is AED ((?!0+\\.00)(?=.{1,9}(\\.|$))(?!0(?!\\.))\\d{1,3}(,\\d{3})*(\\.\\d+)?)";
        Transaction trans = ParseSms.getInstance().getSmsByTemplate(strSms,strPattern);

        if(trans == null) {
            Log.e(Constant.TAG_CURRENT, "parsing failed");
        } else {
            Log.e(Constant.TAG_CURRENT, "parsing success");
        }
    }
    private static void setBank(int mIndex)
    {
        Bank bank = new Bank();
        try {
            JSONObject bankObj = Common.getInstance().jsonArrayBankInfo.getJSONObject(mIndex);
            bank.setAccountName(bankObj.getString(Constant.JSON_NAME));
            bank.setIdxKind(mIndex);
            bank.setFlagActive(1);
            bank.setTimestamp(0);
            Common.getInstance().addOrUpdateBank(bank);
        }
        catch (Exception ex)
        {
            Log.e(Constant.TAG_CURRENT, ex.getMessage());
        }
    }

}
