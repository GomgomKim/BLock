package com.example.block.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.block.R;
import com.example.block.database.MemberPost;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    private String start_time;
    private String end_time;

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
            setDialog();
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
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public void setDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);

        TextView door_id_dialog = (TextView) dialog.findViewById(R.id.door_id);
        RadioButton select_home_dialog = (RadioButton) dialog.findViewById(R.id.select_home);
        Button open_door_dialog = (Button) dialog.findViewById(R.id.open_door);
        Button close_dialog = (Button) dialog.findViewById(R.id.close);

        door_id_dialog.setText("Door ID : "+door_id);

        select_home_dialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Set Home Door");
            builder.setMessage("Would you like to set this door to home?");
            builder.setPositiveButton("Yes",
                    (dialog12, which) -> {
                        postFirebaseDatabase(true);
                        Toast.makeText(getContext(), "set successfully", Toast.LENGTH_SHORT).show();
                    });
            builder.setNegativeButton("No",
                    (dialog1, which) -> {
                        
                    });
            builder.show();
        });

        open_door_dialog.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Open Door");
            builder.setMessage("Would you like to open this door?");
            builder.setPositiveButton("Yes",
                    (dialog13, which) -> {
                        Toast.makeText(getContext(), "open door!", Toast.LENGTH_SHORT).show();
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
            MemberPost post = new MemberPost("김기연", door_id);
            postValues = post.toMap();
        }
        childUpdates.put("/member/" + "01099296975", postValues);
        mPostReference.updateChildren(childUpdates);
    }
}
