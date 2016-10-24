package com.ninja.nanny.Fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.FancyChart.ChartData;
import com.ninja.nanny.FancyChart.FancyChart;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.R;

import at.grabner.circleprogress.CircleProgressView;


public class ViewWishFragment extends CustomFragment {


    public ViewWishFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    CircleProgressView mCircleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_view_wish, container, false);
        mContext = (MainActivity)getActivity();
        mInflater = inflater;

        setUI();

        return mView;
    }

    void setUI() {
        mView.findViewById(R.id.btnBack).setOnClickListener(this);

        mCircleView = (CircleProgressView)mView.findViewById(R.id.circleView);
        mCircleView.setValue(62);

        FancyChart chart = (FancyChart)mView.findViewById(R.id.fancyChart);

        // First data set
        ChartData data = new ChartData(ChartData.LINE_COLOR_BLUE);
        int[] yValues = new int[]{0, 8, 9, 18, 35, 30, 33, 32, 46, 53, 50, 42};
        for(int i = 8; i <= 19; i++) {
            data.addPoint(i, yValues[i-8]);
            data.addXValue(i, i + ":00");
        }
        chart.addData(data);

        // Second data set
        ChartData data2 = new ChartData(ChartData.LINE_COLOR_RED);
        int[] yValues2 = new int[]{0, 5, 9, 23, 15, 35, 45, 50, 41, 45, 32, 24};
        for(int i = 8; i <= 19; i++) {
            data2.addPoint(i, yValues2[i-8]);
            data2.addXValue(i, i + ":00");
        }
        chart.addData(data2);

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
