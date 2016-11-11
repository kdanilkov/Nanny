package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Preference.UserPreference;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Constant;


public class MonthlySavingFragment extends CustomFragment {


    public MonthlySavingFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    EditText etFixed, etPercent;
    Button btnFixed, btnPercent;
    ToggleButton tbtnSwitch;
    boolean isFixed;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_monthly_savings, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        etFixed = (EditText)mView.findViewById(R.id.etFixed);
        etPercent = (EditText)mView.findViewById(R.id.etPercent);
        btnFixed = (Button)mView.findViewById(R.id.btnFixed);
        btnPercent = (Button)mView.findViewById(R.id.btnPercent);
        tbtnSwitch = (ToggleButton)mView.findViewById(R.id.tbtnSwitch);

        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);
        btnFixed.setOnClickListener(this);
        btnPercent.setOnClickListener(this);

        isFixed = true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnSave:
                saveSavingInfo();
                break;

            case R.id.btnFixed:
                isFixed = true;
                btnFixed.setBackgroundResource(R.drawable.ic_radio_on);
                btnPercent.setBackgroundResource(R.drawable.ic_radio_off);
                break;

            case R.id.btnPercent:
                isFixed = false;
                btnFixed.setBackgroundResource(R.drawable.ic_radio_off);
                btnPercent.setBackgroundResource(R.drawable.ic_radio_on);
                break;
        }
    }

    void saveSavingInfo() {
        int nMonthlyIncome = UserPreference.getInstance().getSharedPreference(Constant.PREF_KEY_MONTHLY_INCOME, 0);
        int nFixed = 0;
        int nPercent = 0;

        if(isFixed) {
            String strFixed = etFixed.getText().toString();
            if(strFixed.length() == 0) {
                Toast.makeText(mContext, "Please input the amount for saving", Toast.LENGTH_SHORT).show();
                return;
            }

            nFixed = Integer.valueOf(strFixed);

            if(nFixed == 0) {
                Toast.makeText(mContext, "Please input the amount for saving", Toast.LENGTH_SHORT).show();
                return;
            }

            if(nFixed > nMonthlyIncome) {
                Toast.makeText(mContext, "Your monthly income is " + nMonthlyIncome + ", so you should input amount less than your monthly income", Toast.LENGTH_SHORT ).show();
                return;
            }

        } else {
            String strPercent = etPercent.getText().toString();
            if(strPercent.length() == 0) {
                Toast.makeText(mContext, "Please input the percent value for saving", Toast.LENGTH_SHORT).show();
                return;
            }

            nPercent = Integer.valueOf(strPercent);

            if(nPercent == 0) {
                Toast.makeText(mContext, "Please input the percent value for saving", Toast.LENGTH_SHORT).show();
                return;
            }

            if(nPercent > 100) {
                Toast.makeText(mContext, "Please input value less than 100 for percent", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        NewPaymentFragment fragmentNew = (NewPaymentFragment) mContext.getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_NEW_PAYMENT);
        if(fragmentNew != null) {
            fragmentNew.isSavingSet = true;
            fragmentNew.nFixedAmountSaving = nFixed;
            fragmentNew.nPercentAmountSaving = nPercent;
        }

        EditPaymentFragment fragmentEdit = (EditPaymentFragment) mContext.getSupportFragmentManager().findFragmentByTag(Constant.FRAGMENT_EDIT_PAYMENT);
        if(fragmentEdit != null) {
            fragmentEdit.isSavingSet = true;
            fragmentEdit.nFixedAmountSaving = nFixed;
            fragmentEdit.nPercentAmountSaving = nPercent;
        }

        mContext.getSupportFragmentManager().popBackStackImmediate();
    }

}
