package com.example.edwin.photoarchive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import java.util.HashSet;
import java.util.Iterator;


public class TabFragment2 extends Fragment {
    private Context context = null;
    private ArrayList<String> imgPathList;
    private ArrayList<String> imgPathList2;
    private HashSet<String> imgPathSet = new HashSet<String>();
    private LinearLayout picContainer;
    private LinearLayout picContainer2;
    private Button uploadBtn;
    private Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        imgPathList = new ArrayList<String>(getImagesPath(getActivity()));
        File inAppImagesPath = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");
        imgPathList2 = new ArrayList<String>();

         uploadBtn = (Button) view.findViewById(R.id.button5);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (String s : imgPathSet) {
                    System.out.println(s);
                }



            }
        });

        Bundle extras = getActivity().getIntent().getExtras();
        if(extras != null) {
           if( extras.containsKey("selectedImagesFromGallery")){

               HashSet<String> galleryPathSet = new HashSet<String>((HashSet) extras.get("selectedImagesFromGallery"));

               for(String s: galleryPathSet){
                   imgPathSet.add(s);

               }


            }
            if( extras.containsKey("selectedImagesFromApp")){

                HashSet<String> appPathSet = new HashSet<String>((HashSet) extras.get("selectedImagesFromApp"));

                for(String s: appPathSet){
                    imgPathSet.add(s);

                }


            }

            if(imgPathSet.size()>0)
            getActivity().setTitle("Selected: " + imgPathSet.size());


        }

///////////////////////////// IN-APP IMAGES  //////////////////////////////////////////////////

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
            final int index = i;

            if(counter2 == 15)
                break;

            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics())));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);


            Glide.with(context).load(imgPathList2.get(i)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(), ImagePreview.class);
                    i.putExtra("imagePath", imgPathList2.get(index));
                    startActivity(i);

                }
            });

            picContainer.addView(imageView);

            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
            marginParams.setMargins(0, 0, 10, 0);

            counter2++;
        }

        Button viewAllInAppImages  = (Button) view.findViewById(R.id.button2);
        viewAllInAppImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(), InAppViewAllActivity.class);
                startActivity(i);

            }
        });

        Button viewAllGalleryImages  = (Button) view.findViewById(R.id.button3);
        viewAllGalleryImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(), GalleryViewAllActivity.class);
                startActivity(i);

            }
        });



        ///////////////////////////// IMAGES FROM GALLERY //////////////////////////////////////////////////

        picContainer2 = (LinearLayout) view.findViewById(R.id.picContainer2);
        int counter = 0;

        for(int i = imgPathList.size()-1; i>=0; i--) {
            final int index = i;

            if(counter == 15)
                break;

            final ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()), (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics())));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(context).load(imgPathList.get(i)).into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(getActivity(), ImagePreview.class);
                    i.putExtra("imagePath", imgPathList.get(index));
                    startActivity(i);

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

        if(tagNames.size()>0 && imgPathSet.size()>0){
            uploadBtn.setEnabled(true);

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
                //clear set of paths

                imgPathSet.clear();
                getActivity().setTitle("Photo Archive");
                hideOption(0);
                uploadBtn.setEnabled(false);

                return true;
            }});


        if(imgPathSet.size()<1) {
            hideOption(0);
        }
        super.onCreateOptionsMenu(menu, inflater);
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

