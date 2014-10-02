package com.iwedia.epg_grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import com.iwedia.epg_grid.HorizListView.OnScrollHappenedListener;

import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import it.sephiroth.android.library.widget.AdapterView.OnItemSelectedListener;

import java.lang.ref.WeakReference;

public abstract class EpgGridAdapter extends BaseAdapter implements
        VerticalListInterface {
    private LayoutInflater mInflater;
    protected WeakReference<Context> mContextReference;
    private OnScrollHappenedListener mOnScrollHappenedListener;
    protected int mTotalLeftOffset = 0, mLeftOffset = 0, mFocusedViewWidth = 0;
    private int oneMinutePixelWidth;
    private int mListSelector;
    private int mItemHeight;
    /**
     * Listeners for horizontal lists
     */
    private OnItemSelectedListener mOnItemSelectedListener;
    private OnItemClickListener mOnItemClickListener;

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
        EpgViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.epg_list_item, null);
            holder = getViewHolder(convertView);
            // holder = new EpgViewHolder(convertView);
            if (mListSelector != EpgGrid.INVALID_VALUE) {
                holder.getHList().setSelector(mListSelector);
            }
            holder.getHList().setOnScrollHappenedListener(
                    mOnScrollHappenedListener);
            holder.getHList()
                    .setOnItemSelectedListener(mOnItemSelectedListener);
            holder.getHList().setOnItemClickListener(mOnItemClickListener);
            convertView.setTag(holder);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, mItemHeight));
        } else {
            holder = (EpgViewHolder) convertView.getTag();
        }
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

    /**
     * This method is mandatory to call after data set is ready to present in
     * horizontal list view.
     * 
     * @param horizList
     * @param adapter
     */
    public void refreshHorizontalListWhenDataIsReady(HorizListView horizList,
            BaseAdapter adapter) {
        horizList.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewObserver(horizList));
        horizList.setAdapter(adapter);
        horizList.setPositionBasedOnLeftOffsetFromAdapter(mTotalLeftOffset,
                mLeftOffset, mFocusedViewWidth);
    }

    /**
     * Class for refreshing new adapter views
     */
    protected class ViewObserver implements
            ViewTreeObserver.OnGlobalLayoutListener {
        WeakReference<HorizListView> mViewToObserve;

        public ViewObserver(HorizListView viewToObserve) {
            mViewToObserve = new WeakReference<HorizListView>(viewToObserve);
        }

        @Override
        public void onGlobalLayout() {
            ViewTreeObserver observer = mViewToObserve.get()
                    .getViewTreeObserver();
            observer.removeOnGlobalLayoutListener(this);
            mViewToObserve.get().setPositionBasedOnLeftOffsetFromAdapter(
                    mTotalLeftOffset, mLeftOffset, mFocusedViewWidth);
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
}
