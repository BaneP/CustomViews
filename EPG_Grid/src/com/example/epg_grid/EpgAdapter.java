package com.example.epg_grid;

import android.view.View;
import android.view.ViewGroup;

public class EpgAdapter extends BaseEpgGridAdapter {
    @Override
    public int getChannelsCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getEventsCount(int channelPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Object getChannel(int channelPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getEvent(int channelPosition, int eventPosition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getChannelId(int channelPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getEventId(int channelPosition, int eventPosition) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ViewGroup getChannelView(int channelPosition, View convertView,
            ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getEventView(int channelPosition, int eventPosition,
            View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }
}
