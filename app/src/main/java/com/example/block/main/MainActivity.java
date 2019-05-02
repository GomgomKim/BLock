package com.example.block.main;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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

    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    NavigationTabBar navigationTabBar;
    ArrayList<NavigationTabBar.Model> models;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        backPressCloseHandler.onBackPressed();
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
                        .title("MyDoor")
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor("#e67988"))
                        .title("Contract")
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

    @Override
    public void setViewpager(int index) {
        navigationTabBar.setViewPager(viewPager, index);
    }
}
