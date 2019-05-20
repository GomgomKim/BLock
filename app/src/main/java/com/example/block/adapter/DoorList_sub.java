package com.example.block.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.block.R;
import com.example.block.database.DoorPost;
import com.example.block.database.GuestPost;
import com.example.block.database.HostPost;
import com.example.block.database.MemberPost;
import com.example.block.service.TransactionService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class DoorList_sub extends LinearLayout {

    @BindView(R.id.card_layout) RelativeLayout card_layout;
    @BindView(R.id.door_id_text) TextView door_id_text;
    @BindView(R.id.state_text) TextView state_text;
    @BindView(R.id.time_text) TextView time_text;

    LinearLayout layout;
    private String door_id;

    private DatabaseReference mPostReference;

    public DoorList_sub(Context context) {
        super(context);
        initSetting(context);
    }

    public void initSetting(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.door_card, this, true);
        ButterKnife.bind(layout);
        card_layout.setOnClickListener(v -> {
            setDialog(context);
        });
    }

    public void setHost(String door_id){
        door_id_text.setText("Door ID : "+door_id);
        state_text.setText("State : host");
        time_text.setText("");

        this.door_id = door_id;
    }

    public void setGuest(String door_id, String start_time, String end_time){
        door_id_text.setText("Door ID : "+door_id);
        state_text.setText("State : guest");
        time_text.setText("Start\n"+start_time+"\n"+"End\n"+end_time);

        this.door_id = door_id;
    }

    public void setDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        TextView door_id_dialog = (TextView) dialog.findViewById(R.id.door_id);
        Button auth_list = (Button) dialog.findViewById(R.id.auth_list);
        RadioButton select_home_dialog = (RadioButton) dialog.findViewById(R.id.select_home);
        Button open_door_dialog = (Button) dialog.findViewById(R.id.open_door);
        Button close_dialog = (Button) dialog.findViewById(R.id.close);

        door_id_dialog.setText("Door ID : "+door_id);

        // 권한을 가진 사용자들 list
        auth_list.setOnClickListener(v -> {
            auth_list_popupSetting(door_id);
        });

        select_home_dialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Set Home Door");
            builder.setMessage("Would you like to set this door to home?");
            builder.setPositiveButton("Yes",
                    (dialog12, which) -> {
                        getUserDataForHomeDoor(door_id, context);
                        Toast.makeText(context, "set successfully", Toast.LENGTH_SHORT).show();
                    });
            builder.setNegativeButton("No",
                    (dialog1, which) -> {

                    });
            builder.show();
        });

        open_door_dialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Open Door");
            builder.setMessage("Would you like to open this door?");
            builder.setPositiveButton("Yes",
                    (dialog13, which) -> {

                        getDoorAddress(door_id, context);


                    });
            builder.setNegativeButton("No",
                    (dialog1, which) -> {

                    });
            builder.show();
            dialog.dismiss();
        });

        close_dialog.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    // 유저 정보 불러오기
    public void getUserDataForHomeDoor(String home_door_id, Context context){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_id, get.user_name, get.token};

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String u_id = user.getUid();

                    if(u_id.equals(info[0])){
                        postHomeDoor(info[1], home_door_id, info[2], u_id, key);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("member");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    public void postHomeDoor(String user_name, String home_door_id, String user_token, String user_id, String user_key){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        MemberPost post = new MemberPost(user_name, home_door_id, user_token, user_id);
        postValues = post.toMap();

        childUpdates.put("/member/" + user_key, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    // 권한있는 유저 리스트 팝업창
    public void auth_list_popupSetting(String door_id) {

        //팝업으로 띄울 커스텀뷰를 설정하고
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.auth_list, null);

        //팝업 윈도우 생성
        PopupWindow popup = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        RelativeLayout popupLayout = (RelativeLayout)popupView.findViewById(R.id.auth_layout);
        popup.showAtLocation(popupLayout, Gravity.CENTER, 0, 0);

        ScrollView scroll_view = (ScrollView) popupView.findViewById(R.id.scroll_view);
        LinearLayout host_list = (LinearLayout) popupView.findViewById(R.id.host_list);
        LinearLayout guest_list = (LinearLayout) popupView.findViewById(R.id.guest_list);
        Button close = (Button) popupView.findViewById(R.id.close);

        scroll_view.scrollTo(0, 0);

        getHostUsers(door_id, host_list);
        getGuestUsers(door_id, guest_list);

        popupView.bringToFront();

        close.setOnClickListener(view -> popup.dismiss());
    }

    // host list 불러오기
    public void getHostUsers(String door_id, LinearLayout host_list){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    HostPost get = postSnapshot.getValue(HostPost.class);
                    String[] info = {get.door_id, get.user_id};

                    if(door_id.equals(info[0])){
                        createUserInfoButton(info[1], host_list);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("host");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    // guest list 불러오기
    public void getGuestUsers(String door_id, LinearLayout guest_list){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    GuestPost get = postSnapshot.getValue(GuestPost.class);
                    String[] info = {get.door_id, get.user_id};
                    if(door_id.equals(info[0])){
                        createUserInfoButton(info[1], guest_list);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("guest");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    // 유저별 버튼 생성 불러오기
    public void createUserInfoButton(String user_id, LinearLayout layout){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_id, get.user_name};

                    if(user_id.equals(info[0])){
                        Button btn = new Button(getContext());
                        btn.setText(info[1]+"("+key+")");
                        layout.addView(btn);
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("member");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    // 문 정보 불러오기
    public void getDoorAddress(String door_id, Context context){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    DoorPost get = postSnapshot.getValue(DoorPost.class);
                    String[] info = {get.door_id, get.deploy_address};

                    if(door_id.equals(info[0])){
                        Intent intent = new Intent(getContext(), TransactionService.class);
                        intent.putExtra("door_id", door_id);
                        intent.putExtra("address", info[1]);
                        intent.putExtra("type", "host");
                        intent.putExtra("state", "get");
                        context.startService(intent);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("door");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }
}