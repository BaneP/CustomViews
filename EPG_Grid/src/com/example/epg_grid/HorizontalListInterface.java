package com.example.epg_grid;

import android.util.SparseIntArray;

public interface HorizontalListInterface {
    public SparseIntArray getElementWidths();

    public int getElementWidth(int position);
}
