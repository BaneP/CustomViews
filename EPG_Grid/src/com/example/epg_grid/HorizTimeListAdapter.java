package com.example.epg_grid;

import android.content.Context;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.epg_grid.dtv.MyEpgEvent;
import com.iwedia.dtv.epg.EpgEvent;

import it.sephiroth.android.library.widget.AbsHListView.LayoutParams;

import java.util.ArrayList;
import java.util.Random;

/**
 * Adapter that populates time line with desired hours.
 * 
 * @author Branimir Pavlovic
 */
public class HorizTimeListAdapter extends BaseAdapter implements
        HorizontalListInterface {
    private ArrayList<HorizTimeObject<EpgEvent>> mElementWidths;
    private ArrayList<HorizTimeObject<Integer>> mElementValues;
    private LayoutInflater mInflater;
    private int mTimeLineTextSize, mTimeLineTextSizeHalfHour;

    public HorizTimeListAdapter(Context ctx, int oneMinutePixelWidth,
            int startHour, int endHour, int timeLineTextSize,
            int timeLineTextSizeHalfHour) {
        this.mTimeLineTextSize = timeLineTextSize;
        this.mTimeLineTextSizeHalfHour = timeLineTextSizeHalfHour;
        mInflater = LayoutInflater.from(ctx);
        mElementValues = new ArrayList<HorizTimeObject<Integer>>();
        mElementWidths = new ArrayList<HorizTimeObject<EpgEvent>>();
        for (int i = startHour; i < endHour; i++) {
            mElementValues.add(new HorizTimeObject<Integer>(
                    oneMinutePixelWidth * 60, i));
            mElementWidths.add(new HorizTimeObject<EpgEvent>(
                    oneMinutePixelWidth * 60, null));
        }
    }

    @Override
    public int getCount() {
        return mElementWidths.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.epg_time_line_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new LayoutParams(mElementWidths.get(
                position).getWidth(), LayoutParams.MATCH_PARENT));
        setView(holder, position);
        return convertView;
    }

    private void setView(ViewHolder holder, int position) {
        // Set text sizes if needed
        if (holder.left.getTextSize() != mTimeLineTextSize) {
            holder.left.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mTimeLineTextSize);
            holder.right.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mTimeLineTextSize);
        }
        if (holder.central.getTextSize() != mTimeLineTextSizeHalfHour) {
            holder.central.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mTimeLineTextSizeHalfHour);
        }
        // FIRST
        if (position == 0) {
            holder.left.setText(String.format("%02d",
                    mElementValues.get(position).getObject())
                    + ":00");
        } else {
            holder.left.setText(":00");
        }
        // LAST
        if (position == mElementValues.size() - 1) {
            holder.right.setText(String.format("%02d",
                    mElementValues.get(position).getObject() + 1)
                    + ":00");
        } else {
            holder.right.setText(String.format("%02d",
                    mElementValues.get(position).getObject() + 1));
        }
        holder.central.setText(String.format("%02d",
                mElementValues.get(position).getObject())
                + ":30");
    }

    private static class ViewHolder {
        TextView left, central, right;

        public ViewHolder(View convertView) {
            left = (TextView) convertView
                    .findViewById(R.id.textViewEpgTimeLineLeft);
            right = (TextView) convertView
                    .findViewById(R.id.textViewEpgTimeLineRight);
            central = (TextView) convertView
                    .findViewById(R.id.textViewEpgTimeLineCentral);
        }
    }

    @Override
    public ArrayList<HorizTimeObject<EpgEvent>> getElementWidths() {
        return mElementWidths;
    }

    @Override
    public int getElementWidth(int position) {
        return mElementWidths.get(position).getWidth();
    }
}
