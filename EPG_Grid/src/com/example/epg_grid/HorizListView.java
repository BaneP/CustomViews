package com.example.epg_grid;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.iwedia.dtv.epg.EpgEvent;

import it.sephiroth.android.library.widget.AbsHListView;
import it.sephiroth.android.library.widget.HListView;

import java.util.ArrayList;

/**
 * Horizontal list view that is populated by some events.
 * 
 * @author Branimir Pavlovic
 */
public class HorizListView extends HListView {
    /**
     * Listener to call when list is scrolled horizontally
     * 
     * @author Branimir Pavlovic
     */
    public interface OnScrollHappenedListener {
        public void scrollTo(HorizListView v, int offset, int totalOffset);
    }

    private OnScrollHappenedListener mScrollHappened;
    /**
     * Flag that indicates if callback should be called
     */
    private boolean shouldISendCallback = true;
    /**
     * Holds current scroll offset of this horizontal list view
     */
    private int mOldOffset = 0;
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
        progress.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
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

    // private int mSelector;
    //
    // protected void onFocusChanged(boolean gainFocus, int direction,
    // Rect previouslyFocusedRect) {
    // Log.i("onFocusChanged", "onFocusChanged");
    // super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    // if (mSelector != 0) {
    // if (gainFocus) {
    // super.setSelector(mSelector);
    // } else {
    // super.setSelector(android.R.color.transparent);
    // }
    // }
    // invalidateViews();
    // }
    //
    // @Override
    // public void setSelector(int sel) {
    // mSelector = sel;
    // super.setSelector(sel);
    // }
    /**
     * Listener for horizontal list view scrolling
     */
    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsHListView view, int scrollState) {
            switch (scrollState) {
                case SCROLL_STATE_IDLE: {
                    isInTouchScroll = false;
                    shouldISendCallback = true;
                    break;
                }
                case SCROLL_STATE_FLING: {
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
            if (getAdapter() != null) {
                if (getAdapter() instanceof HorizontalListInterface) {
                    View c = getChildAt(0);
                    // Calculate current list scroll position
                    int leftOffset = 0;
                    ArrayList<HorizTimeObject<EpgEvent>> events = ((HorizontalListInterface) getAdapter())
                            .getElementWidths();
                    for (int i = 0; i < firstVisibleItem; i++) {
                        leftOffset += events.get(i).getWidth();
                    }
                    if (c != null) {
                        // Current list scroll position minus first child
                        // invisible
                        // part
                        leftOffset = leftOffset - c.getLeft();
                        // Send scroll event trough listener
                        if (mScrollHappened != null && shouldISendCallback
                                && mOldOffset != leftOffset
                                && (hasFocus() || isInTouchScroll)) {
                            mScrollHappened.scrollTo(HorizListView.this,
                                    leftOffset - mOldOffset, leftOffset);
                        }
                    }
                    // Save old offset value
                    mOldOffset = leftOffset;
                } else {
                    throw new RuntimeException(
                            "Adapter is not implementing HorizontalListInterface!");
                }
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
     * Get total offset value from beginning of list for specific position
     * 
     * @param position
     * @return Calculated value
     */
    public int getTotalOffsetForChildAt(int position) {
        if (getAdapter() instanceof HorizontalListInterface) {
            int topOffset = 0;
            ArrayList<HorizTimeObject<EpgEvent>> elementWidths = ((HorizontalListInterface) getAdapter())
                    .getElementWidths();
            for (int i = 0; i < position; i++) {
                topOffset += elementWidths.get(i).getWidth();
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
        if (position > getLastVisiblePosition()) {
            position = getLastVisiblePosition();
        } else if (position < getFirstVisiblePosition()) {
            position = getFirstVisiblePosition();
        }
        int desiredPosition = position - getFirstVisiblePosition();
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
            final int viewWidth, boolean isFromEmpty) {
        Log.d("setPositionBasedOnLeftOffset", "leftOffset=" + leftOffset
                + ", viewWidth=" + viewWidth);
        if (getAdapter() != null) {
            if (getAdapter() instanceof HorizontalListInterface) {
                if (getAdapter().getCount() == 0) {
                    return;
                }
                int desiredIndex = getFirstVisiblePosition();
                View child = null;
                int overlapValue = 0;
                View overlapView = null;
                for (desiredIndex = getFirstVisiblePosition(); desiredIndex <= getLastVisiblePosition(); desiredIndex++) {
                    Log.d("setPositionBasedOnLeftOffset", "get child at "
                            + (desiredIndex - getFirstVisiblePosition()));
                    child = getChildAt(desiredIndex - getFirstVisiblePosition());
                    // If focus is gained from dummy element we just need to
                    // find first visible enabled item
                    if (isFromEmpty) {
                        if (getAdapter().isEnabled(desiredIndex)) {
                            break;
                        } else {
                            continue;
                        }
                    }
                    Log.d("setPositionBasedOnLeftOffset", "child " + child);
                    if (child != null) {
                        // When Old child is inside new child bounds
                        // | | old child | |
                        // | | new child | |
                        if (child.getLeft() <= leftOffset
                                && child.getRight() >= leftOffset + viewWidth) {
                            Log.d("setPositionBasedOnLeftOffset",
                                    "When Old child is inside new child bounds, "
                                            + child.getLeft());
                            overlapView = null;
                            overlapValue = 0;
                            break;
                        }
                        // When Old child is between two possible new child's
                        // bounds
                        // (LEFT CHILD)
                        // | | | old child | |
                        // | new child | new child | |
                        else if (child.getLeft() <= leftOffset
                                && child.getRight() > leftOffset) {
                            overlapView = child;
                            overlapValue = child.getWidth()
                                    - (leftOffset - child.getLeft());
                            Log.d("setPositionBasedOnLeftOffset",
                                    "When Old child is between two possible new child's bounds (LEFT CHILD), "
                                            + child.getLeft() + ", "
                                            + child.getRight());
                        }
                        // When new child is inside old child
                        else if (child.getLeft() > leftOffset
                                && child.getRight() < leftOffset + viewWidth) {
                            Log.d("setPositionBasedOnLeftOffset",
                                    "When new child is inside old child "
                                            + child.getLeft());
                            overlapView = null;
                            overlapValue = 0;
                            break;
                        }
                        // When Old child is between two possible new child's
                        // bounds
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
                            Log.d("setPositionBasedOnLeftOffset",
                                    "When Old child is between two possible new child's bounds (RIGHT CHILD), "
                                            + child.getLeft() + ", "
                                            + child.getRight());
                            break;
                        } else {
                            Log.d("setPositionBasedOnLeftOffset",
                                    "ELSE!!!!!, left=" + child.getLeft()
                                            + ", right=" + child.getRight());
                        }
                    }
                }
                if (overlapView != null) {
                    child = overlapView;
                }
                Log.d("setPositionBasedOnLeftOffset", "child.getLeft()="
                        + child.getLeft() + ", desiredIndex=" + desiredIndex);
                shouldISendCallback = false;
                setSelectionFromLeft(desiredIndex,
                        child == null ? 0 : child.getLeft());
                setSelectionInt(desiredIndex);
                shouldISendCallback = true;
            } else {
                throw new RuntimeException(
                        "Adapter is not implementing HorizontalListInterface!");
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
        Log.d("setPositionBasedOnLeftOffsetFromAdapter", "totalLeftOffset="
                + totalLeftOffset);
        if (getAdapter() != null) {
            if (getAdapter() instanceof HorizontalListInterface) {
                if (getAdapter().getCount() == 0) {
                    return;
                }
                ArrayList<HorizTimeObject<EpgEvent>> elementWidths = ((HorizontalListInterface) getAdapter())
                        .getElementWidths();
                int desiredIndex = 0;
                int widthSum = 0;
                int overlapValue = 0;
                int overlapWidthSum = 0;
                for (int i = 0; i < elementWidths.size(); i++) {
                    // When Old child is inside new child bounds
                    // | | old child | |
                    // | | new child | |
                    if (widthSum <= leftOffset + totalLeftOffset
                            && widthSum + elementWidths.get(i).getWidth() >= totalLeftOffset
                                    + leftOffset + viewWidth) {
                        overlapWidthSum = 0;
                        overlapValue = 0;
                        desiredIndex = i;
                        break;
                    }
                    // When Old child is between two possible new child's bounds
                    // (LEFT CHILD)
                    // | | | old child | |
                    // | new child | new child | |
                    else if (widthSum <= totalLeftOffset + leftOffset
                            && widthSum + elementWidths.get(i).getWidth() > totalLeftOffset
                                    + leftOffset) {
                        overlapWidthSum = widthSum;
                        overlapValue = elementWidths.get(i).getWidth()
                                - (totalLeftOffset + leftOffset - widthSum);
                        desiredIndex = i;
                        widthSum += elementWidths.get(i).getWidth();
                    }
                    // When new child is inside old child
                    else if (widthSum > totalLeftOffset + leftOffset
                            && widthSum + elementWidths.get(i).getWidth() < totalLeftOffset
                                    + leftOffset + viewWidth) {
                        overlapWidthSum = 0;
                        overlapValue = 0;
                        desiredIndex = i;
                        break;
                    }
                    // When Old child is between two possible new child's bounds
                    // (RIGHT CHILD)
                    // | | | old child | |
                    // | new child | new child | |
                    else if (widthSum > totalLeftOffset + leftOffset
                            && widthSum + elementWidths.get(i).getWidth() >= totalLeftOffset
                                    + leftOffset + viewWidth) {
                        int newOverlapValue = totalLeftOffset + leftOffset
                                + viewWidth - widthSum;
                        if (newOverlapValue > overlapValue) {
                            overlapWidthSum = widthSum;
                            desiredIndex = i;
                        } else {
                            desiredIndex = i - 1;
                        }
                        break;
                    } else {
                        desiredIndex = i;
                        widthSum += elementWidths.get(i).getWidth();
                    }
                }
                if (overlapWidthSum != 0) {
                    widthSum = overlapWidthSum;
                }
                shouldISendCallback = false;
                setSelectionFromLeft(desiredIndex, widthSum - totalLeftOffset);
                setSelectionInt(desiredIndex);
                shouldISendCallback = true;
            } else {
                throw new RuntimeException(
                        "Adapter is not implementing HorizontalListInterface!");
            }
        }
    }

    /**
     * This method is used to scroll this list by x amount of pixels
     */
    public void scrollListByPixels(int x) {
        shouldISendCallback = false;
        super.smoothScrollBy(x, 0);
    }

    /**
     * Class that contain necessary informations about currently selected view.
     */
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
