package com.example.block.adapter;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.block.R;
import com.example.block.database.HostRequestPost;
import com.example.block.main.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HostRequestConfirm_sub extends LinearLayout {

    @BindView(R.id.card_layout) RelativeLayout card_layout;
    @BindView(R.id.type_text) TextView type_text;
    @BindView(R.id.sender_text) TextView sender_text;

    LinearLayout layout;

    private DatabaseReference mPostReference;

    private String sender_name, sender_phone, door_id, receiver_token;

    public HostRequestConfirm_sub(Context context) {
        super(context);
        initSetting(context);
    }

    public void initSetting(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.door_card_request, this, true);
        ButterKnife.bind(layout);
        card_layout.setOnClickListener(v -> {
            setDialog(context);
        });
    }

    public void setHostConfirm(String sender_name, String sender_phone, String door_id, String receiver_token){
        type_text.setText("[Type]\nhost");
        sender_text.setText("[Sender]\n"+sender_name+"\n("+sender_phone+")");
        this.sender_name = sender_name;
        this.sender_phone = sender_phone;
        this.door_id = door_id;
        this.receiver_token = receiver_token;
    }

    public void setDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_dialog_request_host_confirm);
        TextView type = (TextView) dialog.findViewById(R.id.type);
        TextView door = (TextView) dialog.findViewById(R.id.door);
        TextView sender = (TextView) dialog.findViewById(R.id.sender);
        Button confirm_btn = (Button) dialog.findViewById(R.id.confirm_btn);

        type.setText("[Type]\nHost Invitation");
        door.setText("[Door]\n"+door_id);
        sender.setText("[Sender]\n"+sender_name+"\n("+sender_phone+")");

        confirm_btn.setOnClickListener(v -> {
            getHostReqData();
            ((MainActivity)getContext()).setViewpager(1);
            dialog.dismiss();
        });

        dialog.show();
    }

    public void getHostReqData(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    HostRequestPost get = postSnapshot.getValue(HostRequestPost.class);
                    String[] info = {get.accept_count, get.sender_name, get.sender_phone, get.receiver_token, get.door_id};

                   // 어떤 요청인지 찾기 (sender는 receiver에게 같은 요청 중복 불가)
                   if(sender_name.equals(info[1]) && receiver_token.equals(info[3]) && door_id.equals(info[4])){
                       deleteHostReq(key);
                   }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("hostreq");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }


    public void deleteHostReq(String index){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        childUpdates.put("/hostreq/" + index, postValues);
        mPostReference.updateChildren(childUpdates);
    }


}