package com.example.edwin.photoarchive.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.edwin.photoarchive.Activities.ImagePreview;

import java.util.ArrayList;

public class ImageAdapterDashboard extends BaseAdapter  {

    private Context context;
    private ArrayList<String> imgPathList;

    public ImageAdapterDashboard(Context context, ArrayList<String> imgPathList) {
        this.context = context;
        this.imgPathList = imgPathList;
    }

    public int getCount() {
        return this.imgPathList.size();
    }

    public Object getItem(int position) {
        return this.imgPathList.get(position);
    }

    public void removePath(String path){
        this.imgPathList.remove(path);
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView) v;

                Intent i = new Intent(context, ImagePreview.class);
                i.putExtra("imagePath", imgPathList.get(position));
                context.startActivity(i);
            }
        });

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    });

        return imageView;
    }

}