package com.example.edwin.photoarchive.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.edwin.photoarchive.Activities.ImagePreview;
import com.example.edwin.photoarchive.Activities.InAppViewAllActivity;

import java.util.ArrayList;

public class ImageAdapterForAppImages extends BaseAdapter  {

    private Activity callerActivity;
    private ArrayList<String> imgPathList;
    private InAppViewAllActivity inAppViewAllActivityInstance;


    public ImageAdapterForAppImages(Activity callerActivity, ArrayList<String> imgPathList, InAppViewAllActivity inAppViewAllActivityInstance) {
        this.callerActivity = callerActivity;
        this.imgPathList = imgPathList;
        this.inAppViewAllActivityInstance = inAppViewAllActivityInstance;


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

    public View getView( final int position, View convertView, ViewGroup parent) {


        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(callerActivity);
            imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, callerActivity.getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, callerActivity.getResources().getDisplayMetrics())));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);



        } else {
            imageView = (ImageView) convertView;


       }
        imageView.clearColorFilter();
        inAppViewAllActivityInstance.removeFromImageViewSet(imageView);

        if(inAppViewAllActivityInstance.imagePathSetContains(this.imgPathList.get(position))){
            imageView.setColorFilter(Color.argb(110, 20, 197, 215));
            inAppViewAllActivityInstance.addToImageViewSet(imageView);

        }


        Glide.with(callerActivity).load(this.imgPathList.get(position)).into(imageView);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView) v;

                if(! inAppViewAllActivityInstance.getIsSelectEnabled()) {

                    Intent i = new Intent(callerActivity, ImagePreview.class);
                    i.putExtra("imagePath", imgPathList.get(position));
                    callerActivity.startActivity(i);
                }
                else{
                    if(iv.getColorFilter() == null){
                        iv.setColorFilter(Color.argb(110, 20, 197, 215));

                        inAppViewAllActivityInstance.addToImagePathSet(imgPathList.get(position));
                        inAppViewAllActivityInstance.addToImageViewSet(iv);

                        inAppViewAllActivityInstance.setTitle("Selected: " + inAppViewAllActivityInstance.getImagePathSetSize());

                    }

                    else {
                        iv.clearColorFilter();

                        inAppViewAllActivityInstance.removeFromImagePathSet(imgPathList.get(position));
                        inAppViewAllActivityInstance.removeFromImageViewSet(iv);

                        if(inAppViewAllActivityInstance.getImagePathSetSize() == 0){
                            inAppViewAllActivityInstance.setTitle("App Images (" + imgPathList.size()+")");

                        }
                        else{
                            inAppViewAllActivityInstance.setTitle("Selected: " + inAppViewAllActivityInstance.getImagePathSetSize());

                        }

                    }

                }

            }
        });


        return imageView;
    }


}