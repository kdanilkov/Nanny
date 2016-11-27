package com.ninja.nanny.Fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Adapter.CustomSpinnerAdapter;
import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Payment;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewPaymentFragment extends CustomFragment {


    public NewPaymentFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    TextView tvAmount;
    EditText etTitle, etAmount, etDateofMonth, etDetail;
    Button btnCheckRecurrent, btnCheckSaving;
    boolean isRecurrent;
    boolean isSaving;
    String[] arrPatternNames, arrPatternWords, arrPatternDates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_new_payment, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        initData();
        setUI();

        return mView;
    }

    void initData() {
        arrPatternNames = getResources().getStringArray(R.array.payment_pattern_name);
        arrPatternWords = getResources().getStringArray(R.array.payment_pattern_words);
        arrPatternDates = getResources().getStringArray(R.array.payment_pattern_date);
    }

    void setUI() {
        tvAmount = (TextView)mView.findViewById(R.id.tvAmount);
        etTitle = (EditText)mView.findViewById(R.id.etTitle);
        etDetail = (EditText)mView.findViewById(R.id.etDetail);
        etAmount = (EditText)mView.findViewById(R.id.etAmount);
        etDateofMonth = (EditText)mView.findViewById(R.id.etDateOfMonth);
        btnCheckRecurrent = (Button)mView.findViewById(R.id.btnChkBoxRecurrent);
        btnCheckSaving = (Button)mView.findViewById(R.id.btnChkBoxSaving);

        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);
        mView.findViewById(R.id.btnPattern).setOnClickListener(this);

        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etTitle.getText().toString();

                    if(strText.length() == 0) {
                        etTitle.setText("payment");
                    }
                }
            }
        });

        etDetail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etDetail.getText().toString();

                    if(strText.length() == 0) {
                        etDetail.setText("identifier");
                    }
                }
            }
        });

        etAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etAmount.getText().toString();

                    if(strText.length() == 0) {
                        etAmount.setText("0");
                    }
                }
            }
        });

        etDateofMonth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etDateofMonth.getText().toString();

                    if(strText.length() == 0) {
                        etDateofMonth.setText("15");
                    }
                }
            }
        });

        btnCheckRecurrent.setOnClickListener(this);
        btnCheckSaving.setOnClickListener(this);

        isRecurrent = false;
        isSaving = false;

        mView.findViewById(R.id.lyContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
            }
        });
    }

    boolean isUniqueIdentifier(String strIdentifier) {
        List<Payment> listCurrentPayments = Common.getInstance().getCurrentPayments();

        for(int i = 0; i < listCurrentPayments.size(); i ++) {
            Payment payment = listCurrentPayments.get(i);

            if(strIdentifier.equals(payment.getIdentifier())) return false;
        }

        return true;
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

        if(!isUniqueIdentifier(strDetail)) {
            etDetail.setError(Html.fromHtml("<font color='red'>your word patterns has been used with other payments, pls use other</font>"));
            return;
        }

        int nPaymentMode = 0;

        if(isRecurrent) {
            nPaymentMode = isSaving ? 0 : 2;
        } else {
            nPaymentMode = isSaving? 1 : 3;
        }

        Payment paymentNew = new Payment(strTitle, strDetail, nAmount, nDateOfMonth, nPaymentMode, -1, Common.getInstance().getTimestamp()); // lastPaidId = -1 because it is new

        int payment_id = Common.getInstance().dbHelper.createPayment(paymentNew);

        paymentNew.setId(payment_id);

        Common.getInstance().listAllPayments.add(paymentNew);

        Toast.makeText(mContext, "new payment info has been added successfully", Toast.LENGTH_SHORT).show();
        mContext.getSupportFragmentManager().popBackStackImmediate();
    }

    void showDialogForPattern() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
// ...Irrelevant code for customizing the buttons and title
        View dialogView = mInflater.inflate(R.layout.dialog_payment_pattern, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final EditText etWords = (EditText)dialogView.findViewById(R.id.etWords);
        final EditText etDateOfMonthTmp = (EditText)dialogView.findViewById(R.id.etDateOfMonth);

        etWords.setText(arrPatternWords[0]);
        etDateOfMonthTmp.setText(arrPatternDates[0]);

        final Spinner spinnerName = (Spinner)dialogView.findViewById(R.id.spinnerName);
        List<String> myNameList = Arrays.asList(arrPatternNames);
        ArrayList<String> myNameArrayList = new ArrayList<String>(myNameList);

        CustomSpinnerAdapter spinnerAdapterName = new CustomSpinnerAdapter(mContext,myNameArrayList);
        spinnerName.setAdapter(spinnerAdapterName);
        spinnerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etWords.setText(arrPatternWords[position]);
                etDateOfMonthTmp.setText(arrPatternDates[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialogView.findViewById(R.id.btnSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etTitle.setText(spinnerName.getSelectedItem().toString());
                etDetail.setText(etWords.getText().toString());
                etDateofMonth.setText(etDateOfMonthTmp.getText().toString());

                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });




//        new AlertDialog.Builder(mContext)
//                .setTitle("Delete entry")
//                .setMessage("Are you sure you want to delete this entry?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // continue with delete
//                    }
//                })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do nothing
//                    }
//                })
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .show();
    }



    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);

        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnSave:
                saveNewPayment();
                break;

            case R.id.btnPattern:
                showDialogForPattern();
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

//                    MonthlySavingFragment f = new MonthlySavingFragment();
//                    String title = Constant.FRAGMENT_MONTHLY_SAVINGS;
//
//                    FragmentTransaction transaction = mContext.getSupportFragmentManager()
//                            .beginTransaction();
//                    transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                } else {
                    btnCheckSaving.setBackgroundResource(R.drawable.ic_unchecked);
//                    isSavingSet = false;
//                    tvAmount.setText("Amount ($)");
//                    etAmount.setEnabled(true);
//                    etAmount.setText("0");
                }

                break;
        }
    }

}
