package com.ninja.nanny.Model;

import java.util.Date;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Wish {
    int id;
    String title;
    int totalAmount;
    int monthlyPayment;
    int savedAmount;
    String updatedAt;
    Date updatedDate;
    int flagActive; //0-Finished, 1-Active

    public Wish() {

    }

    public Wish(String title, int nTotalAmount, int monthlyPayment, int savedAmount, String updatedAt, int flagActive) {
        this.title = title;
        this.totalAmount = nTotalAmount;
        this.monthlyPayment = monthlyPayment;
        this.savedAmount = savedAmount;
        this.updatedAt = updatedAt;
        this.flagActive = flagActive;
    }

    public Wish(int id, String title, int totalAmount, int monthlyPayment, int savedAmount, String updatedAt, int flagActive) {
        this.id = id;
        this.title = title;
        this.totalAmount = totalAmount;
        this.monthlyPayment = monthlyPayment;
        this.savedAmount = savedAmount;
        this.updatedAt = updatedAt;
        this.flagActive = flagActive;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setMonthlyPayment(int monthlyPayment) {
        this.monthlyPayment = monthlyPayment;
    }

    public void setSavedAmount(int savedAmount) {
        this.savedAmount = savedAmount;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getFlagActive() {
        return flagActive;
    }

    public void setFlagActive(int flagActive) {
        this.flagActive = flagActive;
    }

    public int getMonthlyPayment() {
        return monthlyPayment;
    }

    public int getSavedAmount() {
        return savedAmount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}
