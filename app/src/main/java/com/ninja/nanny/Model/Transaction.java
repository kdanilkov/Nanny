package com.ninja.nanny.Model;

import com.ninja.nanny.Utils.Common;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Transaction {
    int _id;
    String _accountName;
    String _identifier;
    int _bankId;
    int _smsId;
    int _amount;
    int _mode; //0-balance, 1-income, 2-spending
    long _timestmapCreated;

    public Transaction() {

    }

    public Transaction(String accountName, String identifier, int bankId, int smsId, int amount, int mode, int timestampCreated) {
        _accountName = accountName;
        _identifier = identifier;
        _bankId = bankId;
        _smsId = smsId;
        _amount = amount;
        _mode = mode;
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

    public void setSmsId(int smsId) {
        _smsId = smsId;
    }

    public void setAmount(int amount) {
        _amount = amount;
    }

    public void setMode(int mode) {
        _mode = mode;
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

    public int getSmsId() {
        return _smsId;
    }

    public int getAmount() {
        return _amount;
    }

    public int getMode() {
        return _mode;
    }

    public long getTimestampCreated() {
        return _timestmapCreated;
    }

    public String getText() {
        Sms sms = Common.getInstance().dbHelper.getSms(_smsId);
        return sms.getText();
    }
}
