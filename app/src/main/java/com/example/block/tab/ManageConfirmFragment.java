package com.example.block.tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;

import com.example.block.R;
import com.example.block.adapter.InvitationList_sub;
import com.example.block.database.HostPost;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageConfirmFragment extends Fragment {

    @BindView(R.id.door_grid) GridLayout door_grid;

    RelativeLayout layout = null;

    public ManageConfirmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (RelativeLayout) inflater.inflate(R.layout.fragment_manage_confirm, container, false);
        ButterKnife.bind(this, layout);
        getFirebaseDatabaseHost();
        return layout;
    }

    public void createLayout(String door_id){ // host
        InvitationList_sub doorList_sub = new InvitationList_sub(getContext());
        doorList_sub.setHost(door_id);
        door_grid.addView(doorList_sub);
    }

    public void getFirebaseDatabaseHost(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    HostPost get = postSnapshot.getValue(HostPost.class);
                    String[] info = {get.user_id, get.user_name, get.door_id};
                    Log.d("gomKim", "index: " + key);
                    Log.d("gomKim", "user_id: " + info[0]);
                    Log.d("gomKim", "user_name: " + info[1]);
                    Log.d("gomKim", "door_id: " + info[2]);
                    createLayout(info[2]);
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

}
