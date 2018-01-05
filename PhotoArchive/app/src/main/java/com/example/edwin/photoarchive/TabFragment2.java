package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.List;
import java.util.Map;

public class TabFragment2 extends Fragment {
    private Context context = null;
    private HashSet<String> imgPathSet = new LinkedHashSet<String>();
    private Menu menu;
    private Map<String, Map<String, Map<String, String>>> imagesTagsMap = new LinkedHashMap<String, Map<String, Map<String, String>>>();
    private SharedPreferences sharedPreferences;
    private Spinner catSpinner;
    private ArrayList<TaggedImageObject> taggedImagesList = new ArrayList<TaggedImageObject>();
    private String username;
    private LinearLayout picContainer;
    private LinearLayout picContainer2;
    private LinearLayout questionViews;
    private Button uploadBtn;
    private List<View> questionInstances = new ArrayList<View>();

    // TODO https://mobikul.com/how-to-get-data-from-dynamically-created-views-android/
    // Make it when the data is updated, the tag information is saved.
    // TODO Make categories persist


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        /** BEG DECLARE VIEW OBJECTS */

        catSpinner = (Spinner) view.findViewById(R.id.category_spinner_tagging);
        questionViews = (LinearLayout) view.findViewById(R.id.questions_layout);

        /** END DECLARE VIEW OBJECTS */


        /** BEG GET SHAREDPREFERENCES DATA */

        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        String[] contextsArray = null;
        Gson gson = new Gson();
        try{
            contextsArray = gson.fromJson(sharedPreferences.getString("contexts", null), String[].class);
        } catch (Exception e) { Log.d("TabFragment2","CRITICAL ERROR! JSON PARSE EXCEPTION"); }

        /** END GET SHAREDPREFERENCES DATA */


        /** BEG VIEW CONTENT CODE */

        if(contextsArray != null) {
            ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, contextsArray);
            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            catSpinner.setAdapter(catAdapter);
        }

        /** END VIEW CONTENT CODE */


        /** START TEST CODE */

            // TODO (FRAGMENT2)
                /* 1. get context_attributes
                 * 2. get their sortnumber (Context_Attribute)
                 * 3. get their fieldtype, required, possibleValues (Attribute) */

            /** JSON HERE FOR ATTRIBUTES */

        for( int itemNumber = 0 ; itemNumber < 3 /* TODO ITEMNUMBER LOGIC */ ; itemNumber++ ){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // TODO Get attribute as Json
                    /* Json will contain option type which will use if/elseif to separate */

            TextView questionName = new TextView(getContext());
            questionName.setText("ItemNumber" + itemNumber); // TEMP
            questionName.setTextSize(18);
            questionName.setPadding(0,15,0,5);
            questionViews.addView(questionName,params);

                    /* RADIO BUTTON */

            if( itemNumber == 0 ) { // TEMP
                RadioGroup radioGroup = new RadioGroup(getContext());
                questionInstances.add(radioGroup);
                for( int radioNumber = 0 ; radioNumber < 2 /* TODO RADIONUMBER LOGIC */; radioNumber++ ) {
                    RadioButton rb = new RadioButton(getContext());
                    radioGroup.addView(rb,params);
                    if (radioNumber == 0)
                        rb.setChecked(true); // First Option Selected, Can Be Changed
                    rb.setTag("Option" + itemNumber + "-" + radioNumber);
                    rb.setText("Option" + itemNumber + "-" + radioNumber);
                }
                questionViews.addView(radioGroup,params);
                continue;
            }

                    /* CHECK BOX */

            if( itemNumber == 1 ) { // TEMP
                CheckBox chkBox = new CheckBox(getContext());
                questionInstances.add(chkBox);
                chkBox.setTag("Check"+itemNumber);
                chkBox.setText("Check"+itemNumber);
                questionViews.addView(chkBox,params);
                continue;
            }

                    /* TEXT BOX */

            if( itemNumber == 2) { // TEMP
                EditText txtBox = new EditText(getContext());
                questionInstances.add(txtBox);
                txtBox.setGravity( Gravity.LEFT | Gravity.TOP );
                txtBox.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                txtBox.setLines(6);
                txtBox.setMaxLines(8);
                txtBox.setMinLines(4);
                txtBox.setVerticalScrollBarEnabled(true);
                txtBox.setSingleLine(false);
                questionViews.addView(txtBox,params);
                continue;
            }
        }

        /** END TEST CODE */


        /** BEG EVENTLISTENERS */

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // TODO (FRAGMENT2)
                /* 1. get context_attributes
                 * 2. get their sortnumber (Context_Attribute)
                 * 3. get their fieldtype, required, possibleValues (Attribute) */

                /** JSON HERE FOR ATTRIBUTES */

                for( int itemNumber = 0 ; itemNumber < 1 /* TODO ITEMNUMBER LOGIC */ ; itemNumber++ ){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    // TODO Get attribute as Json
                    /* Json will contain option type which will use if/elseif to separate */

                    TextView questionName = new TextView(getContext());
                    questionName.setText("ItemNumber" + itemNumber); // TEMP
                    questionName.setTextSize(18);
                    questionName.setPadding(0,15,0,5);
                    questionViews.addView(questionName,params);

                    /* RADIO BUTTON */

                    if( itemNumber == 0 ) { // TEMP
                        RadioGroup radioGroup = new RadioGroup(getContext());
                        questionInstances.add(radioGroup);
                        for( int radioNumber = 0 ; radioNumber < 2 /* TODO RADIONUMBER LOGIC */; radioNumber++ ) {
                            RadioButton rb = new RadioButton(getContext());
                            radioGroup.addView(rb,params);
                            if (radioNumber == 0)
                                rb.setChecked(true); // First Option Selected, Can Be Changed
                            rb.setTag("Option" + itemNumber + "-" + radioNumber);
                            rb.setText("Option" + itemNumber + "-" + radioNumber);
                        }
                        questionViews.addView(radioGroup,params);
                        continue;
                    }

                    /* CHECK BOX */

                    if( itemNumber == 1 ) { // TEMP
                        CheckBox chkBox = new CheckBox(getContext());
                        questionInstances.add(chkBox);
                        chkBox.setTag("Check"+itemNumber);
                        chkBox.setText("Check"+itemNumber);
                        questionViews.addView(chkBox,params);
                        continue;
                    }

                    /* TEXT BOX */

                    if( itemNumber == 2) { // TEMP
                        EditText txtBox = new EditText(getContext());
                        questionInstances.add(txtBox);
                        txtBox.setGravity( Gravity.LEFT | Gravity.TOP );
                        txtBox.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                        txtBox.setLines(6);
                        txtBox.setMaxLines(8);
                        txtBox.setMinLines(4);
                        txtBox.setVerticalScrollBarEnabled(true);
                        txtBox.setSingleLine(false);
                        questionViews.addView(txtBox,params);
                        continue;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do Nothing
            }
        });

        /** END EVENT LISTENERS */
        /*
        setHasOptionsMenu(true);



        uploadBtn = (Button) view.findViewById(R.id.button5);
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

        if (sharedPreferences.contains("loggedInUser")) {
            username = sharedPreferences.getString("loggedInUser", null);

        }

        ///////////////////////////////////// SEND TO QUEUE CODE/////////////////////////////////////

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get tags from shared preferences

                uploadBtn.setEnabled(false);

                String mapString = sharedPreferences.getString("listOfTags", null);

                Map<String, Map<String, String>> outputMap = new LinkedHashMap<String, Map<String, String>>();
                try {
                    JSONObject jsonObject2 = new JSONObject(mapString);
                    Iterator<String> keysItr = jsonObject2.keys();

                    while (keysItr.hasNext()) {
                        String key = keysItr.next();

                        Map<String, String> valueMap = new LinkedHashMap<String, String>();
                        Iterator<String> keysItr2 = ((JSONObject) jsonObject2.get(key)).keys();

                        while (keysItr2.hasNext()) {
                            String key2 = keysItr2.next();
                            String value = (String) ((JSONObject) jsonObject2.get(key)).get(key2);

                            valueMap.put(key2, value);
                        }

                        outputMap.put(key, valueMap);
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
                ;

                for (String s : imgPathSet) {

                    // imagesTagsMap.put(s, outputMap);

                    ExtractLatLong ell = new ExtractLatLong(s);
                    TaggedImageObject tagImgObj = new TaggedImageObject(s, ell.getLat(), ell.getLon(), username, outputMap);
                    taggedImagesList.add(tagImgObj);

                }


                // put imagesTagsMap in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();


                Gson gson = new Gson();

                if (!sharedPreferences.contains("listOfImagesWithTags")) {

                    String taggedImageslistAsString = gson.toJson(taggedImagesList);
                    editor.putString("listOfImagesWithTags", taggedImageslistAsString);
                    editor.apply();
                    clearTagsAndSelectedImages();
                    Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();

                    //Start Upload

                } else {
                    String savedArraylist = sharedPreferences.getString("listOfImagesWithTags", null);
                    Type type = new TypeToken<ArrayList<TaggedImageObject>>() {
                    }.getType();
                    ArrayList<TaggedImageObject> taggedImageObjectsList = gson.fromJson(savedArraylist, type);


                    for (String s : imgPathSet) {
                        ExtractLatLong ell = new ExtractLatLong(s);
                        TaggedImageObject tagImgObj = new TaggedImageObject(s, ell.getLat(), ell.getLon(), username, outputMap);
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
        if (extras != null) {
            if (extras.containsKey("selectedImagesFromGallery")) {

                HashSet<String> galleryPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImagesFromGallery"));

                for (String s : galleryPathSet) {
                    imgPathSet.add(s);

                }

            }
            if (extras.containsKey("selectedImagesFromApp")) {

                HashSet<String> appPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImagesFromApp"));

                for (String s : appPathSet) {
                    imgPathSet.add(s);

                }

            }

            if (imgPathSet.size() > 0)
                getActivity().setTitle("Selected: " + imgPathSet.size());
            getActivity().getIntent().putExtra("totalSelected", imgPathSet.size());

        }

        // POPULATE RECENT IN-APP IMAGES GRIDVIEW

        picContainer = (LinearLayout) view.findViewById(R.id.picContainer);
        new PopulateAppImages(picContainer, context, getActivity());


        Button viewAllInAppImages = (Button) view.findViewById(R.id.button2);
        viewAllInAppImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), InAppViewAllActivity.class);
                i.putExtra("selectedImages", imgPathSet);
                startActivity(i);

            }
        });

        Button viewAllGalleryImages = (Button) view.findViewById(R.id.button3);
        viewAllGalleryImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GalleryViewAllActivity.class);
                i.putExtra("selectedImages", imgPathSet);
                startActivity(i);

            }
        });

        //POPULATE IMAGES FROM GALLERY GRIDVIEW

        picContainer2 = (LinearLayout) view.findViewById(R.id.picContainer2);
        new PopulateGalleryImages(picContainer2, context, getActivity());

        //////////////////////////////////// ADDING TAGS /////////////////////////////////////////////////////////

        final LinearLayout tagsContainer = (LinearLayout) view.findViewById(R.id.tagsContainer);

        ArrayList<String> tagNames = new ArrayList<String>();

        if (sharedPreferences.contains("listOfTags")) {
            String mapString = sharedPreferences.getString("listOfTags", null);

            try {
                JSONObject jsonObject2 = new JSONObject(mapString);
                Iterator<String> keysItr = jsonObject2.keys();

                while (keysItr.hasNext()) {

                    tagNames.add(keysItr.next());
                }


            } catch (Exception e) {
                e.printStackTrace();

            }
            ;

        }

        if (tagNames.size() > 0 && imgPathSet.size() > 0) {
            uploadBtn.setEnabled(true);

        }

        for (String s : tagNames) {
            final Button tag1 = new Button(context);
            tag1.setText(s);
            tagsContainer.addView(tag1);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) tag1.getLayoutParams();
            marginParams.setMargins(0, 0, 10, 0);

            tag1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity(), ActivityEditDeleteTags.class);
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
                    HashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>> caa = (LinkedHashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>>) azureDB.get("azure");

                    if (caa != null) {
                        i.putExtra("selectedImages", imgPathSet);
                        i.putExtra("azure", caa);

                        startActivity(i);
                    } else {
                        Toast.makeText(context, "Please wait until contexts finish syncing", Toast.LENGTH_LONG).show();

                    }
                } catch (NullPointerException e) {

                    Toast.makeText(context, "Please wait until contexts finish syncing", Toast.LENGTH_LONG).show();
                }


            }
        });

    */
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
            }
        });


        if (imgPathSet.size() < 1) {
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

    private void clearTagsAndSelectedImages() {
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

