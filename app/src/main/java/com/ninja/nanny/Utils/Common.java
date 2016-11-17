package com.ninja.nanny.Utils;

import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.Model.WishSaving;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
    public List<Transaction> listTransactions;
    public int nMonthlyIncome;
    public int nMinimalDayAmount;
    public int nSalaryDate;
    public int nUsedAmount;
    public int nToleranceDays;
    public int nTolerancePercents;
    public Bank bankActive;
    public JSONArray jsonArrayBankInfo;

    public long getTimestamp() {
        Date date = new Date();
        return date.getTime();
    }

    public boolean isActiveBankExist() {
        boolean isExist = false;

        for(int i = 0; i < Common.getInstance().listBanks.size(); i ++) {
            Bank bank = Common.getInstance().listBanks.get(i);

            if(bank.getFlagActive() == 1) {
                isExist = true;
                bankActive = bank;
                break;
            }
        }

        return isExist;
    }

    public void getTransaction() {
        if(!isActiveBankExist()) {
            return;
        }

        listTransactions = new ArrayList<Transaction>();

        for(int i = 0; i < listSms.size(); i ++) {
            Sms sms = Common.getInstance().listSms.get(i);
            Transaction transaction = Common.getInstance().convertToTransactionFromSms(sms);

            if(transaction == null) continue;

            Common.getInstance().listTransactions.add(transaction);
        }

        Collections.sort(Common.getInstance().listTransactions, new TransactionComparator());

        if(listTransactions.size() > 0) {
            calculatteBalance();
        }
    }

    public void calculatteBalance() {
        int nVal = bankActive.getBalance();

        for(int i = listTransactions.size() - 1; i >= 0 ; i --) {
            Transaction trans = listTransactions.get(i);

            if(trans.getMode() == 0) nVal = trans.getAmount();
            if(trans.getMode() == 1) nVal += trans.getAmount();
            if(trans.getMode() == 2) nVal -= trans.getAmount();
        }

        bankActive.setBalance(nVal);

        dbHelper.updateBank(bankActive);
    }

    public Transaction convertToTransactionFromSms(Sms sms) {
        Transaction transaction = new Transaction();

        try {
            JSONObject jsonObjBank = jsonArrayBankInfo.getJSONObject(bankActive.getIdxKind());
            String strAccountName = jsonObjBank.getString(Constant.JSON_NAME);

            transaction.setAccountName(strAccountName);
            transaction.setBankId(bankActive.getIdxKind());
            transaction.setSmsId(sms.getId());
            transaction.setTimestampCreated(sms.getTimestamp());

            JSONObject jsonObjTransaction = jsonObjBank.getJSONObject(Constant.JSON_TRANSACTION);

            JSONArray jsonArraySpending = jsonObjTransaction.getJSONArray(Constant.JSON_SPENDING);
            JSONArray jsonArrayIncome = jsonObjTransaction.getJSONArray(Constant.JSON_INCOME);

            String strSmsText = sms.getText();

            boolean isGetResult = false;

            for(int i  = 0; i < jsonArraySpending.length(); i ++) {
                String strSpending = jsonArraySpending.getString(i);
                String[] arrStrPattern = strSpending.split("xxx");

                if(arrStrPattern.length == 3) {
                    String strMiddle = arrStrPattern[1];
                    String[] arrStrCurrent = strSmsText.split(strMiddle);

                    if(arrStrCurrent.length != 2) continue;

                    String[] arrFirst = arrStrCurrent[0].split(arrStrPattern[0]);
                    String strIdentifier = arrStrCurrent[1];

                    if(strIdentifier.substring(strIdentifier.length() - 1).equals(".")) {
                        strIdentifier = strIdentifier.substring(0, strIdentifier.length() - 1);
                    }

                    String strAmount = arrFirst[1];

                    String[] strArrAmount = strAmount.split(" ");

                    String strCurrency = strArrAmount[0];
                    String strRealAmount = strArrAmount[1].replace(",", "");

                    int nAmount = Double.valueOf(strRealAmount).intValue();

                    transaction.setAmount(nAmount);
                    transaction.setIdentifier(strIdentifier);
                    transaction.setMode(2);

                    isGetResult = true;

                    break;
                }
            }

            if(isGetResult) return transaction;

            for(int i = 0; i < jsonArrayIncome.length(); i ++) {
                String strIncome = jsonArrayIncome.getString(i);
                String[] arrStrPattern = strIncome.split("xxx");

                if(arrStrPattern.length == 2 && arrStrPattern[0].length() == 0) {
                    String strLater = arrStrPattern[1];
                    String[] arrStrCurrent = strSmsText.split(strLater);

                    if(arrStrCurrent.length == 1) continue;

                    String[] arrStrAmount = arrStrCurrent[0].split(" ");

                    String strCurrency = arrStrAmount[0];
                    String strRealAmount = arrStrAmount[1].replace(",", "");
                    int nAmount = Double.valueOf(strRealAmount).intValue();

                    transaction.setMode(1);
                    transaction.setAmount(nAmount);

                    isGetResult = true;

                    break;
                }
            }

            if(isGetResult) return transaction;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
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

        if(nDayOfMonth >= nSalaryDate) {
            nMonth ++;
            if(nMonth == 12) {
                nYear ++;
                nMonth = 0;
            }
        }

        int nDateSaving = nYear * 100 + nMonth;

        for(int i = 0; i < listActiveWishes.size(); i ++) {
            Wish wishItem = listActiveWishes.get(i);

            int nLastSavingId = wishItem.getLastSavingId();

            if(nLastSavingId == -1) continue;

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

    public int sumOfTransactionThisWeek() { // for B Group Transaction, Transaction.mode = 2
        int nAns = 0;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK,Calendar.SUNDAY);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);

        long nLow = c.getTimeInMillis();

        c.add(Calendar.DATE, 7);
        long nHigh = c.getTimeInMillis();

        for(int i = 0; i < listTransactions.size(); i ++) {
            Transaction transaction = listTransactions.get(i);

            if(transaction.getTimestampCreated() >= nHigh) continue;
            if(transaction.getTimestampCreated() < nLow) break;

            nAns += transaction.getAmount();
        }

        return nAns;
    }

    public int sumOfTransactionThisMonth() { // for B Group Transaction, Transaction.mode = 2
        int nAns = 0;

        Calendar c = Calendar.getInstance();
        int nDayNow = c.get(Calendar.DAY_OF_MONTH);

        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);

        long nLow = 0;
        long nHigh = 0;

        if(nSalaryDate > nDayNow) {
            nHigh = c.getTimeInMillis();
            c.add(Calendar.MONTH, -1);
            nLow = c.getTimeInMillis();
        } else {
            nLow = c.getTimeInMillis();
            c.add(Calendar.MONTH, 1);
            nHigh = c.getTimeInMillis();
        }

        for(int i = 0; i < listTransactions.size(); i ++) {
            Transaction transaction = listTransactions.get(i);

            if(transaction.getTimestampCreated() >= nHigh) continue;
            if(transaction.getTimestampCreated() < nLow) break;

            nAns += transaction.getAmount();
        }

        return nAns;
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
        if(bankActive == null) return 0;
        return bankActive.getBalance();
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

    class TransactionComparator implements Comparator<Transaction> {
        public int compare(Transaction transA, Transaction transB) {
            int nResult = 0;
            if(transB.getTimestampCreated() > transA.getTimestampCreated()) nResult = 1;
            if(transB.getTimestampCreated() < transA.getTimestampCreated()) nResult = -1;
            return nResult;
        }
    }
}
