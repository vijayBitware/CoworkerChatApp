package com.bitware.coworker.adapter;

/**
 * Created by user on 21-07-2017.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bitware.coworker.R;

import java.util.ArrayList;
import java.util.List;


public class ImageAdapter extends BaseAdapter {


    private Context mContext;

    List<Integer> colors=new ArrayList<>();


    // Constructor
    public ImageAdapter(Context c, List<Integer> colors){
        mContext = c;
        this.colors=colors;
    }

    @Override
    public int getCount() {
        return colors.size();
    }

    @Override
    public Object getItem(int position) {
        return colors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        grid = inflater.inflate(R.layout.grid_single, null);
            ImageView imageView = (ImageView)grid.findViewById(R.id.imageView) ;
            Log.d("getView: ","colorvalues:"+colors.get(position));
            imageView.setBackgroundColor(colors.get(position));

        return grid;
    }

    }
