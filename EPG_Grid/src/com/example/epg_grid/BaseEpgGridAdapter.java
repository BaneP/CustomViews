package com.example.epg_grid;

import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.service.ServiceDescriptor;

public abstract class BaseEpgGridAdapter implements EpgGridAdapter {
    private final DataSetObservable mDataSetObservable = new DataSetObservable();

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.registerObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObservable.unregisterObserver(observer);
    }

    public void notifyDataSetInvalidated() {
        mDataSetObservable.notifyInvalidated();
    }

    public void notifyDataSetChanged() {
        mDataSetObservable.notifyChanged();
    }

    @Override
    public long getCombinedEventId(long channelId, long eventId) {
        return 0x8000000000000000L | ((channelId & 0x7FFFFFFF) << 32)
                | (eventId & 0xFFFFFFFF);
    }

    @Override
    public long getCombinedChannelId(long channelId) {
        return (channelId & 0x7FFFFFFF) << 32;
    }
}
