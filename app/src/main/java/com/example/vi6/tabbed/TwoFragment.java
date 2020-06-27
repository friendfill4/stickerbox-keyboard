package com.example.vi6.tabbed;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vi6 on 03-Mar-17.
 */

public class TwoFragment extends Fragment {
    GridView grid;
    TextView tv;
    String name;
    ArrayList<String> imagesListl = new ArrayList<>();
    String[] extensions = {"gif"};
    StaticImageAdapter staticImageAdapter;

    public TwoFragment(String name) {
        this.name = name;
      //  imagesListl.clear();
    }

    public TwoFragment() {
    }

    public static Fragment getInstance(int position, String name){
        TwoFragment fragment = new TwoFragment(name);

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1, container, false);

        tv=(TextView)view.findViewById(R.id.tv);

        grid = (GridView) view.findViewById(R.id.gridView);
        staticImageAdapter= new StaticImageAdapter(imagesListl,getContext());
        grid.setAdapter(staticImageAdapter);
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" +name;
        File file = new File(baseDir);
        if (imagesListl.size() > 0) {
            imagesListl.clear();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                    } else {
                        Log.e("fragment size",""+imagesListl.size());
                        for (int i = 0; i < extensions.length; i++) {
                            if (f.getAbsolutePath().endsWith(extensions[i])) {
                                String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" +name+ "/" + f.getName();
                                imagesListl.add(s);
                                // Log.e("add to download list : ", f.getName());
                                 staticImageAdapter.notifyDataSetChanged();
                                Log.e("offline "+name, "list "+s);
                            }
                        }


                    }
                }
            }


        }

        return view;
    }


    private class getImagesFromCategory extends AsyncTask<Void, Void, Void> {
        ProgressDialog pDialog;

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

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pDialog.dismiss();

        }
    }
    }
