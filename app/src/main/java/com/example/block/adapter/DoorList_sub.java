package com.example.block.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.block.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DoorList_sub extends LinearLayout {

    @BindView(R.id.card_layout) RelativeLayout card_layout;
    @BindView(R.id.door_id_text) TextView door_id_text;
    @BindView(R.id.state_text) TextView state_text;
    @BindView(R.id.time_text) TextView time_text;

    LinearLayout layout;

    public DoorList_sub(Context context) {
        super(context);
        initSetting(context);
    }

    public void initSetting(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.door_card, this, true);
        ButterKnife.bind(layout);
    }

    public void setHost(String door_id){
        door_id_text.setText("Door ID : "+door_id);
        state_text.setText("State : host");
        time_text.setText("");
    }

    public void setGuest(String door_id, String time){
        door_id_text.setText("Door ID : "+door_id);
        state_text.setText("State : guest");
        time_text.setText(time);
    }
}
