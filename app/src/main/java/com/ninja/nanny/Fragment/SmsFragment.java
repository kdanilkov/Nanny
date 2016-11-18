package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Sms;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SmsFragment extends CustomFragment {


    public SmsFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    LinearLayout mLyContainer;
    final SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy, H:m");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_sms, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnAdd).setOnClickListener(this);

        mLyContainer = (LinearLayout)mView.findViewById(R.id.lyContainer);
    }

    @Override
    public void onResume() {
        super.onResume();
        presentData();
    }

    void presentData() {
        mLyContainer.removeAllViews();

        if(Common.getInstance().listSms.size() == 0) {
            Toast.makeText(mContext, "There is no sms to show", Toast.LENGTH_SHORT).show();
            return;
        }

        for(int i = 0; i < Common.getInstance().listSms.size(); i ++) {
            final Sms sms = Common.getInstance().listSms.get(i);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(sms.getTimestamp());
            Date date = cal.getTime();

            View cell = mInflater.inflate(R.layout.cell_transaction_item, null);

            ((TextView)cell.findViewById(R.id.tvDate)).setText(formatter.format(date));
            String strText = sms.getAddress() + "/" + sms.getText();
            if(strText.length() > 30) strText = strText.substring(0, 20);
            ((TextView)cell.findViewById(R.id.tvDetail)).setText(strText);

            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditSmsFragment f = new EditSmsFragment();
                    String title = Constant.FRAGMENT_EDIT_SMS;
                    f.smsSelected = sms;

                    FragmentTransaction transaction = mContext.getSupportFragmentManager()
                            .beginTransaction();
                    transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                }
            });

            mLyContainer.addView(cell);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.toggleMenu();
                break;

            case R.id.btnAdd:
                NewSmsFragment f = new NewSmsFragment();
                String title = Constant.FRAGMENT_NEW_SMS;

                FragmentTransaction transaction = mContext.getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                break;
        }
    }

}
