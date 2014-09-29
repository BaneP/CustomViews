package com.example.epg_grid;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ListView;

public class MainActivity extends Activity {
    private EpgGrid list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // mEpgGrid = (EpgGridView) findViewById(R.id.epgGridView1);
        // mEpgGrid.setAdapter(new EpgAdapter(this));
        // list = (EpgGrid) findViewById(R.id.listView1);
        // list.setAdapter(new ListAdapter(this));
    }
}
