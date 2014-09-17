package com.example.epg_grid;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;

import com.iwedia.dtv.epg.EpgEvent;
import com.iwedia.dtv.service.ServiceDescriptor;

public interface EpgGridAdapter {
    void registerDataSetObserver(DataSetObserver observer);

    void unregisterDataSetObserver(DataSetObserver observer);

    int getChannelsCount();

    int getEventsCount(int channelPosition);

    ServiceDescriptor getChannel(int channelPosition);

    EpgEvent getEvent(int channelPosition, int eventPosition);

    long getChannelId(int channelPosition);

    long getEventId(int channelPosition, int eventPosition);

    boolean hasStableIds();

    ViewGroup getChannelView(int channelPosition, View convertView,
            ViewGroup parent);

    View getEventView(int channelPosition, int eventPosition, View convertView,
            ViewGroup parent);

    long getCombinedEventId(long channelId, long eventId);

    long getCombinedChannelId(long channelId);
}
