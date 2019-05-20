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
import com.example.block.database.GuestRequestPost;
import com.example.block.main.MainActivity;
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
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GuestRequest_sub extends LinearLayout {

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

    private DatabaseReference mPostReference;

    private String sender_name, sender_phone, start_time, end_time, door_id, receiver_token;

    public GuestRequest_sub(Context context) {
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


    public void setGuest(String sender_name, String sender_phone, String start_time, String end_time, String door_id, String receiver_token){
        type_text.setText("[Type]\nguest");
        sender_text.setText("[Sender]\n"+sender_name+"("+sender_phone+")");
        this.sender_name = sender_name;
        this.sender_phone = sender_phone;
        this.start_time = start_time;
        this.end_time = end_time;
        this.door_id = door_id;
        this.receiver_token = receiver_token;
    }

    public void setDialog(Context context){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialog.setContentView(R.layout.custom_dialog_request_guest);
        TextView type = (TextView) dialog.findViewById(R.id.type);
        TextView door = (TextView) dialog.findViewById(R.id.door);
        TextView sender = (TextView) dialog.findViewById(R.id.sender);
        TextView start_date_time = (TextView) dialog.findViewById(R.id.start_date_time);
        TextView end_date_time = (TextView) dialog.findViewById(R.id.end_date_time);
        Button confirm_btn = (Button) dialog.findViewById(R.id.confirm_btn);

        type.setText("[Type]\nGuest invitation");
        door.setText("[Door]\n"+door_id);
        sender.setText("[Sender]\n"+sender_name+"("+sender_phone+")");
        start_date_time.setText("[Start]\n"+start_time);
        end_date_time.setText("[End]\n"+end_time);
        confirm_btn.setOnClickListener(v -> {
            getGuestReqData();
            ((MainActivity)getContext()).setViewpager(1);
            dialog.dismiss();
        });


        dialog.show();
    }

    public void getGuestReqData(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    GuestRequestPost get = postSnapshot.getValue(GuestRequestPost.class);
                    String[] info = {get.sender_name, get.sender_phone, get.receiver_token, get.door_id};
                    // 어떤 요청인지 찾기 (sender는 receiver에게 같은 요청 중복 불가)
                    if(sender_phone.equals(info[1]) && receiver_token.equals(info[2]) && door_id.equals(info[3])){
                        deleteGuestReq(key);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("gomKim","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("guestreq");
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    public void deleteGuestReq(String index){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;

        childUpdates.put("/guestreq/" + index, postValues);
        mPostReference.updateChildren(childUpdates);
    }
}