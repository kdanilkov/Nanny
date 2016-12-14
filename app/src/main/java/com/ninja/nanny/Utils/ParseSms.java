package com.ninja.nanny.Utils;

import android.util.Log;

import com.ninja.nanny.Model.Transaction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 10/19/2016.
 */

public class ParseSms {

    static ParseSms instance = null;

    public static ParseSms getInstance() {
        if(instance == null){
            instance = new ParseSms();
        }

        return instance;
    }

    public Transaction getSmsByTemplate0(String strMsg) {
        String strPattern = "Purchase of AED (\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?) with Debit Card ending (\\d{4}) at (.*). Avl (Balance|Bal) is AED (\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?)";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strMsg);

        if(m.find()) {
            String strAmountChange = m.group(1);
            String strCardNumber = m.group(4);
            String strIdentifier = m.group(5);
            String strAmountBalance = m.group(7);

            Transaction transaction = new Transaction();
            transaction.setAmountChange(getIntValueFrom(strAmountChange));
            transaction.setIdentifier(strIdentifier);
            transaction.setAmountBalance(getIntValueFrom(strAmountBalance));
            transaction.setMode(2);

            Log.e(Constant.TAG_CURRENT, "success on the parse sms with template 0");

            return transaction;
        }

        Log.e(Constant.TAG_CURRENT, "failed on the parse sms with template 0");
        return null;
    }

    public Transaction getSmsByTemplate1(String strMsg) {
        String strPattern = "Purchase of AED (\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?) with Debit Card ending (\\d{4}) at (.*)";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strMsg);

        if(m.find()) {
            String strAmountChange = m.group(1);
            String strCardNumber = m.group(4);
            String strIdentifier = m.group(5);

            Transaction transaction = new Transaction();
            transaction.setAmountChange(getIntValueFrom(strAmountChange));
            transaction.setIdentifier(strIdentifier);
            transaction.setAmountBalance(-1);
            transaction.setMode(2);

            Log.e(Constant.TAG_CURRENT, "success on the parse sms with template 1");

            return transaction;
        }

        Log.e(Constant.TAG_CURRENT, "failed on the parse sms with template 1");

        return null;
    }

    public Transaction getSmsByTemplate2(String strMsg) {
        String strPattern = "Transaction AED(\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?) credit card (\\d{4}) (.*). Bal AED(\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?)";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strMsg);

        if(m.find()) {
            String strAmountChange = m.group(1);
            String strCardNumber = m.group(4);
            String strIdentifier = m.group(5);
            String strAmountBalance = m.group(6);

            Transaction transaction = new Transaction();
            transaction.setAmountChange(getIntValueFrom(strAmountChange));
            transaction.setIdentifier(strIdentifier);
            transaction.setAmountBalance(getIntValueFrom(strAmountBalance));
            transaction.setMode(2);

            Log.e(Constant.TAG_CURRENT, "success on the parse sms with template 2");

            return transaction;
        }

        Log.e(Constant.TAG_CURRENT, "failed on the parse sms with template 2");

        return null;
    }

    public Transaction getSmsByTemplate3(String strMsg) {
        String strPattern = "Cash Withdrawal of AED (\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?) with Debit Card ending (\\d{4})";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strMsg);

        if(m.find()) {
            String strAmountChange = m.group(1);
            String strCardNumber = m.group(4);

            Transaction transaction = new Transaction();
            transaction.setAmountChange(getIntValueFrom(strAmountChange));
            transaction.setIdentifier("");
            transaction.setAmountBalance(-1);
            transaction.setMode(2);

            Log.e(Constant.TAG_CURRENT, "success on the parse sms with template 3");

            return transaction;
        }

        Log.e(Constant.TAG_CURRENT, "failed on the parse sms with template 3");

        return null;
    }

    public Transaction getSmsByTemplate4(String strMsg) {
        String strPattern = "AED (\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?) has been received as TELEGRAHPHIC TRANSFER";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strMsg);

        if(m.find()) {
            String strAmountChange = m.group(1);

            Transaction transaction = new Transaction();
            transaction.setAmountChange(getIntValueFrom(strAmountChange));
            transaction.setIdentifier("");
            transaction.setAmountBalance(-1);
            transaction.setMode(1);

            Log.e(Constant.TAG_CURRENT, "success on the parse sms with template 4");

            return transaction;

        }

        Log.e(Constant.TAG_CURRENT, "failed on the parse sms with template 4");

        return null;
    }

    public Transaction getSmsByTemplate5(String strMsg) {
        String strPattern = "Your salary AED(\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?) has been credited to your account (.*). The available balance AED(\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?)";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strMsg);

        if(m.find()) {
            String strAmountChange = m.group(1);
            String strAmountBalance = m.group(5);

            Transaction transaction = new Transaction();
            transaction.setAmountChange(getIntValueFrom(strAmountChange));
            transaction.setIdentifier("");
            transaction.setAmountBalance(getIntValueFrom(strAmountBalance));
            transaction.setMode(1);

            Log.e(Constant.TAG_CURRENT, "success on the parse sms with template 2");

            return transaction;
        }

        Log.e(Constant.TAG_CURRENT, "failed on the parse sms with template 2");

        return null;
    }

    public Transaction getSmsByTemplate6(String strMsg) {
        String strPattern = "Loan installment AED (\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?) from";

        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(strMsg);

        if(m.find()) {
            String strAmountChange = m.group(1);

            Transaction transaction = new Transaction();
            transaction.setAmountChange(getIntValueFrom(strAmountChange));
            transaction.setIdentifier("");
            transaction.setAmountBalance(-1);
            transaction.setMode(1);

            Log.e(Constant.TAG_CURRENT, "success on the parse sms with template 2");

            return transaction;
        }

        Log.e(Constant.TAG_CURRENT, "failed on the parse sms with template 2");

        return null;
    }

    int getIntValueFrom(String strVal) {
        strVal = strVal.replace(",", "");
        double d = 0;

        d = Double.parseDouble(strVal);

        return (int)d;
    }

}
