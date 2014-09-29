package com.example.epg_grid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.epg_grid.HorizListView.OnScrollHappenedListener;

public class EpgGrid extends LinearLayout implements OnScrollHappenedListener {
    private EpgListView mEpgVerticalList;
    private HorizListView mEpgTimeLineList;

    public EpgGrid(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public EpgGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EpgGrid(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.epg_grid, this);
        // Initialize vertical list view
        mEpgVerticalList = (EpgListView) findViewById(R.id.listViewEpg);
        ListAdapter adapter = new ListAdapter(context);
        adapter.setOnScrollHappenedListener(this);
        mEpgVerticalList.setAdapter(adapter);
        // Initialize time line view
        mEpgTimeLineList = (HorizListView) findViewById(R.id.epg_time_line)
                .findViewById(R.id.epg_hlist);
        mEpgTimeLineList.setFocusable(false);
        mEpgTimeLineList.setManuallyScrollable(false);
        mEpgTimeLineList.setItemsCanFocus(false);
        mEpgTimeLineList.setAdapter(new HorizListAdapter(context,
                ListAdapter.mElementWidthsStatic));
    }

    @Override
    public void scrollTo(HorizListView v, int offset, int totalOffset) {
        if (v != mEpgTimeLineList) {
            mEpgTimeLineList.scrollListByPixels(offset);
        }
        mEpgVerticalList.scrollTo(v, offset, totalOffset);
    }
}
