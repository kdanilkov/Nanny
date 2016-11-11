package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Adapter.CustomSpinnerAdapter;
import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Bank;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AddBankFragment extends CustomFragment {


    public AddBankFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    TextView tvAccountType;
    EditText etAccountName, etBalance;
    Button btnSMS, btnEmail;
    Spinner spinnerBank;
    ArrayList<String> myBankArrayList, myAccountTypeArrayList;
    boolean isSMS;

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
        String[] myBankArray = getResources().getStringArray(R.array.bank_array);
        List<String> myBankList = Arrays.asList(myBankArray);

        myBankArrayList = new ArrayList<String>(myBankList);

        String[] myAccountTypeArray = getResources().getStringArray(R.array.acccount_type_array);
        List<String> myAccountTypeList = Arrays.asList(myAccountTypeArray);

        myAccountTypeArrayList = new ArrayList<String>(myAccountTypeList);

        isSMS = true;
    }

    void setUI() {

        btnSMS = (Button)mView.findViewById(R.id.btnSMS);
        btnEmail = (Button)mView.findViewById(R.id.btnEmail);
        etAccountName = (EditText)mView.findViewById(R.id.etAccountName);
        spinnerBank = (Spinner)mView.findViewById(R.id.spinnerBank);
        tvAccountType = (TextView)mView.findViewById(R.id.tvAccountType);
        etBalance = (EditText) mView.findViewById(R.id.etBalance);

        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);
        btnSMS.setOnClickListener(this);
        btnEmail.setOnClickListener(this);

        CustomSpinnerAdapter spinnerAdapterBank = new CustomSpinnerAdapter(mContext,myBankArrayList);
        spinnerBank.setAdapter(spinnerAdapterBank);
        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();

                Toast.makeText(parent.getContext(), "selected bank is..." + item, Toast.LENGTH_LONG).show();
                tvAccountType.setText(myAccountTypeArrayList.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSMS.setBackgroundResource(R.drawable.ic_checked);
        btnEmail.setBackgroundResource(R.drawable.ic_unchecked);
    }

    void saveBank() {
        String strAccountName = etAccountName.getText().toString();
        String strBank = myBankArrayList.get(spinnerBank.getSelectedItemPosition());
        String strAccountType = myAccountTypeArrayList.get(spinnerBank.getSelectedItemPosition());
        String strBalance = etBalance.getText().toString();

        if(strAccountName.length() == 0) {
            Toast.makeText(mContext, "Please input the account name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strBalance.length() == 0) {
            Toast.makeText(mContext, "Please input the balance value", Toast.LENGTH_SHORT).show();
            return;
        }

        int nBalance = Integer.valueOf(strBalance);
        int nNotificationMode = 1;

        if(isSMS) nNotificationMode = 0;

        int nFlagActive = 0;

        if(Common.getInstance().listBanks.size() == 0) nFlagActive = 1;

        Bank bankNew = new Bank(strAccountName, strBank, strAccountType, nBalance, nNotificationMode, nFlagActive); // activate the new bank info
        int nID =  Common.getInstance().dbHelper.createBank(bankNew);

        bankNew.setId(nID);
        Common.getInstance().listBanks.add(bankNew);

        Toast.makeText(mContext, "new bank info has been added successfully", Toast.LENGTH_SHORT).show();
        mContext.getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
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
