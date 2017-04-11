package com.moneynanny.nanny.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.moneynanny.nanny.AdvisorActivity;
import com.moneynanny.nanny.Custom.CustomFragment;
import com.moneynanny.nanny.MainActivity;
import com.moneynanny.nanny.Model.Wish;
import com.moneynanny.nanny.R;
import com.moneynanny.nanny.Utils.Common;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;


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
                String strTotalAmount = etTotalAmount.getText().toString();

                if(strTotalAmount.length() > 8) {
                    etTotalAmount.setError(Html.fromHtml("<font color='red'>amount value is too large</font>"));

                    tvMonthlyPayment.setText("");
                    tvPeriod.setText("");
                    return;
                }

                if(strTotalAmount.length() < 1) {
                    tvMonthlyPayment.setText("");
                    tvPeriod.setText("");
                    return;
                }

                int nTotalAmount = Integer.valueOf(strTotalAmount);

                if(nTotalAmount == 0) {
                    tvMonthlyPayment.setText("");
                    tvPeriod.setText("");
                    return;
                }

                int nMin = nTotalAmount / 36;
                int nMax = Common.getInstance().nMonthlyIncome;

                if(nMin > nMax) {
                    etTotalAmount.setError(Html.fromHtml("<font color='red'> Your income is not enough to save </font>"));

                    tvMonthlyPayment.setText("");
                    tvPeriod.setText("");
                    return;
                }

                if(nMin == 0) nMin = 1;
                if(nMax > nTotalAmount) nMax = nTotalAmount;

                int nPercent = seekbarPropotion.getProgress();
                int nMonthlyPayment = (nPercent * nMax + (100 - nPercent) * nMin) / 100;

                int nTotalMonths = (nTotalAmount + nMonthlyPayment -1) / nMonthlyPayment;

                tvMonthlyPayment.setText(nMonthlyPayment + " AED");
                tvPeriod.setText(nTotalMonths + " month");
            }
        });

        etTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etTitle.getText().toString();
                    if(strText.length() == 0) {
                        etTitle.setText("wish");
                    }
                }
            }
        });

        etTotalAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calcMonthlyPament();
            }
        });

        etTotalAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);

                    // NOTE: In the author's example, he uses an identifier
                    // called searchBar. If setting this code on your EditText
                    // then use v.getWindowToken() as a reference to your
                    // EditText is passed into this callback as a TextView

                    in.hideSoftInputFromWindow(etTotalAmount
                                    .getApplicationWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    // Must return true here to consume event
                    return true;

                }
                return false;
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

    void calcMonthlyPament() {
        String strTotalAmount = etTotalAmount.getText().toString();

        if(strTotalAmount.length() > 8) {
            etTotalAmount.setError(Html.fromHtml("<font color='red'>amount value is too large</font>"));
            return;
        }

        if(strTotalAmount.length() < 1) {
            return;
        }

        int nTotalAmount = Integer.valueOf(strTotalAmount);

        if(nTotalAmount == 0) return;

        int nMin = nTotalAmount / 36;
        int nMax = Common.getInstance().nMonthlyIncome;

        if(nMin > nMax) {
            etTotalAmount.setError(Html.fromHtml("<font color='red'> Your income is not enough to save </font>"));

            tvMonthlyPayment.setText("");
            tvPeriod.setText("");
            return;
        }

        if(nMin == 0) nMin = 1;
        if(nMax > nTotalAmount) nMax = nTotalAmount;

        int nMonthlyPayment = (nMin + nMax) / 2;
        int nTotalMonths = (nTotalAmount + nMonthlyPayment -1) / nMonthlyPayment;

        seekbarPropotion.setProgress(50);
        tvMonthlyPayment.setText(nMonthlyPayment + " AED");
        tvPeriod.setText(nTotalMonths + " month");
    }

    void saveWish() {
        String strTitle = etTitle.getText().toString();
        String strTotalAmount = etTotalAmount.getText().toString();
        String strMonthlyPament = tvMonthlyPayment.getText().toString();

        if(strTitle.length() == 0) {
            etTitle.setError(Html.fromHtml("<font color='red'>please input the title</font>"));
            return;
        }

        if(strTotalAmount.length() > 8){
            etTotalAmount.setError(Html.fromHtml("<font color='red'>amount value is too large</font>"));
            return;
        }

        if(strTotalAmount.length() < 1) {
            etTotalAmount.setError(Html.fromHtml("<font color='red'>amount value is empty</font>"));
            return;
        }

        int nTotalAmount = Integer.valueOf(strTotalAmount);

        if(nTotalAmount == 0) {
            etTotalAmount.setError(Html.fromHtml("<font color='red'>please input the amount value</font>"));
            return;
        }

        if(strMonthlyPament.length() == 0) {
            tvMonthlyPayment.setError(Html.fromHtml("<font color='red'>please set the monthly pament</font>"));
            return;
        }

        int nMonthlyPayment = Integer.valueOf(strMonthlyPament.substring(0, strMonthlyPament.length() -4));

        Wish wishNew = new Wish(strTitle, nTotalAmount, nMonthlyPayment, 0, Common.getInstance().getTimestamp(), -1, 1);
        int nID = Common.getInstance().dbHelper.createWish(wishNew);

        wishNew.setId(nID);

        Common.getInstance().listAllWishes.add(wishNew);
        Common.getInstance().listActiveWishes.add(wishNew);

        Toast.makeText(mContext, "new item info has been added successfully", Toast.LENGTH_SHORT).show();
        mContext.getSupportFragmentManager().popBackStackImmediate();
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
                saveWish();
                break;
            case R.id.btnAskAdviser:
                startActivity(new Intent(mContext, AdvisorActivity.class));
                break;
        }
    }

}
