package com.iwedia.epg_grid;

/**
 * Class that contains view width, view object pair
 * 
 * @author Branimir Pavlovic
 * @param <T>
 */
public class HorizTimeObject<T> {
    private int mWidth;
    private boolean isRealEvent = true;
    private T mObject;

    public HorizTimeObject(int width, boolean isRealEvent) {
        this(width, null);
        this.isRealEvent = isRealEvent;
    }

    public HorizTimeObject(int width, T object) {
        this.mWidth = width;
        this.mObject = object;
        if (this.mObject == null) {
            isRealEvent = false;
        }
    }

    public int getWidth() {
        return mWidth;
    }

    public T getObject() {
        return mObject;
    }

    public boolean isRealEvent() {
        return isRealEvent;
    }
}
