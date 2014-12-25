package com.iwedia.epg_grid;

import android.widget.BaseAdapter;

/**
 * Interface that EPG list adapter should implement
 *
 * @author Branimir Pavlovic
 */
public interface EpgListInterface {
    public void setCurrentScrollPosition(int totalLeftOffset, int leftOffset,
            int focusedViewWidth);

    public void refreshHorizontalListWhenDataIsReady(HorizListView horizList,
            BaseAdapter adapter);
}
