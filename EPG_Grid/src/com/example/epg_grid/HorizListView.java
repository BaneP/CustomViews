package com.example.epg_grid;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import it.sephiroth.android.library.widget.HListView;

public class HorizListView extends HListView {
    public interface OnScrollHappenedListener {
        public void scrollTo(HorizListView v, int offset, int totalOffset,
                int position);
    }

    private OnScrollHappenedListener mScrollHappened;
    private boolean shouldIScroll = true;
    private int mOldOffset = 0;

    public HorizListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HorizListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizListView(Context context) {
        super(context);
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
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (getAdapter() instanceof HorizontalListInterface) {
            View c = getChildAt(0);
            int leftOffset = 0;
            for (int i = 0; i < getFirstVisiblePosition(); i++) {
                leftOffset += ((HorizontalListInterface) getAdapter())
                        .getElementWidths().get(i);
            }
            if (c != null) {
                leftOffset = leftOffset - c.getLeft();
                if (mScrollHappened != null && shouldIScroll
                        && mOldOffset != leftOffset && hasFocus()) {
                    Log.d("onScrollChanged", "leftOffset=" + leftOffset
                            + ", getSelectedItemPosition()="
                            + getSelectedItemPosition());
                    mScrollHappened.scrollTo(this, leftOffset - mOldOffset,
                            leftOffset, getSelectedItemPosition());
                }
            }
            mOldOffset = leftOffset;
            super.onScrollChanged(l, t, oldl, oldt);
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing HorizontalListInterface!");
        }
    }

    public int getTotalOffsetForChildAt(int position) {
        if (getAdapter() instanceof HorizontalListInterface) {
            int topOffset = 0;
            for (int i = 0; i < position; i++) {
                topOffset += ((HorizontalListInterface) getAdapter())
                        .getElementWidths().get(i);
            }
            return topOffset;
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing HorizontalListInterface!");
        }
    }

    public FocusedViewInfo getViewInfoForElementAt(int position) {
        int desiredPosition = position - getFirstVisiblePosition();
        Log.d("GET CHILD AT", "" + desiredPosition);
        if (desiredPosition > getLastVisiblePosition()) {
            desiredPosition = getLastVisiblePosition();
        }
        Log.d("GET CHILD AT", "" + desiredPosition);
        View c = getChildAt(desiredPosition);
        return new FocusedViewInfo(c);
    }

    public void setPositionBasedOnLeftOffset(final int leftOffset,
            final int viewWidth) {
        Log.d("setPositionBasedOnLeftOffset", "OFFSET DESIRED: " + leftOffset);
        if (getAdapter() instanceof HorizontalListInterface) {
            int desiredIndex = getFirstVisiblePosition();
            View child = null;
            Log.d("setPositionBasedOnLeftOffset", "getFirstVisiblePosition()="
                    + getFirstVisiblePosition() + ", getLastVisiblePosition()="
                    + getLastVisiblePosition());
            for (desiredIndex = getFirstVisiblePosition(); desiredIndex < getLastVisiblePosition(); desiredIndex++) {
                child = getChildAt(desiredIndex - getFirstVisiblePosition());
                // TODO We need better condition here
                if (child != null && child.getLeft() >= leftOffset) {
                    break;
                }
            }
            // while (leftOffset < offset) {
            // leftOffset += elementWidths.get(desiredIndex)
            // + hListView.getDividerWidth();
            // desiredIndex++;
            // }
            Log.d("SET SELECTION FROM TOP", "POSITION=" + desiredIndex
                    + ", offset=" + (child == null ? 0 : child.getLeft()));
            setSelectionFromLeft(desiredIndex,
                    child == null ? 0 : child.getLeft());
            setSelectionInt(desiredIndex);
        } else {
            throw new RuntimeException(
                    "Adapter is not implementing HorizontalListInterface!");
        }
    }

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

    public class FocusedViewInfo {
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

    public void scrollListByPixels(int y) {
        shouldIScroll = false;
        super.smoothScrollBy(y, 0);
        shouldIScroll = true;
    }

    public OnScrollHappenedListener getOnScrollHappenedListener() {
        return mScrollHappened;
    }

    public void setOnScrollHappenedListener(
            OnScrollHappenedListener mScrollHappened) {
        this.mScrollHappened = mScrollHappened;
    }
}
