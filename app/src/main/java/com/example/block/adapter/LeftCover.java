package com.example.block.adapter;


import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.block.R;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeftCover extends Fragment {

    @BindView(R.id.profile_img) ImageView profile_img;
    NavigationView layout = null;

    public LeftCover() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        layout = (NavigationView) inflater.inflate(R.layout.fragment_left_cover, container, false);
        ButterKnife.bind(this, layout);

//        profileSetting();

        return layout;
    }

    public void profileSetting(){
        Glide.with(getContext()).load(R.drawable.profile_sample).apply(new RequestOptions().circleCrop()).into(profile_img);
    }


}
