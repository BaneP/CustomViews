package com.example.epg_grid;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.epg_grid.dtv.MyEpgEvent;
import com.iwedia.dtv.epg.EpgEvent;

import it.sephiroth.android.library.widget.AbsHListView.LayoutParams;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Class for presenting EPG events to user for one channel
 * 
 * @author Branimir Pavlovic
 */
public class HorizListAdapter extends BaseAdapter implements
        HorizontalListInterface {
    private ArrayList<HorizTimeObject<EpgEvent>> mElementWidths;
    private Context ctx;

    public HorizListAdapter(Context ctx,
            ArrayList<HorizTimeObject<EpgEvent>> elementWidths) {
        this.ctx = ctx;
        this.mElementWidths = elementWidths;
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
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return mElementWidths.get(position).isRealEvent();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(ctx);
            convertView.setPadding(5, 5, 5, 5);
            ((TextView) convertView).setTextSize(14);
        }
        convertView.setLayoutParams(new LayoutParams(mElementWidths.get(
                position).getWidth(), LayoutParams.MATCH_PARENT));
        EpgEvent event = mElementWidths.get(position).getObject();
        if (event != null) {
            ((TextView) convertView).setText(event.getName() + "\n"
                    + String.format("%02d", event.getStartTime().getHour())
                    + ":"
                    + String.format("%02d", event.getStartTime().getMin())
                    + " " + event.getStartTime().getDay() + " - "
                    + String.format("%02d", event.getEndTime().getHour()) + ":"
                    + String.format("%02d", event.getEndTime().getMin()) + " "
                    + event.getEndTime().getDay());
        } else {
            if (mElementWidths.get(position).isRealEvent()) {
                ((TextView) convertView).setText("No data available!");
            } else {
                ((TextView) convertView).setText("");
            }
        }
        return convertView;
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
