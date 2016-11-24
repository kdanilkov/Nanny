package com.ninja.nanny.Utils;

import com.ninja.nanny.Comparator.PaymentComparator;
import com.ninja.nanny.Comparator.SmsComparator;
import com.ninja.nanny.Comparator.TransactionComparator;
import com.ninja.nanny.Comparator.WishComparator;
import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.Model.Paid;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.Model.WishSaving;
import com.ninja.nanny.Preference.UserPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
    public List<Sms> listSms;
    public List<Transaction> listAllTransactions;
    List<Transaction> listNewTransactions;
    public long timestampInitConfig;
    public long timestampSms;
    public int nMonthlyIncome;
    public int nMinimalDayAmount;
    public int nSalaryDate;
    public int nUsedAmount;
    public int nToleranceDays;
    public int nTolerancePercents;
    public int timeWishSaving;
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

    public List<Payment> getUnPaidPayments() { // get upaid payments from past period.
        Calendar c = Calendar.getInstance();
        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay < nSalaryDate) {
            c.add(Calendar.MONTH, -1);
        }

        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long timestampPastPeriodStart = c.getTimeInMillis();

        Collections.sort(listAllPayments, new PaymentComparator());

        ArrayList<Payment> listAns = new ArrayList<>();

        for(int i = 0; i < listAllPayments.size(); i ++) {
            Payment payment = listAllPayments.get(i);

            if((payment.getPaymentMode() == 1 || payment.getPaymentMode() == 3) && payment.getRealTimeStampForSingle() < timestampPastPeriodStart) {
                break;
            }

            if(payment.getPaidStatus() == 0) {
                listAns.add(payment);
            }
        }

        return listAns;
    }

    public void  bindBetweenTransactionAndPayment() {
        List<Payment> listUnPaidPayments = getUnPaidPayments();

        if(listUnPaidPayments.size() == 0) return;

        for(int i = listNewTransactions.size() - 1; i >= 0; i --) {
            Transaction trans = listNewTransactions.get(i);

            if(trans.getMode() < 2) continue;

            String strIdentifier = trans.getIdentifier();

            for(int j = 0; j < listUnPaidPayments.size(); j ++) {
                Payment payment = listUnPaidPayments.get(j);

                String strPaymentIdentifier = payment.getIdentifier();

                if(strIdentifier.toLowerCase().contains(strPaymentIdentifier.toLowerCase())) {
                    Paid  paid = new Paid();

                    paid.setPaymentId(payment.getId());
                    paid.setTransactionId(trans.getId());
                    paid.setTimestampCreated(trans.getTimestampCreated());

                    int paid_id = dbHelper.createPaid(paid);

                    trans.setPaidId(paid_id);
                    payment.setLastPaidId(paid_id);

                    dbHelper.updateTransaction(trans);
                    dbHelper.updatePayment(payment);

                    break;
                }
            }
        }

        listAllPayments = dbHelper.getAllPayments();
    }

    public void checkWishSavingPast() {
        Calendar c = Calendar.getInstance();

        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay < nSalaryDate) {
            c.add(Calendar.MONTH, -1);
        }

        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long timestampLastSalaryDate = c.getTimeInMillis(); // just prev salary date

        if(timestampInitConfig > timestampLastSalaryDate) return; // app has been installed after salary date, so there is no wish for past period

        int nYear = c.get(Calendar.YEAR);
        int nMonth = c.get(Calendar.MONTH);

        int nDateWishSaving = nYear * 100 + nMonth;

        if(nDateWishSaving == timeWishSaving) return; // past checking has already been done.

        timeWishSaving = nDateWishSaving;
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_WISH_SAVING_TIME, nDateWishSaving);

        int nLeftOnPast = leftOnPast(nYear, nMonth);

        Collections.sort(listActiveWishes, new WishComparator());

        for(int i = 0; i < listActiveWishes.size(); i ++) {
            Wish wish = listActiveWishes.get(i);

            int nTotalAmount = wish.getTotalAmount();
            int nSavingAmount = wish.getMonthlyPayment();
            int nUpdatedSavedAmount = wish.getSavedAmount() + nSavingAmount;

            if(nUpdatedSavedAmount > nTotalAmount) {
                nSavingAmount = nTotalAmount - wish.getSavedAmount();
            }

            if(nSavingAmount > nLeftOnPast) {
                nSavingAmount = nLeftOnPast;
            }

            nLeftOnPast -= nSavingAmount;
            nUpdatedSavedAmount = wish.getSavedAmount() + nSavingAmount;

            WishSaving wishSaving = new WishSaving(wish.getId(), nSavingAmount, nDateWishSaving);
            int nWishSavingId = dbHelper.createWishSaving(wishSaving);
            wishSaving.setId(nWishSavingId);

            wish.setLastSavingId(nWishSavingId);
            wish.setSavedAmount(nUpdatedSavedAmount);

            if(nUpdatedSavedAmount == nTotalAmount) {
                wish.setFlagActive(0); // this is finished
            }

            dbHelper.updateWish(wish);
        }

        listAllWishes = dbHelper.getAllWishes();
        listActiveWishes = dbHelper.getActiveWishes();
        listFinishedWishes = dbHelper.getFinishedWishes();
    }
    public void syncBetweenTransactionAndSms() {
        if(!isActiveBankExist()) {
            return;
        }

        Collections.sort(listSms, new SmsComparator());

        long timestampBankActive = bankActive.getTimestamp();

        if(timestampBankActive > timestampSms) timestampSms = timestampBankActive;

        long timestampMax = timestampSms;

        listNewTransactions = new ArrayList<Transaction>();

        for(int i = 0; i < listSms.size(); i ++) {
            Sms sms = listSms.get(i);
            long timestampTmp = sms.getTimestamp();
            if(timestampTmp <= timestampSms) break;
            if(timestampTmp > timestampMax) timestampMax = timestampTmp;

            Transaction transaction = convertToTransactionFromSms(sms);

            if(transaction == null) continue;

            listNewTransactions.add(transaction);
        }

        timestampSms = timestampMax;

        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_SMS_TIMESTAMP, timestampSms);

        if(listNewTransactions.size() > 0) {
            calculateBalance();
            bindBetweenTransactionAndPayment();
            addNewTransactions();
        }
    }

    public void addNewTransactions() {
        for(int i = listNewTransactions.size() - 1; i >= 0; i --) {
            Transaction trans = listNewTransactions.get(i);
            int nTransId = dbHelper.createTransaction(trans);

            trans.setId(nTransId);
            listAllTransactions.add(trans);
        }

        Collections.sort(listAllTransactions, new TransactionComparator());
    }

    public void calculateBalance() {
        int nVal = bankActive.getBalance();

        for(int i = listNewTransactions.size() - 1; i >= 0 ; i --) {
            Transaction trans = listNewTransactions.get(i);

            if(trans.getMode() == 0) nVal = trans.getAmount();
            if(trans.getMode() == 1) nVal += trans.getAmount();
            if(trans.getMode() == 2) nVal -= trans.getAmount();
        }

        bankActive.setBalance(nVal);

        dbHelper.updateBank(bankActive);
    }

    public Transaction convertToTransactionFromSms(Sms sms) {
        String strBankName = bankActive.getBankName().toLowerCase();
        if(!sms.getAddress().toLowerCase().equals(strBankName)) return null;

        Transaction transaction = new Transaction();
        transaction.setPaidId(-1);

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

    public List<Payment> getCurrentPayments() {
        List<Payment> listAns = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay < nSalaryDate) {
            c.add(Calendar.MONTH, -1);
        }

        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long timestampCurrentPeriodStart = c.getTimeInMillis();

        c.add(Calendar.MONTH, 1);

        long timestampCurrentPeriodEnd = c.getTimeInMillis();

        Collections.sort(listAllPayments, new PaymentComparator());

        for(int i = 0; i < listAllPayments.size(); i ++) {
            Payment payment = listAllPayments.get(i);

            if(payment.getPaymentMode() == 0 || payment.getPaymentMode() == 2) {
                listAns.add(payment);
            } else {
                if(payment.getRealTimeStampForSingle() < timestampCurrentPeriodStart) break;
                if(payment.getRealTimeStampForSingle() >= timestampCurrentPeriodEnd) continue;

                listAns.add(payment);
            }
        }

        return listAns;
    }

    public int sumOfPaymentsForMonth() {
        int nAns = 0;

        List<Payment> listCurrentPayments = getCurrentPayments();

        for(int i = 0; i < listCurrentPayments.size(); i ++) {
            Payment paymentItem = listCurrentPayments.get(i);

            nAns += paymentItem.getAmount();
        }

        return nAns;
    }

    public int sumOfWishesForMonth() {
        int nAns = 0;

        for(int i = 0; i < listActiveWishes.size(); i ++) {
            Wish wish = listActiveWishes.get(i);

            nAns += wish.getMonthlyPayment();
        }

        return nAns;
    }

    public int monthlyLimit() {
        return nMonthlyIncome - sumOfPaymentsForMonth() - sumOfWishesForMonth();
    }

    public int weeklyLimit() {
        Calendar c = Calendar.getInstance();
        int nTotalDaysOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        return monthlyLimit() * 7 / nTotalDaysOfMonth;
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

        for(int i = 0; i < listAllTransactions.size(); i ++) {
            Transaction trans = listAllTransactions.get(i);

            if(trans.getTimestampCreated() >= nHigh) continue;
            if(trans.getTimestampCreated() < nLow) break;

            nAns += trans.getAmount();
        }

        return nAns;
    }

    public int sumOfTransactionForMonth() { // for B Group Transaction, Transaction.mode = 2
        int nAns = 0;

        Calendar c = Calendar.getInstance();
        long nHigh = c.getTimeInMillis();
        int nDayNow = c.get(Calendar.DAY_OF_MONTH);

        if(nDayNow < nSalaryDate) {
            c.add(Calendar.MONTH , -1);
        }

        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long nLow = c.getTimeInMillis();

        for(int i = 0; i < listAllTransactions.size(); i ++) {
            Transaction trans = listAllTransactions.get(i);

            if(trans.getTimestampCreated() > nHigh) continue;
            if(trans.getTimestampCreated() < nLow) break;
            if(trans.getMode() < 2) break;
            nAns += trans.getAmount();
        }

        return nAns;
    }

    public int leftOnPast(int nYear, int nMonth) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);

        int nDaysOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        return nMonthlyIncome - sumOfPaymentsForPast(nYear, nMonth) - sumOfRestTransactionsForPast(nYear, nMonth) - nMinimalDayAmount * nDaysOfMonth;
    }

    public int sumOfPaymentsForPast(int nYear, int nMonth) { // sum of paid payments
        int nAns = 0;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, nYear);
        c.set(Calendar.MONTH, nMonth);
        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long timestampHigh = c.getTimeInMillis();

        c.add(Calendar.MONTH, -1);
        long timestampLow = c.getTimeInMillis();

        for(int i = 0; i < listAllPayments.size(); i ++) {
            Payment payment = listAllPayments.get(i);
            int nPaidId = payment.getLastPaidId();

            if(nPaidId == -1) continue;

            Paid paid = dbHelper.getPaid(nPaidId);

            if(paid.getTimestampCreated() >= timestampLow && paid.getTimestampCreated() < timestampHigh) {
                nAns += payment.getAmount();
            }
        }

        return nAns;
    }

    public int sumOfRestTransactionsForPast(int nYear, int nMonth) { // sum of unbinding payments for spending
        int nAns = 0;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, nYear);
        c.set(Calendar.MONTH, nMonth);
        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long timestampHigh = c.getTimeInMillis();

        c.add(Calendar.MONTH, -1);
        long timestampLow = c.getTimeInMillis();

        for(int i = 0; i < listAllTransactions.size(); i ++) {
            Transaction trans = listAllTransactions.get(i);

            if(trans.getTimestampCreated() >= timestampHigh) continue;
            if(trans.getTimestampCreated() < timestampLow) break;
            if(trans.getMode() < 2) continue;

            int nPaidId = trans.getPaidId();

            if(nPaidId == -1){
                nAns += trans.getAmount();
            }
        }

        return nAns;
    }

    public int sumOfTotalTransactionsForPast(int  nYear, int nMonth) { // sum of paymetns for spending
        int nAns = 0;

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, nYear);
        c.set(Calendar.MONTH, nMonth);
        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long timestampHigh = c.getTimeInMillis();

        c.add(Calendar.MONTH, -1);
        long timestampLow = c.getTimeInMillis();

        for(int i = 0; i < listAllTransactions.size(); i ++) {
            Transaction trans = listAllTransactions.get(i);

            if(trans.getTimestampCreated() >= timestampHigh) continue;
            if(trans.getTimestampCreated() < timestampLow) break;
            if(trans.getMode() < 2) continue;

            nAns += trans.getAmount();
        }

        return nAns;
    }

    public int freeOnThisWeek() {
        return weeklyLimit() - sumOfTransactionThisWeek();
    }

//    public int upcomingPayments() { // SUM(predefiend payments for next 7 days)
//        int nAns = 0;
//
//        Calendar c = Calendar.getInstance();
//        int nTotalDaysOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//        int nDayCurrent = c.get(Calendar.DAY_OF_MONTH);
//
//        for(int i = 0; i < listCurrentPayments.size(); i ++) {
//            Payment paymentItem = listCurrentPayments.get(i);
//            int nDayPayment = paymentItem.getDateOfMonth();
//
//            if(nDayCurrent + 7 > nTotalDaysOfMonth) {
//                if(nDayPayment > nDayCurrent){
//                    nAns += paymentItem.getAmount();
//                } else {
//                    if(nDayPayment <= nDayCurrent + 7 - nTotalDaysOfMonth) {
//                        nAns += paymentItem.getAmount();
//                    }
//                }
//            } else {
//                if(nDayPayment > nDayCurrent && nDayPayment <= nDayCurrent + 7) {
//                    nAns += paymentItem.getAmount();
//                }
//            }
//        }
//
//        return nAns;
//    }

    public int upcomingPayments() { // sum of unpaid payments during this period.
        int nAns = 0;

        Calendar c = Calendar.getInstance();

        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay < nSalaryDate) {
            c.add(Calendar.MONTH , -1);
        }

        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);

        long nLow = c.getTimeInMillis();

        List<Payment> listCurrentPayments = getCurrentPayments();

        for(int i = 0; i < listCurrentPayments.size(); i ++) {
            Payment payment = listCurrentPayments.get(i);
            int nPaidId = payment.getLastPaidId();

            if(nPaidId == -1) { // not paid
                nAns += payment.getAmount();
                continue;
            }

            if(payment.getPaymentMode() == 0 || payment.getPaymentMode() == 2) {
                Paid paid = dbHelper.getPaid(nPaidId);

                if(paid.getTimestampCreated() < nLow) { // paid timstamp is older than nLow
                    nAns += payment.getAmount();
                }
            }
        }

        return nAns;
    }


    public int freeOnThisMonth() {
        return monthlyLimit() - sumOfTransactionForMonth();
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

        List<Payment> listCurrentPayments = getCurrentPayments();

        for(int i = 0; i < listCurrentPayments.size(); i ++) {

            Payment payment = listCurrentPayments.get(i);

            if(payment.getPaymentMode() > 1) continue;

            nAns += payment.getAmount();
        }

        return nAns;
    }

    public int totalBills() { // for checking the advisor
        return sumOfPaymentsForMonth() - totalSavings();
    }

    public int checkAdvisorStatus(int nAskingAmount) {
        int nStatus = 0;

        if (freeOnThisWeek() - daysLeftForThisWeek() * nMinimalDayAmount >= nAskingAmount) {
            nStatus = 0;
        } else if (freeOnThisWeek() >= nAskingAmount){
            nStatus = 1;
        } else if (freeOnThisMonth() - daysLeftForThisMonth() * nMinimalDayAmount >= nAskingAmount) {
            nStatus = 2;
        } else if (freeOnThisMonth() >= nAskingAmount) {
            nStatus = 3;
        } else if(freeOnThisMonth() + totalWishes() >= nAskingAmount) {
            nStatus = 4;
        } else if(freeOnThisMonth() + totalWishes() + totalSavings() >= nAskingAmount) {
            nStatus = 5;
        } else if(freeOnThisMonth() + totalWishes() + totalSavings() + totalBills() >= nAskingAmount) {
            nStatus = 6;
        } else if(balanceOfActiveBank() >= nAskingAmount) {
            nStatus = 7;
        } else {
            nStatus = 8;
        }

        return nStatus;
    }


}
