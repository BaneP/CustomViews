package com.example.epg_grid;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import it.sephiroth.android.library.widget.AbsHListView.LayoutParams;

import java.util.Random;

public class HorizListAdapter extends BaseAdapter implements
        HorizontalListInterface {
    private SparseIntArray mElementWidths = new SparseIntArray();
    private Context ctx;

    public HorizListAdapter(Context ctx, SparseIntArray elementWidths) {
        this.ctx = ctx;
        this.mElementWidths = elementWidths;
    }

    @Override
    public int getCount() {
        return mElementWidths.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new TextView(ctx);
            convertView.setPadding(20, 20, 20, 20);
            ((TextView) convertView).setTextSize(25);
        }
        convertView.setLayoutParams(new LayoutParams(mElementWidths
                .get(position), LayoutParams.MATCH_PARENT));
        ((TextView) convertView).setText("" + (position + 1));
        return convertView;
    }

    @Override
    public SparseIntArray getElementWidths() {
        return mElementWidths;
    }

    @Override
    public int getElementWidth(int position) {
        return mElementWidths.get(position, 0);
    }
}
