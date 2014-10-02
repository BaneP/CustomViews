package com.example.epg_try;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.epg_try.dtv.DvbManager;
import com.example.epg_try.dtv.EpgAsyncTaskLoader;
import com.iwedia.epg_grid.EpgGridAdapter;
import com.iwedia.epg_grid.EpgViewHolder;

import java.util.ArrayList;

public class EpgAdapter extends EpgGridAdapter {
    private int mCount = 0;
    private ArrayList<String> mChannels;

    public EpgAdapter(Context ctx) {
        super(ctx);
        mCount = DvbManager.getInstance().getChannelListSize();
        mChannels = DvbManager.getInstance().getChannelNames();
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
        holder.getHList().setAdapter(null);
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
}
