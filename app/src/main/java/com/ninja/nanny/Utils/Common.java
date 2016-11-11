package com.ninja.nanny.Utils;

import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.Model.WishSaving;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Common {

    static Common instance = null;

    public static Common getInstance() {
        if(instance == null){
            instance = new Common();
        }

        return instance;
    }

    public DatabaseHelper dbHelper;
    public List<Bank> listBanks;
    public List<Wish> listAllWishes;
    public List<Wish> listActiveWishes;
    public List<Wish> listFinishedWishes;
    public List<Payment> listAllPayments;
    public List<Payment> listCurrentPayments;
    public List<Sms> listSms;
    public int nMonthlyIncome;
    public int nMinimalDayAmount;
    public int nSalaryDate;
    public int nUsedAmount;
    public int nToleranceDays;
    public int nTolerancePercents;
    public Bank bankActive;

    public Transaction convertToTransactionFromSms(Sms sms) {
        Transaction transaction = new Transaction();

        transaction.setAccountName(bankActive.getAccountName());
        //***********To Implement With sms
        transaction.setIdentifier("identifier");
        transaction.setAmount(500);
        transaction.setMode(2);
        //************************
        transaction.setBankId(bankActive.getId());
        transaction.setSmsId(sms.getId());
        transaction.setTimestampCreated(sms.getTimestamp());

        return transaction;
    }

    public int sumOfPredefinedPaymentsForMonth() {
        int nAns = 0;

        for(int i = 0; i < listCurrentPayments.size(); i ++) {
            Payment paymentItem = listCurrentPayments.get(i);

            nAns += paymentItem.getAmount();
        }

        return nAns;
    }

    public int sumOfWishesForMonth() {
        int nAns = 0;

        Calendar c = Calendar.getInstance();
        int nDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int nMonth = c.get(Calendar.MONTH);
        int nYear = c.get(Calendar.YEAR);

        if(nDayOfMonth > nSalaryDate) {
            nMonth ++;
            if(nMonth == 12) {
                nYear ++;
                nMonth = 0;
            }
        }

        int nDateSaving = nYear + 100 + nMonth;

        for(int i = 0; i < listActiveWishes.size(); i ++) {
            Wish wishItem = listActiveWishes.get(i);

            int nLastSavingId = wishItem.getLastSavingId();

            WishSaving wishSavingItem = dbHelper.getWishSaving(nLastSavingId);

            if(wishSavingItem.getDateCreated() == nDateSaving) {
                nAns += wishSavingItem.getSavedAmount();
            }
        }

        return nAns;
    }

    public int monthlyLimit() {
        return nMonthlyIncome - sumOfPredefinedPaymentsForMonth() - sumOfWishesForMonth();
    }

    public int weeklyLimit() {
        Calendar c = Calendar.getInstance();
        int nTotalDaysOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        return monthlyLimit() / nTotalDaysOfMonth;
    }

    public int sumOfTransactionThisWeek() { // for B Group Transaction
        return 0;
    }

    public int sumOfTransactionThisMonth() { // for B Group Transaction
        return 0;
    }

    public int leftOnThisWeek() {
        return weeklyLimit() - sumOfTransactionThisWeek();
    }

    public int upcomingPayments() { // SUM(predefiend payments for next 7 days)
        int nAns = 0;

        Calendar c = Calendar.getInstance();
        int nTotalDaysOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int nDayCurrent = c.get(Calendar.DAY_OF_MONTH);

        for(int i = 0; i < listCurrentPayments.size(); i ++) {
            Payment paymentItem = listCurrentPayments.get(i);
            int nDayPayment = paymentItem.getDateOfMonth();

            if(nDayCurrent + 7 > nTotalDaysOfMonth) {
                if(nDayPayment > nDayCurrent){
                    nAns += paymentItem.getAmount();
                } else {
                    if(nDayPayment <= nDayCurrent + 7 - nTotalDaysOfMonth) {
                        nAns += paymentItem.getAmount();
                    }
                }
            } else {
                if(nDayPayment > nDayCurrent && nDayPayment <= nDayCurrent + 7) {
                    nAns += paymentItem.getAmount();
                }
            }
        }

        return nAns;
    }

    public int leftOnThisMonth() {
        return monthlyLimit() - sumOfTransactionThisMonth();
    }

    public int balanceOfActiveBank() {
        return 0;
    }

    public int daysLeftForThisWeek() {
        Calendar c = Calendar.getInstance();
        int nDayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return 7 - nDayOfWeek;
    }

    public int daysLeftForThisMonth() {
        Calendar c = Calendar.getInstance();
        int nDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int nTotalDaysOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        return nTotalDaysOfMonth - nDayOfMonth + 1;
    }

    public int totalWishes() { // for checking the advisor
        return sumOfWishesForMonth();
    }

    public int totalSavings() { // for checking the advisor
        int nAns = 0;

        for(int i = 0; i < listCurrentPayments.size(); i ++) {
            Payment paymentItem = listCurrentPayments.get(i);
            int nPaymentMode = paymentItem.getPaymentMode();

            if(nPaymentMode == 1 || nPaymentMode == 3) continue;

            nAns += paymentItem.getAmount();
        }

        return nAns;
    }

    public int totalBills() { // for checking the advisor
        return sumOfPredefinedPaymentsForMonth() - totalSavings();
    }

    public int checkAdvisorStatus(int nAskingAmount) {
        int nStatus = 0;

        if (leftOnThisWeek() - daysLeftForThisWeek() * nMinimalDayAmount >= nAskingAmount) {
            nStatus = 0;
        } else if (leftOnThisWeek() >= nAskingAmount){
            nStatus = 1;
        } else if (leftOnThisMonth() - daysLeftForThisMonth() * nMinimalDayAmount >= nAskingAmount) {
            nStatus = 2;
        } else if (leftOnThisMonth() >= nAskingAmount) {
            nStatus = 3;
        } else if(leftOnThisMonth() + totalWishes() >= nAskingAmount) {
            nStatus = 4;
        } else if(leftOnThisMonth() + totalWishes() + totalSavings() >= nAskingAmount) {
            nStatus = 5;
        } else if(leftOnThisMonth() + totalWishes() + totalSavings() + totalBills() >= nAskingAmount) {
            nStatus = 6;
        } else if(balanceOfActiveBank() >= nAskingAmount) {
            nStatus = 7;
        } else {
            nStatus = 8;
        }

        return nStatus;
    }
}
