package com.example.vi6.tabbed;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

/**
 * Created by vi6 on 27-Feb-17.
 */

public class KeyboardFragment extends Fragment{

    String imageUrl;
    FragmentActivity  ctx;

/*
    @Override
    public void onAttach(Context context) {
        ctx=(FragmentActivity)context;
        super.onAttach(ctx);
    }*/

  /*  @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUrl = getArguments().getString("imageUrl");
    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_2, container, false);

        com.example.vi6.tabbed.SquareImageView imageView = (SquareImageView) view.findViewById(R.id.keyboardGif);
        Log.e("IMAGEURL : ",imageUrl);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
        Glide.with(getActivity()).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.loading).into(imageViewTarget);
        Bundle args= new Bundle();
        args.putString("imageUrl",imageUrl);
        setArguments(args);
        return view;
    }
    public  KeyboardFragment (String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
