package com.example.block.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.block.R;
import com.example.block.adapter.LeftCover;
import com.example.block.adapter.ViewPagerAdapter;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    ViewPagerAdapter viewPagerAdapter;
    SetViewPagerTabListener setViewPagerTabListener;

    private boolean fragment_state = false;

    private LeftCover leftCover;
    private ActionBarDrawerToggle dtToggle;

    //tool bar
    Toolbar toolbar;
    DrawerLayout dlDrawer;

    ViewPager mainPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initsetting();
        setToolbar();
    }

    public interface SetViewPagerTabListener{
        void setTab(int position);
    }

    public void initsetting() {
        mainPager=(ViewPager)findViewById(R.id.mainPager);
        mainPager.setOffscreenPageLimit(5);
        setViewPagerTabListener= position -> {
            switch (position){
                case 0:
                    mainPager.setCurrentItem(0);
                    break;
                case 1:
                    mainPager.setCurrentItem(1);
                    break;
                case 2:
                    mainPager.setCurrentItem(2);
                    break;
                case 3:
                    mainPager.setCurrentItem(3);
                    break;
                case 4:
                    mainPager.setCurrentItem(4);
                    break;
            }
        };

        viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager(), setViewPagerTabListener);
        mainPager.setAdapter(viewPagerAdapter);
    }

    public void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlDrawer.addDrawerListener(dtToggle);
        dtToggle.syncState();

        leftCover = (LeftCover) getSupportFragmentManager().findFragmentById(R.id.drawer);

        ImageButton buttonCloseDrawer = (ImageButton) findViewById(R.id.btn_close);
        buttonCloseDrawer.setOnClickListener(arg0 -> dlDrawer.closeDrawers());

        toolbar.bringToFront();

        LinearLayout nav_cover = (LinearLayout) findViewById(R.id.nav_cover);
        nav_cover.setOnClickListener(view -> {
            setViewPagerTabListener.setTab(0);
            toolbar.bringToFront();
            dlDrawer.closeDrawers();
        });

        LinearLayout nav_play_list = (LinearLayout) findViewById(R.id.nav_play_list);
        nav_play_list.setOnClickListener(view -> {
            setViewPagerTabListener.setTab(1);
            toolbar.bringToFront();
            dlDrawer.closeDrawers();
        });

        LinearLayout nav_booklet = (LinearLayout) findViewById(R.id.nav_booklet);
        nav_booklet.setOnClickListener(view -> {
            setViewPagerTabListener.setTab(2);
            toolbar.bringToFront();
            dlDrawer.closeDrawers();
        });

        LinearLayout nav_video = (LinearLayout) findViewById(R.id.nav_video);
        nav_video.setOnClickListener(view -> {
            setViewPagerTabListener.setTab(3);
            toolbar.bringToFront();
            dlDrawer.closeDrawers();
        });

        LinearLayout nav_thanks_to = (LinearLayout) findViewById(R.id.nav_thanks_to);
        nav_thanks_to.setOnClickListener(view -> {
            setViewPagerTabListener.setTab(4);
            toolbar.bringToFront();
            dlDrawer.closeDrawers();
        });

        /*ImageButton nav_schedule = (ImageButton) findViewById(R.id.nav_schedule);
        nav_schedule.setOnClickListener(view -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ScheduleFragment())
                    .commit();
            toolbar.bringToFront();
            dlDrawer.closeDrawers();
            fragment_state = true;
        });

        ImageButton nav_sns = (ImageButton) findViewById(R.id.nav_sns);
        nav_sns.setOnClickListener(view -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new SnsFragment())
                    .commit();
            toolbar.bringToFront();
            dlDrawer.closeDrawers();
            fragment_state = true;
        });

        ImageButton nav_review = (ImageButton) findViewById(R.id.nav_review);
        nav_review.setOnClickListener(view -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ReviewFragment())
                    .commit();
            toolbar.bringToFront();
            dlDrawer.closeDrawers();
            fragment_state = true;
        });*/
    }
}
