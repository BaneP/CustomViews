package com.iwedia.epg_grid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.iwedia.epg_grid.HorizListView.OnScrollHappenedListener;

import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import it.sephiroth.android.library.widget.AdapterView.OnItemSelectedListener;

import java.lang.ref.WeakReference;

public abstract class EpgGridAdapter extends EpgBaseAdapter implements
        VerticalListInterface {
    private LayoutInflater mInflater;
    protected WeakReference<Context> mContextReference;
    private OnScrollHappenedListener mOnScrollHappenedListener;

    private int oneMinutePixelWidth;
    private int mListSelector;
    private int mItemHeight;
    private int mDividerWidth = 0;
    private LinearLayout.LayoutParams mHListParams;
    /**
     * Listeners for horizontal lists
     */
    private OnItemSelectedListener mOnItemSelectedListener;
    private OnItemClickListener mOnItemClickListener;
    private View.OnKeyListener mOnKeyListener;

    public EpgGridAdapter(Context ctx, int oneMinutePixelWidth,
            int listSelector, int itemHeight) {
        this(ctx, listSelector, itemHeight);
        this.oneMinutePixelWidth = oneMinutePixelWidth;
    }

    public EpgGridAdapter(Context ctx, int listSelector, int itemHeight) {
        this(ctx);
        this.mListSelector = listSelector;
        this.mItemHeight = itemHeight;
    }

    public EpgGridAdapter(Context ctx) {
        this.mContextReference = new WeakReference<Context>(ctx);
        mInflater = LayoutInflater.from(ctx);
        mHListParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.9f);
        mHListParams.setMargins(mDividerWidth, 0, 0, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EpgViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.epg_list_item, null);
            holder = getViewHolder(convertView);
            if (mListSelector != EpgGrid.INVALID_VALUE) {
                holder.getHList().setSelector(mListSelector);
            }
            holder.getHList().setOnScrollHappenedListener(
                    mOnScrollHappenedListener);
            holder.getHList()
                    .setOnItemSelectedListener(mOnItemSelectedListener);
            holder.getHList().setDivider(new ColorDrawable(Color.TRANSPARENT));
            holder.getHList().setDividerWidth(mDividerWidth);
            holder.getHList().setOnItemClickListener(mOnItemClickListener);
            holder.getHList().setOnKeyListener(mOnKeyListener);
            convertView.setTag(holder);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, mItemHeight));
        } else {
            holder = (EpgViewHolder) convertView.getTag();
        }
        holder.setPosition(position);
        setView(holder, position);
        return convertView;
    }

    /**
     * Bind data to list views, if you use extended {@link EpgViewHolder} class
     * just cast holder to your object. After data set is loaded you must call
     * {@link refreshHorizontalListWhenDataIsReady()} method in order to present
     * data in correct positions
     */
    protected abstract void setView(EpgViewHolder holder, int position);

    /**
     * If you want to use custom channel indicator, extend {@link EpgViewHolder}
     * and expand its fields. Also you must create your layout with name
     * "epg_channel_indicator.xml"
     * 
     * @param convertView
     *        Inflated view
     * @return Created view holder instance
     */
    protected abstract EpgViewHolder getViewHolder(View convertView);

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
    public void setHorizListListeners(
            OnItemSelectedListener onItemSelectedListener,
            OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        this.mOnItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public void setListSelector(int selector) {
        mListSelector = selector;
        notifyDataSetChanged();
    }

    public int getListSelector() {
        return mListSelector;
    }

    @Override
    public void setItemsHeight(int height) {
        mItemHeight = height;
        notifyDataSetChanged();
    }

    public int getItemsHeight() {
        return mItemHeight;
    }

    @Override
    public void setOneMinutePixelWidth(int width) {
        oneMinutePixelWidth = width;
    }

    public int getOneMinutePixelWidth() {
        return oneMinutePixelWidth;
    }

    public int getDividerWidth() {
        return mDividerWidth;
    }

    public void setDividerWidth(int mDividerWidth) {
        this.mDividerWidth = mDividerWidth;
        mHListParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 0.9f);
        mHListParams.setMargins(mDividerWidth, 0, 0, 0);
        notifyDataSetChanged();
    }

    public View.OnKeyListener getOnKeyListener() {
        return mOnKeyListener;
    }

    public void setOnKeyListener(View.OnKeyListener mOnKeyListener) {
        this.mOnKeyListener = mOnKeyListener;
    }
}
