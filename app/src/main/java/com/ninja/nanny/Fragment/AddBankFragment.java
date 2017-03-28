package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ninja.nanny.Adapter.CustomSpinnerAdapter;
import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AddBankFragment extends CustomFragment {


    public AddBankFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    EditText etAccountName, etBalance;
    Spinner spinnerBank;
    ArrayList<String> myBankArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_add_bank, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        initData();
        setUI();

        return mView;
    }

    void initData() {
        myBankArrayList = new ArrayList<String>();

        for(int i = 0; i < Common.getInstance().jsonArrayBankInfo.length(); i ++) {
            try {
                JSONObject jsonObject = Common.getInstance().jsonArrayBankInfo.getJSONObject(i);
                myBankArrayList.add(jsonObject.getString(Constant.JSON_NAME));
            } catch (JSONException e) {
                Log.e(Constant.TAG_CURRENT, Log.getStackTraceString(e));
            }
        }
    }

    void setUI() {
        etAccountName = (EditText)mView.findViewById(R.id.etAccountName);
        spinnerBank = (Spinner)mView.findViewById(R.id.spinnerBank);
        etBalance = (EditText) mView.findViewById(R.id.etBalance);

        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);

        CustomSpinnerAdapter spinnerAdapterBank = new CustomSpinnerAdapter(mContext,myBankArrayList);
        spinnerBank.setAdapter(spinnerAdapterBank);

        etAccountName.setText("Bank");

        etAccountName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etAccountName.getText().toString();

                    if(strText.length() == 0) {
                        etAccountName.setText("Bank");
                    }
                }
            }
        });

        etBalance.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etBalance.getText().toString();

                    if(strText.length() == 0) {
                        etBalance.setText("0");
                    }
                }
            }
        });

        mView.findViewById(R.id.lyContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etAccountName.getWindowToken(), 0);
            }
        });

    }

    void saveBank() {
        String strAccountName = etAccountName.getText().toString();
        String strBalance = etBalance.getText().toString();

        if(strAccountName.length() == 0) {
            etAccountName.setError(Html.fromHtml("<font color='red'>Please input the account name</font>"));
            return;
        }

        if(strBalance.length() == 0) {
            etBalance.setError(Html.fromHtml("<font color='red'>Please input the balance value</font>"));
            return;
        }

        int nBalance = Integer.valueOf(strBalance);

        int nFlagActive = 0;
        int nIdxKind = spinnerBank.getSelectedItemPosition();

        if(Common.getInstance().listBanks.size() == 0) nFlagActive = 1;

        Bank bankNew = new Bank(strAccountName, nIdxKind, nBalance, nFlagActive, Common.getInstance().getTimestamp()); // activate the new bank info

        Common.getInstance().addBank(bankNew);

        Toast.makeText(mContext, "new bank info has been added successfully", Toast.LENGTH_SHORT).show();
        mContext.getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etAccountName.getWindowToken(), 0);

        switch (v.getId()) {
            case R.id.btnBack:
                if(!Common.getInstance().isActiveBankExist()) {
                    Toast.makeText(mContext, "You should set active bank info", Toast.LENGTH_SHORT).show();
                    break;
                }
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnSave:
                saveBank();
                break;
//            case R.id.btnSMS:
//                isSMS = true;
//                btnSMS.setBackgroundResource(R.drawable.ic_checked);
//                btnEmail.setBackgroundResource(R.drawable.ic_unchecked);
//                break;
//            case R.id.btnEmail:
//                isSMS = false;
//                btnSMS.setBackgroundResource(R.drawable.ic_unchecked);
//                btnEmail.setBackgroundResource(R.drawable.ic_checked);
//                break;
        }
    }

}
