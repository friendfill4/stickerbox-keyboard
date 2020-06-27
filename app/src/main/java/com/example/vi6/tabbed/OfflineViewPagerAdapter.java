package com.example.vi6.tabbed;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by vi6 on 03-Mar-17.
 */

public class OfflineViewPagerAdapter extends FragmentPagerAdapter{
    ArrayList<String> list= new ArrayList<>();
    Context context;

    public OfflineViewPagerAdapter(FragmentManager fm, ArrayList<String> list) {
        super(fm);
        this.list = list;
    }

    public OfflineViewPagerAdapter(FragmentManager fm, ArrayList<String> list, Context context) {
        super(fm);
        this.list = list;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        String name = list.get(position);
        return TwoFragment.getInstance(position, name);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position);
    }
}
