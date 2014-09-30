package com.example.epg_grid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.epg_grid.HorizListView.FocusedViewInfo;
import com.example.epg_grid.HorizListView.OnScrollHappenedListener;

public class EpgListView extends ListView {
    private HorizListView mFocusedView;
    private int mTotalLeftOffset = 0;
    private int mElementLeftOffset = 0;

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
        if (!(adapter instanceof VerticalListInterface)) {
            throw new RuntimeException(
                    "Adapter is not implementing VerticalListInterface!");
        }
        super.setAdapter(adapter);
    }

    /**
     * Method called to scroll vertical list view children
     * 
     * @param v
     *        View that is scrolled
     * @param offset
     *        Offset difference of scroll (how much pixels view is scrolled)
     * @param totalOffset
     *        Total left offset of scrolled list
     */
    public void scrollTo(HorizListView v, int offset, int totalOffset) {
        mTotalLeftOffset = totalOffset;
        if (getChildCount() > 0) {
            HorizListView hlist = (HorizListView) getChildAt(0).findViewById(
                    R.id.epg_hlist);
            FocusedViewInfo viewInfo = hlist.getViewInfoForElementAt(0);
            ((VerticalListInterface) getAdapter()).setCurrentScrollPosition(
                    mTotalLeftOffset, viewInfo.getLeft(), viewInfo.getWidth());
            for (int i = 0; i < getChildCount(); i++) {
                hlist = (HorizListView) getChildAt(i).findViewById(
                        R.id.epg_hlist);
                if (hlist != v) {
                    hlist.scrollListByPixels(offset);
                }
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
        int oneMinuteWidth = INVALID_POSITION;
        if (getParent() instanceof EpgGrid) {
            oneMinuteWidth = ((EpgGrid) getParent()).getOneMinutePixelWidth();
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN: {
                if (getSelectedItemPosition() < getAdapter().getCount() - 1) {
                    int newPosition = getSelectedItemPosition() + 1;
                    mFocusedView = (HorizListView) getSelectedView()
                            .findViewById(R.id.epg_hlist);
                    FocusedViewInfo viewInfo = mFocusedView
                            .getViewInfoForElementAt(mFocusedView
                                    .getSelectedItemPosition());
                    mElementLeftOffset = viewInfo.getLeft();
                    ((VerticalListInterface) getAdapter())
                            .setCurrentScrollPosition(mTotalLeftOffset,
                                    mElementLeftOffset, viewInfo.getWidth());
                    // We must first call onKeyDown and then set selection to
                    // list
                    // because of onLayout() method was called several times
                    boolean retVal = super.onKeyDown(keyCode, event);
                    if (newPosition <= getLastVisiblePosition()) {
                        HorizListView newFocusedView = (HorizListView) getChildAt(
                                newPosition - getFirstVisiblePosition())
                                .findViewById(R.id.epg_hlist);
                        // This will be NULL if adapter is empty
                        if (newFocusedView != null) {
                            newFocusedView.setPositionBasedOnLeftOffset(
                                    mElementLeftOffset, viewInfo.getWidth(),
                                    viewInfo.getWidth() == oneMinuteWidth
                                            * EpgGrid.NUMBER_OF_MINUTES_IN_DAY);
                        }
                    }
                    return retVal;
                }
                return false;
            }
            case KeyEvent.KEYCODE_DPAD_UP: {
                if (getSelectedItemPosition() > 0) {
                    int newPosition = getSelectedItemPosition() - 1;
                    mFocusedView = (HorizListView) getSelectedView()
                            .findViewById(R.id.epg_hlist);
                    FocusedViewInfo viewInfo = mFocusedView
                            .getViewInfoForElementAt(mFocusedView
                                    .getSelectedItemPosition());
                    mElementLeftOffset = viewInfo.getLeft();
                    ((VerticalListInterface) getAdapter())
                            .setCurrentScrollPosition(mTotalLeftOffset,
                                    mElementLeftOffset, viewInfo.getWidth());
                    // We must first call onKeyDown and then set selection to
                    // list
                    // because of onLayout() method was called several times
                    boolean retVal = super.onKeyDown(keyCode, event);
                    if (newPosition >= getFirstVisiblePosition()) {
                        HorizListView newFocusedView = (HorizListView) getChildAt(
                                newPosition - getFirstVisiblePosition())
                                .findViewById(R.id.epg_hlist);
                        // This will be NULL if adapter is empty
                        if (newFocusedView != null) {
                            newFocusedView.setPositionBasedOnLeftOffset(
                                    mElementLeftOffset, viewInfo.getWidth(),
                                    viewInfo.getWidth() == oneMinuteWidth
                                            * EpgGrid.NUMBER_OF_MINUTES_IN_DAY);
                        }
                    }
                    return retVal;
                }
                return false;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
