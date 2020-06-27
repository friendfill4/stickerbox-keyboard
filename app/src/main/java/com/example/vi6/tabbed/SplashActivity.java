package com.example.vi6.tabbed;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    ArrayList<String> temp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            //we are connected to a network
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/");
                            File[] files = f.listFiles();

                            for (File inFile : files) {
                                if (inFile.isDirectory()) {
                                    // is directory
                                    final String fname = inFile.getName();
                                    temp.add(fname);
                                    Log.e("offline Directory", inFile.getName());
                                }
                            }
                            Intent intent = new Intent(SplashActivity.this, DownloadedGifs.class);
                            intent.putStringArrayListExtra("temp", temp);
                            startActivity(intent);
                            finish();
                        }

                        finish();
                    }
                }
            };
            timerThread.start();
        }
    }

