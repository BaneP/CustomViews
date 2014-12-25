package com.iwedia.epg_grid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.iwedia.epg_grid.HorizListView.OnScrollHappenedListener;
import it.sephiroth.android.library.widget.AdapterView.OnItemClickListener;
import it.sephiroth.android.library.widget.AdapterView.OnItemSelectedListener;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Custom EPG grid view that contains list views with horizontal list views.
 *
 * @author Branimir Pavlovic
 */
public class EpgGrid extends LinearLayout implements OnScrollHappenedListener,
        OnItemSelectedListener,
        android.widget.AdapterView.OnItemSelectedListener, OnItemClickListener {
    private static final String TAG = "EpgGrid";
    public static final int INVALID_VALUE = -1;
    public static final int NUMBER_OF_MINUTES_IN_DAY = 1440,
            NUMBER_OF_MINUTES_IN_HOUR = 60;
    private static final int ONE_MINUTE_PIXELS_WIDTH = 10;
    private static final int ITEMS_DEFAULT_HEIGHT = 100;
    /**
     * Main EPG views
     */
    private EpgListView mEpgVerticalList;
    private HorizListView mEpgTimeLineList;
    /**
     * Values from attributes
     */
    private int mOneMinutePixelWidth = ONE_MINUTE_PIXELS_WIDTH;
    private int mTimeLineTextSize, mTimeLineTextSizeHalfHour, mTimeLineHeight;
    private int mChannelItemHeight;
    private int mGridSelector;
    private int mDividerHeight, mDividerWidth;
    private boolean is24HourFormat;
    /**
     * Time line start time
     */
    private GregorianCalendar mStartTime;

    /**
     * On item selected listener for EPG grid view.
     */
    public interface OnItemSelectedListener {
        public void onItemSelected(EpgListView verticalList,
                View verticalListElement, HorizListView horizontalList,
                View horizontalListElement, int verticalListChildIndex,
                int horizontalListChildIndex);

        public void onNothingSelected(EpgListView arg0);
    }

    /**
     * On item click listener for EPG grid view.
     */
    public interface OnItemClickListener {
        public void onItemClick(EpgListView verticalList,
                View verticalListElement, HorizListView horizontalList,
                View horizontalListElement, int verticalListChildIndex,
                int horizontalListChildIndex);
    }

    /**
     * Listeners
     */
    private OnItemSelectedListener mOnItemSelectedListener;
    private OnItemClickListener mOnItemClickListener;
    private AdapterView.OnItemSelectedListener mListSelectionListener;
    private View.OnKeyListener mOnKeyListener;

    public EpgGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public EpgGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EpgGrid(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        Drawable timeLineBackground = null;
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.EpgGrid, 0, 0);
            try {
                mOneMinutePixelWidth = a.getDimensionPixelSize(
                        R.styleable.EpgGrid_minutePixelsWidth,
                        ONE_MINUTE_PIXELS_WIDTH);
                mTimeLineTextSize = a.getDimensionPixelSize(
                        R.styleable.EpgGrid_timeLineTextSize, 15);
                mTimeLineTextSizeHalfHour = a.getDimensionPixelSize(
                        R.styleable.EpgGrid_timeLineTextSizeHalfHour,
                        INVALID_VALUE);
                if (mTimeLineTextSizeHalfHour == INVALID_VALUE) {
                    mTimeLineTextSizeHalfHour = mTimeLineTextSize;
                }
                timeLineBackground = a
                        .getDrawable(R.styleable.EpgGrid_timeLineBackground);
                mGridSelector = a.getResourceId(
                        R.styleable.EpgGrid_gridSelector, INVALID_VALUE);
                mTimeLineHeight = a.getDimensionPixelSize(
                        R.styleable.EpgGrid_timeLineHeight,
                        ITEMS_DEFAULT_HEIGHT);
                mChannelItemHeight = a.getDimensionPixelSize(
                        R.styleable.EpgGrid_gridChannelItemHeight,
                        ITEMS_DEFAULT_HEIGHT);
                mDividerHeight = a.getDimensionPixelSize(
                        R.styleable.EpgGrid_gridDividerWidth, 0);
                mDividerWidth = a.getDimensionPixelSize(
                        R.styleable.EpgGrid_gridDividerWidth, 0);
                is24HourFormat = (0 == a.getInt(
                        R.styleable.EpgGrid_timeLineHourFormat, 0));
            } finally {
                a.recycle();
            }
        }
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.epg_grid, this);
        // Initialize vertical list view
        mEpgVerticalList = (EpgListView) findViewById(R.id.listViewEpg);
        mEpgVerticalList.setSelector(android.R.color.transparent);
        mEpgVerticalList.setDivider(new ColorDrawable(Color.TRANSPARENT));
        mEpgVerticalList.setDividerHeight(mDividerHeight);
        // mEpgVerticalList.setAnimationCacheEnabled(false);
        // mEpgVerticalList.setSmoothScrollbarEnabled(true);
        // mEpgVerticalList.setScrollingCacheEnabled(false);
        // Initialize time line view
        View timeLineView = findViewById(R.id.epg_time_line);
        timeLineView.findViewById(R.id.epg_channel_indicator)
                .setBackgroundColor(Color.TRANSPARENT);
        if (timeLineBackground != null) {
            timeLineView.setBackground(timeLineBackground);
        }
        timeLineView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, mTimeLineHeight));
        mEpgTimeLineList = (HorizListView) timeLineView
                .findViewById(R.id.epg_hlist);
        final LinearLayout.LayoutParams params = new LayoutParams(0,
                LayoutParams.MATCH_PARENT, 0.9f);
        params.setMargins(mDividerWidth, 0, 0, 0);
        mEpgTimeLineList.setLayoutParams(params);
        mEpgTimeLineList.setFocusable(false);
        mEpgTimeLineList.setManuallyScrollable(false);
        mEpgTimeLineList.setItemsCanFocus(false);
        mEpgTimeLineList.setAdapter(new HorizTimeListAdapter(context,
                mOneMinutePixelWidth, 0, 24, mTimeLineTextSize,
                mTimeLineTextSizeHalfHour, is24HourFormat));
        // Set listeners
        mEpgVerticalList.setOnItemSelectedListener(this);
    }

    public void setTimeTableStartTime(GregorianCalendar startTime,
            Context context) {
        this.mStartTime = startTime;
        final HorizTimeListAdapter timeAdapter = new HorizTimeListAdapter(
                context,
                mOneMinutePixelWidth, startTime.get(Calendar.HOUR_OF_DAY), 24,
                mTimeLineTextSize, mTimeLineTextSizeHalfHour, is24HourFormat);
        timeAdapter.refreshHorizontalListWhenDataIsReady(mEpgTimeLineList,
                timeAdapter);
        // Refresh start time of vertical list adapter
        if (mEpgVerticalList.getAdapter() != null) {
            ((EpgGridAdapter) mEpgVerticalList.getAdapter())
                    .setStartDate(mStartTime);
        }
    }

    @Override
    public void scrollTo(HorizListView v, int totalOffset) {
        if (v != mEpgTimeLineList) {
            ((HorizTimeListAdapter) mEpgTimeLineList.getAdapter())
                    .setCurrentScrollPosition(totalOffset, 0, 0);
            mEpgTimeLineList.scrollListToPixel(totalOffset);
        }
        mEpgVerticalList.scrollTo(v, totalOffset);
    }

    @Override
    public void onItemClick(
            it.sephiroth.android.library.widget.AdapterView<?> parent,
            View view, int position, long id) {
        final View parentView = (View) parent.getParent();
        final EpgViewHolder holder = (EpgViewHolder) parentView.getTag();
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(
                    mEpgVerticalList,
                    mEpgVerticalList.getSelectedView(),
                    (HorizListView) parent,
                    view,
                    mEpgVerticalList.getSelectedItemPosition() == -1 ? holder
                            .getPosition() : mEpgVerticalList
                            .getSelectedItemPosition(), position);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
            long arg3) {
        if (mListSelectionListener != null) {
            mListSelectionListener.onItemSelected(arg0, arg1, arg2, arg3);
        }
        if (mOnItemSelectedListener != null) {
            HorizListView hlist = (HorizListView) arg1
                    .findViewById(R.id.epg_hlist);
            View horizontalListElement = null;
            int horizontalListChildIndex = ListView.INVALID_POSITION;
            if (hlist != null) {
                horizontalListElement = hlist.getSelectedView();
                horizontalListChildIndex = hlist.getSelectedItemPosition();
            }
            mOnItemSelectedListener.onItemSelected(mEpgVerticalList, arg1,
                    hlist, horizontalListElement, arg2,
                    horizontalListChildIndex);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        if (mListSelectionListener != null) {
            mListSelectionListener.onNothingSelected(arg0);
        }
        if (mOnItemSelectedListener != null) {
            mOnItemSelectedListener.onNothingSelected(mEpgVerticalList);
        }
    }

    @Override
    public void onItemSelected(
            it.sephiroth.android.library.widget.AdapterView<?> parent,
            View horizontalListElement, int horizontalListChildIndex, long id) {
        if (mOnItemSelectedListener != null) {
            HorizListView hlist = (HorizListView) parent;
            View verticalListElement = mEpgVerticalList.getSelectedView();
            mOnItemSelectedListener.onItemSelected(mEpgVerticalList,
                    verticalListElement, hlist, horizontalListElement,
                    mEpgVerticalList.getSelectedItemPosition(),
                    horizontalListChildIndex);
        }
    }

    @Override
    public void onNothingSelected(
            it.sephiroth.android.library.widget.AdapterView<?> parent) {
    }

    /**
     * Sets custom adapter to grid list.
     */
    public void setAdapter(EpgGridAdapter adapter) {
        if (adapter == null) {
            return;
        }
        adapter.setHorizListListeners(this, this);
        adapter.setOnScrollHappenedListener(this);
        adapter.setOneMinutePixelWidth(mOneMinutePixelWidth);
        adapter.setListSelector(mGridSelector);
        adapter.setItemsHeight(mChannelItemHeight);
        adapter.setDividerWidth(mDividerWidth);
        adapter.setOnKeyListener(mOnKeyListener);
        mEpgVerticalList.setAdapter(adapter);
    }

    /**
     * Returns active EPG grid adapter
     */
    public EpgGridAdapter getAdapter() {
        return (EpgGridAdapter) mEpgVerticalList.getAdapter();
    }

    public void setGridSelector(int mGridSelector) {
        this.mGridSelector = mGridSelector;
        Adapter adapter = mEpgVerticalList.getAdapter();
        if (adapter != null && adapter instanceof VerticalListInterface) {
            ((VerticalListInterface) adapter).setListSelector(mGridSelector);
        }
    }

    public void setChannelItemHeight(int mChannelItemHeight) {
        this.mChannelItemHeight = mChannelItemHeight;
        Adapter adapter = mEpgVerticalList.getAdapter();
        if (adapter != null && adapter instanceof VerticalListInterface) {
            ((VerticalListInterface) adapter)
                    .setItemsHeight(mChannelItemHeight);
        }
    }

    public void setTimeLineBackground(Drawable mTimeLineBackground) {
        findViewById(R.id.epg_time_line).setBackground(mTimeLineBackground);
    }

    public void setTimeLineHeight(int mTimeLineHeight) {
        this.mTimeLineHeight = mTimeLineHeight;
        findViewById(R.id.epg_time_line).setLayoutParams(
                new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        mTimeLineHeight));
    }

    public GregorianCalendar getTimeTableStartTime() {
        if (mStartTime == null) {
            mStartTime = (GregorianCalendar) GregorianCalendar.getInstance()
                    .clone();
            mStartTime.set(Calendar.HOUR_OF_DAY, 0);
        }
        return mStartTime;
    }

    public int getFirstVisiblePosition() {
        return mEpgVerticalList.getFirstVisiblePosition();
    }

    public int getLastVisiblePosition() {
        return mEpgVerticalList.getLastVisiblePosition();
    }

    public int getVerticalChildCount(){
        return mEpgVerticalList.getChildCount();
    }

    /**
     * Returns selected item position object
     */
    public SelectedPositionInfo getSelectedPosition() {
        int horizPosition = INVALID_VALUE;
        final int verticalPosition = mEpgVerticalList.getSelectedItemPosition();
        try {
            EpgViewHolder holder = (EpgViewHolder) mEpgVerticalList.getChildAt(
                    verticalPosition
                            - mEpgVerticalList.getFirstVisiblePosition())
                    .getTag();
            horizPosition = holder.getHList().getSelectedItemPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SelectedPositionInfo(
                mEpgVerticalList.getSelectedItemPosition(), horizPosition);
    }

    /**
     * Sets selection to desired position in EpgGrid
     *
     * @param selection Object that contains vertical and horizontal position
     */
    public void setSelectedPosition(SelectedPositionInfo selection) {
        if (mEpgVerticalList.getSelectedItemPosition() == selection
                .getVerticalPosition()) {
            return;
        }
        final int firstVisiblePosition = mEpgVerticalList
                .getFirstVisiblePosition();
        final int lastVisiblePosition = mEpgVerticalList
                .getLastVisiblePosition();
        // First set selection to vertical list
        if (selection.getVerticalPosition() >= firstVisiblePosition
                && selection.getVerticalPosition() <= lastVisiblePosition) {
            final View verticalChild = mEpgVerticalList.getChildAt(selection
                    .getVerticalPosition() - firstVisiblePosition);
            mEpgVerticalList.setSelectionFromTop(
                    selection.getVerticalPosition(), verticalChild.getTop());
            final EpgViewHolder holder = (EpgViewHolder) verticalChild.getTag();
            final int firstVisibleHorizPosition = holder.getHList()
                    .getFirstVisiblePosition();
            final int lastVisibleHorizPosition = holder.getHList()
                    .getLastVisiblePosition();
            int horizontalPosition = selection.getHorizontalPosition();
            if (horizontalPosition == ListView.INVALID_POSITION) {
                horizontalPosition = firstVisibleHorizPosition;
            }
            // Desired horizontal position is not valid
            if (horizontalPosition < 0
                    || horizontalPosition >= holder.getHList().getCount()) {
                return;
            }
            // Item is visible on screen
            if (horizontalPosition >= firstVisibleHorizPosition
                    && horizontalPosition <= lastVisibleHorizPosition) {
                final View horizontalChild = holder.getHList().getChildAt(
                        horizontalPosition - firstVisibleHorizPosition);
                holder.getHList().setSelectionFromLeft(horizontalPosition,
                        horizontalChild.getLeft());
                holder.getHList().setSelectionInt(horizontalPosition);
            }
            // Item is not visible on screen
            else {
                holder.getHList().setSelection(horizontalPosition);
            }
        } else
        // Desired position is not visible on the screen
        {
            mEpgVerticalList.setSelection(selection.getVerticalPosition());
        }
    }

    public void setTimeLineTextSize(int mTimeLineTextSize) {
        this.mTimeLineTextSize = mTimeLineTextSize;
    }

    public void setTimeLineTextSizeHalfHour(int mTimeLineTextSizeHalfHour) {
        this.mTimeLineTextSizeHalfHour = mTimeLineTextSizeHalfHour;
    }

    public void setOneMinutePixelWidth(int mOneMinutePixelWidth) {
        this.mOneMinutePixelWidth = mOneMinutePixelWidth;
    }

    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mEpgVerticalList.setOnScrollListener(l);
    }

    public void setOnTouchListener(View.OnTouchListener l) {
        mEpgVerticalList.setOnTouchListener(l);
    }

    public int getOneMinutePixelWidth() {
        return mOneMinutePixelWidth;
    }

    public int getTimeLineTextSize() {
        return mTimeLineTextSize;
    }

    public int getTimeLineTextSizeHalfHour() {
        return mTimeLineTextSizeHalfHour;
    }

    public int getTimeLineHeight() {
        return mTimeLineHeight;
    }

    public int getChannelItemHeight() {
        return mChannelItemHeight;
    }

    public int getGridSelector() {
        return mGridSelector;
    }

    public OnItemSelectedListener getOnItemSelectedListener() {
        return mOnItemSelectedListener;
    }

    public void setOnItemSelectedListener(
            OnItemSelectedListener mOnItemSelectedListener) {
        this.mOnItemSelectedListener = mOnItemSelectedListener;
    }

    public void setOnItemSelectedListener(
            AdapterView.OnItemSelectedListener mListSelectionListener) {
        this.mListSelectionListener = mListSelectionListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(
            OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    protected EpgListView getEpgVerticalList() {
        return mEpgVerticalList;
    }

    @Override
    public void setOnKeyListener(OnKeyListener l) {
        mOnKeyListener = l;
        EpgGridAdapter adapter = (EpgGridAdapter) mEpgVerticalList.getAdapter();
        if (adapter != null) {
            adapter.setOnKeyListener(mOnKeyListener);
        }
    }

    public boolean is24HourFormat() {
        return is24HourFormat;
    }

    public void set24HourFormat(boolean is24HourFormat) {
        this.is24HourFormat = is24HourFormat;
    }

    /**
     * Class that contains information about currently selected position
     */
    public static class SelectedPositionInfo {
        private int mVerticalPosition, mHorizontalPosition;

        public SelectedPositionInfo(int mVerticalPosition,
                int mHorizontalPosition) {
            this.mVerticalPosition = mVerticalPosition;
            this.mHorizontalPosition = mHorizontalPosition;
        }

        public int getVerticalPosition() {
            return mVerticalPosition;
        }

        public int getHorizontalPosition() {
            return mHorizontalPosition;
        }
    }
}
