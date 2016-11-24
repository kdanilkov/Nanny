package com.ninja.nanny.Model;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Paid {
    private int _id;
    private int _paymentId;
    private int _transactionId; // -1: cash
    private long _timestampCreated;

    public Paid() {

    }

    public Paid(int paymentId, int transactionId, long timestampCreated) {
        _paymentId = paymentId;
        _transactionId = transactionId;
        _timestampCreated = timestampCreated;
    }

    public int getId() {
        return _id;
    }

    public int getPaymentId() {
        return _paymentId;
    }

    public int getTransactionId() {
        return _transactionId;
    }

    public long getTimestampCreated() {
        return _timestampCreated;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setPaymentId(int paymentId) {
        _paymentId = paymentId;
    }

    public void setTransactionId(int transactionId) {
        _transactionId = transactionId;
    }

    public void setTimestampCreated(long timestampCreated) {
        _timestampCreated = timestampCreated;
    }
}
