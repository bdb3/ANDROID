package com.example.edwin.photoarchive;

import android.util.TypedValue;
import android.widget.*;
import java.util.*;
import android.view.*;
import android.content.*;

import com.bumptech.glide.Glide;

public class ImageAdapter extends BaseAdapter  {

    private Context context;
    private ArrayList<String> imgPathList;

    public ImageAdapter(Context context, ArrayList<String> imgPathList) {
        this.context = context;
        this.imgPathList = imgPathList;
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

        final ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(this.context);
            imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, context.getResources().getDisplayMetrics())));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        } else {
            imageView = (ImageView) convertView;
        }

        Glide.with(this.context).load(this.imgPathList.get(position)).into(imageView);

        return imageView;
    }

}