package com.example.epg_grid;

import android.content.Context;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.epg_grid.HorizListView.OnScrollHappenedListener;

import it.sephiroth.android.library.widget.AbsHListView.LayoutParams;

import java.lang.ref.WeakReference;
import java.util.Random;

public class ListAdapter extends BaseAdapter implements VerticalListInterface {
    private LayoutInflater inflater;
    private Context ctx;
    private OnScrollHappenedListener mOnScrollHappenedListener;
    private int mTotalLeftOffset = 0, mLeftOffset = 0, mFocusedViewWidth = 0;

    public ListAdapter(Context ctx) {
        this.inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return 20;
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
    public void setCurrentScrollPosition(int totalLeftOffset, int leftOffset,
            int focusedViewWidth) {
        this.mTotalLeftOffset = totalLeftOffset;
        this.mLeftOffset = leftOffset;
        this.mFocusedViewWidth = focusedViewWidth;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_epg, null);
            holder = new ViewHolder(convertView);
            holder.hList.setOnScrollHappenedListener(mOnScrollHappenedListener);
            convertView.setTag(holder);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, 150));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.number.setText((position + 1) + ".");
        holder.hList.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewObserver(holder.hList));
        holder.hList.setAdapter(new HListAdapter());
        holder.hList.setPositionBasedOnLeftOffsetFromAdapter(mTotalLeftOffset,
                mLeftOffset, mFocusedViewWidth);
        return convertView;
    }

    private class ViewHolder {
        TextView number;
        HorizListView hList;

        public ViewHolder(View convertView) {
            number = (TextView) convertView.findViewById(R.id.textview);
            hList = (HorizListView) convertView.findViewById(R.id.hlist);
        }
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

    class HListAdapter extends BaseAdapter implements HorizontalListInterface {
        private SparseIntArray mElementWidths = new SparseIntArray();

        public HListAdapter() {
            Random rand = new Random();
            for (int i = 0; i < 20; i++) {
                mElementWidths.put(i, 100 + rand.nextInt(300));
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 20;
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
                convertView.setLayoutParams(new LayoutParams(mElementWidths
                        .get(position), LayoutParams.MATCH_PARENT));
            }
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

    @Override
    public OnScrollHappenedListener getOnScrollHappenedListener() {
        return mOnScrollHappenedListener;
    }

    @Override
    public void setOnScrollHappenedListener(
            OnScrollHappenedListener mOnScrollHappenedListener) {
        this.mOnScrollHappenedListener = mOnScrollHappenedListener;
    }
}
