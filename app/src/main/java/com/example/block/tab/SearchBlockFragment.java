package com.example.block.tab;


import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.block.R;
import com.example.block.database.DoorPost;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchBlockFragment extends Fragment {

    @BindView(R.id.user_phone) TextView user_phone;
    @BindView(R.id.user_name) TextView user_name;
    @BindView(R.id.home_door_id) TextView home_door_id;

    @BindView(R.id.add_door_btn) RelativeLayout add_door_btn;
    @BindView(R.id.logout_btn) RelativeLayout logout_btn;

    RelativeLayout layout = null;

    String u_id = "";

    private DatabaseReference mPostReference;

    public SearchBlockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_search_block, container, false);
        ButterKnife.bind(this, layout);
        initSetting();
        getFirebaseDatabase();

//        testSolidity();
        return layout;
    }

    public void initSetting(){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) u_id = user.getUid();

        add_door_btn.setOnClickListener(v -> {
            setDialog();
        });
    }

    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_id, get.user_name, get.home_door_id};

                    if(u_id.equals(info[0])){
                        user_phone.setText(key);
                        user_name.setText(info[1]);
                        home_door_id.setText(info[2]);
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


    public void setDialog(){
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_door);

        RelativeLayout dialog_layout = (RelativeLayout) dialog.findViewById(R.id.dialog_layout);

        EditText search_door_id = (EditText) dialog.findViewById(R.id.search_door_id);
        Button add_door_btn = (Button) dialog.findViewById(R.id.add_door_btn);
        Button close_btn = (Button) dialog.findViewById(R.id.close_btn);

        add_door_btn.setOnClickListener(v -> {
            String insert_door_id = search_door_id.getText().toString();
            isDuplicatedDoor(insert_door_id);
            dialog.dismiss();
        });

        close_btn.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    public void isDuplicatedDoor(String door_id){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean is_duplicated = false;

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    DoorPost get = postSnapshot.getValue(DoorPost.class);
                    String[] info = {get.door_id};

                    if(door_id.equals(info[0])) is_duplicated = true;
                }

                if(is_duplicated){ // 중복입력
                    Toast.makeText(getContext(), "Registered door.", Toast.LENGTH_SHORT).show();
                }
                else{ // 문 정보 저장
                    Toast.makeText(getContext(), "It takes a few minutes to deploy block", Toast.LENGTH_LONG).show();
                    Toast.makeText(getContext(), "Please wait for notification message.", Toast.LENGTH_SHORT).show();

                    getUserData(door_id);
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

    public void getUserData(String door_id){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_id, get.token, get.user_name};

                    if(u_id.equals(info[0])){
                        Intent intent = new Intent(getContext(), TransactionService.class);
                        intent.putExtra("door_id", door_id);
                        intent.putExtra("user_token", info[1]);
                        intent.putExtra("user_id", info[0]);
                        intent.putExtra("user_name", info[2]);
                        intent.putExtra("state", "deploy");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            getActivity().startForegroundService(intent);
                        } else{
                            getActivity().startService(intent);
                        }
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){ // 유저가 화면을 보고있을 때
            if(this.layout != null){

            }
            return;
        }
        else
            Log.d("SetUserHint","Cover OFF");
    }



}
