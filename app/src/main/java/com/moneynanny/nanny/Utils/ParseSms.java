package com.moneynanny.nanny.Utils;

import android.util.Log;

import com.moneynanny.nanny.Model.Transaction;

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


    public Transaction getSmsByTemplate(String strMsg,String strPattern) {
       // String strPattern = "Loan installment AED (\\d*\\.?\\d+|\\d{1,3}(,\\d{3})*(\\.\\d+)?) from";

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
