package com.example.block.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.example.block.R;
import com.example.block.adapter.BackPressCloseHandler;
import com.example.block.adapter.ViewPagerAdapter;
import com.example.block.interfaces.MainInterface;

import java.util.ArrayList;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity implements MainInterface {

    static ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    NavigationTabBar navigationTabBar;
    ArrayList<NavigationTabBar.Model> models;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        onNewIntent(getIntent());
        checkPermission();
        setIsNetwork();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }


    @Override
    protected void onResume() {
        super.onResume();
        viewPagerAdapter.notifyDataSetChanged();
    }

    //noti 누르면 바로 accept화면으로
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if(extras != null){
            viewPager.setCurrentItem(3);
        }
    }

    public void setIsNetwork(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){

        } else{
            // alert
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setPositiveButton("확인", (dialog, which) -> dialog.dismiss());
            alert.setMessage("네트워크가 연결되지 않았습니다. 일부 기능이 제한될 수 있습니다.");
            alert.show();
        }
    }

    private void initUI() {
        backPressCloseHandler = new BackPressCloseHandler(this);

        viewPager = (ViewPager) findViewById(R.id.vp_horizontal_ntb);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(true, new CubeOutTransformer());

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        navigationTabBar  = (NavigationTabBar) findViewById(R.id.ntb_horizontal);
        models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.home),
                        Color.parseColor("#6ab9c5"))
                        .title("Home")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.locking),
                        Color.parseColor("#e7b452"))
                        .title("Door List")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor("#e67988"))
                        .title("Invitation")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.history),
                        Color.parseColor("#858edb"))
                        .title("Request")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.search),
                        Color.parseColor("#aec865"))
                        .title("Info")
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 0);
        navigationTabBar.post(() -> {
            final View viewPager = findViewById(R.id.vp_horizontal_ntb);
            ((ViewGroup.MarginLayoutParams) viewPager.getLayoutParams()).topMargin =
                    (int) -navigationTabBar.getBadgeMargin();
            viewPager.requestLayout();
        });

        navigationTabBar.setOnTabBarSelectedIndexListener(new NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final NavigationTabBar.Model model, final int index) {

            }

            @Override
            public void onEndTabSelected(final NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });
    }

    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        }
    }

    @Override
    public void setViewpager(int index) {
        navigationTabBar.setViewPager(viewPager, index);
    }

    static void setCurrentViewpager(int index){
        viewPager.setCurrentItem(index);
    }
}
