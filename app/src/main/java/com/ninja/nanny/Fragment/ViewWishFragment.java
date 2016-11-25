package com.ninja.nanny.Fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.ninja.nanny.Custom.CustomFragment;
import com.ninja.nanny.MainActivity;
import com.ninja.nanny.Model.Wish;
import com.ninja.nanny.Model.WishSaving;
import com.ninja.nanny.R;
import com.ninja.nanny.Utils.Common;
import com.ninja.nanny.Utils.Constant;

import java.util.ArrayList;
import java.util.List;

import at.grabner.circleprogress.CircleProgressView;


public class ViewWishFragment extends CustomFragment implements OnChartValueSelectedListener {


    public ViewWishFragment() {
        // Required empty public constructor
    }

    LayoutInflater mInflater;
    View mView;
    MainActivity mContext;
    CircleProgressView mCircleView;
    public Wish wishSelected;
    List<WishSaving> listSaving;
    LineChart mChart;
    ArrayList<Entry> yVals1, yVals2, yVals3, yVals4;
    int nLeftHigh, nRightHigh;

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

    @Override
    public void onResume() {
        super.onResume();

        setUI();
    }

    void setUI() {
        int nWishId = wishSelected.getId();
        listSaving = Common.getInstance().dbHelper.getAllWishSavings(nWishId);


        mView.findViewById(R.id.btnBack).setOnClickListener(this);
        mView.findViewById(R.id.btnDelete).setOnClickListener(this);
        mView.findViewById(R.id.btnEdit).setOnClickListener(this);

        mCircleView = (CircleProgressView)mView.findViewById(R.id.circleView);

        ((TextView)mView.findViewById(R.id.tvTitle)).setText(wishSelected.getTitle());
        ((TextView)mView.findViewById(R.id.tvAmount)).setText(wishSelected.getTotalAmount() + "");
        ((TextView)mView.findViewById(R.id.tvPayment)).setText(wishSelected.getMonthlyPayment() + "");
        ((TextView)mView.findViewById(R.id.tvSaved)).setText(wishSelected.getSavedAmount() + "");
        ((TextView)mView.findViewById(R.id.tvLeft)).setText((wishSelected.getTotalAmount() - wishSelected.getSavedAmount()) + "");

        int nPercent = wishSelected.getSavedAmount() * 100 / wishSelected.getTotalAmount();

        mCircleView.setValue(nPercent);

        mChart = (LineChart) mView.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        setData();
        configChart();

    }

    void configChart() {
        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        mChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);
        mChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.LTGRAY);

        // add data

        mChart.animateX(2500);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
//        l.setTypeface(mTfLight);
        l.setTextSize(11f);
        l.setTextColor(Color.WHITE);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
//        l.setYOffset(11f);

        XAxis xAxis = mChart.getXAxis();
//        xAxis.setTypeface(mTfLight);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.BLUE);
        leftAxis.setAxisMaximum(nLeftHigh + 50);
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setTypeface(mTfLight);
        rightAxis.setTextColor(Color.RED);
        rightAxis.setAxisMaximum(nRightHigh + 50);
        rightAxis.setAxisMinimum(0);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setGranularityEnabled(false);


        LineDataSet set1 = null, set2 = null, set3 = null, set4 = null;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            if(yVals1.size() > 0 && yVals3.size() > 0) {
                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
                set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
                set4 = (LineDataSet) mChart.getData().getDataSetByIndex(3);
                set1.setValues(yVals1);
                set2.setValues(yVals2);
                set3.setValues(yVals3);
                set4.setValues(yVals4);
            } else if(yVals1.size() > 0 && yVals3.size() == 0) {
                set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
                set1.setValues(yVals1);
                set2.setValues(yVals2);
            } else if(yVals1.size() == 0 && yVals3.size() > 0) {
                set3 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                set4 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
                set3.setValues(yVals3);
                set4.setValues(yVals4);
            }

            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            if(yVals1.size() > 0) {
                set1 = new LineDataSet(yVals1, "Current");

                set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set1.setColor(Color.RED);
                set1.setCircleColor(Color.WHITE);
                set1.setLineWidth(2f);
                set1.setCircleRadius(3f);
//            set1.setFillAlpha(255);
                set1.setFillColor(Color.RED);
                set1.setHighLightColor(Color.rgb(244, 117, 117));
                set1.setDrawCircleHole(false);
            }

            if(yVals2.size() > 0) {
                set2 = new LineDataSet(yVals2, "Sum");
                set2.setAxisDependency(YAxis.AxisDependency.LEFT);
                set2.setColor(Color.BLUE);
                set2.setCircleColor(Color.WHITE);
                set2.setLineWidth(2f);
                set2.setCircleRadius(3f);
//            set2.setFillAlpha(255);
                set2.setFillColor(Color.BLUE);
                set2.setDrawCircleHole(false);
                set2.setHighLightColor(Color.rgb(244, 117, 117));
            }

            if(yVals3.size() > 0) {
                set3 = new LineDataSet(yVals3, "Future");
                set3.setAxisDependency(YAxis.AxisDependency.RIGHT);
                set3.setColor(ColorTemplate.colorWithAlpha(Color.RED, 100));
                set3.setCircleColor(Color.WHITE);
                set3.setLineWidth(2f);
                set3.setCircleRadius(3f);
//            set3.setFillAlpha(65);
                set3.setFillColor(ColorTemplate.colorWithAlpha(Color.RED, 100));
                set3.setDrawCircleHole(false);
                set3.setHighLightColor(Color.rgb(244, 117, 117));
            }

            if(yVals4.size() > 0) {
                set4 = new LineDataSet(yVals4, "Sum");
                set4.setAxisDependency(YAxis.AxisDependency.LEFT);
                set4.setColor(ColorTemplate.colorWithAlpha(Color.BLUE, 100));
                set4.setCircleColor(Color.WHITE);
                set4.setLineWidth(2f);
                set4.setCircleRadius(3f);
//            set4.setFillAlpha(65);
                set4.setFillColor(ColorTemplate.colorWithAlpha(Color.BLUE, 100));
                set4.setDrawCircleHole(false);
                set4.setHighLightColor(Color.rgb(244, 117, 117));
            }

            // create a data object with the datasets
            LineData data = null;
            if(yVals1.size() > 0 && yVals3.size() > 0) {
                data = new LineData(set1, set2, set3, set4);
            } else if(yVals1.size() > 0 && yVals3.size() == 0) {
                data = new LineData(set1, set2);
            } else if(yVals1.size() == 0 && yVals3.size() > 0) {
                data = new LineData(set3, set4);
            }

            if(data != null) {
//                data = new LineData(set1, set2, set3, set4);
                data.setValueTextColor(Color.WHITE);
                data.setValueTextSize(9f);

                // set data
                mChart.setData(data);
            }
        }
    }

    void setData() {

        yVals1 = new ArrayList<Entry>();
        yVals2 = new ArrayList<Entry>();
        yVals3 = new ArrayList<Entry>();
        yVals4 = new ArrayList<Entry>();

        nLeftHigh = 0;
        nRightHigh = 0;

        int nSum = 0;

        for(int i = 0; i < listSaving.size(); i++) {
            WishSaving wishSaving = listSaving.get(i);
            nSum += wishSaving.getSavedAmount();

            yVals1.add(new Entry(i, wishSaving.getSavedAmount()));
            yVals2.add(new Entry(i, nSum));

            if(wishSaving.getSavedAmount() > nRightHigh) {
                nRightHigh = wishSaving.getSavedAmount();
            }

            if(nSum > nLeftHigh) {
                nLeftHigh = nSum;
            }

        }

        if(listSaving.size() > 0) {
            int nIdx = listSaving.size() - 1;

            yVals3.add(new Entry(nIdx, listSaving.get(nIdx).getSavedAmount()));
            yVals4.add(new Entry(nIdx, nSum));
        }

        int nTotalAmount = wishSelected.getTotalAmount();

        for(int j = listSaving.size();; j ++) {
            int nSavingAmount = wishSelected.getMonthlyPayment();

            int nTmpSum = nSum + nSavingAmount;

            if(nTmpSum > nTotalAmount) {
                nSavingAmount = nTotalAmount - nSum;
            }

            if(nSavingAmount == 0) break;

            nSum += nSavingAmount;

            yVals3.add(new Entry(j, nSavingAmount));
            yVals4.add(new Entry(j, nSum));

            if(nSavingAmount > nRightHigh) {
                nRightHigh = nSavingAmount;
            }

            if(nSum > nLeftHigh) {
                nLeftHigh = nSum;
            }
        }
    }

    void removeItem() {
        new AlertDialog.Builder(mContext)
                .setTitle("Delete entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Common.getInstance().dbHelper.deleteBank(wishSelected.getId());

                        int nIdx = 0;

                        for(nIdx = 0; nIdx < Common.getInstance().listAllWishes.size(); nIdx ++) {
                            Wish wishTmp = Common.getInstance().listAllWishes.get(nIdx);

                            if(wishTmp.getId() == wishSelected.getId()) break;
                        }

                        Common.getInstance().dbHelper.deleteWish(wishSelected.getId());
                        Common.getInstance().dbHelper.deleteWishSavingGroup(wishSelected.getId());
                        Common.getInstance().listAllWishes.remove(nIdx);

                        if(wishSelected.getFlagActive() == 0) {
                            for(nIdx = 0; nIdx < Common.getInstance().listFinishedWishes.size(); nIdx ++) {
                                Wish wishTmp = Common.getInstance().listFinishedWishes.get(nIdx);

                                if(wishTmp.getId() == wishSelected.getId()) break;
                            }

                            Common.getInstance().listFinishedWishes.remove(nIdx);
                        } else {
                            for(nIdx = 0; nIdx < Common.getInstance().listActiveWishes.size(); nIdx ++) {
                                Wish wishTmp = Common.getInstance().listActiveWishes.get(nIdx);

                                if(wishTmp.getId() == wishSelected.getId()) break;
                            }

                            Common.getInstance().listActiveWishes.remove(nIdx);
                        }

                        mContext.getSupportFragmentManager().popBackStackImmediate();
                        Toast.makeText(mContext, "wish info has been deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                mContext.getSupportFragmentManager().popBackStackImmediate();
                break;

            case R.id.btnDelete:
                removeItem();
                break;

            case R.id.btnEdit:
                EditWishFragment f = new EditWishFragment();
                String title = Constant.FRAGMENT_EDIT_WISH;

                f.wishSelected = wishSelected;

                FragmentTransaction transaction = mContext.getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.content_frame, f, title).addToBackStack(title).commit();
                break;
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());

        mChart.centerViewToAnimated(e.getX(), e.getY(), mChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }
}
