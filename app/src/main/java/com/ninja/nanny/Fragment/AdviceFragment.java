package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;

public class AdviceFragment extends CustomFragment {


    public AdviceFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;

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

        View cellYellow = mView.findViewById(R.id.cellYellow);
        View cellBlue = mView.findViewById(R.id.cellBlue);
        View cellGreen = mView.findViewById(R.id.cellGreen);

        ((ImageView)cellYellow.findViewById(R.id.imgvTop)).setImageResource(R.drawable.bg_chat_left_top_yellow);
        ((ImageView)cellYellow.findViewById(R.id.imgvMiddle)).setImageResource(R.drawable.bg_chat_left_middle_yellow);
        ((ImageView)cellYellow.findViewById(R.id.imgvBottom)).setImageResource(R.drawable.bg_chat_left_bottom_yellow);

        ((ImageView)cellBlue.findViewById(R.id.imgvTop)).setImageResource(R.drawable.bg_chat_left_top_blue);
        ((ImageView)cellBlue.findViewById(R.id.imgvMiddle)).setImageResource(R.drawable.bg_chat_left_middle_blue);
        ((ImageView)cellBlue.findViewById(R.id.imgvBottom)).setImageResource(R.drawable.bg_chat_left_bottom_blue);

        ((ImageView)cellGreen.findViewById(R.id.imgvTop)).setImageResource(R.drawable.bg_chat_left_top_green);
        ((ImageView)cellGreen.findViewById(R.id.imgvMiddle)).setImageResource(R.drawable.bg_chat_left_middle_green);
        ((ImageView)cellGreen.findViewById(R.id.imgvBottom)).setImageResource(R.drawable.bg_chat_left_bottom_green);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;
        }
    }
}
