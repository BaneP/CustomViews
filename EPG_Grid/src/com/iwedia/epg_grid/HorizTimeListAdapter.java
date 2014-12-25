package com.iwedia.epg_grid;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import it.sephiroth.android.library.widget.AbsHListView.LayoutParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Adapter that populates time line with desired hours.
 * 
 * @author Branimir Pavlovic
 */
public class HorizTimeListAdapter extends EpgBaseAdapter implements
        HorizontalListInterface {
    private ArrayList<HorizTimeObject<Integer>> mElementValues;
    private LayoutInflater mInflater;
    private int mTimeLineTextSize, mTimeLineTextSizeHalfHour;
    private boolean is24HourFormat = true;

    public HorizTimeListAdapter(Context ctx, int oneMinutePixelWidth,
            int startHour, int endHour, int timeLineTextSize,
            int timeLineTextSizeHalfHour, boolean is24HourFormat) {
        this.mTimeLineTextSize = timeLineTextSize;
        this.mTimeLineTextSizeHalfHour = timeLineTextSizeHalfHour;
        this.is24HourFormat = is24HourFormat;
        mInflater = LayoutInflater.from(ctx);
        mElementValues = new ArrayList<HorizTimeObject<Integer>>();
        for (int i = startHour; i < endHour; i++) {
            mElementValues.add(new HorizTimeObject<Integer>(
                    oneMinutePixelWidth * 60, i));
        }
    }

    @Override
    public int getCount() {
        return mElementValues.size();
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
        convertView.setLayoutParams(new LayoutParams(mElementValues.get(
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
        final int hour = mElementValues.get(position).getObject();
        if (is24HourFormat) {
            // FIRST
            if (position == 0) {
                holder.left.setText(String.format("%02d", hour) + ":00");
            } else {
                holder.left.setText(":00");
            }
            // LAST
            if (position == mElementValues.size() - 1) {
                holder.right.setText(String.format("%02d", hour + 1) + ":00");
            } else {
                holder.right.setText(String.format("%02d", hour + 1));
            }
            // MIDDLE
            holder.central.setText(String.format("%02d", hour) + ":30");
        } else {
            final GregorianCalendar time = (GregorianCalendar) GregorianCalendar
                    .getInstance();
            time.set(Calendar.HOUR_OF_DAY, hour);
            time.set(Calendar.MINUTE, 0);
            // FIRST
            if (position == 0) {
                holder.left.setText(getDateFromFormat(time, "ha"));
            } else {
                holder.left.setText(getDateFromFormat(time, "a"));
            }
            // MIDDLE
            time.set(Calendar.MINUTE, 30);
            holder.central.setText(getDateFromFormat(time, "h:mma"));
            // LAST
            time.set(Calendar.HOUR_OF_DAY, hour + 1);
            if (position == mElementValues.size() - 1) {
                holder.right.setText(getDateFromFormat(time, "ha"));
            } else {
                holder.right.setText(getDateFromFormat(time, "h"));
            }
        }
    }

    /**
     * Get a String of the date or time base on format and date passed in. (ex.
     * h:mma will return something like 10:55PM)
     * 
     * @param cal
     *        The calendar of the time.
     * @param format
     *        The time format requested based on passed in time.
     * @return The formatted String.
     */
    private static String getDateFromFormat(GregorianCalendar cal, String format) {
        String dateFormatted = "";
        try {
            final SimpleDateFormat fmt = new SimpleDateFormat(format);
            fmt.setCalendar(cal);
            fmt.setTimeZone(cal.getTimeZone());
            dateFormatted = fmt.format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateFormatted;
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
    public int getElementWidth(int position) {
        return mElementValues.get(position).getWidth();
    }
}
