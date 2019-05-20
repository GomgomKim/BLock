package com.example.block.tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.example.block.R;
import com.example.block.adapter.GuestRequest_sub;
import com.example.block.adapter.HostRequestConfirm_sub;
import com.example.block.adapter.HostRequest_sub;
import com.example.block.database.GuestRequestPost;
import com.example.block.database.HostPost;
import com.example.block.database.HostRequestPost;
import com.example.block.database.MemberPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class AcceptFragment extends Fragment {

    @BindView(R.id.request_grid)
    GridLayout request_grid;

    RelativeLayout layout = null;


    public AcceptFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_accept, container, false);
        ButterKnife.bind(this, layout);
        initSetting();
        return layout;
    }

    public void initSetting(){
        getHostRequest();
        getGuestRequest();
    }

    public void createHostReqConfirmLayout(String sender_name, String sender_phone, String door_id, String receiver_token){ // host confirm
        if(getContext() != null){
            HostRequestConfirm_sub hostRequestConfirm_sub = new HostRequestConfirm_sub(getContext());
            hostRequestConfirm_sub.setHostConfirm(sender_name, sender_phone, door_id, receiver_token);
            request_grid.addView(hostRequestConfirm_sub);
        }
    }

    public void createHostReqLayout(String sender_name, String sender_phone, String receiver_name, String receiver_phone, String door_id, String receiver_token){ // host
        if(getContext() != null){
            HostRequest_sub hostRequest_sub = new HostRequest_sub(getContext());
            hostRequest_sub.setHost(sender_name, sender_phone, receiver_name, receiver_phone, door_id, receiver_token);
            request_grid.addView(hostRequest_sub);
        }
    }

    public void createGuestReqLayout(String sender_name, String sender_phone, String start_time, String end_time, String door_id, String receiver_token){ // host
        if(getContext() != null){
            GuestRequest_sub guestRequest_sub = new GuestRequest_sub(getContext());
            guestRequest_sub.setGuest(sender_name, sender_phone, start_time, end_time, door_id, receiver_token);
            request_grid.addView(guestRequest_sub);
        }
    }

    public void getHostRequest(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    HostRequestPost get = postSnapshot.getValue(HostRequestPost.class);
                    String[] info = {get.receiver_token, get.sender_name, get.sender_phone, get.door_id, get.accept_count};

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String u_id = user.getUid();



                    // 초대 받은사람
                    String token = FirebaseInstanceId.getInstance().getToken();
                    if(token.equals(info[0])){
                        if(info[4].equals("0")){
                            createHostReqConfirmLayout(info[1], info[2], info[3], info[0]);
                        }
                    } else{
                        findhosts(u_id, info[3], info[0], info[1], info[2]);
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

    public void findhosts(String user_id, String door_id, String receiver_token, String sender_name, String sender_phone){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    HostPost get = postSnapshot.getValue(HostPost.class);
                    String[] info = {get.user_id, get.user_name, get.door_id};

                    if(user_id.equals(info[0]) && door_id.equals(info[2])){
                        findreceiver(receiver_token, sender_name, sender_phone, door_id);
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

    public void getGuestRequest(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    GuestRequestPost get = postSnapshot.getValue(GuestRequestPost.class);
                    String[] info = {get.receiver_token, get.sender_name, get.sender_phone, get.start_time, get.end_time, get.door_id};

                    String token = FirebaseInstanceId.getInstance().getToken();
                    if(token.equals(info[0])){
                        createGuestReqLayout(info[1], info[2], info[3], info[4], info[5], info[0]);
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

    public void findreceiver(String receiver_token, String sender_name, String sender_phone, String door_id){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String receiver_name = "";
                String receiver_phone = "";

                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.token, get.user_name};

                    if(receiver_token.equals(info[0])){
                        receiver_phone = key;
                        receiver_name = info[1];
                        createHostReqLayout(sender_name, sender_phone, receiver_name, receiver_phone, door_id, receiver_token);
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

    public void refreshView(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}
