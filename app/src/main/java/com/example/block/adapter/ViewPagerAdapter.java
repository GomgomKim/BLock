package com.example.block.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.example.block.tab.AcceptFragment;
import com.example.block.tab.HomeFragment;
import com.example.block.tab.ManageConfirmFragment;
import com.example.block.tab.MyBlockFragment;
import com.example.block.tab.SearchBlockFragment;


//Pager Adapter
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        switch(position){
            case 0:
                return new HomeFragment();
            case 1:
                return new MyBlockFragment();
            case 2:
                return new ManageConfirmFragment();
            case 3:
                return new AcceptFragment();
            case 4:
                return new SearchBlockFragment();
            default:
                return null;
        }
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return 5;
    }


}