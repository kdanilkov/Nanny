package com.moneynanny.nanny.Utils;

import android.content.Context;
import android.util.Log;

import com.moneynanny.nanny.Helper.TimestampHelper;
import com.moneynanny.nanny.Model.Bank;
import com.moneynanny.nanny.Model.Sms;
import com.moneynanny.nanny.Model.Transaction;
import com.moneynanny.nanny.R;


/**
 * Created by nikolay on 03.04.17.
 */

public class SmsReader {
    public static int readSalaryDay(Context context, Bank activeBank) {
        int day = Common.getInstance().nSalaryDate;
        try {
            for (int i = Common.getInstance().listSms.size()-1; i >= 0; i--) {
                Sms sms = Common.getInstance().listSms.get(i);
                // ignore Sms from future
                if (sms.getTimestamp() > Common.getInstance().getTimestamp())
                    continue;

                Transaction trans = Common.getInstance().convertSmsToTransaction(sms, activeBank.getIdxKind());
                if (null == trans)
                    continue;
                // check fpr income operations with big amount of money
                if (trans.getMode() == 1
                        && trans.getAmountChange() > context.getResources().getInteger(R.integer.salary_limit)) {
                    day = TimestampHelper.getDayOfMonthFromTimestamp(trans.getTimestampCreated());
                    break;
                }
            }
        } catch (Exception e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
        return day;
    }
}
