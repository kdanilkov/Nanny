package com.moneynanny.nanny.Fragment;

import android.os.Bundle;
import android.provider.SyncStateContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moneynanny.nanny.Custom.RegularEditText;
import com.moneynanny.nanny.Model.Transaction;
import com.moneynanny.nanny.Preference.UserPreference;
import com.moneynanny.nanny.R;
import com.moneynanny.nanny.Utils.Common;
import com.moneynanny.nanny.Utils.Constant;

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
//        int salaryDate = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SALARY_DATE, Constant.DEFAULT_SALARY_DATE);
//        int income = calculateIncomeForLastPeriod(salaryDate);
        int income = Common.getInstance().sumOfIncomeTransactionsForMonth(Common.getInstance().getTimestampCurrentPeriodStart());
        mIncomeEdit.setText(String.valueOf(income));
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
            Common.getInstance().nMonthlyIncome = income;
//            Common.getInstance().updateTimestamp();
        } catch (Exception e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
    }
}
