package com.example.epg_try;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.epg_grid.HorizTimeObject;
import com.iwedia.epg_grid.HorizontalListInterface;

import it.sephiroth.android.library.widget.AbsHListView.LayoutParams;

import java.util.ArrayList;

/**
 * Class for presenting EPG events to user for one channel
 * 
 * @author Branimir Pavlovic
 */
public class HorizListAdapter extends BaseAdapter implements
        HorizontalListInterface {
    private ArrayList<HorizTimeObject<EpgEvent>> mElementWidths;
    private ArrayList<Integer> mWidths;
    private LayoutInflater inflater;

    public HorizListAdapter(Context ctx,
            ArrayList<HorizTimeObject<EpgEvent>> elementWidths) {
        this.mElementWidths = elementWidths;
        inflater = LayoutInflater.from(ctx);
        // calculate view types
        mWidths = new ArrayList<Integer>();
        HorizTimeObject<EpgEvent> item = null;
        int width = 0;
        for (int i = 0; i < mElementWidths.size(); i++) {
            item = mElementWidths.get(i);
            width = item.isRealEvent() ? item.getWidth() : 0;
            int indexOf = mWidths.indexOf(width);
            if (indexOf == -1) {
                mWidths.add(width);
            }
            item.setViewType(indexOf == -1 ? mWidths.size() - 1 : indexOf);
        }
    }

    @Override
    public int getCount() {
        return mElementWidths.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        // return super.getViewTypeCount();
        return mWidths.size();
    }

    @Override
    public int getItemViewType(int position) {
        // return super.getItemViewType(position);
        return mElementWidths.get(position).getViewType();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return mElementWidths.get(position).isRealEvent();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HorizTimeObject<EpgEvent> event = mElementWidths.get(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.program_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            holder.mTitleText.setTextSize(17);
            holder.mTitleText.setTypeface(holder.mTitleText.getTypeface(),
                    Typeface.BOLD);
            convertView.setLayoutParams(new LayoutParams(event.getWidth(),
                    LayoutParams.MATCH_PARENT));
            if (event.getObject() == null) {
                holder.mProgressBar.setVisibility(View.INVISIBLE);
                if (mElementWidths.get(position).isRealEvent()) {
                    holder.mTitleText.setText("No data available!");
                } else {
                    holder.mTitleText.setText("");
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                    holder.mOutline.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (event.getObject() != null) {
            holder.mTitleText.setText(event.getObject().getName());
            holder.mTimeText.setText(String.format("%02d", event.getObject()
                    .getStartTime().getHour())
                    + ":"
                    + String.format("%02d", event.getObject().getStartTime()
                            .getMin())
                    + " "
                    + event.getObject().getStartTime().getDay()
                    + " - "
                    + String.format("%02d", event.getObject().getEndTime()
                            .getHour())
                    + ":"
                    + String.format("%02d", event.getObject().getEndTime()
                            .getMin())
                    + " "
                    + event.getObject().getEndTime().getDay());
        } else {
            if (mElementWidths.get(position).isRealEvent()) {
                holder.mTitleText.setText("No data available!");
            } else {
                convertView.setLayoutParams(new LayoutParams(event.getWidth(),
                        LayoutParams.MATCH_PARENT));
            }
        }
        return convertView;
    }

    protected class ViewHolder {
        ProgressBar mProgressBar;
        TextView mTitleText, mAtributeText, mTimeText;
        ImageView mRecordIcon;
        View mOutline;

        public ViewHolder(View convertView) {
            mProgressBar = (ProgressBar) convertView
                    .findViewById(R.id.highlight_layout);
            mTitleText = (TextView) convertView.findViewById(R.id.title_text);
            mAtributeText = (TextView) convertView
                    .findViewById(R.id.attribute_text);
            mTimeText = (TextView) convertView.findViewById(R.id.time_text);
            mRecordIcon = (ImageView) convertView
                    .findViewById(R.id.record_icon);
            mOutline = convertView.findViewById(R.id.program_item_outline);
        }
    }

    @Override
    public int getElementWidth(int position) {
        return mElementWidths.get(position).getWidth();
    }
}
