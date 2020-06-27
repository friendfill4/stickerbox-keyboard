package com.example.vi6.tabbed;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by vi6 on 20-Feb-17.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter{
    private JSONArray jsonArray;
    Context context;

   /* public ViewPagerAdapter(FragmentManager fm, JSONArray jsonArray, Context context) {
        super(fm);
        this.jsonArray = jsonArray;
        this.context = context;
    }*/

    public ViewPagerAdapter(FragmentManager fm, JSONArray jsonArray) {
        super(fm);
        this.jsonArray = jsonArray;

    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }


    @Override
    public Fragment getItem(int position) {
        try {
            Log.e("name",jsonArray.getJSONObject(position).getString("id").toString() );
            String id=jsonArray.getJSONObject(position).getString("id").toString();
            Log.e("FROM ViewPagerAdapter ", "id : "+id);
            return  OneFragment.getInstance(position, id);
        } catch (JSONException e) {
            return  OneFragment.getInstance(position, "exception");
        }

    }

 /*   @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        TextView tv;
        tv= new TextView(context);
        try {
            Log.e("name",jsonArray.getJSONObject(position).getString("name").toString() );
            tv.setText(jsonArray.getJSONObject(position).getString("name").toString());
            ((ViewPager)collection).addView(tv);
            return tv;
        } catch (JSONException e) {
            Log.e("name","exception"+position );
            tv.setText("exception"+position);
            ((ViewPager)collection).addView(tv);
            return tv;
        }
    }*/

    @Override
    public CharSequence getPageTitle(int position) {
        try {
            return jsonArray.getJSONObject(position).getString("name");
        } catch (JSONException e) {
            return null;
        }
    }
}
