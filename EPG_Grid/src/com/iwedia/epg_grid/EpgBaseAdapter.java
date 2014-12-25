package com.iwedia.epg_grid;

import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;

import java.lang.ref.WeakReference;

/**
 * Created by bane on 10/12/14.
 */
abstract class EpgBaseAdapter extends BaseAdapter implements EpgListInterface {
    protected int mTotalLeftOffset = 0, mLeftOffset = 0, mFocusedViewWidth = 0;

    @Override
    public void setCurrentScrollPosition(int totalLeftOffset, int leftOffset,
            int focusedViewWidth) {
        this.mTotalLeftOffset = totalLeftOffset;
        this.mLeftOffset = leftOffset;
        this.mFocusedViewWidth = focusedViewWidth;
    }

    /**
     * This method is mandatory to call after data set is ready to present in
     * horizontal list view.
     *
     * @param horizList
     * @param adapter
     */
    @Override
    public void refreshHorizontalListWhenDataIsReady(HorizListView horizList,
            BaseAdapter adapter) {
        horizList.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewObserver(horizList));
        horizList.setAdapter(adapter);
        horizList.setPositionBasedOnLeftOffsetFromAdapter(mTotalLeftOffset,
                mLeftOffset, mFocusedViewWidth);
    }

    /**
     * Class for refreshing new adapter views
     */
    private class ViewObserver implements
            ViewTreeObserver.OnGlobalLayoutListener {
        WeakReference<HorizListView> mViewToObserve;

        public ViewObserver(HorizListView viewToObserve) {
            mViewToObserve = new WeakReference<HorizListView>(viewToObserve);
        }

        @Override
        public void onGlobalLayout() {
            ViewTreeObserver observer = mViewToObserve.get()
                    .getViewTreeObserver();
            observer.removeOnGlobalLayoutListener(this);
            mViewToObserve.get().setPositionBasedOnLeftOffsetFromAdapter(
                    mTotalLeftOffset, mLeftOffset, mFocusedViewWidth);
        }
    }
}
