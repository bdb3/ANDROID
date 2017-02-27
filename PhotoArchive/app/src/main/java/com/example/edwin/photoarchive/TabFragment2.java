package com.example.edwin.photoarchive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class TabFragment2 extends Fragment {
    private Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;
    private ArrayList<String> imgPathList2;
    private HashSet<ImageView> imgViewSet = new HashSet<ImageView>();
    private Menu menu;
    private LinearLayout picContainer;
    private LinearLayout picContainer2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        imgPathList = new ArrayList<String>(getImagesPath(getActivity()));
        File inAppImagesPath = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");
        imgPathList2 = new ArrayList<String>();

        String[] inAppImgList = null;

        if (inAppImagesPath.exists()) {
            inAppImgList = inAppImagesPath.list();

            for (int i = inAppImgList.length-1; i>=0; i--) {
                if(!inAppImgList[i].equals(".nomedia")){
                    imgPathList2.add(inAppImagesPath + "/" + inAppImgList[i]);
                }
            }

        }

         picContainer = (LinearLayout) view.findViewById(R.id.picContainer);

        int counter2 = 0;

        for(int i = 0; i<imgPathList2.size(); i++) {

            if(counter2 == 15)
                break;

            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics())));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(context).load(imgPathList2.get(i)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(imageView.getColorFilter() == null){
                        imageView.setColorFilter(Color.argb(110, 20, 197, 215));
                        imgViewSet.add(imageView);
                        getActivity().setTitle("Selected: " + imgViewSet.size());
                        showOption(0);

                    }
                    else {
                        imageView.clearColorFilter();
                        imgViewSet.remove(imageView);

                        if(imgViewSet.size() == 0){
                            getActivity().setTitle("Photo Archive");
                            hideOption(0);

                        }
                        else{
                            getActivity().setTitle("Selected: " + imgViewSet.size());

                        }

                    }

                }
            });

            picContainer.addView(imageView);

            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginParams.setMargins(0, 0, 10, 0);

            counter2++;
        }



        ///////////////////////////// IMAGES FROM GALLERY //////////////////////////////////////////////////

        picContainer2 = (LinearLayout) view.findViewById(R.id.picContainer2);
        int counter = 0;

        for(int i = imgPathList.size()-1; i>=0; i--) {

            if(counter == 15)
                break;

            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics())));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(context).load(imgPathList.get(i)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        if(imageView.getColorFilter() == null){
                            imageView.setColorFilter(Color.argb(110, 20, 197, 215));
                            imgViewSet.add(imageView);
                            getActivity().setTitle("Selected: " + imgViewSet.size());
                            showOption(0);

                        }
                        else {
                            imageView.clearColorFilter();
                            imgViewSet.remove(imageView);

                            if(imgViewSet.size() == 0){
                                getActivity().setTitle("Photo Archive");
                                hideOption(0);

                            }
                            else{
                                getActivity().setTitle("Selected: " + imgViewSet.size());

                            }

                        }

                }
            });
            picContainer2.addView(imageView);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginParams.setMargins(0, 0, 10, 0);

            counter++;
        }



        //////////////////////////////////// ADDING TAGS /////////////////////////////////////////////////////////

        final LinearLayout tagsContainer = (LinearLayout) view.findViewById(R.id.tagsContainer);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        ArrayList<String> tagNames =  new ArrayList<String>();

        if(sharedPreferences.contains("listOfTags")) {
            String mapString = sharedPreferences.getString("listOfTags", null);

            try {
                JSONObject jsonObject2 = new JSONObject(mapString);
                Iterator<String> keysItr = jsonObject2.keys();

                while(keysItr.hasNext()) {

                    tagNames.add(keysItr.next());
                }


            }catch(Exception e){
                e.printStackTrace();

            };

        }

        for(String s: tagNames) {
            final Button tag1 = new Button(context);
            tag1.setText(s);
            tagsContainer.addView(tag1);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) tag1.getLayoutParams();
            marginParams.setMargins(0, 0, 10, 0);

            tag1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i= new Intent(getActivity(), ActivityEditDeleteTags.class);
                    i.putExtra("tag_name", tag1.getText());
                    startActivity(i);

                }
            });

        }

        final Button tagsBtn = (Button) view.findViewById(R.id.button4);

        tagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(), TagsActivity.class);
                startActivity(i);

            }
        });


            return view;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        menu.add(Menu.NONE, 0, Menu.NONE, "Cancel")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        MenuItem item = menu.findItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deselectAllImages();
                return true;
            }});


        hideOption(0);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void deselectAllImages(){
        for(int i=0; i<picContainer.getChildCount(); i++ ){
            ((ImageView)picContainer.getChildAt(i)).clearColorFilter();

        }
        for(int i=0; i<picContainer2.getChildCount(); i++ ){
            ((ImageView)picContainer2.getChildAt(i)).clearColorFilter();

        }
        imgViewSet.clear();
        getActivity().setTitle("Photo Archive");
        hideOption(0);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
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