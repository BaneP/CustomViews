package com.example.epg_grid.dtv;

import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.types.TimeDate;

public class MyEpgEvent {
    private EpgEvent mEvent;
    /**
     * Duration in seconds
     */
    private long mDuration;
    /**
     * Event width in pixels
     */
    private int mEventWidth;

    public MyEpgEvent(EpgEvent mEvent, int oneMinutePixelWidth) {
        this.mEvent = mEvent;
        TimeDate startTime = this.mEvent.getStartTime();
        TimeDate endTime = this.mEvent.getEndTime();
        mDuration = endTime.getCalendar().getTimeInMillis()
                - startTime.getCalendar().getTimeInMillis();
        mDuration = mDuration / 1000;
        float numberOfMins = ((float) mDuration) / 60f;
        mEventWidth = (int) (numberOfMins * oneMinutePixelWidth);
    }

    public EpgEvent getEvent() {
        return mEvent;
    }

    public long getDuration() {
        return mDuration;
    }

    public int getEventWidth() {
        return mEventWidth;
    }
}
