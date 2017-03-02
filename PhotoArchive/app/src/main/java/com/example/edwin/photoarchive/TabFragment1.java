package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TabFragment1 extends Fragment {
    private Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;

    private ArrayList<String> pathSet = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context= this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        imageGrid = (GridView) view.findViewById(R.id.gridview);
        imgPathList = new ArrayList<String>();

        File path2 = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");
        String[] fileNames2 = null;

        if (path2.exists()) {
            fileNames2 = path2.list();


            for (int i = fileNames2.length-1; i>=0; i--) {
                if(!fileNames2[i].equals(".nomedia")){
                    imgPathList.add(path2 + "/" + fileNames2[i]);
                }
            }

        }

        imageGrid.setAdapter(new ImageAdapter(context, imgPathList));

        return view;
    }
}