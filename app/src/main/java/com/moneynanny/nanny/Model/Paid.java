package com.moneynanny.nanny.Model;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Paid {
    private int _id;
    private int _paymentId;
    private int _transactionId; // 0: cash
    private int _prevPaidId; // previous last paid id for recurrent payment, -1: null
    private long _timestampPayment; // at that moment, payment timestamp
    private long _timestampCreated;

    public Paid() {

    }

    public Paid(int paymentId, int transactionId, int prevPaidId, long timestampPayment, long timestampCreated) {
        _paymentId = paymentId;
        _transactionId = transactionId;
        _prevPaidId = prevPaidId;
        _timestampPayment = timestampPayment;
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

    public int getPrevPaidId() {
        return _prevPaidId;
    }

    public long getTimestampPayment() {
        return _timestampPayment;
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

    public void setPrevPaidId(int prevPaidId) {
        _prevPaidId = prevPaidId;
    }

    public void setTimestampPayment(long timestampPayment) {
        _timestampPayment = timestampPayment;
    }

    public void setTimestampCreated(long timestampCreated) {
        _timestampCreated = timestampCreated;
    }
}
