package com.example.epg_grid;

import android.util.SparseIntArray;

/**
 * Interface that horizontal list adapter should implement
 * 
 * @author Branimir Pavlovic
 */
public interface HorizontalListInterface {
    public SparseIntArray getElementWidths();

    public int getElementWidth(int position);
}
