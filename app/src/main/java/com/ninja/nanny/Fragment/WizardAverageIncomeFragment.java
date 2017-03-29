package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by petra on 12.03.2017.
 */

public class WizardAverageIncomeFragment extends BaseWizardFragment {

    private RegularEditText mIncomeEdit;

    public WizardAverageIncomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_average_income, container, false);
        mIncomeEdit = ((RegularEditText) mView.findViewById(R.id.etText));
        initData();
        return mView;
    }

    public void initData() {
        int salaryDate = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SALARY_DATE, Constant.DEFAULT_SALARY_DATE);
        int income = calculateIncomeForLastPeriod(salaryDate);
        mIncomeEdit.setText(String.valueOf(income));
    }

    private int calculateIncomeForLastPeriod(int salaryDate) {
        // todo: works differently in different cases.
        // Case 1:
        // currentdate: 12.02.2017
        // income day: 20
        // startPeriod: 20.01.2017
        // endPeriod: 20.02.2017
        // not a full month

        // Case 2:
        // currentdate: 12.02.2017
        // income day: 10
        // startPeriod: 10.01.2017
        // endPeriod: 10.02.2017
        // full month

        Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) - 1;
        if (month == -1) {
            month = 0;
            year -= 1;
        }
        // set period time range
        calendar.set(year, month, salaryDate, 0, 0, 0);
        long startPreviewRange = calendar.getTimeInMillis();
        calendar.set(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), salaryDate, 23, 59, 59);
        long endPreviewRange = calendar.getTimeInMillis();
        // get income in preview period
        int income = 0;
        int previousBalance = 0;
        for (int i = Common.getInstance().listAllTransactions.size() - 1; i >= 0; i--) {
            Transaction transaction = Common.getInstance().listAllTransactions.get(i);
            if (startPreviewRange <= transaction.getTimestampCreated() && endPreviewRange >= transaction.getTimestampCreated()) {
                if (previousBalance == 0) {
                    previousBalance = transaction.getAmountBalance();
                    continue;
                } else {
                    int diff = transaction.getAmountBalance() - previousBalance;
                    if (diff > 0) {
                        income += diff;
                    }
                }
                previousBalance = transaction.getAmountBalance();
            }
        }
        return income;
    }

    @Override
    public boolean isValidate() {
        String text = mIncomeEdit.getText().toString();
        if (text.isEmpty() || Integer.valueOf(text) == 0) {
            mIncomeEdit.setError(Html.fromHtml("<font color='red'>please input the income value</font>"));
            return false;
        }
        return true;
    }

    @Override
    public void setData() {
        try {
            int income = Integer.parseInt(mIncomeEdit.getText().toString());
            UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_MONTHLY_INCOME, income);
            Common.getInstance().updateTimestamp();
        } catch (Exception e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
    }
}
