package com.example.block.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.block.R;
import com.example.block.database.HostPost;
import com.example.block.database.HostRequestPost;
import com.example.block.database.MemberPost;
import com.example.block.main.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestList_sub extends LinearLayout {

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
    @BindView(R.id.type_text) TextView type_text;
    @BindView(R.id.sender_text) TextView sender_text;

    LinearLayout layout;

    int is_host = 0; // 0: guest / 1: host

    private DatabaseReference mPostReference;

    private String sender_name, sender_phone, start_time, end_time, receiver_name, receiver_phone, door_id, receiver_token;

    public RequestList_sub(Context context) {
        super(context);
        initSetting(context);
    }

    public void initSetting(Context context){
        LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout) inflater.inflate(R.layout.door_card_request, this, true);
        ButterKnife.bind(layout);
        card_layout.setOnClickListener(v -> {
            setDialog();
        });
    }

    public void setHost(String sender_name, String sender_phone, String receiver_name, String receiver_phone, String door_id, String receiver_token){
        type_text.setText("Type : host");
        sender_text.setText("Sender : "+sender_name+"("+sender_phone+")");
        is_host = 1;
        this.sender_name = sender_name;
        this.sender_phone = sender_phone;
        this.receiver_name = receiver_name;
        this.receiver_phone = receiver_phone;
        this.door_id = door_id;
        this.receiver_token = receiver_token;
    }

    public void setGuest(String sender_name, String sender_phone, String start_time, String end_time, String door_id){
        type_text.setText("Type : guest");
        sender_text.setText("Sender : "+sender_name+"("+sender_phone+")");
        is_host = 0;
        this.sender_name = sender_name;
        this.sender_phone = sender_phone;
        this.start_time = start_time;
        this.end_time = end_time;
        this.door_id = door_id;
        this.receiver_token = receiver_token;
    }

    public void setDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if(is_host == 1) { // host
            dialog.setContentView(R.layout.custom_dialog_request_host);
            TextView type = (TextView) dialog.findViewById(R.id.type);
            TextView sender = (TextView) dialog.findViewById(R.id.sender);
            TextView receiver = (TextView) dialog.findViewById(R.id.receiver);
            Button accept_btn = (Button) dialog.findViewById(R.id.accept_btn);
            Button deny_btn = (Button) dialog.findViewById(R.id.deny_btn);
            Button close = (Button) dialog.findViewById(R.id.close);

            type.setText("Type : host request");
            sender.setText("Sender : "+sender_name+"("+sender_phone+")");
            receiver.setText("Receiver : "+receiver_name+"("+receiver_phone+")");
            accept_btn.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Host request accept");
                builder.setMessage("Would you like to accept this request?");
                builder.setPositiveButton("Yes",
                        (dialog12, which) -> {
                            getHostReqData(false);
                            Toast.makeText(getContext(), "accepted", Toast.LENGTH_SHORT).show();
                        });
                builder.setNegativeButton("No",
                        (dialog1, which) -> {

                        });
                builder.show();
            });

            deny_btn.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Host request deny");
                builder.setMessage("Would you like to deny this request?");
                builder.setPositiveButton("Yes",
                        (dialog12, which) -> {
                            getHostReqData(true);
                            Toast.makeText(getContext(), "denyed", Toast.LENGTH_SHORT).show();
                        });
                builder.setNegativeButton("No",
                        (dialog1, which) -> {

                        });
                builder.show();
            });
            close.setOnClickListener(v -> {
                dialog.dismiss();
            });
        }
        else if(is_host == 0) { // guest
            dialog.setContentView(R.layout.custom_dialog_request_guest);
            TextView type = (TextView) dialog.findViewById(R.id.type);
            TextView sender = (TextView) dialog.findViewById(R.id.sender);
            TextView start_date_time = (TextView) dialog.findViewById(R.id.start_date_time);
            TextView end_date_time = (TextView) dialog.findViewById(R.id.end_date_time);
            Button confirm_btn = (Button) dialog.findViewById(R.id.confirm_btn);

            type.setText("Type : host request");
            sender.setText("Sender : "+sender_name+"("+sender_phone+")");
            start_date_time.setText("Start : "+start_time);
            end_date_time.setText("End : "+end_time);
            confirm_btn.setOnClickListener(v -> {
                ((MainActivity)getContext()).setViewpager(1);
            });
        }

        dialog.show();
    }

    public void getHostReqData(boolean is_deny){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    HostRequestPost get = postSnapshot.getValue(HostRequestPost.class);
                    String[] info = {get.accept_count, get.sender_name, get.sender_phone, get.receiver_token, get.door_id};
                   /*
                   index : key
                   accept count : info[0]
                   sender name : info[1]
                   sender phone : info[2]
                   receiver token : info[3]
                   door id : info[4]
                   */
                   // 어떤 요청인지 찾기 (sender는 receiver에게 같은 요청 중복 불가)
                   if(sender_name.equals(info[1]) && receiver_token.equals(info[3]) && door_id.equals(info[4])){
                       if(is_deny){
                           deleteHostReq(key); // host req에서 해당 req 삭제
                           postFCMToSender(info[2]); // sender에게 거절됐다는 FCM 보내기
                       } else{
                           int count = Integer.parseInt(info[0]);
                           if(count == 1){ // 모두 accept상황
                               deleteHostReq(key);
                               postFCMToReceiver(info[1], info[2], info[3]); // receiver에게 host 초대사실 FCM 보내기
                           } else{ // 아직 accept 진행중 -> count 줄이기
                               count --;
                               String accept_count = String.valueOf(count);
                               postAcceptCount(key, accept_count, info[2], info[1], info[3], info[4]);
                           }
                       }
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

    public void postFCMToSender(String sender_phone){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                String receiver_token = "";
                String denyer_name = "";
                String denyer_token =  FirebaseInstanceId.getInstance().getToken();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.token, get.user_name};
                   /*
                   sender phone : key
                   sender token : info[0]
                   user name : info[1]
                   */
                    if(sender_phone.equals(key)) receiver_token = info[0]; //  FCM 받을사람
                    if(denyer_token.equals(info[0])) denyer_name = info[1]; // 거절한사람 이름
                }

                sendPostToFCM(receiver_token,denyer_name+"로 부터의 host 요청 거절!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("member");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    public void postFCMToReceiver(String sender_name, String sender_phone, String receiver_phone){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.token, get.user_id, get.user_name};
                   /*
                   receiver phone : key
                   receiver token : info[0]
                   receiver id : info[1]
                   receiver name : info[2]
                   */
                    if(receiver_phone.equals(key)) {
                        sendPostToFCM( info[0],sender_name+"("+sender_phone+")의 host초대!");
                        postHome(info[1], info[2], door_id); // host 테이블에 계약내용 저장
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

    public void postAcceptCount(String index, String accept_count, String sender_phone, String sender_name, String receiver_token, String door_id){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        HostRequestPost post = new HostRequestPost(accept_count, sender_phone, sender_name, receiver_token, door_id);
        postValues = post.toMap();

        childUpdates.put("/hostreq/" + index, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void deleteHostReq(String index){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        HostRequestPost post = new HostRequestPost();
        postValues = post.toMap();

        childUpdates.put("/hostreq/" + index, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void postHome(String user_id, String user_name, String door_id){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        HostPost post = new HostPost(user_id, user_name, door_id);
        postValues = post.toMap();

        getHostReqRow(childUpdates, postValues);
    }

    public void getHostReqRow(Map<String, Object> childUpdates, Map<String, Object> postValues){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long host_req_row = dataSnapshot.getChildrenCount();
                childUpdates.put("/host/" + String.valueOf(host_req_row+1), postValues);
                mPostReference.updateChildren(childUpdates);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("hostreq");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }
}