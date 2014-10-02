package com.example.epg_grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.epg_grid.HorizListView.OnScrollHappenedListener;
import com.example.epg_grid.dtv.DvbManager;
import com.example.epg_grid.dtv.EpgAsyncTaskLoader;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter implements VerticalListInterface {
    private LayoutInflater inflater;
    private Context ctx;
    private OnScrollHappenedListener mOnScrollHappenedListener;
    private int mTotalLeftOffset = 0, mLeftOffset = 0, mFocusedViewWidth = 0;
    private int oneMinutePixelWidth;
    private int mCount = 0;
    private int mListSelector;
    private ArrayList<String> mChannels;
    private int mItemHeight;

    public ListAdapter(Context ctx, int oneMinutePixelWidth, int listSelector,
            int itemHeight) {
        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.oneMinutePixelWidth = oneMinutePixelWidth;
        this.mListSelector = listSelector;
        this.mItemHeight = itemHeight;
        mCount = DvbManager.getInstance().getChannelListSize();
        mChannels = DvbManager.getInstance().getChannelNames();
    }

    @Override
    public int getCount() {
        return mCount;
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
    public void setCurrentScrollPosition(int totalLeftOffset, int leftOffset,
            int focusedViewWidth) {
        this.mTotalLeftOffset = totalLeftOffset;
        this.mLeftOffset = leftOffset;
        this.mFocusedViewWidth = focusedViewWidth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.epg_list_item, null);
            holder = new ViewHolder(convertView);
            if (mListSelector != EpgGrid.INVALID_VALUE) {
                holder.hList.setSelector(mListSelector);
            }
            holder.hList.setOnScrollHappenedListener(mOnScrollHappenedListener);
            convertView.setTag(holder);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, mItemHeight));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        setView(holder, position);
        return convertView;
    }

    /**
     * Bind data to list views
     */
    private void setView(ViewHolder holder, int position) {
        holder.number.setText(mChannels.get(position));
        holder.hList.setAdapter(null);
        EpgAsyncTaskLoader loader = new EpgAsyncTaskLoader(holder.hList, ctx,
                mTotalLeftOffset, mLeftOffset, mFocusedViewWidth);
        loader.execute(position, oneMinutePixelWidth, 0);// TODO third param is
                                                         // day
    }

    private static class ViewHolder {
        TextView number;
        HorizListView hList;

        public ViewHolder(View convertView) {
            number = (TextView) convertView.findViewById(
                    R.id.epg_channel_indicator).findViewById(R.id.textview);
            hList = (HorizListView) convertView.findViewById(R.id.epg_hlist);
        }
    }

    @Override
    public OnScrollHappenedListener getOnScrollHappenedListener() {
        return mOnScrollHappenedListener;
    }

    @Override
    public void setOnScrollHappenedListener(
            OnScrollHappenedListener mOnScrollHappenedListener) {
        this.mOnScrollHappenedListener = mOnScrollHappenedListener;
    }

    @Override
    public void setListSelector(int selector) {
        mListSelector = selector;
        notifyDataSetChanged();
    }

    @Override
    public void setItemsHeight(int height) {
        mItemHeight = height;
        notifyDataSetChanged();
    }
}
