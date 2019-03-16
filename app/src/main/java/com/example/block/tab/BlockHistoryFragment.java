package com.example.block.tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.block.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlockHistoryFragment extends Fragment {

    RelativeLayout layout = null;

    public BlockHistoryFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_block_history, container, false);
        ButterKnife.bind(this, layout);
        dbSetting();
        return layout;
    }

    public void dbSetting(){

    }
}
