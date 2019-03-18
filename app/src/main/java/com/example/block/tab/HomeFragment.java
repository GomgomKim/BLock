package com.example.block.tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.block.R;
import com.example.block.database.MemberPost;
import com.example.block.items.MemberItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    // do not have home
    @BindView(R.id.none_block_layout) RelativeLayout none_block_layout;
    @BindView(R.id.home_back) ImageView home_back;
    @BindView(R.id.none_block_img) ImageView none_block_img;

    // have home
    @BindView(R.id.on_block_layout) RelativeLayout on_block_layout;
    @BindView(R.id.door_id) TextView door_id;
    @BindView(R.id.my_state) TextView my_state;
    @BindView(R.id.open_btn) ImageButton open_btn;

    // database
    static ArrayList<MemberItem> user_info =  new ArrayList<MemberItem>();

    RelativeLayout layout = null;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, layout);
        initSetting();
        getFirebaseDatabase();


        return layout;
    }

    public void initSetting() {
        // do not have home
        Glide.with(this).load(R.raw.home_motion).into(none_block_img);
    }

    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                user_info.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_name, get.home_door_id};
                    Log.d("gomKim", "user_id: " + key);
                    Log.d("gomKim", "user_name: " + info[0]);
                    Log.d("gomKim", "home_door_id: " + info[1]);
                    user_info.add(new MemberItem(String.valueOf(key), info[0], info[1]));
                    afterSetting(key, info[0], info[1]);
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

    public void afterSetting(String user_id, String user_name, String home_door_id){
        if(home_door_id.equals("")){ // do not have home
            none_block_layout.setVisibility(View.VISIBLE);
            on_block_layout.setVisibility(View.GONE);
            none_block_layout.bringToFront();
        } else{ // have home
            none_block_layout.setVisibility(View.GONE);
            on_block_layout.setVisibility(View.VISIBLE);
            on_block_layout.bringToFront();
            door_id.setText(home_door_id);
            open_btn.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Door open !", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
