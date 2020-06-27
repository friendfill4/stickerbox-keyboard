package com.example.vi6.tabbed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vi6 on 02-Mar-17.
 */

public class StaticImageAdapter extends BaseAdapter {
    ArrayList<String> list;
    Context ctx;

    public StaticImageAdapter(ArrayList<String> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            view = inflater.inflate(R.layout.grid_item, null);
            com.example.vi6.tabbed.SquareImageView imageView = (SquareImageView) view.findViewById(R.id.picture);
            if (!list.isEmpty()) {
                GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(imageView);
                Glide.with(ctx).load(list.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.loading).into(imageViewTarget);
            } else {
                return null;
            }
        }
        return view;
    }
}
