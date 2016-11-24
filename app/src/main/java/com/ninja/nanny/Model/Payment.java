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

    public int getPaidStatus() { // 0-unpaid, 1-paid
        if(_lastPaidId == -1) return 0;
        if(_lastPaidId != -1 && (_paymentMode == 1 || _paymentMode == 3)) {
            return 1;  // for case of single payment
        }

        Paid paid = Common.getInstance().dbHelper.getPaid(_lastPaidId);
        long timestampePaid = paid.getTimestampCreated();
        Calendar c = Calendar.getInstance();
        long timestampHigh = c.getTimeInMillis();;

        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay < Common.getInstance().nSalaryDate) {
            c.add(Calendar.MONTH, -1);
        }

        c.set(Calendar.DAY_OF_MONTH, Common.getInstance().nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long timestampLow = c.getTimeInMillis();

        if(timestampLow < timestampePaid && timestampePaid < timestampHigh){
            return 1;
        }

        return 0;
    }

    public long getRealTimeStampForSingle() { // for single payment, return the timestamp when real payment will be happened.
        if(_paymentMode == 0 || _paymentMode == 2) return 0;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(_timestampCreated);

        int nDayCreated = c.get(Calendar.DAY_OF_MONTH);

        if(nDayCreated > _dateOfMonth) {
            c.add(Calendar.MONTH, 1);
        }

        c.set(Calendar.DAY_OF_MONTH, _dateOfMonth);

        return c.getTimeInMillis();
    }
}
