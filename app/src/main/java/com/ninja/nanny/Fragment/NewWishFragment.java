package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.Model.WishSaving;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.Calendar;


public class NewWishFragment extends CustomFragment {


    public NewWishFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    EditText etTitle, etTotalAmount;
    TextView tvPeriod, tvMonthlyPayment;
    DiscreteSeekBar seekbarPropotion;
    int nLeftOnMonth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_new_wish, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnSave).setOnClickListener(this);
        mView.findViewById(R.id.btnAskAdviser).setOnClickListener(this);

        etTitle = (EditText)mView.findViewById(R.id.etTitle);
        etTotalAmount = (EditText)mView.findViewById(R.id.etAmount);
        tvPeriod = (TextView)mView.findViewById(R.id.tvTotalMonths);
        tvMonthlyPayment = (TextView)mView.findViewById(R.id.tvMonthlyPayment);
        seekbarPropotion = (DiscreteSeekBar)mView.findViewById(R.id.seekbarPropotion);

        seekbarPropotion.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
                //to hide it, call the method again
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etTotalAmount.getWindowToken(), 0);
            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                if(etTotalAmount.getText().toString().length() > 8) return;

                int nTotalAmount = Integer.valueOf(etTotalAmount.getText().toString());

                if(nTotalAmount == 0) return;

                int nMonthlyPayment = nLeftOnMonth * seekBar.getProgress() / 100;
                if(nMonthlyPayment == 0) nMonthlyPayment = 1;
                if(nMonthlyPayment > nTotalAmount) nMonthlyPayment = nTotalAmount;

                int nTotalMonths = (nTotalAmount + nMonthlyPayment -1) / nMonthlyPayment;

                tvMonthlyPayment.setText(nMonthlyPayment + " $");
                tvPeriod.setText(nTotalMonths + " month");
            }
        });

        etTotalAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    int nTotalAmount = Integer.valueOf(etTotalAmount.getText().toString());

                    if(nTotalAmount == 0) return;

                    int nMonthlyPayment = nLeftOnMonth * seekbarPropotion.getProgress() / 100;
                    if(nMonthlyPayment == 0) nMonthlyPayment = 1;
                    if(nMonthlyPayment > nTotalAmount) nMonthlyPayment = nTotalAmount;

                    int nTotalMonths = (nTotalAmount + nMonthlyPayment -1) / nMonthlyPayment;

                    tvMonthlyPayment.setText(nMonthlyPayment + " $");
                    tvPeriod.setText(nTotalMonths + " month");
                }
            }
        });

        nLeftOnMonth = Common.getInstance().leftOnThisMonth();

        if(nLeftOnMonth <= 0) {
            Toast.makeText(mContext, "There is no money left on this month!", Toast.LENGTH_SHORT).show();
            etTotalAmount.setEnabled(false);
            etTitle.setEnabled(false);
            seekbarPropotion.setEnabled(false);
            return;
        }

        mView.findViewById(R.id.lyContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etTitle.getWindowToken(), 0);
            }
        });
    }

    void saveWish() {
        if(nLeftOnMonth <= 0) {
            Toast.makeText(mContext, "There is no money left on this month!", Toast.LENGTH_SHORT).show();
            return;
        }

        String strTitle = etTitle.getText().toString();

        if(etTotalAmount.getText().toString().length() > 8){
            etTotalAmount.setError(Html.fromHtml("<font color='red'>amount value is too large</font>"));
            return;
        }

        int nTotalAmount = Integer.valueOf(etTotalAmount.getText().toString());

        if(strTitle.length() == 0) {
            etTitle.setError(Html.fromHtml("<font color='red'>please input the title</font>"));
            return;
        }

        if(nTotalAmount == 0) {
            etTotalAmount.setError(Html.fromHtml("<font color='red'>please input the amount value</font>"));
            return;
        }

        int nMonthlyPayment = nLeftOnMonth * seekbarPropotion.getProgress() / 100;
        if(nMonthlyPayment == 0) nMonthlyPayment = 1;
        if(nMonthlyPayment > nTotalAmount) nMonthlyPayment = nTotalAmount;

        Wish wishNew = new Wish(strTitle, nTotalAmount, nMonthlyPayment, 0, Common.getInstance().getTimestamp(), -1, 1);
        int nID = Common.getInstance().dbHelper.createWish(wishNew);

        wishNew.setId(nID);

        Calendar c = Calendar.getInstance();
        int nDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        int nMonth = c.get(Calendar.MONTH);
        int nYear = c.get(Calendar.YEAR);

        if(nDayOfMonth >= Common.getInstance().nSalaryDate) {
            nMonth ++;
            if(nMonth == 12) {
                nYear ++;
                nMonth = 0;
            }
        }

        int nDateSaving = nYear * 100 + nMonth;

        WishSaving wishSaving = new WishSaving(wishNew.getId(), wishNew.getMonthlyPayment(), nDateSaving);
        int nWishSavingId = Common.getInstance().dbHelper.createWishSaving(wishSaving);
        wishSaving.setId(nWishSavingId);

        wishNew.setLastSavingId(nWishSavingId);
        wishNew.setSavedAmount(wishNew.getMonthlyPayment());

        Common.getInstance().dbHelper.updateWish(wishNew);

        Common.getInstance().listAllWishes.add(wishNew);
        Common.getInstance().listActiveWishes.add(wishNew);

        Toast.makeText(mContext, "new wish info has been added successfully", Toast.LENGTH_SHORT).show();
        mContext.getSupportFragmentManager().popBackStackImmediate();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnSave:
                saveWish();
                break;
            case R.id.btnAskAdviser:
                AdviceFragment f = new AdviceFragment();
                String title = Constant.FRAGMENT_ADVICE;

                FragmentTransaction transaction = mContext.getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                break;
        }
    }

}
