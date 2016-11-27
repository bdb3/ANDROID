package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TabFragment1 extends Fragment {
    private Context context = null;

    private ArrayList<String> pathSet = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context= this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);




        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(TabFragment2.MyPREFERENCES, Context.MODE_PRIVATE);

        if(sharedpreferences.contains("pathList")) {
            pathSet= new ArrayList<>(sharedpreferences.getStringSet("pathList", null)) ;
            Collections.sort(pathSet, Collections.<String>reverseOrder());


        }



        final LinearLayout queueContainer = (LinearLayout) view.findViewById(R.id.queueContainer);

        if(pathSet !=null) {

            for (String s: pathSet) {
                ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Glide.with(context).load(s).into(imageView);
                queueContainer.addView(imageView);

                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                marginParams.setMargins(0, 0, 10, 0);
            }

        }

        return view;
    }
}