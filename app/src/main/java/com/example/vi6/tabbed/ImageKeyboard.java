/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.vi6.tabbed;

import android.app.AppOpsManager;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.inputmethodservice.InputMethodService;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.view.inputmethod.EditorInfoCompat;
import android.support.v13.view.inputmethod.InputConnectionCompat;
import android.support.v13.view.inputmethod.InputContentInfoCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputBinding;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ImageKeyboard extends InputMethodService implements View.OnClickListener{
    LinearLayout layout;
    RecyclerView recyclerView;
    private static final String TAG = "ImageKeyboard";
//    private static final String AUTHORITY = "com.example.android.commitcontent.ime.inputcontent";
    private static final String MIME_TYPE_GIF = "image/gif";
    String[] extensions = {"gif"};
    LinearLayout temp, temp2;
    private ArrayList<String> giflists = new ArrayList<String>();
    ArrayList<String> imageList = new ArrayList<String>();
    ArrayList<String> imageList1 = new ArrayList<String>();
    ArrayList<String> buttonList = new ArrayList<String>();
    ArrayList<String> directoryList;
    ViewPager mViewPager;
    Button button;
    TabLayout tabLayout;
    KeyboardPagerAdapter keyboardPagerAdapter;
    ImageView gifImage;
    HorizontalScrollView buttonArrangement;
    RecyclerAdapter recyclerAdapter;

    private boolean isCommitContentSupported(
            @Nullable EditorInfo editorInfo, @NonNull String mimeType) {
        if (editorInfo == null) {
            return false;
        }

        final InputConnection ic = getCurrentInputConnection();
        if (ic == null) {
            return false;
        }

        if (!validatePackageName(editorInfo)) {
            return false;
        }

        final String[] supportedMimeTypes = EditorInfoCompat.getContentMimeTypes(editorInfo);
        for (String supportedMimeType : supportedMimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, supportedMimeType)) {
                return true;
            }
        }
        return false;
    }

    private void doCommitContent(@NonNull String description, @NonNull String mimeType,
                                 @NonNull File file) {
        final EditorInfo editorInfo = getCurrentInputEditorInfo();

        // Validate packageName again just in case.
        if (!validatePackageName(editorInfo)) {
            return;
        }

        final Uri contentUri = Uri.fromFile(file);

        // As you as an IME author are most likely to have to implement your own content provider
        // to support CommitContent API, it is important to have a clear spec about what
        // applications are going to be allowed to access the content that your are going to share.
        final int flag;
        if (Build.VERSION.SDK_INT >= 25) {
            // On API 25 and later devices, as an analogy of Intent.FLAG_GRANT_READ_URI_PERMISSION,
            // you can specify InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION to give
            // a temporary read access to the recipient application without exporting your content
            // provider.
            flag = InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION;
        } else {
            // On API 24 and prior devices, we cannot rely on
            // InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION. You as an IME author
            // need to decide what access control is needed (or not needed) for content URIs that
            // you are going to expose. This sample uses Context.grantUriPermission(), but you can
            // implement your own mechanism that satisfies your own requirements.
            flag = 0;
            try {
                // TODO: Use revokeUriPermission to revoke as needed.
                grantUriPermission(
                        editorInfo.packageName, contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e) {
                Log.e(TAG, "grantUriPermission failed packageName=" + editorInfo.packageName
                        + " contentUri=" + contentUri, e);
            }
        }

        final InputContentInfoCompat inputContentInfoCompat = new InputContentInfoCompat(
                contentUri,
                new ClipDescription(description, new String[]{mimeType}),
                null /* linkUrl */);
        InputConnectionCompat.commitContent(
                getCurrentInputConnection(), getCurrentInputEditorInfo(), inputContentInfoCompat,
                flag, null);
    }

    private boolean validatePackageName(@Nullable EditorInfo editorInfo) {
        if (editorInfo == null) {
            return false;
        }
        final String packageName = editorInfo.packageName;
        if (packageName == null) {
            return false;
        }

        // In Android L MR-1 and prior devices, EditorInfo.packageName is not a reliable identifier
        // of the target application because:
        //   1. the system does not verify it [1]
        //   2. InputMethodManager.startInputInner() had filled EditorInfo.packageName with
        //      view.getContext().getPackageName() [2]
        // [1]: https://android.googlesource.com/platform/frameworks/base/+/a0f3ad1b5aabe04d9eb1df8bad34124b826ab641
        // [2]: https://android.googlesource.com/platform/frameworks/base/+/02df328f0cd12f2af87ca96ecf5819c8a3470dc8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true;
        }

        final InputBinding inputBinding = getCurrentInputBinding();
        if (inputBinding == null) {
            // Due to b.android.com/225029, it is possible that getCurrentInputBinding() returns
            // null even after onStartInputView() is called.
            // TODO: Come up with a way to work around this bug....
            Log.e(TAG, "inputBinding should not be null here. "
                    + "You are likely to be hitting b.android.com/225029");
            return false;
        }
        final int packageUid = inputBinding.getUid();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            final AppOpsManager appOpsManager =
                    (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            try {
                appOpsManager.checkPackage(packageUid, packageName);
            } catch (Exception e) {
                return false;
            }
            return true;
        }

        final PackageManager packageManager = getPackageManager();
        final String possiblePackageNames[] = packageManager.getPackagesForUid(packageUid);
        for (final String possiblePackageName : possiblePackageNames) {
            if (packageName.equals(possiblePackageName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
         // TODO: Avoid file I/O in the main thread.
        final File imagesDir = new File(getFilesDir(), "images");
        imagesDir.mkdirs();
    }

    @Override
    public View onCreateInputView() {
        directoryList = new ArrayList<>();

        float width = getResources().getDimension(R.dimen.lheight);
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) width));
        layout.setOrientation(LinearLayout.VERTICAL);

        recyclerView = new RecyclerView(this);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (width * 0.8)
        );
        recyclerView.setLayoutParams(param);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
      /*  RecyclerAdapter recyclerAdapter = new RecyclerAdapter(getApplicationContext(), imageList);
        recyclerView.setAdapter(recyclerAdapter);*/
        recyclerAdapter  = new RecyclerAdapter(getApplicationContext(), imageList1);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                File file1 = new File(imageList1.get(position));
                Log.e("KEYBOARDDDDD", "Position : " + position + "\n\nName : " + imageList1.get(position));
                ImageKeyboard.this.doCommitContent("A waving flag", MIME_TYPE_GIF, file1);
            }
        }));

        buttonArrangement = new HorizontalScrollView(this);
        temp = new LinearLayout(this);
        LinearLayout.LayoutParams paramTemp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        temp.setLayoutParams(paramTemp);
        temp.setOrientation(LinearLayout.HORIZONTAL);
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/");
        File[] files = f.listFiles();
        for (File inFile : files) {
            if (inFile.isDirectory()) {
                // is directory
                final String fname = inFile.getName();
                directoryList.add(fname);
                File fileL = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" + fname);
                Log.e("Directoey ", fname);
                if (fileL.isDirectory()) {
                    File[] filesL = fileL.listFiles();
                    if (filesL != null && filesL.length > 0) {
                        for (File fL : filesL) {
                            if (fL.isDirectory()) {
                            } else {
                                for (int j = 0; j < extensions.length; j++) {
                                    if (fL.getAbsolutePath().endsWith(extensions[j])) {
                                        String s = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Nemojis app/Animated Gifs/"+fname+"/"+fL.getName();
                                        giflists.add(s);
                                        Log.e("add to list : ", s);
                                    }
                                }
                            }
                        }
                    }
                }
                button = new Button(this);
                button.setText(fname);
                button.setTextSize(11);
               // button.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_button));
               /* button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "clicked " + fname, Toast.LENGTH_SHORT).show();
                    }
                });*/
                button.setOnClickListener(this);
                button.setTag(fname);
                buttonList.add(fname);
                button.setTextColor(Color.parseColor("#000000"));
                temp.addView(button);
            }
        }
        button.setOnClickListener(this);
        /*for (int i = 0; i < directoryList.size(); i++) {
            String tempL = directoryList.get(i);

        }*/

        buttonArrangement.addView(temp);

        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        buttonArrangement.setLayoutParams(param2);
        temp2 = new LinearLayout(this);

        LinearLayout.LayoutParams paramTemp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                0.2f
        );
        temp2.setLayoutParams(paramTemp2);
        temp2.addView(buttonArrangement);

        layout.addView(recyclerView);
        layout.addView(temp2);
        return layout;
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        // In full-screen mode the inserted content is likely to be hidden by the IME. Hence in this
        // sample we simply disable full-screen mode.
        return false;
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        String[] mimeTypes = EditorInfoCompat.getContentMimeTypes(info);

        boolean gifSupported = false;
        for (String mimeType : mimeTypes) {
            if (ClipDescription.compareMimeTypes(mimeType, "image/gif")) {
                gifSupported = true;
                layout.removeAllViews();
                layout.addView(recyclerView);
                layout.addView(temp2);
            }
        }

        if (gifSupported) {
            // the target editor supports GIFs. enable corresponding content
        } else {
            // the target editor does not support GIFs. disable corresponding content
            layout.removeAllViews();
            TextView textView = new TextView(this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(-1, 400));
            textView.setText("  :(  Gif not Supported by this editor");
            layout.addView(textView);
        }
    }
    String tag;


    @Override
    public void onClick(View v) {
     /*   for (int i=0;i<buttonList.size();i++) {
            String currentButton=buttonList.get(i);
            v.setTag(currentButton);
            v.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }*/
        int childCount=temp.getChildCount();
        for (int i=0; i<childCount;i++) {
            View view = temp.getChildAt(i);
            view.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }
       tag = (String) v.getTag();
        Toast.makeText(this, tag,Toast.LENGTH_SHORT ).show();
        Log.e("Clicked button", tag);
        v.setBackgroundColor(Color.parseColor("#003399"));
        imageList1.clear();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/"+tag+"/");
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                    } else {
                        for (int i = 0; i < extensions.length; i++) {
                            if (f.getAbsolutePath().endsWith(extensions[i])) {
                                String s = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Nemojis app/Animated Gifs/" + tag + "/" + f.getName();
                                imageList1.add(s);
                                // Log.e("add to download list : ", f.getName());
                                Log.e("recycler list ", s);

                                recyclerAdapter.notifyDataSetChanged();
                            } /*else {
                                Log.e("bb","bb");
                                recyclerView.setVisibility(View.INVISIBLE);
                            }*/
                        }
                    }
                }
            }

        }
        if (imageList1.size() <= 0) {
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            //   recyclerAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
        }


    }
}
