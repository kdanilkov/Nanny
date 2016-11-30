package com.ninja.nanny.Model;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Transaction {
    int _id;
    String _accountName;
    String _identifier;
    int _bankId;
    String _text;
    int _amount;
    int _mode; //0-balance, 1-income, 2-spending
    int _paidId;
    long _timestmapCreated;

    public Transaction() {

    }

    public Transaction(String accountName, String identifier, int bankId, String text, int amount, int mode, int paidId, int timestampCreated) {
        _accountName = accountName;
        _identifier = identifier;
        _bankId = bankId;
        _text = text;
        _amount = amount;
        _mode = mode;
        _paidId = paidId;
        _timestmapCreated = timestampCreated;
    }


    public void setId(int id) {
        _id = id;
    }

    public void setAccountName(String accountName) {
        _accountName = accountName;
    }

    public void setIdentifier(String identifier) {
        _identifier = identifier;
    }

    public void setBankId(int bankId) {
        _bankId = bankId;
    }

    public void setText(String text) {
        _text = text;
    }

    public void setAmount(int amount) {
        _amount = amount;
    }

    public void setMode(int mode) {
        _mode = mode;
    }

    public void setPaidId(int paidId) {
        _paidId = paidId;
    }

    public void setTimestampCreated(long timestampCreated) {
        _timestmapCreated = timestampCreated;
    }

    public int getId() {
        return _id;
    }

    public String getAccountName() {
        return _accountName;
    }

    public String getIdentifier() {
        return _identifier;
    }

    public int getBankId() {
        return _bankId;
    }

    public String getText() {
        return _text;
    }

    public int getAmount() {
        return _amount;
    }

    public int getMode() {
        return _mode;
    }

    public int getPaidId() {
        return _paidId;
    }

    public long getTimestampCreated() {
        return _timestmapCreated;
    }
}
