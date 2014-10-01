package com.example.epg_grid;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.epg_grid.HorizListView.OnScrollHappenedListener;
import com.iwedia.dtv.epg.EpgEvent;

import java.util.ArrayList;

public class EpgGrid extends LinearLayout implements OnScrollHappenedListener {
    private static final int ONE_MINUTE_PIXELS_WIDTH = 10;
    public static final int NUMBER_OF_MINUTES_IN_DAY = 1440;
    private EpgListView mEpgVerticalList;
    private HorizListView mEpgTimeLineList;
    private int mOneMinutePixelWidth = ONE_MINUTE_PIXELS_WIDTH;

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
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.EpgGrid, 0, 0);
            try {
                mOneMinutePixelWidth = a.getDimensionPixelSize(
                        R.styleable.EpgGrid_minutePixelsWidth,
                        ONE_MINUTE_PIXELS_WIDTH);
            } finally {
                a.recycle();
            }
        }
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.epg_grid, this);
        // Initialize vertical list view
        mEpgVerticalList = (EpgListView) findViewById(R.id.listViewEpg);
        ListAdapter adapter = new ListAdapter(context, mOneMinutePixelWidth);
        adapter.setOnScrollHappenedListener(this);
        mEpgVerticalList.setAdapter(adapter);
        // Initialize time line view
        mEpgTimeLineList = (HorizListView) findViewById(R.id.epg_time_line)
                .findViewById(R.id.epg_hlist);
        mEpgTimeLineList.setFocusable(false);
        mEpgTimeLineList.setManuallyScrollable(false);
        mEpgTimeLineList.setItemsCanFocus(false);
        mEpgTimeLineList.setAdapter(new HorizTimeListAdapter(context,
                mOneMinutePixelWidth, 0, 24));
    }

    @Override
    public void scrollTo(HorizListView v, int offset, int totalOffset) {
        if (v != mEpgTimeLineList) {
            mEpgTimeLineList.scrollListByPixels(offset);
        }
        mEpgVerticalList.scrollTo(v, offset, totalOffset);
    }

    public int getOneMinutePixelWidth() {
        return mOneMinutePixelWidth;
    }

    public void setOneMinutePixelWidth(int mOneMinutePixelWidth) {
        this.mOneMinutePixelWidth = mOneMinutePixelWidth;
    }
}
