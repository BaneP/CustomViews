package com.iwedia.epg_grid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.iwedia.epg_grid.HorizListView.FocusedViewInfo;

import java.util.Calendar;

/**
 * Vertical list that contains horizontal list items that presents one channel
 * and its EPG events.
 * 
 * @author Branimir Pavlovic
 */
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

    /**
     * This initialization is very important, because horizontal list items will
     * take focus from this list view.
     */
    private void init(Context context) {
        setItemsCanFocus(true);
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
    public void scrollTo(HorizListView v, int totalOffset) {
        mTotalLeftOffset = totalOffset;
        if (getChildCount() > 0) {
            HorizListView hlist = null;
            int i;
            EpgViewHolder viewHolder = null;
            for (i = getChildCount(); --i >= 0;) {
                viewHolder = (EpgViewHolder) getChildAt(i).getTag();
                hlist = viewHolder.getHList();
                // Set current scroll position to adapter
                // Take first element because all elements have same scroll
                // value
                if (i == 0) {
                    final FocusedViewInfo viewInfo = hlist
                            .getViewInfoForElementAt(0);
                    ((VerticalListInterface) getAdapter())
                            .setCurrentScrollPosition(mTotalLeftOffset,
                                    viewInfo.getLeft(), viewInfo.getWidth());
                }
                // Scroll other list views to desired scroll value
                if (hlist != v) {
                    hlist.scrollListToPixel(totalOffset);
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getSelectedItemPosition() == INVALID_POSITION) {
            return super.onKeyDown(keyCode, event);
        }
        int oneMinuteWidth = INVALID_POSITION;
        int startHour = 0;
        if (getParent() instanceof EpgGrid) {
            oneMinuteWidth = ((EpgGrid) getParent()).getOneMinutePixelWidth();
            startHour = ((EpgGrid) getParent()).getTimeTableStartTime().get(
                    Calendar.HOUR_OF_DAY);
        }
        final int dayMaxWidth = oneMinuteWidth
                * ((24 - startHour) * EpgGrid.NUMBER_OF_MINUTES_IN_HOUR);
        EpgViewHolder viewHolder = null;
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN: {
                if (getSelectedItemPosition() < getAdapter().getCount() - 1) {
                    final int newPosition = getSelectedItemPosition() + 1;
                    viewHolder = (EpgViewHolder) getSelectedView().getTag();
                    mFocusedView = viewHolder.getHList();
                    // Get selected view informations
                    FocusedViewInfo viewInfo = mFocusedView
                            .getViewInfoForElementAt(mFocusedView
                                    .getSelectedItemPosition());
                    mElementLeftOffset = viewInfo.getLeft();
                    ((VerticalListInterface) getAdapter())
                            .setCurrentScrollPosition(mTotalLeftOffset,
                                    mElementLeftOffset, viewInfo.getWidth());
                    if (newPosition <= getLastVisiblePosition()) {
                        View nextView = getChildAt(newPosition
                                - getFirstVisiblePosition());
                        // We must first select next child and then set
                        // selection to
                        // horizontal list
                        // because of onLayout() method was called several
                        // times.
                        if (newPosition < getLastVisiblePosition()) {
                            setSelectionFromTop(newPosition, nextView.getTop());
                        }
                        // Last element must be whole visible
                        else {
                            setSelectionFromTop(newPosition, getHeight()
                                    - nextView.getHeight());
                        }
                        viewHolder = (EpgViewHolder) nextView.getTag();
                        HorizListView newFocusedView = viewHolder.getHList();
                        // This will be NULL if adapter is empty
                        if (newFocusedView != null) {
                            boolean isEmpty = viewInfo.getWidth() == dayMaxWidth;
                            newFocusedView.setPositionBasedOnLeftOffset(
                                    mElementLeftOffset, viewInfo.getWidth(),
                                    isEmpty);
                        }
                    }
                    // For elements not yet visible
                    else {
                        setSelectionFromTop(newPosition, getSelectedView()
                                .getTop());
                    }
                    return true;
                }
                return false;
            }
            case KeyEvent.KEYCODE_DPAD_UP: {
                if (getSelectedItemPosition() > 0) {
                    int newPosition = getSelectedItemPosition() - 1;
                    viewHolder = (EpgViewHolder) getSelectedView().getTag();
                    mFocusedView = viewHolder.getHList();
                    // Get selected view informations
                    FocusedViewInfo viewInfo = mFocusedView
                            .getViewInfoForElementAt(mFocusedView
                                    .getSelectedItemPosition());
                    mElementLeftOffset = viewInfo.getLeft();
                    ((VerticalListInterface) getAdapter())
                            .setCurrentScrollPosition(mTotalLeftOffset,
                                    mElementLeftOffset, viewInfo.getWidth());
                    if (newPosition >= getFirstVisiblePosition()) {
                        View nextView = getChildAt(newPosition
                                - getFirstVisiblePosition());
                        // We must first select next child and then set
                        // selection to
                        // horizontal list
                        // because of onLayout() method was called several
                        // times.
                        if (newPosition > getFirstVisiblePosition()) {
                            setSelectionFromTop(newPosition, nextView.getTop());
                        }
                        // First element must be whole visible
                        else {
                            setSelectionFromTop(newPosition, 0);
                        }
                        viewHolder = (EpgViewHolder) nextView.getTag();
                        HorizListView newFocusedView = viewHolder.getHList();
                        // This will be NULL if adapter is empty
                        if (newFocusedView != null) {
                            boolean isEmpty = viewInfo.getWidth() == dayMaxWidth;
                            newFocusedView.setPositionBasedOnLeftOffset(
                                    mElementLeftOffset, viewInfo.getWidth(),
                                    isEmpty);
                        }
                    }
                    // For elements not yet visible
                    else {
                        setSelectionFromTop(newPosition, 0);
                    }
                    return true;
                }
                return false;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
