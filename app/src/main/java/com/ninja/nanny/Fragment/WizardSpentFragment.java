package com.ninja.nanny.Fragment;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.RegularEditText;
import com.ninja.nanny.Model.SettingWizardModel;
import com.ninja.nanny.Model.Transaction;
import com.ninja.nanny.Model.UsedAmount;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.Calendar;

/**
 * Created by petra on 12.03.2017.
 */

public class WizardSpentFragment extends BaseWizardFragment {
    private RegularEditText mSpendingsEdit;

    public WizardSpentFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_wizard_spent, container, false);
        mSpendingsEdit = ((RegularEditText)mView.findViewById((R.id.etSpendings)));
        initData();
        return mView;
    }

    @Override
    public boolean isValidate() {
        String text = mSpendingsEdit.getText().toString();
        if (text.isEmpty()) {
            mSpendingsEdit.setError(Html.fromHtml("<font color='red'>please input the balance</font>"));
            return false;
        }
        return true;
    }

    private void initData() {
        int salaryDate = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SALARY_DATE, Constant.DEFAULT_SALARY_DATE);
        int spendings = calculateSpendingsForCurrentPeriod(salaryDate);
        if (spendings == 0) {
            spendings = Common.getInstance().getUsedAmount(Common.getInstance().getTimestampCurrentPeriodEnd()).getUsedAmount();
        }
        mSpendingsEdit.setText(String.valueOf(spendings));
    }

    private int calculateSpendingsForCurrentPeriod(int salaryDate) {
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        if (currentDate.get(Calendar.DAY_OF_MONTH) < salaryDate) {
            month -= 1;
            if(month < 0) {
                month = 0;
                year -= 1;
            }
        }

        Calendar lastSalaryDate = Calendar.getInstance();
        lastSalaryDate.set(year, month, salaryDate);
        long periodStart = lastSalaryDate.getTimeInMillis();
        long periodEnd = currentDate.getTimeInMillis();
        int spendings = 0;
        int previousBalance = 0;
        for (int i = Common.getInstance().listAllTransactions.size() -1; i>=0; i--) {
            Transaction transaction = Common.getInstance().listAllTransactions.get(i);
            if (periodStart <= transaction.getTimestampCreated() && periodEnd >= transaction.getTimestampCreated()) {
                if (previousBalance == 0) {
                    previousBalance = transaction.getAmountBalance();
                    continue;
                } else {
                    int diff = transaction.getAmountBalance() - previousBalance;
                    if (diff < 0) {
                        spendings -= diff;
                    }
                }
                previousBalance = transaction.getAmountBalance();
            }
        }
        return spendings;
    }

    @Override
    public void setData() {
        try {
            int spendings = Integer.parseInt(mSpendingsEdit.getText().toString());
            Common.getInstance().updateUsedAmount(Common.getInstance().getTimestampCurrentPeriodEnd(), spendings);
            UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_INIT_USED_MONEY, spendings);
            Common.getInstance().updateTimestamp();
        } catch (Exception e) {
            Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
        }
    }
}
