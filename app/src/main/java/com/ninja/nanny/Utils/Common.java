package com.ninja.nanny.Utils;

import android.content.Context;
import android.util.Log;

import com.ninja.nanny.Comparator.PaymentComparator;
import com.ninja.nanny.Comparator.TransactionComparator;
import com.ninja.nanny.Comparator.WishComparator;
import com.ninja.nanny.Helper.DatabaseHelper;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.Model.Paid;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Model.UsedAmount;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.Model.WishSaving;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 10/28/2016.
 */

public class Common {

    private static Common instance = null;

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
    public int nMonthlyIncome;
    public int nMinimalDayAmount;
    public int nSalaryDate;
    public int nToleranceDays;
    public int nTolerancePercents;
    public int timeWishSaving;
    public Bank bankActive;
    public JSONArray jsonArrayBankInfo;
    public JSONArray jsonArrayTemplates;

    public long getTimestamp() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public void setInitTime(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    public long getTimestampPeriodEndOf(long timestampSpec) {
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(timestampSpec);

        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay >= nSalaryDate) {
            c.add(Calendar.MONTH, 1);
        }

        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        setInitTime(c);

        return c.getTimeInMillis();
    }

    public long getTimestampCurrentSalaryDate() {
        Calendar c = Calendar.getInstance();

        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        setInitTime(c);

        return c.getTimeInMillis();
    }

    public long getTimestampCurrentPeriodStart() {
        Calendar c = Calendar.getInstance();
        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay < nSalaryDate) {
            c.add(Calendar.MONTH, -1);
        }

        c.set(Calendar.DAY_OF_MONTH, nSalaryDate);
        setInitTime(c);

        return c.getTimeInMillis();
    }

    public long getTimestampCurrentPeriodEnd() {
        long timestampCurrentPeriodStart = getTimestampCurrentPeriodStart();
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(timestampCurrentPeriodStart);
        c.add(Calendar.MONTH, 1);

        return c.getTimeInMillis();
    }

    public long getTimestampPastPeriodEnd() {
        return getTimestampCurrentPeriodStart();
    }

    public long getTimestampPastPeriodStart() {
        long timestampPastPeriodEnd = getTimestampPastPeriodEnd();
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(timestampPastPeriodEnd);
        c.add(Calendar.MONTH, -1);

        return c.getTimeInMillis();
    }

    public boolean isActiveBankExist() {
        boolean isExist = false;

        for(int i = 0; i < listBanks.size(); i ++) {
            Bank bank = listBanks.get(i);

            if(bank.getFlagActive() == 1) {
                isExist = true;
                bankActive = bank;
                break;
            }
        }

        return isExist;
    }

    public void syncSettingInfo() {
        timestampInitConfig = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_INIT_CONFIG_TIMESTAMP, (long)0);
        nMinimalDayAmount = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_MINIMAL_AMOUNT_PER_DAY, 0);
        nSalaryDate = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SALARY_DATE, 15);
        nMonthlyIncome = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_MONTHLY_INCOME, 0);
        nToleranceDays = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_TOLERANCE_DAYS, 2);
        nTolerancePercents = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_TOLERANCE_PERCENT, 5);
        timeWishSaving = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_WISH_SAVING_TIME, 0);
    }

    public List<Payment> getUnPaidPayments() { // get upaid payments from past period.

        long timestampPastPeriodStart = getTimestampPastPeriodStart();

        Collections.sort(listAllPayments, new PaymentComparator());

        ArrayList<Payment> listAns = new ArrayList<>();

        for(int i = 0; i < listAllPayments.size(); i ++) {
            Payment payment = listAllPayments.get(i);

            if(payment.getPaymentMode() == 1 || payment.getPaymentMode() == 3) {

                if (payment.getNextPaymentTimestamp() < timestampPastPeriodStart) {
                    // those are single payment, and real payment timestamp is older than timestamp of past period start
                    break;
                }

                if(payment.getLastPaidId() > -1) {
                    //those single payments has been paid.
                    continue;
                }
            }

            listAns.add(payment); // all recurrent payment should be added for tollerance concept.
        }

        return listAns;
    }

    public void calculateUsedAmount() {
        for(int i = 0; i < listNewTransactions.size(); i ++) {
            Transaction trans = listNewTransactions.get(i);

            if(trans.getMode() < 2) continue;

            int nPaidId = trans.getPaidId();
            long timestampPeriodEnd = 0;

            if(nPaidId == -1) {
                timestampPeriodEnd = getTimestampPeriodEndOf(trans.getTimestampCreated());
            } else {
                Paid paid = dbHelper.getPaid(nPaidId);
                timestampPeriodEnd = getTimestampPeriodEndOf(paid.getTimestampPayment());
            }

            UsedAmount usedAmount = dbHelper.getUsedAmount(timestampPeriodEnd);
            int nUpdatedUsedAmount = usedAmount.getUsedAmount() + trans.getAmountChange();

            usedAmount.setUsedAmount(nUpdatedUsedAmount);
            usedAmount.setTimestampUpdated(getTimestamp());

            dbHelper.updateUsedAmount(usedAmount);
        }
    }

    public void  bindBetweenTransactionAndPayment() {
        List<Payment> listUnPaidPayments = getUnPaidPayments();

        if(listUnPaidPayments.size() == 0) return;

        for(int i = 0; i < listNewTransactions.size(); i ++) {
            Transaction trans = listNewTransactions.get(i);

            if(trans.getMode() < 2) continue; // if balance or income transaction

            String strIdentifier = trans.getIdentifier();

            for(int j = 0; j < listUnPaidPayments.size(); j ++) {
                Payment payment = listUnPaidPayments.get(j);

                // --- identifer match part --

                String strPaymentIdentifier = payment.getIdentifier();

                boolean isMatchIdentifier = strIdentifier.toLowerCase().contains(strPaymentIdentifier.toLowerCase());

                // --- date match part --
                Calendar c = Calendar.getInstance();
                //set timestamp of transaction
                c.setTimeInMillis(trans.getTimestampCreated());
                //subtract tolerance days from it
                c.add(Calendar.DAY_OF_YEAR, - nToleranceDays);
                setInitTime(c);

                long nLow = c.getTimeInMillis();

                //set timestamp of transaction
                c.setTimeInMillis(trans.getTimestampCreated());
                //add tolerance days to it, and add one day for high limit
                c.add(Calendar.DAY_OF_YEAR, nToleranceDays + 1);
                setInitTime(c);

                long nHigh = c.getTimeInMillis();

                long timestampReal = payment.getPaymentTimestampBetween(nLow, nHigh);

                boolean isMatchDate = (timestampReal != 0);

                // --- amount match part --
                int nAmountLow = payment.getAmount() * (100 - nTolerancePercents) / 100;
                int nAmountHigh = payment.getAmount() * (100 + nTolerancePercents) / 100;

                boolean isMatchAmount = (trans.getAmountChange() >= nAmountLow) && (trans.getAmountChange() <= nAmountHigh);

                if(isMatchIdentifier && isMatchDate && isMatchAmount) {
                    Paid  paid = new Paid(payment.getId(), trans.getId(), payment.getLastPaidId(), timestampReal, trans.getTimestampCreated());

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

    public int sumOfIncomeTransactionsForMonth(long timestampSalaryDate) { // timestampSalaryDate- End of Period.
        int nAns = 0;

        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(timestampSalaryDate);

        c.add(Calendar.MONTH, -1);

        long timestampStart = c.getTimeInMillis();
        long timestampEnd = timestampSalaryDate;

        c.add(Calendar.DAY_OF_YEAR, - nToleranceDays);

        long timestampIntervalStart = c.getTimeInMillis();

        c.setTimeInMillis(timestampSalaryDate);
        c.add(Calendar.DAY_OF_YEAR, nToleranceDays);

        long timestampIntervalEnd = c.getTimeInMillis();

        for(int i = 0; i < listAllTransactions.size(); i ++) {
            Transaction trans = listAllTransactions.get(i);
            long timestampTrans = trans.getTimestampCreated();

            if(trans.getMode() != 1) continue;
            if(timestampTrans >= timestampIntervalEnd) continue;
            if(timestampTrans < timestampIntervalStart) break;

            int nPaidId = trans.getPaidId();

            if(nPaidId == -1) continue;

            Paid paid = dbHelper.getPaid(nPaidId);
            long timestampPayment = paid.getTimestampPayment();

            if(timestampPayment < timestampStart) continue;
            if(timestampPayment >= timestampEnd) continue;

            nAns += trans.getAmountChange();
        }

        return nAns;
    }

    public int sumOfBGrupTransactionForMonth(long timestampSalaryDate) { // timestampSalaryDate- End of Period.
        int nAns = 0;

        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(timestampSalaryDate);

        c.add(Calendar.MONTH, -1);

        long timestampStart = c.getTimeInMillis();
        long timestampEnd = timestampSalaryDate;

        for(int i = 0; i < listAllTransactions.size(); i ++) {
            Transaction trans = listAllTransactions.get(i);
            long timestampTrans = trans.getTimestampCreated();

            if(trans.getMode() < 2) continue;
            if(trans.getPaidId() == -1) {
                if(timestampTrans < timestampEnd && timestampTrans >= timestampStart) {
                    nAns += trans.getAmountChange();
                }
                continue;
            }
        }

        return nAns;
    }

    public int sumOfSpendingTransactionForMonth(long timestampSalaryDate) { // timestampSalaryDate- End of Period.
        int nAns = 0;

        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(timestampSalaryDate);

        c.add(Calendar.MONTH, -1);

        long timestampStart = c.getTimeInMillis();
        long timestampEnd = timestampSalaryDate;

        if(timestampInitConfig > timestampStart) {
            nAns = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_INIT_USED_MONEY, 0);
        }

        c.add(Calendar.DAY_OF_YEAR, - nToleranceDays);

        long timestampIntervalStart = c.getTimeInMillis();

        c.setTimeInMillis(timestampSalaryDate);
        c.add(Calendar.DAY_OF_YEAR, nToleranceDays);

        long timestampIntervalEnd = c.getTimeInMillis();

        for(int i = 0; i < listAllTransactions.size(); i ++) {
            Transaction trans = listAllTransactions.get(i);
            long timestampTrans = trans.getTimestampCreated();

            if(trans.getMode() < 2) continue;
            if(timestampTrans >= timestampIntervalEnd) continue;
            if(timestampTrans < timestampIntervalStart) break;

            int nPaidId = trans.getPaidId();

            if(nPaidId == -1) {
                if(timestampTrans < timestampEnd && timestampTrans >= timestampStart) {
                    nAns += trans.getAmountChange();
                }
                continue;
            }

            Paid paid = dbHelper.getPaid(nPaidId);
            long timestampPayment = paid.getTimestampPayment();

            if(timestampPayment < timestampStart) continue;
            if(timestampPayment >= timestampEnd) continue;

            nAns += trans.getAmountChange();
        }

        return nAns;
    }

    public void checkIncomeTransaction() {
        long timestampSalaryDate = getTimestampCurrentSalaryDate();
        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(timestampSalaryDate);
        c.add(Calendar.DAY_OF_YEAR, - nToleranceDays);

        long timestampIntervalStart = c.getTimeInMillis();

        c.setTimeInMillis(timestampSalaryDate);
        c.add(Calendar.DAY_OF_YEAR, nToleranceDays);

        long timestampIntervalEnd = c.getTimeInMillis();

        int nSumTransSoFar = sumOfIncomeTransactionsForMonth(timestampSalaryDate);

        int nAmountHigh = nMonthlyIncome * (100 + nTolerancePercents) / 100;
        int nAmountLow = nMonthlyIncome * (100 - nTolerancePercents) / 100;

        for(int i = 0; i < listNewTransactions.size(); i ++) {
            Transaction trans = listNewTransactions.get(i);

            if(trans.getMode() != 1) continue;

            long timestampTrans = trans.getTimestampCreated();
            int nAmountTrans = trans.getAmountChange();
            long timestampAns = timestampTrans;

            if(timestampTrans >= timestampIntervalStart && timestampTrans < timestampSalaryDate) {
                if(nAmountTrans >= nAmountLow) {
                    timestampAns = timestampIntervalEnd;
                } else {
                    if(nSumTransSoFar >= nAmountHigh) {
                        timestampAns = timestampIntervalEnd;
                    } else {
                        timestampAns = timestampIntervalStart;
                        nSumTransSoFar += trans.getAmountChange();
                    }
                }
            }

            if(timestampTrans >= timestampSalaryDate && timestampTrans < timestampIntervalEnd) {
                if(nAmountTrans >= nAmountLow) {
                    timestampAns = timestampIntervalEnd;
                } else {
                    if(nSumTransSoFar < nAmountLow) {
                        timestampAns = timestampIntervalStart;
                        nSumTransSoFar += trans.getAmountChange();
                    } else {
                        timestampAns = timestampIntervalEnd;
                    }
                }
            }

            Paid  paid = new Paid(-1, trans.getId(), -1, timestampAns, trans.getTimestampCreated());
            int nPaidId = dbHelper.createPaid(paid);

            trans.setPaidId(nPaidId);
        }
    }

    public void checkWishSavingPast() {
        long timestampCurretnt = getTimestamp();
        long timestampPastPeriodEnd = getTimestampPastPeriodEnd();

        Calendar c = Calendar.getInstance();

        c.setTimeInMillis(timestampPastPeriodEnd);
        c.add(Calendar.DAY_OF_YEAR, nToleranceDays);

        long timestampPastPeriodIntervalEnd = c.getTimeInMillis();

        if(timestampPastPeriodIntervalEnd >= timestampCurretnt) return; // current day is bofore tolerance days of preve salary date.

        if(timestampInitConfig > timestampPastPeriodIntervalEnd) return; // app has been installed after salary date, so there is no wish for past period

        c.setTimeInMillis(timestampPastPeriodEnd);

        int nYear = c.get(Calendar.YEAR);
        int nMonth = c.get(Calendar.MONTH);

        int nDateWishSaving = nYear * 100 + nMonth;

        if(nDateWishSaving == timeWishSaving) return; // past checking has already been done.

        timeWishSaving = nDateWishSaving;
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_WISH_SAVING_TIME, nDateWishSaving);

        int nLeftMoney = leftMoneyForMonth(c.getTimeInMillis());

        Collections.sort(listActiveWishes, new WishComparator());

        for(int i = 0; i < listActiveWishes.size(); i ++) {
            Wish wish = listActiveWishes.get(i);

            int nTotalAmount = wish.getTotalAmount();
            int nSavingAmount = wish.getMonthlyPayment();
            int nUpdatedSavedAmount = wish.getSavedAmount() + nSavingAmount;

            if(nUpdatedSavedAmount > nTotalAmount) {
                nSavingAmount = nTotalAmount - wish.getSavedAmount();
            }

            if(nSavingAmount > nLeftMoney && nLeftMoney >= 0) {
                nSavingAmount = nLeftMoney;
            }

            nLeftMoney -= nSavingAmount;
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

    boolean isNewSms(Sms sms) {
        long timestampSms = sms.getTimestamp();

        int nStart = 0, nEnd = listAllTransactions.size();
        if(nEnd == 0) return true;

        while(true) {
            int nCurrent = (nStart + nEnd) / 2;

            if(nStart == nCurrent) {
                Transaction trans = listAllTransactions.get(nStart);
                long timestampTrans = trans.getTimestampCreated();

                if(timestampTrans == timestampSms) return false;

                if(nEnd < listAllTransactions.size()) {
                    trans = listAllTransactions.get(nEnd);
                    timestampTrans = trans.getTimestampCreated();

                    if(timestampTrans == timestampSms) return false;
                }

                break;
            }

            Transaction trans = listAllTransactions.get(nCurrent);
            long timestampTrans = trans.getTimestampCreated();

            if(timestampTrans == timestampSms) return false;
            if(timestampTrans > timestampSms) {
                nStart = nCurrent;
            } else {
                nEnd = nCurrent;
            }
        }

        return true;
    }

    public void syncBetweenTransactionAndSms() {
        if(!isActiveBankExist()) {
            return;
        }

        Collections.sort(listAllTransactions, new TransactionComparator());

        long timestampBankActive = bankActive.getTimestamp();

        listNewTransactions = new ArrayList<Transaction>();

        for(int i = 0; i < listSms.size(); i ++) {
            Sms sms = listSms.get(i);
            long timestampSms = sms.getTimestamp();
            if(timestampSms <= timestampBankActive) continue;
            if(!isNewSms(sms)) continue;

            Transaction transaction = convertSmsToTransaction(sms);
            if(transaction == null) continue;

            listNewTransactions.add(0,transaction);
            Log.e(Constant.TAG_CURRENT, "added transaction to the listNewTransaction");
        }

        if(listNewTransactions.size() > 0) {
            calculateBalance();
            bindBetweenTransactionAndPayment();
            calculateUsedAmount();
            checkIncomeTransaction();
            addNewTransactions();
        }
    }



    Transaction convertSmsToTransaction(Sms sms) {
        String strAccountName = "";
        String strAddress = "";

        try {
            JSONObject jsonObjBank = jsonArrayBankInfo.getJSONObject(bankActive.getIdxKind());
            strAccountName = jsonObjBank.getString(Constant.JSON_NAME);
            strAddress = jsonObjBank.getString(Constant.JSON_ADDRESS);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!sms.getAddress().toLowerCase().equals(strAddress.toLowerCase())) return null;

        for(int i = 0; i < jsonArrayTemplates.length(); i ++) {
            Transaction transaction = null;

            try {
                JSONObject jsonTemplate = jsonArrayTemplates.getJSONObject(i);
                //check that template is for the right bank
                String strBankAddress = jsonTemplate.getString(Constant.JSON_ADDRESS);
                if(!strAddress.toLowerCase().equals(strBankAddress.toLowerCase()))continue;
                //process template
                transaction = parseTextUsingTempalte(sms.getText(), jsonTemplate);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(transaction == null) continue;

            Log.e(Constant.TAG_CURRENT, "success on parsing sms in the parseSmsUsingRegex");

            transaction.setPaidId(-1);
            transaction.setAccountName(strAccountName);
            transaction.setBankId(Common.getInstance().bankActive.getIdxKind());
            transaction.setText(sms.getText());
            transaction.setTimestampCreated(sms.getTimestamp());

            return transaction;
        }

        return null;
    }

    Transaction parseTextUsingTempalte(String strMsg, JSONObject jsonTemplate) {
        try {
            String strRegex = jsonTemplate.getString(Constant.JSON_REGEX);
            String strMode = jsonTemplate.getString(Constant.JSON_MODE);
            JSONObject jsonParams = jsonTemplate.getJSONObject(Constant.JSON_PARAMS);
            int nPosChange = -1, nPosWords = -1, nPosBalance = -1;

            if(jsonParams.has(Constant.JSON_CHANGE)) {
                nPosChange = jsonParams.getInt(Constant.JSON_CHANGE);
            }

            if(jsonParams.has(Constant.JSON_WORDS)) {
                nPosWords = jsonParams.getInt(Constant.JSON_WORDS);
            }

            if(jsonParams.has(Constant.JSON_BALANCE)) {
                nPosBalance = jsonParams.getInt(Constant.JSON_BALANCE);
            }

            Pattern p = Pattern.compile(strRegex);
            Matcher m = p.matcher(strMsg);

            if(m.find()) {
                Transaction transaction = new Transaction();

                if(nPosChange > 0) {
                    String strChange = m.group(nPosChange);
                    transaction.setAmountChange(getIntValueFrom(strChange));
                } else {
                    transaction.setAmountChange(0);
                }

                if(nPosWords > 0) {
                    String strIdentifier = m.group(nPosWords);
                    transaction.setIdentifier(strIdentifier);
                } else {
                    transaction.setIdentifier("");
                }

                if(nPosBalance > 0) {
                    String strBalance = m.group(nPosBalance);
                    transaction.setAmountBalance(getIntValueFrom(strBalance));
                } else {
                    transaction.setAmountBalance(-1);
                }

                if(strMode.equals(Constant.JSON_INCOME)) {
                    transaction.setMode(1);
                } else if(strMode.equals(Constant.JSON_SPENDING)) {
                    transaction.setMode(2);
                } else {
                    transaction.setMode(0);
                }

                Log.e(Constant.TAG_CURRENT, "success on the parse sms with template 0");

                return transaction;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    int getIntValueFrom(String strVal) {
        strVal = strVal.replace(",", "");
        double d = 0;

        d = Double.parseDouble(strVal);

        return (int)d;
    }


    public void addNewTransactions() {
        for(int i = 0; i < listNewTransactions.size(); i ++) {
            Transaction trans = listNewTransactions.get(i);
            int nTransId = dbHelper.createTransaction(trans);

            trans.setId(nTransId);
            listAllTransactions.add(0, trans);
            Log.e(Constant.TAG_CURRENT, "added transaction to the listAllTransactions");
        }

        Collections.sort(listAllTransactions, new TransactionComparator());
    }

    public void calculateBalance() {
        int nVal = bankActive.getBalance();

        for(int i = 0; i < listNewTransactions.size() ; i ++) {
            Transaction trans = listNewTransactions.get(i);

            if(trans.getMode() == 1) nVal += trans.getAmountChange();
            if(trans.getMode() == 2) nVal -= trans.getAmountChange();

            if(trans.getAmountBalance() >= 0) {
                nVal = trans.getAmountBalance();
            }
        }

        bankActive.setBalance(nVal);

        dbHelper.updateBank(bankActive);
    }

    public List<Payment> getCurrentPayments() {
        List<Payment> listAns = new ArrayList<>();

        Collections.sort(listAllPayments, new PaymentComparator());

        for(int i = 0; i < listAllPayments.size(); i ++) {
            Payment payment = listAllPayments.get(i);
            long timestampAns = payment.getPaymentTimstampInCurrentPeriod();

            if(timestampAns > 0) {
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
        int nRealIncome = nMonthlyIncome;
        if(timestampInitConfig > getTimestampCurrentPeriodStart()) {
            nRealIncome -= UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_INIT_USED_MONEY, 0);
        }

        int nSumOfPaymentsForMonth = sumOfPaymentsForMonth();
        int nSumOfWishesForMonth = sumOfWishesForMonth();
        return nRealIncome - nSumOfPaymentsForMonth - nSumOfWishesForMonth;
    }

    public int weeklyLimit() {
        Calendar c = Calendar.getInstance();
        int nTotalDaysOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
//        int nTotalDaysOfMonth = daysLeftForThisPeriod();
//        if(nTotalDaysOfMonth < 7) nTotalDaysOfMonth = 7;

        return monthlyLimit() * 7 / nTotalDaysOfMonth;
    }

    public int sumOfTransactionThisWeek() { // for Transaction.mode = 2
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

            if(trans.getMode() < 2) continue;
            if(trans.getPaidId() > -1) continue;
            if(trans.getTimestampCreated() >= nHigh) continue;
            if(trans.getTimestampCreated() < nLow) break;

            nAns += trans.getAmountChange();
        }

        return nAns;
    }

    public int leftMoneyForMonth(long timestampSalaryDate) { // timestampSalaryDate - End Of Period
        return sumOfIncomeTransactionsForMonth(timestampSalaryDate) - sumOfSpendingTransactionForMonth(timestampSalaryDate);
    }


    public int freeOnThisWeek() {
        int nWeeklyLimit = weeklyLimit();
        int nSumOfTransactionThisWeek = sumOfTransactionThisWeek();
        return nWeeklyLimit - nSumOfTransactionThisWeek;
    }


    public int upcomingPayments() { // sum of unpaid payments during this period.
        int nAns = 0;

        List<Payment> listCurrentPayments = getCurrentPayments();

        for(int i = 0; i < listCurrentPayments.size(); i ++) {
            Payment payment = listCurrentPayments.get(i);

            if(payment.getPaidStatus() == 0) {
                nAns += payment.getAmount();
            }
        }

        return nAns;
    }


    public int freeOnThisMonth() {
        long timestampCurrentPeriodEnd = getTimestampCurrentPeriodEnd();

        int nMonthlyLimit = monthlyLimit();
        int nSumOfBGroupTransactionForMonth = sumOfBGrupTransactionForMonth(timestampCurrentPeriodEnd);

        return nMonthlyLimit - nSumOfBGroupTransactionForMonth;
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

    public int daysLeftForThisPeriod() {
        Calendar c = Calendar.getInstance();
        int nDay = c.get(Calendar.DAY_OF_MONTH);

        if(nDay >= nSalaryDate) {
            int nTotalDaysOfMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            return nSalaryDate + nTotalDaysOfMonth - nDay;
        }

        return nSalaryDate - nDay;
    }

    public int totalWishes() { // for checking the advisor
        return sumOfWishesForMonth();
    }

    public int checkAdvisorStatus(int nAskingAmount) {
        int nStatus = 0;

        if (freeOnThisWeek() - daysLeftForThisWeek() * nMinimalDayAmount >= nAskingAmount) {
            nStatus = 0;
        } else if (freeOnThisWeek() >= nAskingAmount){
            nStatus = 1;
        } else if (freeOnThisMonth() >= nAskingAmount) {
            nStatus = 2;
        } else if(freeOnThisMonth() + totalWishes() >= nAskingAmount) {
            nStatus = 3;
        } else if(balanceOfActiveBank() >= nAskingAmount) {
            nStatus = 4;
        } else {
            nStatus = 5;
        }

        return nStatus;
    }

    public void readBankJsonData(Context mContext) {
        InputStream is = mContext.getResources().openRawResource(R.raw.bank_json);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();

        try {
            jsonArrayBankInfo = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void readTemplateJsonData(Context mContext) {
        InputStream is = mContext.getResources().openRawResource(R.raw.template_json);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();

        try {
            jsonArrayTemplates = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // Fill all transactions
    public  void fillAllTransactions(){
        for (Sms sms : listSms)
        {
            for (int j = 0; j < jsonArrayBankInfo.length(); j++) {
                String strAddress = "";
                String strAccountName = "";
                try {
                    JSONObject bank = jsonArrayBankInfo.getJSONObject(j);
                    strAccountName = bank.getString(Constant.JSON_NAME);
                    strAddress = bank.getString(Constant.JSON_ADDRESS);
                }
                catch (Exception ex)
                {
                    Log.e(Constant.TAG_CURRENT, "error get bank address");
                }
                if(!strAddress.toLowerCase().equals(sms.getAddress().toLowerCase())) {
                    continue;
                }
                for (int i = 0; i < jsonArrayTemplates.length(); i++) {
                    Transaction transaction = null;

                    try {
                        JSONObject jsonTemplate = jsonArrayTemplates.getJSONObject(i);
                        //check that template is for the right bank
                        String strBankAddress = jsonTemplate.getString(Constant.JSON_ADDRESS);
                        if (!strAddress.toLowerCase().equals(strBankAddress.toLowerCase()))
                            continue;
                        //process template
                        transaction = parseTextUsingTempalte(sms.getText(), jsonTemplate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (transaction == null) continue;

                    Log.i(Constant.TAG_CURRENT, "success on parsing sms in the parseSmsUsingRegex");

                    transaction.setPaidId(-1);
                    transaction.setAccountName(strAccountName);
                    transaction.setBankId(j);
                    transaction.setText(sms.getText());
                    transaction.setTimestampCreated(sms.getTimestamp());

                    listAllTransactions.add(transaction);
                }
            }
        }
        Collections.sort(listAllTransactions, new TransactionComparator());
    }
}
