package com.ninja.nanny;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomActivity;
import com.ninja.nanny.Utils.Common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdvisorActivity extends CustomActivity {

    EditText etText;
    LinearLayout lyContainer;
    int nChatStatus, nFinanceStatus;
    String strShortAns, strLongAns;
    ScrollView scrollView;
    LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Do your operation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setContentView(R.layout.activity_advice);

        setUI();
    }

    void setUI() {
        mInflater = LayoutInflater.from(this);

        findViewById(R.id.btnBack).setOnClickListener(this);
        findViewById(R.id.btnHelp).setOnClickListener(this);
        findViewById(R.id.btnMic).setOnClickListener(this);

        etText = (EditText)findViewById(R.id.editText);
        lyContainer = (LinearLayout)findViewById(R.id.lyContainer);
        scrollView = (ScrollView)findViewById(R.id.scrollViewAdvisor);

        lyContainer.removeAllViews();

        nChatStatus = 0;

        etText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    sendMsg();
                    return true;
                }
                return false;
            }
        });

        firstOperation();
    }

    void firstOperation() {
        etText.setInputType(InputType.TYPE_CLASS_NUMBER);

        etText.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        View cellLeft = mInflater.inflate(R.layout.cell_chat_left_blue, null);

        cellLeft.findViewById(R.id.imgvTop).setBackgroundResource(R.drawable.bg_chat_left_top_blue);
        cellLeft.findViewById(R.id.imgvMiddle).setBackgroundResource(R.drawable.bg_chat_left_middle_blue);
        cellLeft.findViewById(R.id.imgvBottom).setBackgroundResource(R.drawable.bg_chat_left_bottom_blue);

        ((TextView)cellLeft.findViewById(R.id.tvText)).setText("Please enter the amount to check");

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");
        Date date = new Date();

        ((TextView)cellLeft.findViewById(R.id.tvDate)).setText(formatter.format(date));
        ((TextView)cellLeft.findViewById(R.id.tvDetail)).setText("");

        lyContainer.addView(cellLeft);
    }

    void sendMsg() {
        String strText = etText.getText().toString().trim();
        if(strText.length() == 0) return;

        if(nChatStatus == 0) {

            String regexStr = "^[0-9]*$";

            if(!strText.matches(regexStr))
            {
                Toast.makeText(this, "pls input the number value", Toast.LENGTH_SHORT).show();
                return;
            }

            if(strText.length() > 8) {
                Toast.makeText(this, "Input value is too large", Toast.LENGTH_SHORT).show();
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

            cellLeft.findViewById(R.id.imgvTop).setBackgroundResource(R.drawable.bg_chat_left_top_blue);
            cellLeft.findViewById(R.id.imgvMiddle).setBackgroundResource(R.drawable.bg_chat_left_middle_blue);
            cellLeft.findViewById(R.id.imgvBottom).setBackgroundResource(R.drawable.bg_chat_left_bottom_blue);

            ((TextView)cellLeft.findViewById(R.id.tvText)).setText(strShortAns + ", Details? \n Please input the Yes/No");

            SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");
            Date date = new Date();

            ((TextView)cellLeft.findViewById(R.id.tvDate)).setText(formatter.format(date));
            ((TextView)cellLeft.findViewById(R.id.tvDetail)).setText("");

            lyContainer.addView(cellLeft);

            etText.setInputType(InputType.TYPE_CLASS_TEXT);

        } else if(nChatStatus == 1) {

            if(strText.toLowerCase().equals("yes")) {
                View cell = mInflater.inflate(R.layout.cell_chat_right_gray, null);

                ((TextView)cell.findViewById(R.id.tvTitle)).setText(strText);

                lyContainer.addView(cell);

                nChatStatus = 0;

                View cellLeft = mInflater.inflate(R.layout.cell_chat_left_yellow, null);

                cellLeft.findViewById(R.id.imgvTop).setBackgroundResource(R.drawable.bg_chat_left_top_yellow);
                cellLeft.findViewById(R.id.imgvMiddle).setBackgroundResource(R.drawable.bg_chat_left_middle_yellow);
                cellLeft.findViewById(R.id.imgvBottom).setBackgroundResource(R.drawable.bg_chat_left_bottom_yellow);

                ((TextView)cellLeft.findViewById(R.id.tvText)).setText(strLongAns);
                ((TextView)cellLeft.findViewById(R.id.tvDate)).setText("");
                ((TextView)cellLeft.findViewById(R.id.tvDetail)).setText("");

                lyContainer.addView(cellLeft);

                firstOperation();

            } else if(strText.toLowerCase().equals("no")) {
                View cell = mInflater.inflate(R.layout.cell_chat_right_gray, null);

                ((TextView)cell.findViewById(R.id.tvTitle)).setText(strText);

                lyContainer.addView(cell);

                nChatStatus = 0;
            } else {
                Toast.makeText(this, "Please input the Yes/No", Toast.LENGTH_SHORT).show();
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
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etText.getWindowToken(), 0);

        switch (v.getId()) {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnHelp:
                break;
            case R.id.btnMic:
                sendMsg();
                break;
        }
    }
}
