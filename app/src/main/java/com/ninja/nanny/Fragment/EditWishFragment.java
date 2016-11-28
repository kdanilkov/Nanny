package com.ninja.nanny.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.AdvisorActivity;
import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;

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
    int nSavedAmount;

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
        nSavedAmount = wishSelected.getSavedAmount();
        int nTotalMonths = (nTotalAmount - nSavedAmount + nMonthlyPayment -1) / nMonthlyPayment;

        int nPercentage = 1;
        if(nTotalAmount > nSavedAmount) {
            nPercentage = nMonthlyPayment * 100 / (nTotalAmount - nSavedAmount);
        }

        if(nPercentage == 0) nPercentage = 1;
        if(nPercentage > 100) nPercentage = 100;

        etTotalAmount.setText(nTotalAmount + "");
        tvMonthlyPayment.setText(nMonthlyPayment + " $");
        tvPeriod.setText(nTotalMonths + " month");
        seekbarPropotion.setProgress(nPercentage);

        if(wishSelected.getFlagActive() == 0) {
            etTotalAmount.setEnabled(false);
            etTitle.setEnabled(false);
            seekbarPropotion.setEnabled(false);
            mView.findViewById(R.id.btnSave).setVisibility(View.INVISIBLE);
            mView.findViewById(R.id.btnAskAdviser).setVisibility(View.INVISIBLE);
        }

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

                if(nTotalAmount == 0 || nTotalAmount <= nSavedAmount) {
                    tvMonthlyPayment.setText("");
                    tvPeriod.setText("");
                    return;
                }

                int nMonthlyPayment = (nTotalAmount- nSavedAmount) * seekBar.getProgress() / 100;

                if(nMonthlyPayment == 0) {
                    nMonthlyPayment = 1;
                }

                int nTotalMonths = (nTotalAmount + nMonthlyPayment -1) / nMonthlyPayment;

                tvMonthlyPayment.setText(nMonthlyPayment + " $");
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

        etTotalAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    String strText = etTotalAmount.getText().toString();
                    if(strText.length() == 0) {
                        etTitle.setText("0");
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

        if(nTotalAmount == 0 || nTotalAmount <= nSavedAmount) return;

        int nMonthlyPayment = Common.getInstance().freeOnThisWeek();

        if(nMonthlyPayment == 0) {
            nMonthlyPayment = Common.getInstance().freeOnThisMonth();
        }

        if(nMonthlyPayment == 0) {
            nMonthlyPayment = nTotalAmount / 100;
        }

        if(nMonthlyPayment == 0) {
            nMonthlyPayment = 1;
        }

        nTotalAmount -= nSavedAmount;

        if(nMonthlyPayment > nTotalAmount) {
            nMonthlyPayment = nTotalAmount;
        }

        int nTotalMonths = (nTotalAmount + nMonthlyPayment -1) / nMonthlyPayment;
        int nPercent = nMonthlyPayment * 100 / nTotalAmount;

        if(nPercent == 0) {
            nPercent = 1;
        }

        seekbarPropotion.setProgress(nPercent);
        tvMonthlyPayment.setText(nMonthlyPayment + " $");
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

        if(nTotalAmount == 0 || nTotalAmount < nSavedAmount) {
            etTotalAmount.setError(Html.fromHtml("<font color='red'>please input the amount value bigger than saved amount</font>"));
            return;
        }

        if(nTotalAmount == nSavedAmount) {
            wishSelected.setTitle(strTitle);
            wishSelected.setTotalAmount(nTotalAmount);
            wishSelected.setFlagActive(0);
        } else {
            if(strMonthlyPament.length() == 0) {
                Toast.makeText(mContext, "please set the monthly pament", Toast.LENGTH_SHORT).show();
                return;
            }

            int nMonthlyPayment = Integer.valueOf(strMonthlyPament.substring(0, strMonthlyPament.length() -2));

            wishSelected.setTitle(strTitle);
            wishSelected.setTotalAmount(nTotalAmount);
            wishSelected.setMonthlyPayment(nMonthlyPayment);
        }

        Common.getInstance().dbHelper.updateWish(wishSelected);

        Common.getInstance().listAllWishes = Common.getInstance().dbHelper.getAllWishes();
        Common.getInstance().listActiveWishes = Common.getInstance().dbHelper.getActiveWishes();
        Common.getInstance().listFinishedWishes = Common.getInstance().dbHelper.getFinishedWishes();

        Toast.makeText(mContext, "wish info has been updated successfully", Toast.LENGTH_SHORT).show();
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
