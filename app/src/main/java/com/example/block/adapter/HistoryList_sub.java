package com.example.block.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.block.R;
import com.google.firebase.database.DatabaseReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryList_sub extends LinearLayout {

    @BindView(R.id.history) TextView history;

    LinearLayout layout;
    private String time;

    private DatabaseReference mPostReference;

    public HistoryList_sub(Context context) {
        super(context);
        initSetting(context);
    }

    public void initSetting(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.door_card, this, true);
        ButterKnife.bind(layout);
    }

    public void setHistory(String time){
        history.setText("USER ID : 01099296975 / state : HOST / time :"+time);
        this.time = time;
    }


}
