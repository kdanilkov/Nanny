package com.moneynanny.nanny.Utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.moneynanny.nanny.Model.Bank;
import com.moneynanny.nanny.Model.Sms;
import com.moneynanny.nanny.Model.Transaction;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by nikolay on 31.03.17.
 */

public class SmsTransactionFiller {
    public static void saveNewSms(String strTitle, String strText, long timestamp) {
        Sms sms = new Sms(strTitle, strText, timestamp);
        int nId = Common.getInstance().dbHelper.createSMS(sms);

        sms.setId(nId);

        Common.getInstance().listSms.add(0, sms);
    }

    public static void fillSmsFromDemoJSON(Bank bank, InputStream is) {
        JSONArray transactions = getTransactionsJSONArray(is);

        for (int i = 0; i < transactions.length(); i++) {
            try {
                Transaction trans = new Transaction(transactions.getJSONObject(i));
                saveNewSms(bank.getBankAddress(), trans.getText(), trans.getTimestampCreated());
            } catch (Exception e) {
                Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
            }
        }
    }

    public static void fillTestTransactions(InputStream is) {
        JSONArray transactions = getTransactionsJSONArray(is);

        for (int i = 0; i < transactions.length(); i++) {
            try {
                Transaction trans = new Transaction(transactions.getJSONObject(i));
                Common.getInstance().listAllTransactions.add(trans);
            } catch (Exception e) {
                Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
            }
        }
    }

    @NonNull
    static JSONArray getTransactionsJSONArray(InputStream is) {
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (IOException e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
            }
        }

        String jsonString = writer.toString();
        JSONArray transactions = new JSONArray();

        try {
            transactions = new JSONArray(jsonString);
        } catch (JSONException e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
        return transactions;
    }

}
