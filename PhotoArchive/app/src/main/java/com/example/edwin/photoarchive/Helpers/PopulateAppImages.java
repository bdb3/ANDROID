package com.example.edwin.photoarchive.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.edwin.photoarchive.Activities.ImagePreview;
import com.example.edwin.photoarchive.Activities.TagsActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Edwin on 3/21/2017.
 */

public class PopulateAppImages {

    private ArrayList<String> imgPathList2;
    private String username;
    private SharedPreferences sharedPreferences;

    public PopulateAppImages(LinearLayout picContainer, android.content.Context context, final Activity activity) {

        sharedPreferences = activity.getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.contains("loggedInUser")) {
            username = sharedPreferences.getString("loggedInUser", null);
        }

        File inAppImagesPath = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images/" + username);
        imgPathList2 = new ArrayList<String>();
        String[] inAppImgList = null;

        if (inAppImagesPath.exists()) {
            inAppImgList = inAppImagesPath.list();

            for (int i = inAppImgList.length - 1; i >= 0; i--) {
                if (!inAppImgList[i].equals(".nomedia")) {
                    imgPathList2.add(inAppImagesPath + "/" + inAppImgList[i]);
                }
            }

        }


        int counter2 = 0;

        for (int i = 0; i < imgPathList2.size(); i++) {
            final int index = i;

            if (counter2 == 15)
                break;

            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, activity.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, activity.getResources().getDisplayMetrics())));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            Glide.with(context).load(imgPathList2.get(i)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent i = new Intent(activity, ImagePreview.class);
                i.putExtra("imagePath", imgPathList2.get(index));
                activity.startActivity(i);
                }
            });

            picContainer.addView(imageView);

            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginParams.setMargins(0, 0, 10, 0);

            counter2++;
        }

    }
}
