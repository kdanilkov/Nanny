package com.ninja.nanny.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;


public class EditPaymentFragment extends CustomFragment {


    public EditPaymentFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    TextView tvAmount;
    EditText etTitle, etAmount, etDateofMonth, etDetail;
    Button btnCheckRecurrent, btnCheckSaving;
    boolean isRecurrent, isSaving;
    public Payment paymentSelected;
    public boolean isSavingSet;
    public int nFixedAmountSaving;
    public int nPercentAmountSaving;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_payment, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {

        etTitle = (EditText)mView.findViewById(R.id.etTitle);
        etDetail = (EditText)mView.findViewById(R.id.etDetail);
        etAmount = (EditText)mView.findViewById(R.id.etAmount);
        etDateofMonth = (EditText)mView.findViewById(R.id.etDateOfMonth);
        btnCheckRecurrent = (Button)mView.findViewById(R.id.btnChkBoxRecurrent);
        btnCheckSaving = (Button)mView.findViewById(R.id.btnChkBoxSaving);
        tvAmount = (TextView)mView.findViewById(R.id.tvAmount);

        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);
        mView.findViewById(R.id.btnDelete).setOnClickListener(this);

        btnCheckRecurrent.setOnClickListener(this);
        btnCheckSaving.setOnClickListener(this);

        etTitle.setText(paymentSelected.getTitle());
        etDetail.setText(paymentSelected.getDetail());
        etAmount.setText(paymentSelected.getAmount() + "");
        etDateofMonth.setText(paymentSelected.getDateOfMonth() + "");

        int nPaymentMode = paymentSelected.getPaymentMode();

        switch (nPaymentMode) {
            case 0:
                isSaving = true;
                isRecurrent = true;
                break;
            case 1:
                isSaving = true;
                isRecurrent = false;
                break;
            case 2:
                isSaving = false;
                isRecurrent = true;
                break;
            case 3:
                isSaving = false;
                isRecurrent = false;
                break;
        }

        if(isSaving) {
            btnCheckSaving.setBackgroundResource(R.drawable.ic_checked);
        } else {
            btnCheckSaving.setBackgroundResource(R.drawable.ic_unchecked);
        }

        if(isRecurrent) {
            btnCheckRecurrent.setBackgroundResource(R.drawable.ic_checked);
        } else {
            btnCheckRecurrent.setBackgroundResource(R.drawable.ic_unchecked);
        }

        if(isSaving) {
            isSavingSet = true;
            if(paymentSelected.getSavingMode() == 0) {
                nFixedAmountSaving = paymentSelected.getAmount();
                nPercentAmountSaving = 0;
            } else {
                nFixedAmountSaving = 0;
                nPercentAmountSaving = paymentSelected.getAmount();
            }
        } else {
            isSavingSet = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presentSavingData();
    }

    void presentSavingData() {
        if(isSavingSet) {
            etAmount.setEnabled(false);

            if(nPercentAmountSaving == 0) {
                tvAmount.setText("Amount ($)");
                etAmount.setText(nFixedAmountSaving + "");
            } else {
                tvAmount.setText("Amount (%)");
                etAmount.setText(nPercentAmountSaving + "");
            }

        } else {
            tvAmount.setText("Amount ($)");
            etAmount.setEnabled(true);
        }
    }

    void saveNewPayment() {
        String strTitle = etTitle.getText().toString();
        String strDetail = etDetail.getText().toString();
        String strAmount = etAmount.getText().toString();
        String strDateOfMonth = etDateofMonth.getText().toString();

        if(strTitle.length() == 0) {
            etTitle.setError(Html.fromHtml("<font color='red'>please input the title</font>"));
            return;
        }

        if(strDetail.length() == 0) {
            etDetail.setError(Html.fromHtml("<font color='red'>please input the detail</font>"));
            return;
        }

        if(strAmount.length() == 0) {
            etAmount.setError(Html.fromHtml("<font color='red'>please input the amount value</font>"));
            return;
        }

        if(strAmount.length() > 8) {
            etAmount.setError(Html.fromHtml("<font color='red'>amount value is too large</font>"));
            return;
        }

        if(strDateOfMonth.length() == 0) {
            etDateofMonth.setError(Html.fromHtml("<font color='red'>please input the date of month</font>"));
            return;
        }

        int nDateOfMonth = Integer.valueOf(strDateOfMonth);

        if(nDateOfMonth > 31) {
            etDateofMonth.setError(Html.fromHtml("<font color='red'>please input the value between 1 and 31</font>"));
            return;
        }

        int nAmount = Integer.valueOf(strAmount);

        if(nAmount == 0) {
            etAmount.setError(Html.fromHtml("<font color='red'>please input the amount value</font>"));
            return;
        }

        int nPaymentMode = 0;

        if(isRecurrent) {
            nPaymentMode = isSaving ? 0 : 2;
        } else {
            nPaymentMode = isSaving? 1 : 3;
        }

        int nSavingMode = 0;
        if(nFixedAmountSaving == 0) nSavingMode = 1;
        if(!isSaving) nSavingMode = 0;

        paymentSelected.setTitle(strTitle);
        paymentSelected.setDetail(strDetail);
        paymentSelected.setAmount(nAmount);
        paymentSelected.setDateOfMonth(nDateOfMonth);
        paymentSelected.setPaymentMode(nPaymentMode);
        paymentSelected.setSavingMode(nSavingMode);
        paymentSelected.setTimestampCreated(Common.getInstance().dbHelper.getTimestamp());

        Common.getInstance().dbHelper.updatePayment(paymentSelected);

        Toast.makeText(mContext, "new payment info has been updated successfully", Toast.LENGTH_SHORT).show();
        mContext.getSupportFragmentManager().popBackStackImmediate();
    }

    void removeItem() {
        new AlertDialog.Builder(mContext)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Common.getInstance().dbHelper.deletePayment(paymentSelected.getId());
                        Common.getInstance().listAllPayments.remove(paymentSelected);

                        for(int i = 0; i < Common.getInstance().listCurrentPayments.size(); i ++) {
                            Payment paymentTmp = Common.getInstance().listCurrentPayments.get(i);

                            if(paymentTmp.getId() == paymentSelected.getId()){
                                Common.getInstance().listCurrentPayments.remove(i);
                                break;
                            }
                        }

                        mContext.getSupportFragmentManager().popBackStackImmediate();
                        Toast.makeText(mContext, "payment info has been deleted successfully", Toast.LENGTH_SHORT).show();
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

        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnSave:
//                CalendarPaymentFragment f = new CalendarPaymentFragment();
//                String title = Constant.FRAGMENT_CALENDAR_PAYMENT;
//
//                FragmentTransaction transaction = mContext.getSupportFragmentManager()
//                        .beginTransaction();
//                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                saveNewPayment();
                break;

            case R.id.btnDelete:
                removeItem();
                break;

            case R.id.btnChkBoxRecurrent:
                isRecurrent = !isRecurrent;
                if(isRecurrent) {
                    btnCheckRecurrent.setBackgroundResource(R.drawable.ic_checked);
                } else {
                    btnCheckRecurrent.setBackgroundResource(R.drawable.ic_unchecked);
                }

                //to hide it, call the method again
                imm.hideSoftInputFromWindow(etAmount.getWindowToken(), 0);

                break;

            case R.id.btnChkBoxSaving:
                isSaving = !isSaving;
                if(isSaving) {
                    btnCheckSaving.setBackgroundResource(R.drawable.ic_checked);

                    MonthlySavingFragment f = new MonthlySavingFragment();
                    String title = Constant.FRAGMENT_MONTHLY_SAVINGS;

                    FragmentTransaction transaction = mContext.getSupportFragmentManager()
                            .beginTransaction();
                    transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                } else {
                    btnCheckSaving.setBackgroundResource(R.drawable.ic_unchecked);
                    isSavingSet = false;
                    tvAmount.setText("Amount ($)");
                    etAmount.setEnabled(true);
                    etAmount.setText("0");
                }

                //to hide it, call the method again
                imm.hideSoftInputFromWindow(etAmount.getWindowToken(), 0);

                break;
        }
    }

}
