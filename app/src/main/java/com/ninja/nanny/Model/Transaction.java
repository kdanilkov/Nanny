package com.ninja.nanny.Model;

import java.util.Date;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Transaction {
    int id;
    String accountName;
    String usage;
    int amount;
    String createdAt;
    Date createdDate;

    public Transaction() {

    }

    public Transaction(String accountName, String usage, int amount, String createdAt) {
        this.accountName = accountName;
        this.usage = usage;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public Transaction(int id, String accountName, String usage, int amount, String createdAt) {
        this.id = id;
        this.accountName = accountName;
        this.usage = usage;
        this.amount = amount;
        this.createdAt = createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getUsage() {
        return usage;
    }

    public int getAmount() {
        return amount;
    }
}
