package com.example.block.tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.example.block.R;
import com.example.block.adapter.HistoryList_sub;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlockHistoryFragment extends Fragment {

    @BindView(R.id.door_grid) GridLayout door_grid;

    RelativeLayout layout = null;

    public BlockHistoryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_block_history, container, false);
        ButterKnife.bind(this, layout);
        return layout;
    }

    public void createLayout(String time){ // host
        HistoryList_sub historyList_sub = new HistoryList_sub(getContext());
        historyList_sub.setHistory(time);
        door_grid.addView(historyList_sub);
    }
}
