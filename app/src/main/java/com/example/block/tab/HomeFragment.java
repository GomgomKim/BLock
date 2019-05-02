package com.example.block.tab;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.block.R;
import com.example.block.database.MemberPost;
import com.example.block.interfaces.UIdInterface;
import com.example.block.items.MemberItem;
import com.example.block.web3j.ContactBlockchain;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.web3j.protocol.exceptions.TransactionException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

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
    @BindView(R.id.btn_layout) RelativeLayout btn_layout;
    @BindView(R.id.open_btn) ImageView open_btn;

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
        Glide.with(this).load(R.raw.blockchain).into(none_block_img);
        Glide.with(this).load(R.raw.home_motion).into(open_btn);
    }

    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("gomKing", "row: " + dataSnapshot.getChildrenCount());
                user_info.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    MemberPost get = postSnapshot.getValue(MemberPost.class);
                    String[] info = {get.user_name, get.home_door_id,get.token, get.user_id};
                    user_info.add(new MemberItem(String.valueOf(key), info[0], info[1],info[2]));
//                    String user_id = ((UIdInterface)getActivity()).getUID();
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String user_id = user.getUid();
                    if(user_id.equals(info[3])) { // user id가 일치하는것만
                        afterSetting(key, info[0], info[1]);
                        Log.d("gomKing", "user_id: " + key);
                        Log.d("gomKing", "user_name: " + info[0]);
                        Log.d("gomKing", "home_door_id: " + info[1]);
                    } else continue;
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
            btn_layout.bringToFront();
            btn_layout.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Door open !", Toast.LENGTH_SHORT).show();
                Log.i("gomgomKim", "btn contract");

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    }
                }

                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    } else {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    }
                }

                try {
                    new ContactBlockchain(getContext());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TransactionException e) {
                    e.printStackTrace();
                }

                Log.i("gomgomKim", "start contract");


                long mNow;
                Date mDate;
                SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                mNow = System.currentTimeMillis();
                mDate = new Date(mNow);
                mFormat.format(mDate);
//                ((History)getActivity()).sethistory(String.valueOf(mFormat));
            });
        }
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