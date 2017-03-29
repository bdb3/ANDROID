package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.edwin.photoarchive.Activities.ActivityEditDeleteTags;
import com.example.edwin.photoarchive.Activities.GalleryViewAllActivity;
import com.example.edwin.photoarchive.Activities.InAppViewAllActivity;
import com.example.edwin.photoarchive.Activities.TagsActivity;
import com.example.edwin.photoarchive.AzureClasses.Attribute;
import com.example.edwin.photoarchive.AzureClasses.TaggedImageObject;
import com.example.edwin.photoarchive.Helpers.ExtractLatLong;
import com.example.edwin.photoarchive.Helpers.PopulateAppImages;
import com.example.edwin.photoarchive.Helpers.PopulateGalleryImages;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;


public class TabFragment2 extends Fragment {
    private Context context = null;
    private HashSet<String> imgPathSet = new LinkedHashSet<String>();
    private LinearLayout picContainer;
    private LinearLayout picContainer2;
    private Button uploadBtn;
    private Menu menu;
    private Map<String, Map<String, Map<String, String>>> imagesTagsMap = new LinkedHashMap<String, Map<String, Map<String, String>>>();
    private SharedPreferences sharedPreferences;
    private ArrayList<TaggedImageObject> taggedImagesList = new ArrayList<TaggedImageObject>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        uploadBtn = (Button) view.findViewById(R.id.button5);
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

        ///////////////////////////////////// SEND TO QUEUE CODE/////////////////////////////////////

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get tags from shared preferences

                    uploadBtn.setEnabled(false);

                    String mapString  = sharedPreferences.getString("listOfTags", null);

                    Map<String, Map<String, String>> outputMap = new LinkedHashMap<String, Map<String, String>>();
                    try {
                        JSONObject jsonObject2 = new JSONObject(mapString);
                        Iterator<String> keysItr = jsonObject2.keys();

                        while(keysItr.hasNext()) {
                            String key = keysItr.next();

                            Map<String, String> valueMap = new LinkedHashMap<String, String>();
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

                       // imagesTagsMap.put(s, outputMap);

                        ExtractLatLong ell = new ExtractLatLong(s);
                        TaggedImageObject tagImgObj = new TaggedImageObject(s, ell.getLat(),ell.getLon(), "user",outputMap);
                        taggedImagesList.add(tagImgObj);

                    }


                // put imagesTagsMap in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();


                Gson gson = new Gson();

                if(!sharedPreferences.contains("listOfImagesWithTags")) {

                    String taggedImageslistAsString = gson.toJson(taggedImagesList);
                    editor.putString("listOfImagesWithTags", taggedImageslistAsString);
                    editor.commit();
                    clearTagsAndSelectedImages();
                    Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();

                }



                else{
                    String savedArraylist  = sharedPreferences.getString("listOfImagesWithTags", null);
                    Type type = new TypeToken<ArrayList<TaggedImageObject>>(){}.getType();
                    ArrayList<TaggedImageObject> taggedImageObjectsList = gson.fromJson(savedArraylist, type);



                    for(String s: imgPathSet){
                        ExtractLatLong ell = new ExtractLatLong(s);
                        TaggedImageObject tagImgObj = new TaggedImageObject(s, ell.getLat(),ell.getLon(), "user",outputMap);
                        taggedImageObjectsList.add(tagImgObj);

                    }
                    String taggedImageslistAsString = gson.toJson(taggedImageObjectsList);
                    editor.remove("listOfImagesWithTags");
                    editor.apply();
                    editor.putString("listOfImagesWithTags", taggedImageslistAsString);
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

               HashSet<String> galleryPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImagesFromGallery"));

               for(String s: galleryPathSet){
                   imgPathSet.add(s);

               }

            }
            if( extras.containsKey("selectedImagesFromApp")){

                HashSet<String> appPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImagesFromApp"));

                for(String s: appPathSet){
                    imgPathSet.add(s);

                }

            }

            if(imgPathSet.size()>0)
                getActivity().setTitle("Selected: " + imgPathSet.size());
                getActivity().getIntent().putExtra("totalSelected", imgPathSet.size());

        }

        // POPULATE RECENT IN-APP IMAGES GRIDVIEW

         picContainer = (LinearLayout) view.findViewById(R.id.picContainer);
        new PopulateAppImages(picContainer, context, getActivity());


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

        //POPULATE IMAGES FROM GALLERY GRIDVIEW

        picContainer2 = (LinearLayout) view.findViewById(R.id.picContainer2);
        new PopulateGalleryImages(picContainer2, context, getActivity());

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

                Bundle azureDB = getActivity().getIntent().getExtras();
                //grab ContextsAndAttributes from extras


               try {
                   Intent i = new Intent(getActivity(), TagsActivity.class);
                   HashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>> caa = (HashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>>) azureDB.get("azure");

                   i.putExtra("selectedImages", imgPathSet);
                   i.putExtra("azure", caa);

                   startActivity(i);
               }catch(NullPointerException e){

                   Toast.makeText(context, "Please wait until contexts finish syncing", Toast.LENGTH_LONG).show();
               }







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
        getActivity().getSupportFragmentManager().popBackStack();

    }


}

