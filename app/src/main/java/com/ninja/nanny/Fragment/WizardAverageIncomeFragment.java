package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.utils.Utils;
import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by petra on 12.03.2017.
 */

public class WizardAverageIncomeFragment extends BaseWizardFragment {
    public WizardAverageIncomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_average_income, container, false);
        initData();
        return mView;
    }

    public void initData() {
        int salaryDate = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SALARY_DATE, 15);
        Calendar currentCalendar = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) - 1;
        if(month == -1) {
            month = 0;
            year -= 1;
        }
        // set period income
        calendar.set(year, month, salaryDate);
        long startPreviewRage = calendar.getTimeInMillis();
        calendar.set(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), salaryDate);
        long endPreviewRage = calendar.getTimeInMillis();
        // get income in preview period
        int income = 0;
        int previewsBalance = 0;
        for (int i = Common.getInstance().listAllTransactions.size() -1;i>=0;i--)
        {
            Transaction transaction = Common.getInstance().listAllTransactions.get(i);
            if(transaction.getAccountName().equals(mModel.getBank().getAccountName())
                    && startPreviewRage<=transaction.getTimestampCreated()
                    && endPreviewRage >= transaction.getTimestampCreated())
            {
                if (previewsBalance == 0) {
                    previewsBalance = transaction.getAmountBalance();
                    continue;
                }
                else {
                    int diff = transaction.getAmountBalance() - previewsBalance;
                    if (diff > 0) {
                        income += diff;
                    }
                }
                previewsBalance = transaction.getAmountBalance();
            }
        }
        ((RegularEditText)mView.findViewById(R.id.etText)).setText(income + "");
    }

    @Override
    public boolean isValidate() {
        return true;
    }

    @Override
    public void setData() {

    }
}
