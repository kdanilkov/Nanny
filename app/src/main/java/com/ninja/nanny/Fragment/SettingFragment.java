package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Constant;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class SettingFragment extends CustomFragment implements DiscreteSeekBar.OnProgressChangeListener {


    public SettingFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    TextView tvToleranceDays, tvTolerancePercent;
    EditText etMinimalAmountPerDay, etSalaryDate, etMonthlyIncome, etUsedSalary;
    DiscreteSeekBar seekBarDays, seekBarPercent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_setting, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnMenu).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);

        etMinimalAmountPerDay = (EditText)mView.findViewById(R.id.etMinimalAmountPerDay);
        etSalaryDate = (EditText)mView.findViewById(R.id.etSalaryDate);
        etMonthlyIncome = (EditText)mView.findViewById(R.id.etMonthlyIncome);
        etUsedSalary = (EditText)mView.findViewById(R.id.etUsedSalary);

        seekBarDays = (DiscreteSeekBar)mView.findViewById(R.id.seekbarDays);
        seekBarPercent = (DiscreteSeekBar)mView.findViewById(R.id.seekbarPercent);

        seekBarDays.setOnProgressChangeListener(this);
        seekBarPercent.setOnProgressChangeListener(this);

        tvToleranceDays = (TextView)mView.findViewById(R.id.tvToleranceDays);
        tvTolerancePercent = (TextView)mView.findViewById(R.id.tvTolerancePercent);

        int nMinimalAmountPerDay = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_MINIMAL_AMOUNT_PER_DAY, 0);
        int nSalaryDate = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_SALARY_DATE, 15);
        int nMonthlyIncome = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_MONTHLY_INCOME, 0);
        int nUsedSalary = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_USED_SALARY, 0);
        int nToleranceDays = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_TOLERANCE_DAYS, 2);
        int nTolerancePercent = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_TOLERANCE_PERCENT, 5);

        etMinimalAmountPerDay.setText(nMinimalAmountPerDay + "");
        etSalaryDate.setText(nSalaryDate + "");
        etMonthlyIncome.setText(nMonthlyIncome + "");
        etUsedSalary.setText(nUsedSalary + "");
        seekBarDays.setProgress(nToleranceDays);
        seekBarPercent.setProgress(nTolerancePercent);
        tvToleranceDays.setText(nToleranceDays + " DAYS");
        tvTolerancePercent.setText(nTolerancePercent + " %");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnMenu:
                mContext.toggleMenu();
                break;

            case R.id.btnSave:
                saveSettingInfo();
                break;
        }
    }

    void saveSettingInfo() {
        int nMinimalAmountPerDay = Integer.valueOf(etMinimalAmountPerDay.getText().toString());
        int nSalaryDate = Integer.valueOf(etSalaryDate.getText().toString());
        int nMonthlyIncome = Integer.valueOf(etMonthlyIncome.getText().toString());
        int nUsedSalary = Integer.valueOf(etUsedSalary.getText().toString());
        int nToleranceDays = seekBarDays.getProgress();
        int nTolerancePercent = seekBarPercent.getProgress();

        if(nSalaryDate == 0 || nSalaryDate > 31) {
            etSalaryDate.setError(Html.fromHtml("<font color='red'>please input the valid value between 1 and 31 for salary date</font>"));
            return;
        }

        if(nMonthlyIncome == 0) {
            etMonthlyIncome.setError(Html.fromHtml("<font color='red'>please input monthly income</font>"));
            return;
        }

        if(nMinimalAmountPerDay > nMonthlyIncome / 30) {
            etMinimalAmountPerDay.setError(Html.fromHtml("<font color='red'>minimal amount per day should be lower than one 30th of monthly income</font>"));
        }

        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_MINIMAL_AMOUNT_PER_DAY, nMinimalAmountPerDay);
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_SALARY_DATE, nSalaryDate);
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_MONTHLY_INCOME, nMonthlyIncome);
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_USED_SALARY, nUsedSalary);
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_TOLERANCE_DAYS, nToleranceDays);
        UserPreference.getInstance().putSharedPreference(Constant.PREF_KEY_TOLERANCE_PERCENT, nTolerancePercent);

        Toast.makeText(mContext, "Setting Info has been saved successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
    }

    @Override
    public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
        if(seekBar == seekBarDays) {
            tvToleranceDays.setText(seekBar.getProgress() + " DAYS");
        } else {
            tvTolerancePercent.setText(seekBar.getProgress() + " %");
        }
    }
}
