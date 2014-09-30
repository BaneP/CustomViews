package com.example.epg_grid.dtv;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewTreeObserver;

import com.example.epg_grid.EpgGrid;
import com.example.epg_grid.HorizListAdapter;
import com.example.epg_grid.HorizListView;
import com.example.epg_grid.HorizTimeObject;
import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.types.TimeDate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;

public class EpgAsyncTaskLoader extends
        AsyncTask<Integer, Void, ArrayList<EpgEvent>> {
    private ArrayList<HorizTimeObject<EpgEvent>> mElementWidths;
    private HorizListView mHorizList;
    private WeakReference<Context> mCtx;
    private int mTotalLeftOffset;
    private int mLeftOffset;
    private int mFocusedViewWidth;

    public EpgAsyncTaskLoader(HorizListView horizList, Context ctx,
            int mTotalLeftOffset, int mLeftOffset, int mFocusedViewWidth) {
        mCtx = new WeakReference<Context>(ctx);
        this.mHorizList = horizList;
        this.mTotalLeftOffset = mTotalLeftOffset;
        this.mLeftOffset = mLeftOffset;
        this.mFocusedViewWidth = mFocusedViewWidth;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ArrayList<EpgEvent> doInBackground(Integer... params) {
        /**
         * Take parameters
         */
        final int channelIndex = params[0];
        final int oneMinutePixelWidth = params[1];
        final int day = params[2];
        final int dayMaxWidth = oneMinutePixelWidth
                * EpgGrid.NUMBER_OF_MINUTES_IN_DAY;
        /**
         * Load EPG events
         */
        ArrayList<EpgEvent> events = DvbManager.getInstance().loadEvents(
                channelIndex, oneMinutePixelWidth, day);
        /**
         * Create start time and end time
         */
        TimeDate lCurrentTime = DvbManager.getInstance().getTimeFromStream();
        Calendar lCalendar = lCurrentTime.getCalendar();
        lCalendar.add(Calendar.DATE, day);
        TimeDate startTimeDate = new TimeDate(0, 0, 0,
                lCalendar.get(Calendar.DAY_OF_MONTH),
                lCalendar.get(Calendar.MONTH) + 1, lCalendar.get(Calendar.YEAR));
        lCalendar.add(Calendar.DATE, 1);
        TimeDate endTimeDate = new TimeDate(0, 0, 0,
                lCalendar.get(Calendar.DAY_OF_MONTH),
                lCalendar.get(Calendar.MONTH) + 1, lCalendar.get(Calendar.YEAR));
        /**
         * Time calculations
         */
        mElementWidths = new ArrayList<HorizTimeObject<EpgEvent>>();
        // If size is 0 just create empty element
        if (events.size() == 0) {
            mElementWidths
                    .add(new HorizTimeObject<EpgEvent>(dayMaxWidth, true));
        } else {
            TimeDate previousEndTime = startTimeDate;
            int currentDayWidth = 0;
            for (int i = 0; i < events.size(); i++) {
                EpgEvent event = events.get(i);
                int difference = previousEndTime.getCalendar().compareTo(
                        event.getStartTime().getCalendar());
                // Previous end time is after events start time
                if (difference > 0) {
                    // If event is before 00:00 we dont want it
                    if (previousEndTime.getCalendar().compareTo(
                            event.getEndTime().getCalendar()) >= 0) {
                        continue;
                    }
                    // Event is started before 00:00 and ends after 00:00
                    else {
                        int eventWidth = calculateTimeWidth(previousEndTime,
                                event.getEndTime(), oneMinutePixelWidth);
                        if (currentDayWidth + eventWidth > dayMaxWidth) {
                            eventWidth = dayMaxWidth - currentDayWidth;
                        }
                        mElementWidths.add(new HorizTimeObject<EpgEvent>(
                                eventWidth, event));
                        currentDayWidth += eventWidth;
                        previousEndTime = event.getEndTime();
                    }
                }
                // Previous end time is before events start time
                else if (difference < 0) {
                    int dummyEventWidth = calculateTimeWidth(previousEndTime,
                            event.getStartTime(), oneMinutePixelWidth);
                    if (currentDayWidth + dummyEventWidth > dayMaxWidth) {
                        dummyEventWidth = dayMaxWidth - currentDayWidth;
                    }
                    mElementWidths.add(new HorizTimeObject<EpgEvent>(
                            dummyEventWidth, null));
                    currentDayWidth += dummyEventWidth;
                    // If day is populated with events
                    if (currentDayWidth == dayMaxWidth) {
                        break;
                    }
                    int eventWidth = calculateEventWidth(event,
                            oneMinutePixelWidth);
                    if (currentDayWidth + eventWidth > dayMaxWidth) {
                        eventWidth = dayMaxWidth - currentDayWidth;
                    }
                    mElementWidths.add(new HorizTimeObject<EpgEvent>(
                            eventWidth, event));
                    currentDayWidth += eventWidth;
                    previousEndTime = event.getEndTime();
                }
                // Time values are equal
                else {
                    int eventWidth = calculateEventWidth(event,
                            oneMinutePixelWidth);
                    if (currentDayWidth + eventWidth > dayMaxWidth) {
                        eventWidth = dayMaxWidth - currentDayWidth;
                    }
                    mElementWidths.add(new HorizTimeObject<EpgEvent>(
                            eventWidth, event));
                    currentDayWidth += eventWidth;
                    previousEndTime = event.getEndTime();
                }
                // If day is populated with events
                if (currentDayWidth == dayMaxWidth) {
                    break;
                }
            }
            //If day is not fully populated with events
            if (currentDayWidth < dayMaxWidth) {
                int dummyEventWidth = dayMaxWidth-currentDayWidth;
                mElementWidths.add(new HorizTimeObject<EpgEvent>(
                        dummyEventWidth, null));
            }
        }
        return events;
    }

    /**
     * Calculate width of event in pixels
     * 
     * @param event
     * @param oneMinutePixelWidth
     * @return
     */
    private int calculateEventWidth(EpgEvent event, int oneMinutePixelWidth) {
        TimeDate startTime = event.getStartTime();
        TimeDate endTime = event.getEndTime();
        return calculateTimeWidth(startTime, endTime, oneMinutePixelWidth);
    }

    /**
     * Calculate width of time frame in pixels
     * 
     * @param startTime
     * @param endTime
     * @param oneMinutePixelWidth
     * @return
     */
    private int calculateTimeWidth(TimeDate startTime, TimeDate endTime,
            int oneMinutePixelWidth) {
        long duration = endTime.getCalendar().getTimeInMillis()
                - startTime.getCalendar().getTimeInMillis();
        duration = duration / 1000;
        float numberOfMins = ((float) duration) / 60f;
        return (int) (numberOfMins * oneMinutePixelWidth);
    }

    @Override
    protected void onPostExecute(ArrayList<EpgEvent> result) {
        super.onPostExecute(result);
        mHorizList.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewObserver(mHorizList));
        mHorizList.setAdapter(new HorizListAdapter(mCtx.get(), mElementWidths));
        mHorizList.setPositionBasedOnLeftOffsetFromAdapter(mTotalLeftOffset,
                mLeftOffset, mFocusedViewWidth);
    }

    private class ViewObserver implements
            ViewTreeObserver.OnGlobalLayoutListener {
        WeakReference<HorizListView> mViewToObserve;

        public ViewObserver(HorizListView viewToObserve) {
            mViewToObserve = new WeakReference<HorizListView>(viewToObserve);
        }

        @Override
        public void onGlobalLayout() {
            Log.d("ON GLOBAL LAYOUT", "ENTERED");
            ViewTreeObserver observer = mViewToObserve.get()
                    .getViewTreeObserver();
            observer.removeOnGlobalLayoutListener(this);
            mViewToObserve.get().setPositionBasedOnLeftOffsetFromAdapter(
                    mTotalLeftOffset, mLeftOffset, mFocusedViewWidth);
        }
    }
}
