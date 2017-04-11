package com.moneynanny.nanny.Model;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Wish {
    private int _id;
    private String _title;
    private int _totalAmount;
    private int _monthlyPayment;
    private int _savedAmount;
    private long _timestampCreated;
    private int _lastSavingId; // id for last wishSaving item
    private int _flagActive; //0-Finished, 1-Active

    public Wish() {

    }

    public Wish(String title, int totalAmount, int monthlyPayment, int savedAmount, long timestampCreated, int lastSavingId, int flagActive) {
        _title = title;
        _totalAmount = totalAmount;
        _monthlyPayment = monthlyPayment;
        _savedAmount = savedAmount;
        _timestampCreated = timestampCreated;
        _lastSavingId = lastSavingId;
        _flagActive = flagActive;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setTitle(String title) {
        _title = title;
    }

    public void setTotalAmount(int totalAmount) {
        _totalAmount = totalAmount;
    }

    public void setMonthlyPayment(int monthlyPayment) {
        _monthlyPayment = monthlyPayment;
    }

    public void setSavedAmount(int savedAmount) {
        _savedAmount = savedAmount;
    }

    public void setTimestampCreated(long timestampCreated) {
        _timestampCreated = timestampCreated;
    }

    public void setLastSavingId(int lastSavingId) { _lastSavingId = lastSavingId; }

    public void setFlagActive(int flagActive) { _flagActive = flagActive;}

    public int getId() {
        return _id;
    }

    public String getTitle() {
        return _title;
    }

    public int getTotalAmount() {
        return _totalAmount;
    }

    public int getFlagActive() {
        return _flagActive;
    }

    public int getMonthlyPayment() {
        return _monthlyPayment;
    }

    public int getSavedAmount() {
        return _savedAmount;
    }

    public long getTimestampCreated() {
        return _timestampCreated;
    }

    public int getLastSavingId() { return _lastSavingId; }
}
