package com.example.vi6.tabbed;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vi6 on 27-Feb-17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>{
    ArrayList<String> imageList= new ArrayList<>();
    Context context;

    public RecyclerAdapter(Context cctx, ArrayList<String> list) {
        this.imageList = list;
        this.context = cctx;
    }

    String[] extensions = { "gif" };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.e("NUMBER OF IMAGES", ""+imageList.size());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(holder.img_android);
        Glide.with(context).load(imageList.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.loading).into(imageViewTarget);

    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img_android;
        public ViewHolder(View view) {
            super(view);
            img_android = (ImageView) view.findViewById(R.id.img_android);
        }
    }

}
