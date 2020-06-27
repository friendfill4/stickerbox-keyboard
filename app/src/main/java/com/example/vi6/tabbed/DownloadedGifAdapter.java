package com.example.vi6.tabbed;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by vi6 on 24-Feb-17.
 */

public class DownloadedGifAdapter  extends BaseAdapter{
    ArrayList<String> list;
    Context ctx;

    public DownloadedGifAdapter(ArrayList<String> list, Context ctx) {
        this.list = list;
        this.ctx = ctx;
    }
    @Override
    public int getCount() {
        return 0;
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
        return null;
    }
}
