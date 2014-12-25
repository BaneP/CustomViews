package com.example.epg_try;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.epg_try.dtv.DvbManager;
import com.example.epg_try.dtv.EpgAsyncTaskLoader;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.epg_grid.EpgGrid;
import com.iwedia.epg_grid.EpgGridAdapter;
import com.iwedia.epg_grid.EpgViewHolder;
import com.iwedia.epg_grid.HorizTimeObject;

import java.util.ArrayList;
import java.util.Calendar;

public class EpgAdapter extends EpgGridAdapter {
    private int mCount = 0;
    private ArrayList<String> mChannels;
    private static HorizListAdapter sDummyAdapter;

    public EpgAdapter(Context ctx) {
        super(ctx);
        mCount = DvbManager.getInstance().getChannelListSize();
        mChannels = DvbManager.getInstance().getChannelNames();
    }

    @Override
    public void setOneMinutePixelWidth(int width) {
        ArrayList<HorizTimeObject<EpgEvent>> mElementWidths = new ArrayList<HorizTimeObject<EpgEvent>>();
        mElementWidths.add(new HorizTimeObject<EpgEvent>(width
                * EpgGrid.NUMBER_OF_MINUTES_IN_DAY, true));
        sDummyAdapter = new HorizListAdapter(mContextReference.get(),
                mElementWidths);
        super.setOneMinutePixelWidth(width);
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(int position) {
        return mChannels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    protected EpgViewHolder getViewHolder(View convertView) {
        return new ViewHolder(convertView);
    }

    @Override
    protected void setView(EpgViewHolder holder, int position) {
        ((ViewHolder) holder).text.setText(mChannels.get(position));
        holder.getHList().setAdapter(sDummyAdapter);
        // refreshHorizontalListWhenDataIsReady(holder.getHList(),
        // sDummyAdapter);
        EpgAsyncTaskLoader loader = new EpgAsyncTaskLoader(holder.getHList(),
                mContextReference.get(), mTotalLeftOffset, mLeftOffset,
                mFocusedViewWidth, this);
        loader.execute(position, getOneMinutePixelWidth(), 0);// TODO third
                                                              // param is
                                                              // day
    }

    private class ViewHolder extends EpgViewHolder {
        TextView text;

        public ViewHolder(View convertView) {
            super(convertView);
            text = (TextView) getChannelIndicator().findViewById(R.id.textview);
        }
    }

    @Override
    public void setStartDate(Calendar startDate) {
        // TODO Auto-generated method stub
    }
}
