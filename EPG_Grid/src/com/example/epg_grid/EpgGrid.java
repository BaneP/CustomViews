package com.example.epg_grid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

import com.example.epg_grid.HorizListView.OnScrollHappenedListener;

/**
 * Custom EPG grid view that contains list views with horizontal list views.
 * 
 * @author Branimir Pavlovic
 */
public class EpgGrid extends LinearLayout implements OnScrollHappenedListener {
    public static final int INVALID_VALUE = -1;
    public static final int NUMBER_OF_MINUTES_IN_DAY = 1440;
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
                        R.styleable.EpgGrid_timeLineTextSizeHalfHour, 15);
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
            } finally {
                a.recycle();
            }
        }
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.epg_grid, this);
        // Initialize vertical list view
        mEpgVerticalList = (EpgListView) findViewById(R.id.listViewEpg);
        mEpgVerticalList.setSelector(android.R.color.transparent);
        // Initialize adapter
        ListAdapter adapter = new ListAdapter(context, mOneMinutePixelWidth,
                mGridSelector, mChannelItemHeight);
        adapter.setOnScrollHappenedListener(this);
        mEpgVerticalList.setAdapter(adapter);
        // Initialize time line view
        View timeLineView = findViewById(R.id.epg_time_line);
        if (timeLineBackground != null) {
            timeLineView.setBackground(timeLineBackground);
        }
        timeLineView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, mTimeLineHeight));
        mEpgTimeLineList = (HorizListView) timeLineView
                .findViewById(R.id.epg_hlist);
        mEpgTimeLineList.setFocusable(false);
        mEpgTimeLineList.setManuallyScrollable(false);
        mEpgTimeLineList.setItemsCanFocus(false);
        mEpgTimeLineList.setAdapter(new HorizTimeListAdapter(context,
                mOneMinutePixelWidth, 0, 24, mTimeLineTextSize,
                mTimeLineTextSizeHalfHour));
    }

    @Override
    public void scrollTo(HorizListView v, int offset, int totalOffset) {
        if (v != mEpgTimeLineList) {
            mEpgTimeLineList.scrollListByPixels(offset);
        }
        mEpgVerticalList.scrollTo(v, offset, totalOffset);
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

    public void setTimeLineTextSize(int mTimeLineTextSize) {
        this.mTimeLineTextSize = mTimeLineTextSize;
    }

    public void setTimeLineTextSizeHalfHour(int mTimeLineTextSizeHalfHour) {
        this.mTimeLineTextSizeHalfHour = mTimeLineTextSizeHalfHour;
    }

    public void setOneMinutePixelWidth(int mOneMinutePixelWidth) {
        this.mOneMinutePixelWidth = mOneMinutePixelWidth;
    }

    public int getOneMinutePixelWidth() {
        return mOneMinutePixelWidth;
    }

    public EpgListView getEpgVerticalList() {
        return mEpgVerticalList;
    }

    public HorizListView getEpgTimeLineList() {
        return mEpgTimeLineList;
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
}
