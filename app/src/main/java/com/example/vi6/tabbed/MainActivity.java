package com.example.vi6.tabbed;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    RequestQueue rq;
    ArrayList<String> cat;
    ArrayList< allEmojisPOJO> listFromApi;
    static int list_size;
    TabLayout tabLayout;
    File dir;
    Intent mServiceIntent;
    HashMap<String, String> map;
    private static final int STORAGE_PERMISSION_CODE = 20;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle((Html.fromHtml("<font color=\"#FFFFFF\">" + "" + "</font>")));
        mViewPager = (ViewPager) findViewById(R.id.container);
        database = new Database(MainActivity.this);
        cat = new ArrayList<String>();
        listFromApi = new ArrayList<allEmojisPOJO>();
        allEmojisPOJO pojo;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isReadStorageAllowed()) {
                //If permission is already having then showing the toast
                Toast.makeText(MainActivity.this, "You already have the permission", Toast.LENGTH_LONG).show();
                //Existing the method with return
                return;
            }

            //If the app has not the permission then asking for the permission
            requestStoragePermission();

        }


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
        new catList().execute();
     //   serviceIni();
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    private void requestStoragePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    public class catList extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this,
                    ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            pDialog.setTitle("Please wait");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Downloading");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.setInverseBackgroundForced(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            rq = Volley.newRequestQueue(MainActivity.this);
            StringRequest sr = new StringRequest(Request.Method.GET, UrlCollection.categoriesListUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject mObject = new JSONObject(response);
                        JSONObject status = mObject.getJSONObject("status");
                        JSONArray data = status.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            String name = object.getString("name");
                            String id = object.getString("id");


                            list_size++;
                            //      Log.e("added", name);
                            //      Log.e("size", ""+list_size);
                            tabLayout.addTab(tabLayout.newTab().setText(name));
                            database.add_cat_id_pair(id, name);
                            dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" + name + "/");
                            if (!dir.exists()) {

                                dir.mkdirs();
                                Log.e("directory created",name+" "+id);
                            } else {

                                Log.e("DIRECTORY", "EXISTS");
                            }
                            File nomedia= new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" + name + "/",".nomedia");
                            boolean fileCreated = nomedia.createNewFile();
                            if (fileCreated) {
                                Log.e("nomedia", "created in "+name);
                            }

                        }
                        //     Log.e("category size api ", ""+tabLayout.getTabCount());
//                        Log.e("list size ", ""+list_size);

                        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), data);
                        mViewPager.setAdapter(adapter);
                        serviceIni();

                        /*    Pager pager = new Pager(getSupportFragmentManager(), list_size);
                        mViewPager.setAdapter(pager);*/
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
                    } catch (JSONException e) {
                        Log.e("parse", "error");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e("nomedia", "write nomedia file");
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            rq.add(sr);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();


        }
    }

    void serviceIni(){
        RequestQueue rq1 = Volley.newRequestQueue(MainActivity.this);
        StringRequest sr1 = new StringRequest(Request.Method.GET, UrlCollection.allEmojisListUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mObject = new JSONObject(response);
                    JSONObject status = mObject.getJSONObject("status");
                    JSONArray data = status.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {

                        JSONObject object = data.getJSONObject(i);
                        //  map= new HashMap<>();
                        String id = object.getString("id");
                        String emoji_title = object.getString("emoji_title");
                        String emoji_image = object.getString("emoji_image");
                        String emoji_category = object.getString("emoji_category");
                        String crate_date = object.getString("crate_date");
                        String name= emoji_title+".gif";

                        Log.e("api name ", emoji_title);
                        Log.e("api category ", emoji_category);
                        String cat=database.getCat(emoji_category);
                        Log.e("api folder ",cat);
                           /* pojo.setName(name);
                            pojo.setCatID(emoji_category);
                            pojo.setLink(emoji_image);*/
                        allEmojisPOJO pojo= new allEmojisPOJO(name,cat,emoji_image);
                        listFromApi.add(pojo);
                        //     Log.e("listFromApi", emoji_title + ".gif");
                    }
                    mServiceIntent = new Intent(MainActivity.this, BackgroundDownloadService.class);
                    mServiceIntent.putExtra("listFromApi", listFromApi);
                    MainActivity.this.startService(mServiceIntent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("JSON", "ErrorListener called");
            }
        });
        rq1.add(sr1);
    }


}
