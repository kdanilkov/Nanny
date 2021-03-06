package com.ninja.nanny.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
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


public class EditBankFragment extends CustomFragment {


    public EditBankFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    EditText etAccountName, etBalance;
    Spinner spinnerBank;
    ArrayList<String> myBankArrayList;
    public int nIndex;
    Bank bankItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_bank, container, false);
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
                e.printStackTrace();
            }
        }

        bankItem = Common.getInstance().listBanks.get(nIndex);
    }

    void setUI() {
        etAccountName = (EditText)mView.findViewById(R.id.etAccountName);
        spinnerBank = (Spinner)mView.findViewById(R.id.spinnerBank);
        etBalance = (EditText) mView.findViewById(R.id.etBalance);

        CustomSpinnerAdapter spinnerAdapterBank = new CustomSpinnerAdapter(mContext,myBankArrayList);
        spinnerBank.setAdapter(spinnerAdapterBank);

        etAccountName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etAccountName.getText().toString();

                    if(strText.length() == 0) {
                        etAccountName.setText("bank");
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

        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);
        mView.findViewById(R.id.btnDelete).setOnClickListener(this);

        etAccountName.setText(bankItem.getAccountName());
        etBalance.setText(bankItem.getBalance() + "");

        spinnerBank.setSelection(bankItem.getIdxKind());

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

        int nIdxKind = spinnerBank.getSelectedItemPosition();

        bankItem.setAccountName(strAccountName);
        bankItem.setIdxKind(nIdxKind);
        bankItem.setBalance(nBalance);
        bankItem.setTimestamp(Common.getInstance().getTimestamp());

        Common.getInstance().dbHelper.updateBank(bankItem);

        mContext.getSupportFragmentManager().popBackStackImmediate();
        Toast.makeText(mContext, "bank info has been saved successfully", Toast.LENGTH_SHORT).show();
    }

    void removeItem() {
        new AlertDialog.Builder(mContext)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Common.getInstance().dbHelper.deleteBank(bankItem.getId());
                        Common.getInstance().listBanks.remove(bankItem);

                        if(bankItem.getFlagActive() == 1 && Common.getInstance().listBanks.size() > 0) {
                            Bank bankFirst = Common.getInstance().listBanks.get(0);
                            bankFirst.setFlagActive(1);
                            Common.getInstance().bankActive = bankFirst;
                            Common.getInstance().dbHelper.updateBank(bankFirst);
                        }

                        Common.getInstance().listAllWishes = Common.getInstance().dbHelper.getAllWishes();

                        mContext.getSupportFragmentManager().popBackStackImmediate();
                        Toast.makeText(mContext, "bank info has been deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etAccountName.getWindowToken(), 0);

        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnSave:
                saveBank();
                break;
            case R.id.btnDelete:
                removeItem();
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
