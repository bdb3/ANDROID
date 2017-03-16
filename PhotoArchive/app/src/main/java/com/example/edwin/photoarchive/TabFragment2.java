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
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;


public class TabFragment2 extends Fragment {
    private Context context = null;
    private ArrayList<String> imgPathList;
    private ArrayList<String> imgPathList2;
    private HashSet<String> imgPathSet = new HashSet<String>();
    private LinearLayout picContainer;
    private LinearLayout picContainer2;
    private Button uploadBtn;
    private Menu menu;
    private Map<String, Map<String, Map<String, String>>> imagesTagsMap = new HashMap<String, Map<String, Map<String, String>>>();
    private SharedPreferences sharedPreferences;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        imgPathList = new ArrayList<String>(getImagesPath(getActivity()));
        File inAppImagesPath = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");
        imgPathList2 = new ArrayList<String>();

        uploadBtn = (Button) view.findViewById(R.id.button5);
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

        ///////////////////////////////////// SEND TO QUEUE CODE/////////////////////////////////////

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get tags from shared preferences

                    uploadBtn.setEnabled(false);

                    String mapString  = sharedPreferences.getString("listOfTags", null);

                    Map<String, Map<String, String>> outputMap = new HashMap<String, Map<String, String>>();
                    try {
                        JSONObject jsonObject2 = new JSONObject(mapString);
                        Iterator<String> keysItr = jsonObject2.keys();

                        while(keysItr.hasNext()) {
                            String key = keysItr.next();

                            Map<String, String> valueMap = new HashMap<String, String>();
                            Iterator<String> keysItr2 = ((JSONObject)jsonObject2.get(key)).keys();

                            while(keysItr2.hasNext()) {
                                String key2 = keysItr2.next();
                                String value = (String)((JSONObject)jsonObject2.get(key)).get(key2);

                                valueMap.put(key2, value);
                            }

                            outputMap.put(key, valueMap);
                        }


                    }catch(Exception e){
                        e.printStackTrace();

                    };

                    for(String s: imgPathSet){
                        imagesTagsMap.put(s, outputMap);

                    }




                // put imagesTagsMap in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();


                if(!sharedPreferences.contains("listOfImagesWithTags")) {


                    JSONObject imagesTagsMapAsJSON = new JSONObject(imagesTagsMap);
                    String imgTagsMapAsJSONString   = imagesTagsMapAsJSON.toString();

                    editor.putString("listOfImagesWithTags", imgTagsMapAsJSONString);
                    editor.commit();

                    // reset data
                    clearTagsAndSelectedImages();
                    //

                    Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();


                }




                else{
                    String mapString2  = sharedPreferences.getString("listOfImagesWithTags", null);

                    Map<String, Map<String, Map<String, String>>> outputMap2 = new HashMap<String, Map<String, Map<String, String>>>();
                    try {
                        JSONObject jsonObject2 = new JSONObject(mapString2);
                        Iterator<String> keysItr = jsonObject2.keys();

                        while(keysItr.hasNext()) {
                            String key = keysItr.next();

                            Map<String, Map<String, String>> valueMap = new HashMap<String, Map<String, String>>();
                            Iterator<String> keysItr2 = ((JSONObject)jsonObject2.get(key)).keys();

                            while(keysItr2.hasNext()) {
                                String key2 = keysItr2.next();

                                Map<String, String> innerMap = new HashMap<String, String>();
                                Iterator<String> keysItr3 = ((JSONObject)((JSONObject)jsonObject2.get(key)).get(key2)).keys();

                                while(keysItr3.hasNext()){
                                    String key3 = keysItr3.next();
                                    String value = (String)((JSONObject)((JSONObject)jsonObject2.get(key)).get(key2)).get(key3);
                                    innerMap.put(key3, value);

                                }

                                valueMap.put(key2, innerMap);
                            }

                            outputMap2.put(key, valueMap);
                        }


                    }catch(Exception e){
                        e.printStackTrace();

                    };

                    //add curent map to sharedpreferences map

                    for(String s: imgPathSet){
                        outputMap2.put(s, outputMap);

                    }



                    JSONObject finalJsonObject = new JSONObject(outputMap2);

                    editor.remove("listOfImagesWithTags");
                    editor.apply();
                    editor.putString("listOfImagesWithTags", finalJsonObject.toString() );
                    editor.apply();
                    clearTagsAndSelectedImages();
                    Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();


                }




            }
        });

        /////////////////////////////////////END OF SEND TO QUEUE CODE//////////////////////////////////

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
                getActivity().getIntent().putExtra("totalSelected", imgPathSet.size());

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
                i.putExtra("selectedImages", imgPathSet);
                startActivity(i);

            }
        });

        Button viewAllGalleryImages  = (Button) view.findViewById(R.id.button3);
        viewAllGalleryImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(), GalleryViewAllActivity.class);
                i.putExtra("selectedImages", imgPathSet);
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
                    i.putExtra("selectedImages", imgPathSet);

                    startActivity(i);

                }
            });

        }

        final Button tagsBtn = (Button) view.findViewById(R.id.button4);

        tagsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getActivity(), TagsActivity.class);
                // send selected images to tags activity so they are not removed

                i.putExtra("selectedImages", imgPathSet);

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
                getActivity().getIntent().putExtra("totalSelected", 0);
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

    private void clearTagsAndSelectedImages(){
        imgPathSet.clear();
        getActivity().setTitle("Photo Archive");

        getActivity().getIntent().replaceExtras(new Bundle());
        getActivity().getIntent().setAction("");
        getActivity().getIntent().setData(null);
        getActivity().getIntent().setFlags(0);

        //getActivity().getIntent().putExtra("totalSelected", 0);
        hideOption(0);
        imagesTagsMap.clear();

        //remove tags
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("listOfTags");
        editor.apply();


        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        getFragmentManager().beginTransaction().detach(getFragmentManager().getFragments().get(1)).attach(getFragmentManager().getFragments().get(1)).commitAllowingStateLoss();
        getFragmentManager().beginTransaction().detach(getFragmentManager().getFragments().get(0)).attach(getFragmentManager().getFragments().get(0)).commitAllowingStateLoss();
        viewPager.setCurrentItem(0);

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

