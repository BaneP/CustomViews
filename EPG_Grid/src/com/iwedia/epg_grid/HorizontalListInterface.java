package com.iwedia.epg_grid;

/**
 * Interface that horizontal list adapter should implement. Adapter should set
 * fixed width of every child view and provide its width in any time by
 * implementing this interface.
 * 
 * @author Branimir Pavlovic
 */
public interface HorizontalListInterface extends EpgListInterface{
    public int getElementWidth(int position);
}
