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
import com.example.edwin.photoarchive.Activities.GalleryViewAllActivity;
import com.example.edwin.photoarchive.Activities.ImagePreview;

import java.util.ArrayList;

public class ImageAdapterForGallery extends BaseAdapter  {

    private Activity callerActivity;
    private ArrayList<String> imgPathList;
    private GalleryViewAllActivity galleryActivityInstance;


    public ImageAdapterForGallery(Activity callerActivity, ArrayList<String> imgPathList, GalleryViewAllActivity galleryActivityInstance) {
        this.callerActivity = callerActivity;
        this.imgPathList = imgPathList;
        this.galleryActivityInstance = galleryActivityInstance;


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
        galleryActivityInstance.removeFromImageViewSet(imageView);

        if(galleryActivityInstance.imagePathSetContains(this.imgPathList.get(position))){
            imageView.setColorFilter(Color.argb(110, 20, 197, 215));
            galleryActivityInstance.addToImageViewSet(imageView);

        }


        Glide.with(callerActivity).load(this.imgPathList.get(position)).into(imageView);


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView iv = (ImageView) v;

                if(! galleryActivityInstance.getIsSelectEnabled()) {

                    Intent i = new Intent(callerActivity, ImagePreview.class);
                    i.putExtra("imagePath", imgPathList.get(position));
                    callerActivity.startActivity(i);
                }
                else{
                    if(iv.getColorFilter() == null){
                        iv.setColorFilter(Color.argb(110, 20, 197, 215));

                        galleryActivityInstance.addToImagePathSet(imgPathList.get(position));
                        galleryActivityInstance.addToImageViewSet(iv);

                        galleryActivityInstance.setTitle("Selected: " + galleryActivityInstance.getImagePathSetSize());

                    }

                    else {
                        iv.clearColorFilter();

                        galleryActivityInstance.removeFromImagePathSet(imgPathList.get(position));
                        galleryActivityInstance.removeFromImageViewSet(iv);

                        if(galleryActivityInstance.getImagePathSetSize() == 0){
                            galleryActivityInstance.setTitle("Gallery (" + imgPathList.size()+")");

                        }
                        else{
                            galleryActivityInstance.setTitle("Selected: " + galleryActivityInstance.getImagePathSetSize());

                        }

                    }

                }

            }
        });



        return imageView;
    }




}