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

import java.lang.ref.WeakReference;

public class ListAdapter extends BaseAdapter implements VerticalListInterface {
    private LayoutInflater inflater;
    private Context ctx;
    private OnScrollHappenedListener mOnScrollHappenedListener;
    private int mTotalLeftOffset = 0, mLeftOffset = 0, mFocusedViewWidth = 0;
    public static SparseIntArray mElementWidthsStatic = new SparseIntArray();
    static {
        mElementWidthsStatic.put(0, 120);
        mElementWidthsStatic.append(1, 250);
        mElementWidthsStatic.append(2, 200);
        mElementWidthsStatic.append(3, 400);
        mElementWidthsStatic.append(4, 320);
        mElementWidthsStatic.append(5, 50);
        mElementWidthsStatic.append(6, 500);
        mElementWidthsStatic.append(7, 380);
        mElementWidthsStatic.append(8, 220);
        mElementWidthsStatic.append(9, 450);
    }

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
            convertView = inflater.inflate(R.layout.epg_list_item, null);
            holder = new ViewHolder(convertView);
            holder.hList.setOnScrollHappenedListener(mOnScrollHappenedListener);
            convertView.setTag(holder);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, ctx.getResources()
                            .getDimensionPixelSize(R.dimen.list_item_height)));
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.number.setText((position + 1) + ".");
        holder.hList.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewObserver(holder.hList));
        holder.hList
                .setAdapter(new HorizListAdapter(ctx, mElementWidthsStatic));
        holder.hList.setPositionBasedOnLeftOffsetFromAdapter(mTotalLeftOffset,
                mLeftOffset, mFocusedViewWidth);
        return convertView;
    }

    private class ViewHolder {
        TextView number;
        HorizListView hList;

        public ViewHolder(View convertView) {
            number = (TextView) convertView.findViewById(
                    R.id.epg_channel_indicator).findViewById(R.id.textview);
            hList = (HorizListView) convertView.findViewById(R.id.epg_hlist);
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
