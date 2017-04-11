package com.moneynanny.nanny.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.moneynanny.nanny.R;

import java.util.ArrayList;


/**
 * The Adapter class for the ListView displayed in the left navigation drawer.
 */
public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter
{
	/** The context. */
	private Context context;
	private ArrayList<String> asr;

	/**
	 * Instantiates a new left navigation adapter.
	 *
	 * @param context
	 */
	public CustomSpinnerAdapter(Context context, ArrayList<String> asr)
	{
		this.context = context;
		this.asr = asr;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount()
	{
		return asr.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public String getItem(int arg0)
	{
		return asr.get(arg0);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position)
	{
		return (long)position;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView txt = new TextView(this.context);
		txt.setPadding(16, 16, 16, 16);
		txt.setTextSize(18);
		txt.setGravity(Gravity.CENTER_VERTICAL);
		txt.setText(asr.get(position));
		txt.setTextColor(Color.parseColor("#000000"));
		return  txt;
	}

	/* (non-Javadoc)
         * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
         */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		TextView txt = new TextView(this.context);
		txt.setGravity(Gravity.CENTER);
		txt.setPadding(16, 16, 16, 16);
		txt.setTextSize(16);
		txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
		txt.setText(asr.get(position));
		txt.setTextColor(Color.parseColor("#000000"));
		return  txt;
	}

}
