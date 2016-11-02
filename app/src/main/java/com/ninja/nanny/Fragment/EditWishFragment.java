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
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


public class EditWishFragment extends CustomFragment {


    public EditWishFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    EditText etTitle, etTotalAmount;
    TextView tvPeriod, tvMonthlyPayment;
    DiscreteSeekBar seekbarPropotion;
    public Wish wishSelected;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_edit_wish, container, false);
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

        etTitle.setText(wishSelected.getTitle());

        int nTotalAmount = wishSelected.getTotalAmount();
        int nMonthlyPayment = wishSelected.getMonthlyPayment();
        int nPercentage = nMonthlyPayment * 100 / wishSelected.getTotalAmount();
        int nTotalMonths = (nTotalAmount + nMonthlyPayment -1) / nMonthlyPayment;

        etTotalAmount.setText(nTotalAmount + "");
        tvMonthlyPayment.setText(nMonthlyPayment + " $");
        tvPeriod.setText(nTotalMonths + " month");
        seekbarPropotion.setProgress(nPercentage);

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

                int nMonthlyPayment = nTotalAmount * seekBar.getProgress() / 100;
                if(nMonthlyPayment == 0) nMonthlyPayment = 1;

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

                    int nMonthlyPayment = nTotalAmount * seekbarPropotion.getProgress() / 100;
                    int nTotalMonths = (nTotalAmount + nMonthlyPayment -1) / nMonthlyPayment;

                    tvMonthlyPayment.setText(nMonthlyPayment + " $");
                    tvPeriod.setText(nTotalMonths + " month");
                }
            }
        });
    }

    void saveWish() {
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

        int nMonthlyPayment = nTotalAmount * seekbarPropotion.getProgress() / 100;

        wishSelected.setTitle(strTitle);
        wishSelected.setTotalAmount(nTotalAmount);
        wishSelected.setMonthlyPayment(nMonthlyPayment);
        wishSelected.setSavedAmount(nTotalAmount / 3);
        wishSelected.setUpdatedAt(Common.getInstance().dbHelper.getDateTime());

        Common.getInstance().dbHelper.updateWish(wishSelected);

        int nIdx = 0;

        for(nIdx = 0; nIdx < Common.getInstance().listAllWishes.size(); nIdx ++) {
            Wish wishTmp = Common.getInstance().listAllWishes.get(nIdx);

            if(wishSelected.getId() == wishTmp.getId()) break;
        }

        Common.getInstance().listAllWishes.remove(nIdx);
        Common.getInstance().listAllWishes.add(nIdx, wishSelected);

        if(wishSelected.getFlagActive() == 1) {
            for(nIdx = 0; nIdx < Common.getInstance().listActiveWishes.size(); nIdx ++){
                Wish wishTmp = Common.getInstance().listActiveWishes.get(nIdx);

                if(wishSelected.getId() == wishTmp.getId()) break;
            }

            Common.getInstance().listActiveWishes.remove(nIdx);
            Common.getInstance().listActiveWishes.add(nIdx, wishSelected);
        } else {
            for(nIdx = 0; nIdx < Common.getInstance().listFinishedWishes.size(); nIdx ++){
                Wish wishTmp = Common.getInstance().listFinishedWishes.get(nIdx);

                if(wishSelected.getId() == wishTmp.getId()) break;
            }

            Common.getInstance().listFinishedWishes.remove(nIdx);
            Common.getInstance().listFinishedWishes.add(nIdx, wishSelected);
        }


        Toast.makeText(mContext, "wish info has been updated successfully", Toast.LENGTH_SHORT).show();
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
