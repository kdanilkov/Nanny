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
    int _amountChange;
    int _amountBalance; // -1: there is no balance value
    int _mode; //1-income, 2-spending
    int _paidId;
    long _timestmapCreated;

    public Transaction() {

    }

    public Transaction(String accountName, String identifier, int bankId, String text, int amountChange, int amountBalance, int mode, int paidId, int timestampCreated) {
        _accountName = accountName;
        _identifier = identifier;
        _bankId = bankId;
        _text = text;
        _amountChange = amountChange;
        _amountBalance = amountBalance;
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

    public void setAmountChange(int amountChange) {
        _amountChange = amountChange;
    }

    public void setAmountBalance(int amountBalance) {
        _amountBalance = amountBalance;
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

    public int getAmountChange() {
        return _amountChange;
    }

    public int getAmountBalance() {
        return _amountBalance;
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
