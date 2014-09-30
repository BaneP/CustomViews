package com.example.epg_grid;

import android.util.SparseIntArray;

import com.iwedia.dtv.epg.EpgEvent;

import java.util.ArrayList;

/**
 * Interface that horizontal list adapter should implement
 * 
 * @author Branimir Pavlovic
 */
public interface HorizontalListInterface {
    public ArrayList<HorizTimeObject<EpgEvent>> getElementWidths();

    public int getElementWidth(int position);
}
