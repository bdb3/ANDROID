package com.example.edwin.photoarchive;

import android.app.*;
import android.os.*;
import android.widget.*;
import java.util.*;
import android.graphics.*;
import android.view.*;
import android.content.*;

import com.bumptech.glide.Glide;

public class ImageAdapter extends BaseAdapter  {

    private Context context;
    private ArrayList<String> imgPathList;
    private TabFragment3 tf3;


    public ImageAdapter(Context context, ArrayList<String> imgPathList, TabFragment3 t) {
        this.context = context;
        this.imgPathList = imgPathList;
        this.tf3 = t;
    }

    public int getCount() {
        return this.imgPathList.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            imageView = (ImageView) convertView;
        }

        Glide.with(this.context).load(this.imgPathList.get(position)).into(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tf3.setSelected(imgPathList.get(position));
            }
        });

        return imageView;
    }

}