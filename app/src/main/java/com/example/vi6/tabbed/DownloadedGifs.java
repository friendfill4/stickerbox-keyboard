package com.example.vi6.tabbed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class DownloadedGifs extends AppCompatActivity {
    GridView grid;
    Context context;
    private ArrayList<String> giflist = new ArrayList<String>();
    ArrayList<String> temp;
    String[] extensions = {"gif"};
    TabLayout tabLayout;
    StaticImageAdapter staticAdapter;
    Database database;
    boolean check = false;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_gifs);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "" + "</font>")));
     /*   ttv = (TextView) findViewById(R.id.ttv);*/
        //      database = new Database(DownloadedGifs.this);
        mViewPager = (ViewPager) findViewById(R.id.container);
//        temp = database.get_all_categoreies();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));


        temp = getIntent().getStringArrayListExtra("temp");

            for (int i = 0; i < temp.size(); i++) {
              //  Log.e("offline ", temp.get(i));
                tabLayout.addTab(tabLayout.newTab().setText(temp.get(i)));
            }
            OfflineViewPagerAdapter viewPagerAdapter = new OfflineViewPagerAdapter(getSupportFragmentManager(), temp);
            mViewPager.setAdapter(viewPagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                int i = mViewPager.getCurrentItem();
                TabLayout.Tab tab = tabLayout.getTabAt(i);
                tab.select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        }

    }
