package com.example.epg_grid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.epg_grid.dtv.DvbManager;
import com.iwedia.dtv.types.InternalException;

public class MainActivity extends Activity implements
        EpgGrid.OnItemClickListener, EpgGrid.OnItemSelectedListener {
    private EpgGrid mEpgGrid;
    private DvbManager mDvbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEpgGrid = (EpgGrid) findViewById(R.id.epgGrid);
        mEpgGrid.setOnItemSelectedListener(this);
        mEpgGrid.setOnItemClickListener(this);
        // Initialize adapter
        ListAdapter adapter = new ListAdapter(this);
        mEpgGrid.setAdapter(adapter);
        mDvbManager = DvbManager.getInstance();
        try {
            mDvbManager.changeChannelByNumber(1);
        } catch (InternalException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        try {
            mDvbManager.stopDTV();
        } catch (InternalException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public void onItemSelected(EpgListView verticalList,
            View verticalListElement, HorizListView horizontalList,
            View horizontalListElement, int verticalListChildIndex,
            int horizontalListChildIndex) {
        Log.d("ITEM SELECTED", "verticalListChildIndex="
                + verticalListChildIndex + ", horizontalListChildIndex="
                + horizontalListChildIndex);
    }

    @Override
    public void onNothingSelected(EpgListView arg0) {
        Log.d("ON NOTHING SELECTED", "onNothingSelected");
    }

    @Override
    public void onItemClick(EpgListView verticalList, View verticalListElement,
            HorizListView horizontalList, View horizontalListElement,
            int verticalListChildIndex, int horizontalListChildIndex) {
        Log.d("ITEM CLICKED", "verticalListChildIndex="
                + verticalListChildIndex + ", horizontalListChildIndex="
                + horizontalListChildIndex);
    }
}
