package com.example.epg_grid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import it.sephiroth.android.library.widget.HListView;

public class HorizListView extends HListView {
    public interface OnScrollHappenedListener {
        public void scrollTo(HorizListView v, int offset, int totalOffset);
    }

    private OnScrollHappenedListener mScrollHappened;
    private boolean shouldIScroll = true;
    private boolean isPressed = false;
    private int mOldOffset = 0;

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
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int childPosition = getPositionForView(child);
        // Log.d("SHILD WIDTH", "" + child.getWidth());
        if (getAdapter() instanceof HorizontalListInterface) {
            ((HorizontalListInterface) getAdapter()).getElementWidths().put(
                    childPosition, child.getWidth());
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing HorizontalListInterface!");
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    @Override
    @SuppressLint("Override")
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                isPressed = true;
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                isPressed = false;
                break;
            }
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (getAdapter() instanceof HorizontalListInterface) {
            View c = getChildAt(0);
            // Calculate current list scroll position
            int leftOffset = 0;
            for (int i = 0; i < getFirstVisiblePosition(); i++) {
                leftOffset += ((HorizontalListInterface) getAdapter())
                        .getElementWidths().get(i);
            }
            if (c != null) {
                // Current list scroll position minus first child invisible part
                leftOffset = leftOffset - c.getLeft();
                // Send scroll event trough listener
                if (mScrollHappened != null && shouldIScroll
                        && mOldOffset != leftOffset
                        && (hasFocus() || isPressed)) {
                    mScrollHappened.scrollTo(this, leftOffset - mOldOffset,
                            leftOffset);
                }
            }
            // Save old offset value
            mOldOffset = leftOffset;
            super.onScrollChanged(l, t, oldl, oldt);
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing HorizontalListInterface!");
        }
    }

    /**
     * Get total offset value from beginning of list for specific position
     * 
     * @param position
     * @return Calculated value
     */
    public int getTotalOffsetForChildAt(int position) {
        if (getAdapter() instanceof HorizontalListInterface) {
            int topOffset = 0;
            SparseIntArray elementWidths = ((HorizontalListInterface) getAdapter())
                    .getElementWidths();
            for (int i = 0; i < position; i++) {
                topOffset += elementWidths.get(i);
            }
            return topOffset;
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing HorizontalListInterface!");
        }
    }

    /**
     * Get view position information for specific position
     * 
     * @param position
     * @return Object that contains desired view position informations
     */
    public FocusedViewInfo getViewInfoForElementAt(int position) {
        int desiredPosition = position - getFirstVisiblePosition();
        if (desiredPosition > getLastVisiblePosition()) {
            desiredPosition = getLastVisiblePosition();
        } else if (desiredPosition < getFirstVisiblePosition()) {
            desiredPosition = getFirstVisiblePosition();
        }
        View c = getChildAt(desiredPosition);
        return new FocusedViewInfo(c);
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
            final int viewWidth) {
        if (getAdapter() instanceof HorizontalListInterface) {
            int desiredIndex = getFirstVisiblePosition();
            View child = null;
            int overlapValue = 0;
            View overlapView = null;
            for (desiredIndex = getFirstVisiblePosition(); desiredIndex < getLastVisiblePosition(); desiredIndex++) {
                child = getChildAt(desiredIndex - getFirstVisiblePosition());
                // TODO We need better condition here
                if (child != null) {
                    // When Old child is inside new child bounds
                    // | | old child | |
                    // | | new child | |
                    if (child.getLeft() <= leftOffset
                            && child.getRight() >= leftOffset + viewWidth) {
                        overlapView = null;
                        overlapValue = 0;
                        break;
                    }
                    // When Old child is between two possible new child's bounds
                    // (LEFT CHILD)
                    // | | | old child | |
                    // | new child | new child | |
                    else if (child.getLeft() <= leftOffset
                            && child.getRight() > leftOffset) {
                        overlapView = child;
                        overlapValue = child.getWidth()
                                - (leftOffset - child.getLeft());
                    }
                    // When new child is inside old child
                    else if (child.getLeft() > leftOffset
                            && child.getRight() < leftOffset + viewWidth) {
                        overlapView = null;
                        overlapValue = 0;
                        break;
                    }
                    // When Old child is between two possible new child's bounds
                    // (RIGHT CHILD)
                    // | | | old child | |
                    // | new child | new child | |
                    else if (child.getLeft() > leftOffset
                            && child.getRight() >= leftOffset + viewWidth) {
                        int newOverlapValue = leftOffset + viewWidth
                                - child.getLeft();
                        if (newOverlapValue > overlapValue) {
                            overlapView = child;
                        } else {
                            desiredIndex--;
                        }
                        break;
                    }
                }
            }
            if (overlapView != null) {
                child = overlapView;
            }
            setSelectionFromLeft(desiredIndex,
                    child == null ? 0 : child.getLeft());
            setSelectionInt(desiredIndex);
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing HorizontalListInterface!");
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
     * @param offset
     *        Left offset of view
     * @param viewWidth
     *        Width of previously selected child
     */
    public void setPositionBasedOnLeftOffsetFromAdapter(
            final int totalLeftOffset, final int offset, final int viewWidth) {
        Log.d("setPositionBasedOnLeftOffsetFromAdapter", "OFFSET DESIRED: "
                + offset + ", totalLeftOffset=" + totalLeftOffset);
        if (getAdapter() instanceof HorizontalListInterface) {
            SparseIntArray elementWidths = ((HorizontalListInterface) getAdapter())
                    .getElementWidths();
            int desiredIndex = 0;
            int widthSum = 0;
            for (int i = 0; i < elementWidths.size(); i++) {
                if (widthSum + elementWidths.get(i) < totalLeftOffset + offset) {
                    desiredIndex = i;
                    widthSum += elementWidths.get(i);
                } else {
                    break;
                }
            }
            Log.d("setPositionBasedOnLeftOffsetFromAdapter", "desiredIndex="
                    + desiredIndex + ", offset=" + (widthSum - totalLeftOffset)
                    + ", widthSum=" + widthSum);
            shouldIScroll = false;
            setSelectionFromLeft(desiredIndex, widthSum - totalLeftOffset);
            setSelectionInt(desiredIndex);
            shouldIScroll = true;
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing HorizontalListInterface!");
        }
    }

    public void scrollListByPixels(int y) {
        shouldIScroll = false;
        super.smoothScrollBy(y, 0);
        shouldIScroll = true;
    }

    class FocusedViewInfo {
        private int mLeft = 0;
        private int mWidth = 0;

        public FocusedViewInfo(View focusedView) {
            if (focusedView != null) {
                mLeft = focusedView.getLeft();
                mWidth = focusedView.getWidth();
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
        this.mScrollHappened = mScrollHappened;
    }
}
