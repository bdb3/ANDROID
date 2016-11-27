package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class TabFragment3 extends Fragment {
    private Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;
    private boolean deleteBtnClicked = false;
    private boolean uploadBtnClicked= false;
    private HashSet<ImageView> imgViewSet = new HashSet<ImageView>();
    public static Button uploadBtnBottom = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            context = this.getContext();
            View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

            File path = new File(Environment.getExternalStorageDirectory(), "DCIM/Camera");
            File path2 = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");


            String[] fileNames = null;
            String[] fileNames2 = null;

            imageGrid = (GridView) view.findViewById(R.id.gridview);
            imgPathList = new ArrayList<String>();

            if (path2.exists()) {
                fileNames2 = path2.list();


             for (int i = fileNames2.length-1; i>=0; i--) {
                  if(!fileNames2[i].equals(".nomedia")){
                    imgPathList.add(path2 + "/" + fileNames2[i]);
                 }
             }

             }

            if (path.exists()) {
                fileNames = path.list();

                for (String s : fileNames) {
                    if(!s.equals(".nomedia")){
                        imgPathList.add(path + "/" + s);
                    }
                }


            }
            imageGrid.setAdapter(new ImageAdapter(context, imgPathList, this));

             final SurfaceView sv = (SurfaceView) view.findViewById(R.id.surfaceView);
             final Button btn1 = (Button) view.findViewById(R.id.button1);
             uploadBtnBottom = (Button) view.findViewById(R.id.button2);

             btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                 public void onClick(View v) {
                    uploadBtnClicked = false;
                    uploadBtnBottom.setEnabled(false);
                    sv.setVisibility(View.INVISIBLE);
                    btn1.setVisibility(View.INVISIBLE);
                    uploadBtnBottom.setVisibility(View.INVISIBLE);

                    for(ImageView iv: imgViewSet){
                        iv.clearColorFilter();
                    }
                    imgViewSet.clear();

                }

                 });




            Button button = (Button) view.findViewById(R.id.button3);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uploadBtnClicked = true;
                    sv.setVisibility(View.VISIBLE);
                    btn1.setVisibility(View.VISIBLE);
                    uploadBtnBottom.setVisibility(View.VISIBLE);

                    /*
                    AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Photo Archive");
                    alertDialog.setMessage("You chose " + selected);
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    */

                }
            });


            return view;

    }
    public boolean getUploadBtnClicked(){
        return uploadBtnClicked;

    }

    public void addToImgViewSet(ImageView iv){
        imgViewSet.add(iv);

    }
    public void removeFromImgViewSet(ImageView iv){
        imgViewSet.remove(iv);

    }

    public long getImgViewSetSize(){
        return imgViewSet.size();
    }


}