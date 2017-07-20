package com.moneynanny.nanny;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.moneynanny.nanny.Custom.CustomActivity;
import com.moneynanny.nanny.Utils.Common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AdvisorActivity extends CustomActivity {

    EditText etText;
    LinearLayout lyContainer;
    String strShortAns, strLongAns;
    ScrollView scrollView;
    LayoutInflater mInflater;
    String[] arrShortAns, arrLongAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Do your operation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        setContentView(R.layout.activity_advice);

        initData();
        attachKeyboardListeners();
        setUI();
    }

    void initData() {
        arrShortAns = getResources().getStringArray(R.array.advisor_short);
        arrLongAns = getResources().getStringArray(R.array.advisor_long);
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

        ((TextView)cellRight.findViewById(R.id.tvTitle)).setText(nVal + "BHD");

        lyContainer.addView(cellRight);

        int nFinanceStatus = Common.getInstance().checkAdvisorStatus(nVal);

        if(nFinanceStatus >= arrShortAns.length) {
            nFinanceStatus = arrShortAns.length - 1;
        }

        strShortAns = arrShortAns[nFinanceStatus];
        strLongAns = arrLongAns[nFinanceStatus];

        View cellLeft = mInflater.inflate(R.layout.cell_chat_left_blue, null);

        cellLeft.findViewById(R.id.imgvTop).setBackgroundResource(R.drawable.bg_chat_left_top_blue);
        cellLeft.findViewById(R.id.imgvMiddle).setBackgroundResource(R.drawable.bg_chat_left_middle_blue);
        cellLeft.findViewById(R.id.imgvBottom).setBackgroundResource(R.drawable.bg_chat_left_bottom_blue);

        String strAns = strShortAns;
        if(strLongAns.length() > 0) strAns += " \n" + strLongAns;

        ((TextView)cellLeft.findViewById(R.id.tvText)).setText(strAns);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM");
        Date date = new Date();

        ((TextView)cellLeft.findViewById(R.id.tvDate)).setText(formatter.format(date));
        ((TextView)cellLeft.findViewById(R.id.tvDetail)).setText("");

        lyContainer.addView(cellLeft);

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

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(AdvisorActivity.this);

            if(heightDiff <= contentViewTop){
                onHideKeyboard();

                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    protected void onShowKeyboard(int keyboardHeight) {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
    protected void onHideKeyboard() {
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout = (ViewGroup) findViewById(R.id.rootLayout);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }
}
