package com.ninja.nanny.Model;

import android.util.Log;

import com.ninja.nanny.Helper.TimestampHelper;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import org.json.JSONObject;

import hirondelle.date4j.DateTime;

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
    long _timestampCreated;

    public Transaction() {
    }

    public Transaction(String accountName, String identifier, int bankId, String text, int amountChange, int amountBalance, int mode, int paidId, long timestampCreated) {
        _accountName = accountName;
        _identifier = identifier;
        _bankId = bankId;
        _text = text;
        _amountChange = amountChange;
        _amountBalance = amountBalance;
        _mode = mode;
        _paidId = paidId;
        _timestampCreated = timestampCreated;
    }

    public Transaction (JSONObject json) {
        try {
            _accountName = json.getJSONObject(Constant.TRAN_ACCOUNT)
                    .getString(Constant.TRAN_ACC_NAME);
            _identifier = json.getString(Constant.TRAN_ID);
            //int bankId = json.getInt(Constant.TRAN)
            _text = json.getJSONObject(Constant.TRAN_DETAILS)
                    .getString(Constant.TRAN_DETAILS_SMS);
            _amountChange = (int) Math.round(json.getJSONObject(Constant.TRAN_DETAILS)
                    .getJSONObject(Constant.TRAN_DETAILS_VALUE)
                    .getDouble(Constant.TRAN_DETAILS_VALUE_AMOUNT));
            _amountBalance = (int) Math.round(json.getJSONObject(Constant.TRAN_DETAILS)
                    .getJSONObject(Constant.TRAN_DETAILS_BALANCE)
                    .getDouble(Constant.TRAN_DETAILS_BALANCE_AMOUNT));

            // mode defines whether it is a spending or income. Value itself should always be > 0
            _mode = _amountChange > 0 ? 1 : 2;
            _amountChange = Math.abs(_amountChange);
            _bankId=2;
            String completed_time = json.getJSONObject(Constant.TRAN_DETAILS)
                    .getString(Constant.TRAN_DETAILS_COMPLETED);
            _timestampCreated = TimestampHelper.getTimeStampFromString(completed_time);
        } catch (Exception e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
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
        _timestampCreated = timestampCreated;
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
        return _timestampCreated;
    }
}
