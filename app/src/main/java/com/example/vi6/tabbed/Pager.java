package com.example.vi6.tabbed;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by vi6 on 20-Feb-17.
 */

public class Pager extends FragmentStatePagerAdapter{

    int tabCount;

    public Pager(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
  /*      switch (position) {
            case 0:
                Fragment1 fragment1= new Fragment1();
                return fragment1;
            case 1:
                Fragment2 fragment2= new Fragment2();
                return fragment2;
            case 2:
                Fragment3 fragment3= new Fragment3();
                return fragment3;
            case 3:
                Fragment4 fragment4= new Fragment4();
                return fragment4;
            default:
                return null;
        }*/
        return null;
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
