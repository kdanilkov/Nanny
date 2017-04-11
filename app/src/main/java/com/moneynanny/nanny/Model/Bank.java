package com.moneynanny.nanny.Model;

import android.util.Log;

import com.moneynanny.nanny.Utils.Common;
import com.moneynanny.nanny.Utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Bank {
    int _id;
    String _accountName;
    int _idxKind;
    int _balance;
    int _flagActive; //0-disabled, 1-enabled
    long _timestamp;

    public Bank() {

    }

    public Bank(String accountName, int idxKind, int balance, int flagActive, long timestamp) {
        _accountName = accountName;
        _idxKind = idxKind;
        _balance = balance;
        _flagActive = flagActive;
        _timestamp = timestamp;
    }

    public void setId(int id) {
        _id = id;
    }

    public void setAccountName(String accountName) {
        _accountName = accountName;
    }

    public void setIdxKind(int idxKind) {
        _idxKind = idxKind;
    }

    public void setBalance(int balance) {
        _balance = balance;
    }

    public void setFlagActive(int flagActive) {
        _flagActive = flagActive;
    }

    public void setTimestamp(long timestamp) {
        _timestamp = timestamp;
    }

    public int getId() {
        return _id;
    }

    public String getAccountName() {
        return _accountName;
    }

    public int getIdxKind() {
        return _idxKind;
    }

    public int getBalance() {
        return _balance;
    }

    public int getFlagActive() {
        return _flagActive;
    }

    public long getTimestamp() {
        return _timestamp;
    }

    public String getBankName() {
        JSONObject jsonObjBank = null;
        try {
            jsonObjBank = Common.getInstance().jsonArrayBankInfo.getJSONObject(_idxKind);
            String strAccountName = jsonObjBank.getString(Constant.JSON_NAME);

            return strAccountName;
        } catch (JSONException e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }

        return "";
    }

    public String getBankType() {
        JSONObject jsonObjBank = null;
        try {
            jsonObjBank = Common.getInstance().jsonArrayBankInfo.getJSONObject(_idxKind);
            String strAccountType = jsonObjBank.getString(Constant.JSON_TYPE);

            return strAccountType;
        } catch (JSONException e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }

        return "";
    }

    public String getBankAddress() {
        JSONObject jsonObjBank = null;
        try {
            jsonObjBank = Common.getInstance().jsonArrayBankInfo.getJSONObject(_idxKind);
            String address = jsonObjBank.getString(Constant.JSON_ADDRESS);

            return address;
        } catch (JSONException e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }

        return "";
    }

}
