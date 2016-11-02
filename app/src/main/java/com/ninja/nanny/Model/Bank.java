package com.ninja.nanny.Model;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Bank {
    int id;
    String accountName;
    String bank;
    String accountType;
    int balance;
    int notificationMode;// 0-SMS, 1-Email
    int flagActive; //0-disabled, 1-enabled

    public Bank() {

    }

    public Bank(String accountName, String bank, String accountType, int balance, int notificationMode, int flagActive) {
        this.accountName = accountName;
        this.bank = bank;
        this.accountType = accountType;
        this.balance = balance;
        this.notificationMode = notificationMode;
        this.flagActive = flagActive;
    }

    public Bank(int id, String accountName, String bank, String accountType, int balance, int notificationMode, int flagActive) {
        this.id = id;
        this.accountName = accountName;
        this.bank = bank;
        this.accountType = accountType;
        this.balance = balance;
        this.notificationMode = notificationMode;
        this.flagActive = flagActive;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public void setNotificationMode(int notificationMode) {
        this.notificationMode = notificationMode;
    }

    public void setFlagActive(int flagActive) {
        this.flagActive = flagActive;
    }

    public int getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getBank() {
        return bank;
    }

    public String getAccountType() {
        return accountType;
    }

    public int getBalance() {
        return balance;
    }

    public int getNotificationMode() {
        return notificationMode;
    }

    public int getFlagActive() {
        return flagActive;
    }
}
