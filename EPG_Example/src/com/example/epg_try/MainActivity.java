package com.example.epg_try;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.epg_try.dtv.DvbManager;
import com.iwedia.dtv.types.InternalException;
import com.iwedia.epg_grid.EpgGrid;
import com.iwedia.epg_grid.EpgListView;
import com.iwedia.epg_grid.HorizListView;

public class MainActivity extends Activity implements
        EpgGrid.OnItemClickListener, EpgGrid.OnItemSelectedListener {
    private EpgGrid mEpgGrid;
    private DvbManager mDvbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDvbManager = DvbManager.getInstance();
        mEpgGrid = (EpgGrid) findViewById(R.id.epgGrid);
        mEpgGrid.setOnItemSelectedListener(this);
        mEpgGrid.setOnItemClickListener(this);
        // Initialize adapter
        EpgAdapter adapter = new EpgAdapter(this);
        mEpgGrid.setAdapter(adapter);
        try {
            mDvbManager.changeChannelByNumber(1);
        } catch (InternalException e) {
            e.printStackTrace();
        }
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
        try {
            mDvbManager.changeChannelByNumber(verticalListChildIndex);
        } catch (InternalException e) {
            e.printStackTrace();
        }
        Log.d("ITEM CLICKED", "verticalListChildIndex="
                + verticalListChildIndex + ", horizontalListChildIndex="
                + horizontalListChildIndex);
    }
}
