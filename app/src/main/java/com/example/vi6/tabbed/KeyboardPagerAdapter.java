package com.example.vi6.tabbed;

import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;

/**
 * Created by vi6 on 27-Feb-17.
 */

public class KeyboardPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private static int NUM_ITEMS = 3;
    Context c;
    public KeyboardPagerAdapter(android.support.v4.app.FragmentManager fm) {
        super(fm);
    }

    public KeyboardPagerAdapter(android.support.v4.app.FragmentManager fm, Context c) {
        super(fm);
        this.c = c;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                KeyboardFragment fm1 = new KeyboardFragment("http://cp3767.veba.co/~shubantech/admin_emojis/uploads/emojis/cat.gif");
                return fm1;
            case 1:
                KeyboardFragment fm2 = new KeyboardFragment("http://cp3767.veba.co/~shubantech/admin_emojis/uploads/emojis/fear.gif");
                return fm2;// Fragment # 0 - This will show FirstFragment different title
                //return KeyboardFragment.newInstance("http://cp3767.veba.co/~shubantech/admin_emojis/uploads/emojis/kick.gif");
            case 2:
                KeyboardFragment fm3 = new KeyboardFragment("http://cp3767.veba.co/~shubantech/admin_emojis/uploads/emojis/kick.gif");
                return fm3;
                // Fragment # 1 - This will show SecondFragment
                //return KeyboardFragment.newInstance("http://cp3767.veba.co/~shubantech/admin_emojis/uploads/emojis/fear.gif");
            default:
                KeyboardFragment fm4 = new KeyboardFragment("http://cp3767.veba.co/~shubantech/admin_emojis/uploads/emojis/cat.gif");
                return fm4;
                //return KeyboardFragment.newInstance("http://cp3767.veba.co/~shubantech/admin_emojis/uploads/emojis/cat.gif");
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}
