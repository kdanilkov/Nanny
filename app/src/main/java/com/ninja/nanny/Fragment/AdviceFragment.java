package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdviceFragment extends CustomFragment {


    public AdviceFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    EditText etText;
    LinearLayout lyContainer;
    int nChatStatus, nFinanceStatus;
    String strShortAns, strLongAns;
    ScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_advice, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnHelp).setOnClickListener(this);
        mView.findViewById(R.id.btnMic).setOnClickListener(this);

        etText = (EditText)mView.findViewById(R.id.editText);
        lyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);
        scrollView = (ScrollView)mView.findViewById(R.id.scrollViewAdvisor);

        lyContainer.removeAllViews();

        nChatStatus = 0;
    }

    void sendMsg() {
        String strText = etText.getText().toString().trim();
        if(strText.length() == 0) return;

        if(nChatStatus == 0) {

            String regexStr = "^[0-9]*$";

            if(!strText.matches(regexStr))
            {
                Toast.makeText(mContext, "pls input the number value", Toast.LENGTH_SHORT).show();
                return;
            }

            if(strText.length() > 8) {
                Toast.makeText(mContext, "Input value is too large", Toast.LENGTH_SHORT).show();
                return;
            }

            int nVal = Integer.valueOf(strText);

            if(nVal == 0) return;

            View cellRight = mInflater.inflate(R.layout.cell_chat_right_gray, null);

            ((TextView)cellRight.findViewById(R.id.tvTitle)).setText(nVal + "$");

            lyContainer.addView(cellRight);

            nChatStatus = 1;

            nFinanceStatus = Common.getInstance().checkAdvisorStatus(nVal);

            switch (nFinanceStatus) {
                case 0:
                    strShortAns = "Perfect";
                    strLongAns  = "You can get this money with left on this week";
                    break;
                case 1:
                    strShortAns = "Perfect, but!";
                    strLongAns = "You can get ths money with left on this money by canceling the minmial amount per day during this week";
                    break;
                case 2:
                    strShortAns = "Perfect, but!!";
                    strLongAns = "You can get this money with left on this money with left on this month";
                    break;
                case 3:
                    strShortAns = "Perfect, but!!!";
                    strLongAns = "You can get this money with left on this month by canceling the minimal amount per day during this month ";
                    break;
                case 4:
                    strShortAns = "No!";
                    strLongAns = "You can get this money with left on this month and total wishes for this month";
                    break;
                case 5:
                    strShortAns = "No!!";
                    strLongAns = "You can get this money with left on this month, total wishes and total savings for this month";
                    break;
                case  6:
                    strShortAns = "No!!!";
                    strLongAns = "You can get this money with left on this month, total wishes, total savings and total bills for this month";
                    break;
                case 7:
                    strShortAns = "No!!!!";
                    strLongAns = "You can get this money with bank balance";
                    break;
                case 8:
                    strShortAns = "No!!!!!!!";
                    strLongAns = "You can not get this money";
                    break;
            }

            View cellLeft = mInflater.inflate(R.layout.cell_chat_left_blue, null);

            ((ImageView)cellLeft.findViewById(R.id.imgvTop)).setBackgroundResource(R.drawable.bg_chat_left_top_blue);
            ((ImageView)cellLeft.findViewById(R.id.imgvMiddle)).setBackgroundResource(R.drawable.bg_chat_left_middle_blue);
            ((ImageView)cellLeft.findViewById(R.id.imgvBottom)).setBackgroundResource(R.drawable.bg_chat_left_bottom_blue);

            ((TextView)cellLeft.findViewById(R.id.tvText)).setText(strShortAns + ", Details?");

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");
            Date date = new Date();

            ((TextView)cellLeft.findViewById(R.id.tvDate)).setText(formatter.format(date));
            ((TextView)cellLeft.findViewById(R.id.tvDetail)).setText("");

            lyContainer.addView(cellLeft);

        } else if(nChatStatus == 1) {

            if(strText.toLowerCase().equals("yes")) {
                View cell = mInflater.inflate(R.layout.cell_chat_right_gray, null);

                ((TextView)cell.findViewById(R.id.tvTitle)).setText(strText);

                lyContainer.addView(cell);

                nChatStatus = 0;

                View cellLeft = mInflater.inflate(R.layout.cell_chat_left_yellow, null);

                ((ImageView)cellLeft.findViewById(R.id.imgvTop)).setBackgroundResource(R.drawable.bg_chat_left_top_yellow);
                ((ImageView)cellLeft.findViewById(R.id.imgvMiddle)).setBackgroundResource(R.drawable.bg_chat_left_middle_yellow);
                ((ImageView)cellLeft.findViewById(R.id.imgvBottom)).setBackgroundResource(R.drawable.bg_chat_left_bottom_yellow);

                ((TextView)cellLeft.findViewById(R.id.tvText)).setText(strLongAns);
                ((TextView)cellLeft.findViewById(R.id.tvDate)).setText("");
                ((TextView)cellLeft.findViewById(R.id.tvDetail)).setText("");

                lyContainer.addView(cellLeft);

            } else if(strText.toLowerCase().equals("no")) {
                View cell = mInflater.inflate(R.layout.cell_chat_right_gray, null);

                ((TextView)cell.findViewById(R.id.tvTitle)).setText(strText);

                lyContainer.addView(cell);

                nChatStatus = 0;
            } else {
                Toast.makeText(mContext, "Please input the Yes/No", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        etText.setText("");
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
            case R.id.btnHelp:
                break;
            case R.id.btnMic:
                sendMsg();
                break;
        }
    }
}
