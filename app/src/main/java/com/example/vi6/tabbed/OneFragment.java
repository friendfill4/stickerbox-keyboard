package com.example.vi6.tabbed;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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

/**
 * Created by vi6 on 20-Feb-17.
 */

public class OneFragment extends Fragment{
    GridView grid;
    ArrayList<allEmojisPOJO> pojoList;
   // ArrayList<String> imageList;
  /*  HashMap<String, String> map,map1;*/
    TextView tv;
    Database database;
    String foldrName;
    String id,TAG = "save gif ";
    allEmojisPOJO pojo,pojo1;
    ProgressDialog pDialog;
    int REQUEST_WRITE_EXTERNAL_STORAGE=1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE= 20;

    public OneFragment() {
    }

    public OneFragment(String name) {
        this.id = name;
       // Log.e("ACTUAL", "id from constructor : "+id);
    }

    public static Fragment getInstance(int position,String name){
        OneFragment fragment = new OneFragment(name);

        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putInt("position", position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_1, container, false);
        database= new Database(getContext());
        //mapList= new ArrayList<HashMap<String, String>>();
        pojoList= new ArrayList<allEmojisPOJO>();
        //   imageList= new ArrayList<>();
        tv=(TextView)view.findViewById(R.id.tv);
        grid = (GridView) view.findViewById(R.id.gridView);
        int position = getArguments().getInt("position", 0);
        String name = getArguments().getString("name");
       // TextView tv=new TextView(c);
        // set your list base on position

        new getImagesFromCategory().execute();
        return view;
    }



    private class getImagesFromCategory extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(getContext(),
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
   String url = UrlCollection.getImagesByCategoryId + id;
         //   Log.e("ID", "In URL "+id);
      //  Log.e("url", url);

        RequestQueue rq = Volley.newRequestQueue(getContext());
        StringRequest sr= new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject mObject= new JSONObject(response);
                    JSONObject status = mObject.getJSONObject("status");
                    String result = status.getString("result");
                    if (result.equals("1")) {

                        JSONArray data = status.getJSONArray("data");
                        for (int i=0; i<data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            String imageUrl= object.getString("emoji_image");
                            String imageNam = object.getString("emoji_title");
                           String imageName=imageNam+".gif";
                            /* String imageId = object.getString("id");
                            map.put("imageUrl", imageUrl);
                            map.put("imageId", imageId);*/
          //                  map= new HashMap<>();

                           /* map.put("imageUrl", imageUrl);
                            map.put("imageName", imageName);*/
                            String category=database.getCat(id);
                            pojo = new allEmojisPOJO(imageName, category, imageUrl);
                            pojoList.add(pojo);
                  //          Log.e("image Url : ",imageUrl);
                   //         Log.e("image Name : ",imageName);
                        //    imageList.add(imageUrl);
                        }
                    //    Log.e("list size : ",""+mapList.size());

                    }
                    ImageAdapter imageAdapter= new ImageAdapter(pojoList, getContext());
                    grid.setAdapter(imageAdapter);
                } catch (JSONException e) {
                    Log.e("parse","error");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response","error");
            }
        });
        rq.add(sr);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long pid) {
                    pojo1=pojoList.get(position);
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.e("permission", "Granted");
                        new downloadGif(pojo1).execute();
                    }
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Log.e("permission", "Denied");
                        Toast.makeText(getContext(), "You need to provide permission to perform this action", Toast.LENGTH_SHORT).show();
                    }



                }
            });

            pDialog.dismiss();
        }
    }

    private class downloadGif extends AsyncTask<Void, Void, Void> {

        public ProgressDialog pDialog;
        allEmojisPOJO pojoTemp;

        public downloadGif( allEmojisPOJO pojo2) {
            this.pojoTemp = pojo2;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getContext(),
                    ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
            pDialog.setTitle("Downloading");
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage("Writing the gif to your sdcard");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.setInverseBackgroundForced(true);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            /*shareGif(imageUrl);*/
            //foldrName= database.get_cat_name_from_id(id);
           // Log.e("ID", "Calling database " + id);
            String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" +pojoTemp.getCatID();
            File baseDirec = new File(baseDir);
            if (!baseDirec.exists()) {
           //     Log.e("Directory ", "creating");
                baseDirec.mkdirs();
             //   Log.e("Directory ", "created");
            } else {
              //  Log.e("Directory ", "exists");
            }
            String fileName = pojoTemp.getName();
            File file = new File(baseDirec, fileName);
            if (file.exists()) {
                Log.e("File ", "need not create");
                //   share(file);
            } else {
                try {
                    Log.e("File ", "creating");
                    URL url = new URL(pojoTemp.getLink());
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
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                            Log.e(TAG, "permission Granted inside method");
                            FileOutputStream fos = new FileOutputStream(file);
                            Log.e(TAG, "on do in background, FOS object created");
                            fos.write(baos.toByteArray());
                            Log.e(TAG, "on do in background, write to fos");
                            fos.flush();
                            fos.close();
                            is.close();
                            Log.e(TAG, "on do in background, done write to fos");
                        }
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();
            String baseDir = Environment.getExternalStorageDirectory() + "/Nemojis app/Animated Gifs/"+pojoTemp.getCatID()+"/";
            File baseDirec = new File(baseDir);
            String fileName = pojoTemp.getName();
            File file = new File(baseDirec, fileName);
            share(file);


        }
    }

  /*  private void shareGif(String resourceName) {

        String baseDir = Environment.getExternalStorageDirectory()+"/Nemojis app/Animated Gifs/";
        File baseDirec=new File(baseDir);
        if (!baseDirec.exists()) {
            baseDirec.mkdirs();
        }
        String fileName = resourceName.substring(62);
        Log.e("FILE NAME : ", fileName);

        else {

        }
    }*/

    private void share(File resourceName) {

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("image/gif");
        Uri uri = Uri.fromFile(resourceName);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(shareIntent, "Share Emoji"));
    }










/*

    protected boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    protected void requestPermission() {

        *//*if (Fragment.shouldShowRequestPermissionRationale((Activity) getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {*//*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        *//*}*//*
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do your work
                    Log.e("value", "GRANTED .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }*/

/*
    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("Write External Storage permission is necessary to write event!!!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity)getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(), "Yuhuu Granted!!!!!",Toast.LENGTH_SHORT).show();
                    //  writeCalendarEvent();
                    new downloadGif(tempUrl).execute();
                } else {
//code for deny
                    Toast.makeText(getContext(), "!!!!!!!!!!!!!!NOT Granted!!!!!",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }*/
}
