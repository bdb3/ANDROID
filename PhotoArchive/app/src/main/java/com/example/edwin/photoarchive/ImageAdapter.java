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

        final ImageView imageView;
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
                if(tf3.getUploadBtnClicked()){
                    if(imageView.getColorFilter() == null){
                        tf3.addToImgViewSet(imageView);
                        imageView.setColorFilter(Color.argb(110, 20, 197, 215));

                    }
                    else {
                        imageView.clearColorFilter();
                        tf3.removeFromImgViewSet(imageView);
                    }

                    if(tf3.getImgViewSetSize()<1){
                        tf3.uploadBtnBottom.setEnabled(false);

                    }
                    else{tf3.uploadBtnBottom.setEnabled(true);
                        //enable btn
                    }


                }

            }
        });




        return imageView;
    }

}