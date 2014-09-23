package com.example.epg_grid;

import com.example.epg_grid.HorizListView.OnScrollHappenedListener;

/**
 * Interface that vertical list adapter should implement
 * 
 * @author Branimir Pavlovic
 */
public interface VerticalListInterface {
    public void setCurrentScrollPosition(int totalLeftOffset, int leftOffset,
            int focusedViewWidth);

    public void setOnScrollHappenedListener(
            OnScrollHappenedListener mOnScrollHappenedListener);

    public OnScrollHappenedListener getOnScrollHappenedListener();
}
