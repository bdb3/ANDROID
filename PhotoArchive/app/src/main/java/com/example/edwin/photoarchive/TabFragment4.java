package com.example.edwin.photoarchive;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;


import com.example.edwin.photoarchive.Adapters.ImageAdapterHistory;

import java.io.File;
import java.util.ArrayList;

public class TabFragment4 extends Fragment {
    private Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context= this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);

        imageGrid = (GridView) view.findViewById(R.id.gridView4);
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

        imageGrid.setAdapter(new ImageAdapterHistory(context, imgPathList));


        return view;
    }


}