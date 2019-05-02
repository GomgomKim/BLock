package com.example.block.tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.block.R;
import com.example.block.database.MemberPost;
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
public class SearchBlockFragment extends Fragment {

    @BindView(R.id.home_door_id) TextView home_door_id;

    RelativeLayout layout = null;

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
        return layout;
    }

    public void initSetting(){

    }

    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKim", "row: " + dataSnapshot.getChildrenCount());
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_name, get.home_door_id};
                    Log.d("gomKim", "user_id: " + key);
                    Log.d("gomKim", "user_name: " + info[0]);
                    Log.d("gomKim", "home_door_id: " + info[1]);
                    home_door_id.setText(info[1]);
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            }
            return;
        }
        else
            Log.d("SetUserHint","Cover OFF");
    }

}
