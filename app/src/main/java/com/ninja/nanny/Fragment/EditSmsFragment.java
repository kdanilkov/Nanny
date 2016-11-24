package com.ninja.nanny.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ninja.nanny.Adapter.CustomSpinnerAdapter;
import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class EditSmsFragment extends CustomFragment {


    public EditSmsFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;

    TextView tvDateTime;
    EditText etTitle, etText;
    DatePicker datePicker;
    TimePicker timePicker;
    public Sms smsSelected;
    String[] arrPatternTypes, arrPatternTexts;

    int nYear, nMonth, nDay, nHour, nMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_sms, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        initData();
        setUI();

        return mView;
    }

    void initData() {
        arrPatternTypes = getResources().getStringArray(R.array.sms_pattern_type);
        arrPatternTexts = getResources().getStringArray(R.array.sms_pattern_text);
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);
        mView.findViewById(R.id.btnDelete).setOnClickListener(this);
        mView.findViewById(R.id.btnCopy).setOnClickListener(this);

        etTitle = (EditText)mView.findViewById(R.id.etTitle);
        etText = (EditText)mView.findViewById(R.id.etText);
        tvDateTime = (TextView)mView.findViewById(R.id.tvDateTime);
        timePicker = (TimePicker)mView.findViewById(R.id.timePicker);
        datePicker = (DatePicker)mView.findViewById(R.id.datePicker);

        etTitle.setText(smsSelected.getAddress());
        etText.setText(smsSelected.getText());

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(smsSelected.getTimestamp());

        nYear = c.get(Calendar.YEAR);
        nMonth = c.get(Calendar.MONTH);
        nDay = c.get(Calendar.DAY_OF_MONTH);
        nHour = c.get(Calendar.HOUR_OF_DAY);
        nMinute = c.get(Calendar.MINUTE);

        datePicker.init(nYear, nMonth, nDay, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                nYear = i;
                nMonth = i1;
                nDay = i2;

                tvDateTime.setText("(" + nYear + "-" + (nMonth + 1) + "-" + nDay + ") " + nHour + ":" + nMinute);
            }
        });

        timePicker.setCurrentHour(nHour);
        timePicker.setCurrentMinute(nMinute);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                nHour = i;
                nMinute = i1;

                tvDateTime.setText("(" + nYear + "-" + (nMonth + 1) + "-" + nDay + ") " + nHour + ":" + nMinute);
            }
        });

        mView.findViewById(R.id.lyContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
            }
        });
    }

    void showDialogForPattern() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        // ...Irrelevant code for customizing the buttons and title
        View dialogView = mInflater.inflate(R.layout.dialog_sms_pattern, null);
        dialogBuilder.setView(dialogView);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        final EditText etTextDlg = (EditText)dialogView.findViewById(R.id.etText);

        etTextDlg.setText(arrPatternTexts[0]);

        final Spinner spinnerType = (Spinner)dialogView.findViewById(R.id.spinnerType);
        List<String> myTypeList = Arrays.asList(arrPatternTypes);
        ArrayList<String> myTypeArrayList = new ArrayList<String>(myTypeList);

        CustomSpinnerAdapter spinnerAdapterType = new CustomSpinnerAdapter(mContext,myTypeArrayList);
        spinnerType.setAdapter(spinnerAdapterType);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etTextDlg.setText(arrPatternTexts[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialogView.findViewById(R.id.btnSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etText.setText(etTextDlg.getText().toString());
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

    void saveSms() {
        String strTitle = etTitle.getText().toString();
        String strText = etText.getText().toString();
        String strDateTime = tvDateTime.getText().toString();

        if(strTitle.length() == 0) {
            etTitle.setError(Html.fromHtml("<font color='red'>please input the title</font>"));
            return;
        }

        if(strText.length() == 0) {
            etText.setError(Html.fromHtml("<font color='red'>please input the sms text</font>"));
            return;
        }

        if(strDateTime.length() == 0) {
            Toast.makeText(mContext, "pls set time and date", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.YEAR, nYear);
        cal.set(Calendar.MONTH, nMonth);
        cal.set(Calendar.DAY_OF_MONTH, nDay);
        cal.set(Calendar.HOUR_OF_DAY, nHour);
        cal.set(Calendar.MINUTE, nMinute);

        smsSelected.setAddress(strTitle);
        smsSelected.setText(strText);
        smsSelected.setTimestamp(cal.getTimeInMillis());

        Common.getInstance().dbHelper.updateSms(smsSelected);

        Toast.makeText(mContext, "sms has been updated successfully", Toast.LENGTH_SHORT).show();
        mContext.getSupportFragmentManager().popBackStackImmediate();
    }

    void removeSms() {
        new AlertDialog.Builder(mContext)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Common.getInstance().dbHelper.deleteBank(smsSelected.getId());

                        int nIdx = 0;

                        for(nIdx = 0; nIdx < Common.getInstance().listSms.size(); nIdx ++) {
                            Sms smsTmp = Common.getInstance().listSms.get(nIdx);

                            if(smsTmp.getId() == smsSelected.getId()) break;
                        }

                        Common.getInstance().dbHelper.deleteSms(smsSelected.getId());
                        Common.getInstance().listAllWishes.remove(nIdx);

                        mContext.getSupportFragmentManager().popBackStackImmediate();
                        Toast.makeText(mContext, "sms info has been deleted successfully", Toast.LENGTH_SHORT).show();
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
        imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);

        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnSave:
                saveSms();
                break;
            case R.id.btnDelete:
                removeSms();
                break;
            case R.id.btnCopy:
                showDialogForPattern();
                break;
        }
    }

}
