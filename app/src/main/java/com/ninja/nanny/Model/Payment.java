package com.ninja.nanny.Model;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Payment {
    int _id;
    String _title;
    String _detail;
    int _amount;
    int _dateOfMonth;
    int _paymentMode; //0-saving recurrent, 1-saving single, 2-bill payment recurrent, 3-bill payment single
    int _paidStatus; //0-Unpaid, 1-Paid
    long _timestampCreated;

    public Payment() {

    }

    public Payment(String title, String detail, int amount, int dateOfMonth, int paymentMode, int paidStatus, long timestampCreated) {
        _title = title;
        _detail = detail;
        _amount = amount;
        _dateOfMonth = dateOfMonth;
        _paymentMode = paymentMode;
        _paidStatus = paidStatus;
        _timestampCreated = timestampCreated;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public void setDetail(String detail) {_detail = detail; }

    public void setAmount(int amount) {
        _amount = amount;
    }

    public void setDateOfMonth(int dateOfMonth) {
        _dateOfMonth = dateOfMonth;
    }

    public void setPaymentMode(int paymentMode) {
        _paymentMode = paymentMode;
    }

    public void setPaidStatus(int paidStatus) {
        _paidStatus = paidStatus;
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

    public String getDetail() { return _detail; }

    public int getAmount() {
        return _amount;
    }

    public int getDateOfMonth() {
        return _dateOfMonth;
    }

    public int getPaymentMode() {
        return _paymentMode;
    }

    public int getPaidStatus() {
        return _paidStatus;
    }

    public long getTimestampCreated() {
        return _timestampCreated;
    }
}
