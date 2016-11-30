package com.ninja.nanny.Model;

import com.ninja.nanny.Utils.Common;

import java.util.Calendar;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Payment {
    int _id;
    String _title;
    String _identifier;
    int _amount;
    int _dateOfMonth;
    int _paymentMode; //0-saving recurrent, 1-saving single, 2-bill payment recurrent, 3-bill payment single
    int _lastPaidId; //-1 - none,
    long _timestampCreated;

    public Payment() {

    }

    public Payment(String title, String identifier, int amount, int dateOfMonth, int paymentMode, int lastPaidId, long timestampCreated) {
        _title = title;
        _identifier = identifier;
        _amount = amount;
        _dateOfMonth = dateOfMonth;
        _paymentMode = paymentMode;
        _lastPaidId = lastPaidId;
        _timestampCreated = timestampCreated;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public void setIdentifier(String identifier) {_identifier = identifier; }

    public void setAmount(int amount) {
        _amount = amount;
    }

    public void setDateOfMonth(int dateOfMonth) {
        _dateOfMonth = dateOfMonth;
    }

    public void setPaymentMode(int paymentMode) {
        _paymentMode = paymentMode;
    }

    public void setLastPaidId(int lastPaidId) {
        _lastPaidId = lastPaidId;
    }

    public void setTimestampCreated(long timestampCreated) {
        _timestampCreated = timestampCreated;
    }

    public int getId() {
        return _id;
    }

    public String getTitle() {
        return _title;
    }

    public String getIdentifier() { return _identifier; }

    public int getAmount() {
        return _amount;
    }

    public int getDateOfMonth() {
        return _dateOfMonth;
    }

    public int getPaymentMode() {
        return _paymentMode;
    }

    public int getLastPaidId() {
        return _lastPaidId;
    }

    public long getTimestampCreated() {
        return _timestampCreated;
    }

    public int getPaidStatus() { // 0-unpaid, 1-paid for current period
        if(_lastPaidId == -1) return 0;
        if(_lastPaidId != -1 && (_paymentMode == 1 || _paymentMode == 3)) {
            return 1;  // for case of single payment
        }

        Calendar c = Calendar.getInstance();

        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay < Common.getInstance().nSalaryDate) {
            c.add(Calendar.MONTH , -1);
        }

        c.set(Calendar.DAY_OF_MONTH, Common.getInstance().nSalaryDate);
        Common.getInstance().setInitTime(c);

        long nLow = Common.getInstance().getTimestampCurrentPeriodStart();
        long nHigh = Common.getInstance().getTimestampCurrentPeriodEnd();

        Paid paid = Common.getInstance().dbHelper.getPaid(_lastPaidId);

        long timestampPayment = paid.getTimestampPayment();

        if(timestampPayment >= nLow && timestampPayment < nHigh) {
            return 1;
        }

        int nPrevPaidId = paid.getPrevPaidId();

        if(nPrevPaidId == -1) {
            return 0;
        }

        Paid paidPrev = Common.getInstance().dbHelper.getPaid(nPrevPaidId);

        long timestampPaymentPrev = paidPrev.getTimestampPayment();

        if(timestampPaymentPrev >= nLow && timestampPaymentPrev < nHigh) {
            return 1;
        }

        return 0;
    }


    public long getRealTimeStamp() { // at that moment, timestamp of payment date.

        Calendar c = Calendar.getInstance();
        int nDayCurrent = c.get(Calendar.DAY_OF_MONTH);

        if(_paymentMode == 1 || _paymentMode == 3 || _lastPaidId == -1) { // for single payment and new recurrent payment, return timestamp for real payment, or next payment
            c.setTimeInMillis(_timestampCreated);

            int nDayCreated = c.get(Calendar.DAY_OF_MONTH);

            if(nDayCreated > _dateOfMonth) {
                c.add(Calendar.MONTH, 1);
            }

            c.set(Calendar.DAY_OF_MONTH, _dateOfMonth);
            Common.getInstance().setInitTime(c);

            return c.getTimeInMillis();
        }

        //these recurrent payment has been paid before, then return timestamp for next payment.

        c.set(Calendar.DAY_OF_MONTH, _dateOfMonth);
        Common.getInstance().setInitTime(c);

        c.add(Calendar.MONTH, -1);

        Paid paidLast = Common.getInstance().dbHelper.getPaid(_lastPaidId);
        long timestampPayment = paidLast.getTimestampPayment();

        while(c.getTimeInMillis() <= timestampPayment) {
            c.add(Calendar.MONTH, 1);
        }

        return c.getTimeInMillis();
    }
}
