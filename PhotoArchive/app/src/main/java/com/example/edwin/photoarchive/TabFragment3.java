package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;


import java.io.File;
import java.util.ArrayList;

public class TabFragment3 extends Fragment {
    private Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;
    private String selected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);



        File path = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");
        String[] fileNames=null;

        imageGrid = (GridView) view.findViewById(R.id.gridview);
        imgPathList = new ArrayList<String>();

        if(path.exists()) {
             fileNames = path.list();

            for(String s: fileNames){
                imgPathList.add(path+"/"+s);
            }

            ImageAdapter ia = new ImageAdapter(context, imgPathList, this);
            imageGrid.setAdapter(ia);

        }


        Button button = (Button) view.findViewById(R.id.button3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle("Photo Archive");
                alertDialog.setMessage("You chose "+ selected);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();


            }
        });


        return view;
    }
    public void setSelected(String s){
        selected = s;

    }


}