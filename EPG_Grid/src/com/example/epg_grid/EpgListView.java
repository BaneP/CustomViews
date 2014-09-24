package com.example.epg_grid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.epg_grid.HorizListView.FocusedViewInfo;
import com.example.epg_grid.HorizListView.OnScrollHappenedListener;

public class EpgListView extends ListView implements OnScrollHappenedListener {
    private HorizListView mFocusedView;
    int mTotalLeftOffset = 0;
    int mElementLeftOffset = 0;

    public EpgListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public EpgListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EpgListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setItemsCanFocus(true);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (adapter instanceof VerticalListInterface) {
            ((VerticalListInterface) adapter).setOnScrollHappenedListener(this);
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing VerticalListInterface!");
        }
        super.setAdapter(adapter);
    }

    @Override
    public void scrollTo(HorizListView v, int offset, int totalOffset) {
        mTotalLeftOffset = totalOffset;
        for (int i = 0; i < getChildCount(); i++) {
            HorizListView hlist = (HorizListView) getChildAt(i).findViewById(
                    R.id.hlist);
            if (hlist != v) {
                hlist.scrollListByPixels(offset);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Log.d("onKeyListener", "KEY CODE" + keyCode
        // + ", SELECTED ITEM POSITION=" + getSelectedItemPosition());
        if (getSelectedItemPosition() == INVALID_POSITION) {
            return super.onKeyDown(keyCode, event);
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN: {
                if (getSelectedItemPosition() < getAdapter().getCount() - 1) {
                    int newPosition = getSelectedItemPosition() + 1;
                    mFocusedView = (HorizListView) getSelectedView()
                            .findViewById(R.id.hlist);
                    FocusedViewInfo viewInfo = mFocusedView.new FocusedViewInfo(
                            mFocusedView.getSelectedView());// ViewInfoForElementAt(oldViewPosition);
                    mElementLeftOffset = viewInfo.getLeft();
                    ((VerticalListInterface) getAdapter())
                            .setCurrentScrollPosition(mTotalLeftOffset,
                                    mElementLeftOffset, viewInfo.getWidth());
                    if (newPosition <= getLastVisiblePosition()) {
                        HorizListView newFocusedView = (HorizListView) getChildAt(
                                newPosition - getFirstVisiblePosition())
                                .findViewById(R.id.hlist);
                        newFocusedView.setPositionBasedOnLeftOffset(
                                mElementLeftOffset, viewInfo.getWidth());
                    }
                    return super.onKeyDown(keyCode, event);
                }
                return false;
            }
            case KeyEvent.KEYCODE_DPAD_UP: {
                if (getSelectedItemPosition() > 0) {
                    int newPosition = getSelectedItemPosition() - 1;
                    mFocusedView = (HorizListView) getSelectedView()
                            .findViewById(R.id.hlist);
                    FocusedViewInfo viewInfo = mFocusedView.new FocusedViewInfo(
                            mFocusedView.getSelectedView());
                    mElementLeftOffset = viewInfo.getLeft();
                    ((VerticalListInterface) getAdapter())
                            .setCurrentScrollPosition(mTotalLeftOffset,
                                    mElementLeftOffset, viewInfo.getWidth());
                    if (newPosition >= getFirstVisiblePosition()) {
                        HorizListView newFocusedView = (HorizListView) getChildAt(
                                newPosition - getFirstVisiblePosition())
                                .findViewById(R.id.hlist);
                        newFocusedView.setPositionBasedOnLeftOffset(
                                mElementLeftOffset, viewInfo.getWidth());
                    }
                    return super.onKeyDown(keyCode, event);
                }
                return false;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
