package com.example.block.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.block.R;
import com.example.block.database.GuestPost;
import com.example.block.database.GuestRequestPost;
import com.example.block.database.HostPost;
import com.example.block.database.MemberPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InvitationList_sub extends LinearLayout {
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAmgpQZrg:APA91bHs4Bw8PHI_RXxWpxQGFidTm5QJ0Cy8o8dO0GUS5Ua48Oq6Jc0J1dyuLsMBmODmV0zYyL0IMs1diPbSO2tt0qtnF1C1ybLsWRFvhQztO2lqgmVkyTP0yrlcnOuq2ogq-ZqT-QQg";
    private void sendPostToFCM(final String to_fcm, final String message) {
        new Thread(() -> {
            try {
                // FMC 메시지 생성 start
                JSONObject root = new JSONObject();
                JSONObject notification = new JSONObject();
                notification.put("body", message);
                notification.put("title","Block");
                root.put("notification", notification);
                root.put("to", to_fcm);
                // FMC 메시지 생성 end

                URL Url = new URL(FCM_MESSAGE_URL);
                HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Content-type", "application/json");
                OutputStream os = conn.getOutputStream();
                os.write(root.toString().getBytes("utf-8"));
                os.flush();
                conn.getResponseCode();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    ////////////////////////////////////////////////////////////////////////////////////

    @BindView(R.id.card_layout) RelativeLayout card_layout;
    @BindView(R.id.door_id_text) TextView door_id_text;
    @BindView(R.id.state_text) TextView state_text;

    LinearLayout layout;
    private String door_id;

    private DatabaseReference mPostReference;

    private String my_name;
    private String my_phone;
    private String receiver_token;

    private long host_req_row;
    private long guest_req_row;

    String date_text;
    int time_count = 0;

    int is_host = 1;

    public InvitationList_sub(Context context) {
        super(context);
        initSetting(context);
    }

    public void initSetting(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.door_card_invitation, this, true);
        ButterKnife.bind(layout);
        card_layout.setOnClickListener(v -> {
            setDialog();
        });
    }

    public void setHost(String door_id){
        door_id_text.setText("Door ID : "+door_id);
        state_text.setText("State : host");

        this.door_id = door_id;
    }


    public void setDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_contract);

        RelativeLayout dialog_layout = (RelativeLayout) dialog.findViewById(R.id.dialog_layout);

        TextView door_id_dialog = (TextView) dialog.findViewById(R.id.door_id);
        EditText search_user_id = (EditText) dialog.findViewById(R.id.search_user_id);
        Button open_door_dialog = (Button) dialog.findViewById(R.id.open_door);
        Button close_dialog = (Button) dialog.findViewById(R.id.close);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
        RadioButton host_btn = (RadioButton) dialog.findViewById(R.id.host_btn);
        RadioButton guest_btn = (RadioButton) dialog.findViewById(R.id.guest_btn);

        Button start_btn = (Button) dialog.findViewById(R.id.start_btn);
        Button end_btn = (Button) dialog.findViewById(R.id.end_btn);
        Button select_btn = (Button) dialog.findViewById(R.id.select_btn);

        LinearLayout guest_time = (LinearLayout)  dialog.findViewById(R.id.guest_time);

        TextView start_date_time = (TextView) dialog.findViewById(R.id.start_date_time);
        TextView end_date_time = (TextView) dialog.findViewById(R.id.end_date_time);

        DatePicker date_picker = (DatePicker) dialog.findViewById(R.id.date_picker);
        TimePicker time_picker = (TimePicker) dialog.findViewById(R.id.time_picker);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.host_btn:
                    is_host = 1;
                    guest_time.setVisibility(GONE);
                    break;
                case R.id.guest_btn:
                    is_host = 0;
                    guest_time.setVisibility(VISIBLE);
                    break;
            }
        });

        start_btn.setOnClickListener(v -> {
            date_picker.setVisibility(VISIBLE);
            date_picker.init(date_picker.getYear(), date_picker.getMonth(), date_picker.getDayOfMonth(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                        Date date = new Date(year-1900, monthOfYear, dayOfMonth);
                        date_text = dateFormat.format(date);
                        start_date_time.setText(date_text);
                        date_picker.setVisibility(GONE);
                        time_picker.setVisibility(VISIBLE);
                        time_picker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
                            SimpleDateFormat datetimeFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                            Date datetime = new Date(year-1900, monthOfYear, dayOfMonth, hourOfDay, minute);
                            date_text = datetimeFormat.format(datetime);
                            start_date_time.setText(date_text);
                        });
                    });
        });

        end_btn.setOnClickListener(v -> {
            date_picker.setVisibility(VISIBLE);
            date_picker.init(date_picker.getYear(), date_picker.getMonth(), date_picker.getDayOfMonth(),
                    (view, year, monthOfYear, dayOfMonth) -> {
                        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                        Date date = new Date(year-1900, monthOfYear, dayOfMonth);
                        date_text = dateFormat.format(date);
                        end_date_time.setText(date_text);
                        date_picker.setVisibility(GONE);
                        time_picker.setVisibility(VISIBLE);
                        time_picker.setOnTimeChangedListener((view1, hourOfDay, minute) -> {
                            SimpleDateFormat datetimeFormat = new  SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
                            Date datetime = new Date(year-1900, monthOfYear, dayOfMonth, hourOfDay, minute);
                            date_text = datetimeFormat.format(datetime);
                            end_date_time.setText(date_text);
                        });
                    });
        });

        select_btn.setOnClickListener(v -> time_picker.setVisibility(GONE));



        door_id_dialog.setText("Door ID : "+door_id);


        open_door_dialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Send Invitation");
            if(is_host == 1){
                builder.setMessage("door id : "+door_id+"\n"+
                        "user id : "+search_user_id.getText()+"\n"+
                        "type : host\n"+
                        "Would you like to send invitation?");
            } else{
                builder.setMessage("door id : "+door_id+"\n"+
                        "user id : "+search_user_id.getText()+"\n"+
                        "type : guest\n"+
                        "start : "+start_date_time.getText()+"\n"+
                        "end : "+end_date_time.getText()+"\n"+
                        "Would you like to send invitation?");
            }

            builder.setPositiveButton("Yes",
                    (dialog13, which) -> {
                        if(is_host == 0) { // guest 전송
                            getFirebaseDatabase(
                                    String.valueOf(search_user_id.getText()),
                                    String.valueOf(start_date_time.getText()),
                                    String.valueOf(end_date_time.getText())
                            );
                        }
                        else if(is_host == 1){ // host 전송
                            findHosts(door_id);
                        }
                    });
            builder.setNegativeButton("No",
                    (dialog1, which) -> {

                    });
            builder.show();
        });

        close_dialog.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }


    public void postGuestReqFirebaseDatabase(String my_name, String my_phone, String receiver_token, String door_id, String start_time, String end_time){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        GuestRequestPost post = new GuestRequestPost(my_phone, my_name, receiver_token, door_id, start_time, end_time);
        postValues = post.toMap();

        getGuestReqRow(childUpdates, postValues);
    }

    public void postGuestFirebaseDatabase(String rcv_id, String rcv_name, String door_id, String start_time, String end_time){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        GuestPost post = new GuestPost(rcv_id, rcv_name, door_id, start_time, end_time);
        postValues = post.toMap();

        getGuestRow(childUpdates, postValues);
    }

    public void getFirebaseDatabase(String text, String start_time, String end_time){ //guest 전송
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean user_flag = false;
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_name, get.home_door_id,get.token, get.user_id};

                    Log.d("gomKim", "user_phone: " + key);
                    Log.d("gomKim", "user_name: " + info[0]);
                    Log.d("gomKim", "home_door_id: " + info[1]);
                    Log.d("gomKim", "token: " + info[2]);
                    Log.d("gomKim", "user_id: " + info[3]);

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String u_id = user.getUid();
                    if(u_id.equals(info[3])){ // 내 정보 불러오기
                        my_name = info[0];
                        my_phone = key;
                    }

                    if(text.equals(key)) {
                        user_flag = true;
                        receiver_token = info[2];
                        // guest 계약내용 저장
                        postGuestFirebaseDatabase(info[3], info[0], door_id, start_time, end_time);
                        sendPostToFCM( info[2],my_name+"("+my_phone+")의 guest초대!");
                        break;
                    }
                }
                if(user_flag) {
                    //요청정보 DB 저장
                    postGuestReqFirebaseDatabase(my_name, my_phone, receiver_token, door_id, start_time, end_time);
                    Toast.makeText(getContext(), "send !", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getContext(), "No such user !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("member");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }


    public void getGuestReqRow(Map<String, Object> childUpdates, Map<String, Object> postValues){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                guest_req_row = dataSnapshot.getChildrenCount();
                childUpdates.put("/guestreq/" + String.valueOf(guest_req_row+1), postValues);
                mPostReference.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("guestreq");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    public void getGuestRow(Map<String, Object> childUpdates, Map<String, Object> postValues){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long guest_row = dataSnapshot.getChildrenCount();
                childUpdates.put("/guest/" + String.valueOf(guest_row+1), postValues);
                mPostReference.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("guest");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    // host 전송
    public void findHosts(String door_id){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean user_flag = false;
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    HostPost get = postSnapshot.getValue(HostPost.class);
                    String[] info = {get.door_id, get.user_id};

                    /*
                    index : key
                    door_id : info[0]
                    user_id : info[1]
                     */

                    if(door_id.equals(info[1])){
                        sendAllOfHost(info[1]);
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

    // host 전송
    public void sendAllOfHost(String user_id){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean user_flag = false;
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_name, get.token, get.user_id};

                    /*
                    user phone : key
                    user name : info[0]
                    user token : info[1]
                    user id : info[2]
                     */

                    if(user_id.equals(info[2])) {
                        user_flag = true;
                        sendPostToFCM( info[1],"host 초대요청! 수락 / 거절 하세요!");
                        break;
                    }
                }
                if(user_flag) Toast.makeText(getContext(), "send !", Toast.LENGTH_SHORT).show();
                else Toast.makeText(getContext(), "No such user !", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("member");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }


}