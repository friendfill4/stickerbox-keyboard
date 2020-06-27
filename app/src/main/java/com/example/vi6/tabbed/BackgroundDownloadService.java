package com.example.vi6.tabbed;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Button;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vi6 on 02-Mar-17.
 */

public class BackgroundDownloadService extends IntentService {
    String[] extensions = {"gif"};
    String TAG = "service";
    ArrayList<allEmojisPOJO> listFromApi;
    ArrayList<String> localList= new ArrayList<String>();
    ArrayList<String> localDirectoryList= new ArrayList<String>();
    ArrayList<allEmojisPOJO> toDownload= new ArrayList<allEmojisPOJO>();
    boolean check=false;
    allEmojisPOJO pojo;
    Database database;


    //THIS CONSTRUCTOR WAS NOT INVOKED AUTOMATICALLY, NEITHER IT WAS MENTIONED IN developer>.android.com DOCUMENTATION, WITHOUT THIS CONSTRUCTOR, I WAS GETTING ERROR IN MANIFEST, SO MADE IT BY HELP FROM STACKOVERFLOW
    public BackgroundDownloadService() {
        super("BackgroundDownloadService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */


    public BackgroundDownloadService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        listFromApi=(ArrayList<allEmojisPOJO>)intent.getSerializableExtra ("listFromApi");
        //WORK WILL START FROM HERE ON RECEIVING startService INTENT FROM ANY ACTIVITY/FRAGMENT
       // callService();
        database= new Database(this);
       /* for (int i=0;i<listFromApi.size();i++) {
            pojo = listFromApi.get(i);
            Log.e("listFromApi name ", pojo.getName());
            Log.e("listFromApi url ", pojo.getLink());
        }*/
        getLocalList();
        for (int k=0; k<localList.size();k++) {
            Log.e("localList", "val "+localList.get(k).toString());
        }
        getDownloadList();
        for (int k=0; k<toDownload.size();k++) {
            Log.e("toDownload size ",""+toDownload.size());
            pojo = toDownload.get(k);
            String name= pojo.getName();
            String url= pojo.getLink();
            String category= pojo.getCatID();
            /*String folder = database.get_cat_name_from_id(pojo.getCatID());
            Log.e("toDownload", "folder "+folder);*/
            downloadServ(name, url, category);
        }
        }

    private void downloadServ(String name, String imageUrl, String category) {
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" + category;
        File baseDirec = new File(baseDir);
        if (!baseDirec.exists()) {
            //     Log.e("Directory ", "creating");
            baseDirec.mkdirs();
            //   Log.e("Directory ", "created");
        } else {
            //  Log.e("Directory ", "exists");
        }
        File file = new File(baseDirec, name);
        if (file.exists()) {
            Log.e("File ", "need not create");
            //   share(file);
        } else {
            try {
                Log.e("File ", "creating");
                URL url = new URL(imageUrl);
                long startTime = System.currentTimeMillis();
                URLConnection ucon = url.openConnection();
                Log.e(TAG, "on do in background, url open connection");
                InputStream is = ucon.getInputStream();
                Log.e(TAG, "on do in background, url get input stream");
                BufferedInputStream bis = new BufferedInputStream(is);
                Log.e(TAG, "on do in background, create buffered input stream");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                Log.e(TAG, "on do in background, create buffered array output stream");
                int current = 0;
                Log.e(TAG, "on do in background, write byte to baos");
                while ((current = bis.read()) != -1) {
                    baos.write(current);
                }
                Log.e(TAG, "on do in background, done write");
                Log.e(TAG, "on do in background, create fos");
                try {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "permission Granted inside method");
                        FileOutputStream fos = new FileOutputStream(file);
                        Log.e(TAG, "on do in background, FOS object created");
                        fos.write(baos.toByteArray());
                        Log.e(TAG, "on do in background, write to fos");
                        fos.flush();
                        fos.close();
                        is.close();
                        Log.e(TAG, "on do in background, done write to fos ");
                        Log.e(TAG, "Downloaded file "+name+" in "+category);
                       // Toast.makeText(this, "Downloaded : " + name, Toast.LENGTH_SHORT).show();
                    }
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "permission issues inside method");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "on do in background, error write to fos");
                }
                //  share(file);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void getLocalList() {
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/");
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                // is directory
                final String fname = inFile.getName();
                localDirectoryList.add(fname);
                File fileL = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" + fname);
              //  Log.e("Directoey ", fname);
                if (fileL.isDirectory()) {
                    File[] filesL = fileL.listFiles();
                    if (filesL != null && filesL.length > 0) {
                        for (File fL : filesL) {
                            if (fL.isDirectory()) {
                            } else {
                                for (int j = 0; j < extensions.length; j++) {
                                    if (fL.getAbsolutePath().endsWith(extensions[j])) {
                                        String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" + fname + "/" + fL.getName();
                                        localList.add(fL.getName());
                                     //   Log.e("add to list : ", fL.getName());
                                    }
                                }
                            }
                        }
                    }else {
                      //  Log.e("directory : "+fileL.getName(), "empty");
                    }
                }

            }

        }
      //  getDownloadList();
    }

    private void getDownloadList() {
        Log.e("inside ","method");
        for (int i = 0; i < listFromApi.size(); i++) {
            pojo = listFromApi.get(i);
            String sName = pojo.getName();
          //  String sUrl = map.get("url");
            for (int j = 0; j < localList.size(); j++) {
                String s2 = localList.get(j).toString();
                if (s2.equals(sName)) {
                    check = true;
                }
            }
            if (!check) {
                toDownload.add(pojo);
                //check = false;
            } else {
                check = false;
            }

        }
    }
}
