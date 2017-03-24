package com.example.edwin.photoarchive.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.edwin.photoarchive.Activities.ImagePreview;

import java.util.ArrayList;

/**
 * Created by Edwin on 3/22/2017.
 */
public class PopulateGalleryImages {

    private ArrayList<String> imgPathList;

    public PopulateGalleryImages(LinearLayout picContainer2, android.content.Context context, final Activity activity){

        imgPathList = new ArrayList<String>(getImagesPath(activity));

        int counter = 0;

        for(int i = imgPathList.size()-1; i>=0; i--) {
            final int index = i;

            if(counter == 15)
                break;

            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, activity.getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, activity.getResources().getDisplayMetrics())));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(context).load(imgPathList.get(i)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(activity, ImagePreview.class);
                    i.putExtra("imagePath", imgPathList.get(index));
                    activity.startActivity(i);

                }
            });
            picContainer2.addView(imageView);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginParams.setMargins(0, 0, 10, 0);

            counter++;
        }

    }

    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }
}
