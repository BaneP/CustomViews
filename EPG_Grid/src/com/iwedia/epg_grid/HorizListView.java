package com.iwedia.epg_grid;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.HListView;

/**
 * Horizontal list view that is populated by some events.
 * 
 * @author Branimir Pavlovic
 */
public class HorizListView extends HListView {
    private static final String TAG = "HorizListView";

    /**
     * Listener to call when list is scrolled horizontally
     * 
     * @author Branimir Pavlovic
     */
    public interface OnScrollHappenedListener {
        public void scrollTo(HorizListView v, int totalOffset);
    }

    private OnScrollHappenedListener mScrollHappened;
    /**
     * Flag that indicates if callback should be called
     */
    private boolean shouldISendCallback = true;
    /**
     * Flag that indicates if this list view is in touch scroll
     */
    private boolean isInTouchScroll;
    /**
     * Flag that indicates if this list view should be scrollable by user
     */
    private boolean canScrollManually = true;

    public HorizListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public HorizListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorizListView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setDividerWidth(0);
        setOverScrollMode(OVER_SCROLL_NEVER);
        ProgressBar progress = new ProgressBar(context);
        progress.setFocusable(true);
        progress.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(progress);
        }
        setEmptyView(progress);
    }

    public void registerTouchScrollListener() {
        setOnScrollListener(mOnScrollListener);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof HorizontalListInterface) && adapter != null) {
            throw new IllegalArgumentException(
                    "Adapter must implement HorizontalListInterface!");
        }
        super.setAdapter(adapter);
    }

    /**
     * Listener for horizontal list view scrolling
     */
    private final OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsHListView view, int scrollState) {
            switch (scrollState) {
                case SCROLL_STATE_IDLE: {
                    isInTouchScroll = false;
                    shouldISendCallback = true;
                    break;
                }
                case SCROLL_STATE_TOUCH_SCROLL: {
                    isInTouchScroll = true;
                    break;
                }
                default:
                    break;
            }
        }

        @Override
        public void onScroll(AbsHListView view, int firstVisibleItem,
                int visibleItemCount, int totalItemCount) {
            int leftOffset = getCurrentScrollPositionInPixels();
            // Send scroll event trough listener
            if (mScrollHappened != null && shouldISendCallback
                    && (hasFocus() || isInTouchScroll)) {
                mScrollHappened.scrollTo(HorizListView.this, leftOffset);
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!canScrollManually) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * Get view position information for specific position
     * 
     * @param position
     * @return Object that contains desired view position informations
     */
    public FocusedViewInfo getViewInfoForElementAt(int position) {
        if (position > getLastVisiblePosition()) {
            position = getLastVisiblePosition();
        } else if (position < getFirstVisiblePosition()) {
            position = getFirstVisiblePosition();
        }
        final View c = getChildAt(position - getFirstVisiblePosition());
        return new FocusedViewInfo(c, position);
    }

    /**
     * Selects new child based on offset from another HorizListView
     * 
     * @param leftOffset
     *        Offset from another HorizListView child
     * @param viewWidth
     *        Width of previously selected child
     */
    public void setPositionBasedOnLeftOffset(final int leftOffset,
            final int viewWidth, boolean isFromEmpty) {
        // Log.d("setPositionBasedOnLeftOffset", "leftOffset=" + leftOffset
        // + ", viewWidth=" + viewWidth);
        final ListAdapter adapter = getAdapter();
        if (adapter != null) {
            if (adapter.getCount() == 0) {
                return;
            }
            int desiredIndex = getFirstVisiblePosition();
            int overlapValue = -1, currentOverlapValue = 0;
            View desiredView = null;
            final int listWidth = getWidth();
            View child = null;
            boolean isEnabled = true;
            for (int i = getFirstVisiblePosition(); i <= getLastVisiblePosition(); i++) {
                child = getChildAt(i - getFirstVisiblePosition());
                isEnabled = adapter.isEnabled(i);
                // find overlap value for current view
                currentOverlapValue = Math.min(leftOffset + viewWidth,
                        Math.min(child.getRight(), listWidth))
                        - Math.max(
                                leftOffset,
                                Math.max(child.getLeft()
                                        - (i == 0 ? 0 : getDividerWidth()), 0));
                // Find biggest overlap value and save it
                if (currentOverlapValue > overlapValue && isEnabled) {
                    desiredView = child;
                    desiredIndex = i;
                    overlapValue = currentOverlapValue;
                }
            }
            if (desiredView != null) {
                shouldISendCallback = false;
                setSelectionFromLeft(desiredIndex, desiredView == null ? 0
                        : desiredView.getLeft());
                setSelectionInt(desiredIndex);
                shouldISendCallback = true;
            }
        }
    }

    /**
     * Selects new child based on offset from another HorizListView, this method
     * should be called from adapter because it may have to calculate invisible
     * items and its not accurate as {@link setPositionBasedOnLeftOffset()}
     * method
     * 
     * @param totalLeftOffset
     *        Left offset of list
     * @param leftOffset
     *        Left offset of view
     * @param viewWidth
     *        Width of previously selected child
     */
    public void setPositionBasedOnLeftOffsetFromAdapter(
            final int totalLeftOffset, final int leftOffset, final int viewWidth) {
        // Log.d(TAG, "totalLeftOffset=" + totalLeftOffset + ", leftOffset="
        // + leftOffset + ", viewWidth=" + viewWidth);
        final ListAdapter adapter = getAdapter();
        if (adapter != null) {
            if (adapter.getCount() == 0) {
                return;
            }
            final HorizontalListInterface horizAdapter = ((HorizontalListInterface) adapter);
            int desiredIndex = 0, desiredWidthSum = 0;;
            int widthSum = 0;
            int overlapValue = -1, currentOverlapValue = 0;
            boolean isEnabled = true;
            final int listWidth = getWidth() + totalLeftOffset;
            final int rightEdgeOfView = totalLeftOffset + leftOffset
                    + viewWidth;
            final int leftEdgeOfView = totalLeftOffset + leftOffset;
            final int size = getAdapter().getCount();
            for (int i = 0; i < size; i++) {
                final int elementWidth = horizAdapter.getElementWidth(i);
                isEnabled = adapter.isEnabled(i);
                // find overlap value for current view
                currentOverlapValue = Math.min(rightEdgeOfView,
                        Math.min(widthSum + elementWidth, listWidth))
                        - Math.max(leftEdgeOfView,
                                Math.max(widthSum, totalLeftOffset));
                // Find biggest overlap value and save it
                if (currentOverlapValue > overlapValue && isEnabled) {
                    desiredIndex = i;
                    desiredWidthSum = widthSum;
                    overlapValue = currentOverlapValue;
                }
                widthSum += elementWidth;
                // View is outside of listview bounds
                if (widthSum > listWidth) {
                    break;
                }
            }
            shouldISendCallback = false;
            setSelectionFromLeft(desiredIndex, desiredWidthSum
                    - totalLeftOffset
                    + (desiredIndex > 0 ? getDividerWidth() : 0));
            setSelectionInt(desiredIndex);
            shouldISendCallback = true;
        }
    }

    /**
     * This method is used to scroll this list by x amount of pixels
     */
    public void scrollListToPixel(int x) {
        // Log.d(TAG, "SCROLL LIST TO " + x);
        int currentX = getCurrentScrollPositionInPixels();
        // Log.d(TAG, "CURRENT SCROLL POSITION " + currentX);
        // Calculate how much should list scroll
        currentX = x - currentX;
        // Log.d(TAG, "SMOOTH SCROLL BY " + currentX);
        if (currentX != 0) {
            shouldISendCallback = false;
            super.smoothScrollBy(currentX, 0);
        }
    }

    public void scrollListToPixelWithCallback(int x){
        int currentX = getCurrentScrollPositionInPixels();
        currentX = x - currentX;
        if (currentX != 0) {
            super.smoothScrollBy(currentX, 0);
        }
    }

    /**
     * Returns current scroll value
     * 
     * @return Scrolled position
     */
    public int getCurrentScrollPositionInPixels() {
        final ListAdapter adapter = getAdapter();
        if (adapter != null) {
            final View c = getChildAt(0);
            // Calculate current list scroll position
            int leftOffset = 0;
            final HorizontalListInterface horizAdapter = ((HorizontalListInterface) adapter);
            final int firstVisibleItem = getFirstVisiblePosition();
            int i;
            for (i = firstVisibleItem; --i >= 0;) {
                leftOffset += horizAdapter.getElementWidth(i);
            }
            if (c != null) {
                // Current list scroll position minus first child
                // invisible
                // part
                leftOffset -= c.getLeft();
                // Add divider width into account
                if (firstVisibleItem > 0) {
                    leftOffset += getDividerWidth();
                }
            }
            return leftOffset;
        }
        return INVALID_POSITION;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                final boolean retval = super.onKeyDown(keyCode, event);
                // Bug fix for scenario where item is showing but right key is
                // ignored
                if (!retval) {
                    smoothScrollBy(50, 0);
                }
                // Bug fix for scenario when list is fully scrolled but callback
                // is not invoked
                mOnScrollListener.onScroll(null, 0, 0, 0);
                return true;
            }
            case KeyEvent.KEYCODE_DPAD_LEFT: {
                final boolean retval = super.onKeyDown(keyCode, event);
                // Bug fix for scenario where item is showing but left key is
                // ignored
                if (!retval) {
                    smoothScrollBy(-50, 0);
                }
                // Bug fix for scenario when list is fully scrolled but callback
                // is not invoked
                mOnScrollListener.onScroll(null, 0, 0, 0);
                return true;
            }
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Class that contain necessary informations about currently selected view.
     */
    class FocusedViewInfo {
        private int mLeft = 0;
        private int mWidth = 0;

        public FocusedViewInfo(View focusedView, int position) {
            if (focusedView != null) {
                mLeft = focusedView.getLeft();
                mWidth = focusedView.getWidth();
                // We must take divider width into account
                if (position > 0) {
                    mLeft -= getDividerWidth();
                    mWidth += getDividerWidth();
                }
            }
        }

        public int getLeft() {
            return mLeft;
        }

        public int getWidth() {
            return mWidth;
        }
    }

    public OnScrollHappenedListener getOnScrollHappenedListener() {
        return mScrollHappened;
    }

    public void setOnScrollHappenedListener(
            OnScrollHappenedListener mScrollHappened) {
        registerTouchScrollListener();
        this.mScrollHappened = mScrollHappened;
    }

    public boolean isManuallyScrollable() {
        return canScrollManually;
    }

    public void setManuallyScrollable(boolean canScrollManually) {
        this.canScrollManually = canScrollManually;
    }
}
