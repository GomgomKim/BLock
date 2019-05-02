package com.example.block.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.block.R;
import com.example.block.database.MemberPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class InvitationList_sub extends LinearLayout {
    private static final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "AAAAmgpQZrg:APA91bHs4Bw8PHI_RXxWpxQGFidTm5QJ0Cy8o8dO0GUS5Ua48Oq6Jc0J1dyuLsMBmODmV0zYyL0IMs1diPbSO2tt0qtnF1C1ybLsWRFvhQztO2lqgmVkyTP0yrlcnOuq2ogq-ZqT-QQg";
    private void sendPostToFCM(final String to_fcm, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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

        TextView door_id_dialog = (TextView) dialog.findViewById(R.id.door_id);
        EditText search_user_id = (EditText) dialog.findViewById(R.id.search_user_id);
        Button open_door_dialog = (Button) dialog.findViewById(R.id.open_door);
        Button close_dialog = (Button) dialog.findViewById(R.id.close);

        door_id_dialog.setText("Door ID : "+door_id);


        open_door_dialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Send Invitation");
            builder.setMessage("Would you like to send invitation?");
            builder.setPositiveButton("Yes",
                    (dialog13, which) -> {
                        getFirebaseDatabase(String.valueOf(search_user_id.getText()));
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

    public void postFirebaseDatabase(boolean add){
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String u_id = user.getUid();

            String token = FirebaseInstanceId.getInstance().getToken();


//            String token = ((UIdInterface)getContext()).getToken();
            MemberPost post = new MemberPost("김기연", door_id, token, u_id);
//            MemberPost post = new MemberPost("김기연", door_id);
            postValues = post.toMap();
        }
        childUpdates.put("/member/" + "01099296975", postValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void getFirebaseDatabase(String text){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean user_flag = false;
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
//                    String[] info = {get.user_name, get.home_door_id};
                    String[] info = {get.user_name, get.home_door_id,get.token};

                    Log.d("gomKim", "user_id: " + key);
                    Log.d("gomKim", "user_name: " + info[0]);
                    Log.d("gomKim", "home_door_id: " + info[1]);
                    Log.d("solKim", "token: " + info[2]);
                    if(text.equals(key)) {
                        user_flag = true;
                        sendPostToFCM( info[2],"초대합니다!");
                        break;
                    }
                }
                if(user_flag) {
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
}