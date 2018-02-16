package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.example.edwin.photoarchive.Activities.TagsActivity;
import com.example.edwin.photoarchive.AzureClasses.Attribute;
import com.example.edwin.photoarchive.AzureClasses.TaggedImageObject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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
    private Button saveFieldsBtn;
    private ArrayList<Attribute> attributes = null;
    private ArrayList<String> existingData = null;
    private com.example.edwin.photoarchive.AzureClasses.Context globalTargetContext = null;
    private List<View> questionInstances = new ArrayList<View>();
    private List<View> titleInstances = new ArrayList<View>();
    private HashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>> categoryFieldMap;
    SendFields fieldData;

    // TODO https://mobikul.com/how-to-get-data-from-dynamically-created-views-android/
    // Make it when the data is updated, the tag information is saved.
    // TODO Make categories persist


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);

        /** BEG DECLARE VIEW OBJECTS */

        saveFieldsBtn = (Button) view.findViewById(R.id.save_fields);
        catSpinner = (Spinner) view.findViewById(R.id.category_spinner_tagging);
        questionViews = (LinearLayout) view.findViewById(R.id.questions_layout);

        /** END DECLARE VIEW OBJECTS */


        /** BEG GET SHAREDPREFERENCES DATA */

        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        String[] contextsArray = null;
        Gson gson = new Gson();
        try{
            contextsArray = gson.fromJson(sharedPreferences.getString("contexts", null), String[].class);
            String fetchGlobalContext = sharedPreferences.getString("globalContext",null);
            String fetchGlobalData = sharedPreferences.getString("globalData", null);
            if(fetchGlobalContext != null) globalTargetContext = gson.fromJson(fetchGlobalContext, com.example.edwin.photoarchive.AzureClasses.Context.class);
            if(fetchGlobalData != null) {
                Type type = new TypeToken<ArrayList<String>>() {}.getType(); // I have no idea what this does specifically but it is needed GSON Convert the String
                existingData = gson.fromJson(fetchGlobalData, type);
            }
        } catch (Exception e) { Log.d("TabFragment2","CRITICAL ERROR! JSON PARSE EXCEPTION"); }
        // GET Azure Data
        try {
            categoryFieldMap = (HashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>>) getActivity().getIntent().getExtras().get("azure");
        } catch (Exception e) {
            Log.d("TabFragment2","CRITICAL ERROR! HASHMAP CANNOT BE ASSIGNED");
            categoryFieldMap = new LinkedHashMap<>();
            ArrayList<Attribute> a = new ArrayList<>();
            a.add(new Attribute("432","It Broke?","RadioButton","false","truefales"));
            categoryFieldMap.put(new com.example.edwin.photoarchive.AzureClasses.Context("01","test"),a);
        }

        /** END GET SHAREDPREFERENCES DATA */


        /** BEG VIEW CONTENT CODE */

        if(contextsArray != null) {
            ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, contextsArray);
            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            catSpinner.setAdapter(catAdapter);
            // if data exists
            if(globalTargetContext != null && existingData != null){
                int index = 0;
                for (int i = 0; i < catSpinner.getCount() ; i++)
                    if (catSpinner.getItemAtPosition(i).equals(globalTargetContext.getId()))
                        index = i;
                catSpinner.setSelection(index);
                generateCatView();
            }
        }

        /** END VIEW CONTENT CODE */


        /** BEG EVENTLISTENERS */

        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(questionInstances.size()>0){
                    for(View v:questionInstances)
                        ((ViewGroup) v.getParent()).removeView(v);
                    questionInstances = new ArrayList<>();
                }
                if(titleInstances.size()>0){
                    for(View v:titleInstances)
                        ((ViewGroup) v.getParent()).removeView(v);
                    titleInstances = new ArrayList<>();
                }

                attributes = null;

                /** GET CATEGORY FIELDS */
                com.example.edwin.photoarchive.AzureClasses.Context targetContext = null;
                if(categoryFieldMap != null) {
                    for (com.example.edwin.photoarchive.AzureClasses.Context c : categoryFieldMap.keySet()) {
                        if (c.getId().equals(catSpinner.getItemAtPosition(i).toString())) {
                            targetContext = c;
                            if(globalTargetContext != null && !targetContext.getId().equalsIgnoreCase(globalTargetContext.getId())) {
                                existingData = null;
                                globalTargetContext = c;
                            }
                        }
                    }

                    attributes = categoryFieldMap.get(targetContext);
                    // target the Object in the map
                }

                if(attributes != null) {
                    for (Attribute a : attributes)
                        Log.i("F2ATRS", a.getFieldType() + " " + a.getPossibleValues() + " " + a.getRequired());
                } else {Log.i ("F2ATRS", "Attr NULL");}

                generateCatView();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do Nothing
            }
        });


        saveFieldsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> data = new ArrayList<>();
                for(int i = 0; i < attributes.size(); i++){

                    /* RADIO BUTTON */

                    if( attributes.get(i).getFieldType().contains("String") &&
                            attributes.get(i).getPossibleValues() != null ) {
                        RadioGroup rg = (RadioGroup) questionInstances.get(i);
                        RadioButton selRB = (RadioButton) questionViews.findViewById(rg.getCheckedRadioButtonId());
                        data.add(selRB.getTag().toString());
                    }

                    /* CHECK BOX */

                    else if( attributes.get(i).getFieldType().contains("Checkbox") ) {
                        CheckBox cb = (CheckBox) questionInstances.get(i);
                        if(cb.isChecked()) data.add("Yes"); // TODO change when checkbox is finalized db side
                        else data.add("No");
                    }

                    /* TEXT BOX (COMMENT) */

                    else if( attributes.get(i).getFieldType().contains("String")
                            && attributes.get(i).getQuestion().contains("Comment")){
                        EditText et = (EditText) questionInstances.get(i);
                        data.add(et.getText().toString());
                    }

                    /* TEXT BOX (INTEGER OR STRING) */

                    else if( attributes.get(i).getFieldType().contains("Integer")
                            || attributes.get(i).getFieldType().contains("String") ) {
                        EditText et = (EditText) questionInstances.get(i);
                        data.add(et.getText().toString());
                    }
                }

                sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

                // Store data, convert to String
                Gson gson = new Gson();
                String contextString = gson.toJson(globalTargetContext);
                String attributesString = gson.toJson(attributes);
                String dataString = gson.toJson(data);

                // Store
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("globalContext", contextString);
                editor.putString("globalAttributes", attributesString);
                editor.putString("globalData", dataString);

                editor.commit();

                Log.d("Cont",contextString);
                Log.d("Attr",attributesString);
                Log.d("Data",dataString);

                fieldData.sendData(globalTargetContext,attributes,data);
            }
        });


        /** END EVENT LISTENERS */

        /*
        setHasOptionsMenu(true);

        uploadBtn = (Button) view.findViewById(R.id.button5);
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUser", null);


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

//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        this.menu = menu;
//        menu.add(Menu.NONE, 0, Menu.NONE, "Cancel")
//                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//        MenuItem item = menu.findItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                //clear set of paths
//
//                imgPathSet.clear();
//                getActivity().setTitle("Photo Archive");
//                getActivity().getIntent().putExtra("totalSelected", 0);
//                hideOption(0);
//                uploadBtn.setEnabled(false);
//
//                return true;
//            }
//        });
//
//
//        if (imgPathSet.size() < 1) {
//            hideOption(0);
//        }
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    // for transporting data between Fragments - ph
    public interface SendFields{
        void sendData(com.example.edwin.photoarchive.AzureClasses.Context targetCat, ArrayList<Attribute> attributes, ArrayList<String> values);
    }

    // make sure you get the fieldData object from Activity2
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            fieldData = (SendFields) getActivity();
        }catch(Exception e){
            throw new ClassCastException("Cannot get data");
        }
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

    private void generateCatView(){
        for( int itemNumber = 0 ; attributes != null && itemNumber < attributes.size() ; itemNumber++ ){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    /* Json will contain option type which will use if/elseif to separate */

            // DIVIDER
            /*
            View divider = new View(getContext());
            divider.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 1));
            divider.setBackgroundColor(Color.parseColor("#444444"));
            titleInstances.add(divider);
            questionViews.addView(divider);
            */

            // QUESTION TITLE
            TextView questionName = new TextView(getContext());
            titleInstances.add(questionName);
            String questionText = attributes.get(itemNumber).getQuestion();
            if(attributes.get(itemNumber).getRequired().contains("Y"))
                questionText += " *";
            questionName.setText(questionText);
            questionName.setTextSize(18);
            questionName.setPadding(10,15,10,5);
            questionViews.addView(questionName,params);

                    /* RADIO BUTTON */

            if( attributes.get(itemNumber).getFieldType().contains("String") &&
                    attributes.get(itemNumber).getPossibleValues() != null ) {
                String[] options = attributes.get(itemNumber).getPossibleValues().split(",");
                RadioGroup radioGroup = new RadioGroup(getContext());
                questionInstances.add(radioGroup);
                for( int radioNumber = 0 ; radioNumber < options.length ; radioNumber++ ) {
                    RadioButton rb = new RadioButton(getContext());
                    radioGroup.addView(rb,params);
                    if (existingData == null && radioNumber == 0) { // CHECK EXISTING DATA
                        rb.setChecked(true); // First Option Selected, Can Be Changed
                    } else if (existingData != null && existingData.size()>1 && options[radioNumber].equals(existingData.get(itemNumber))){
                        rb.setChecked(true);
                    }
                    rb.setTag(options[radioNumber]);
                    rb.setText(options[radioNumber]);
                }
                radioGroup.setPadding(10,10,10,10);

                questionViews.addView(radioGroup,params);
            }

                    /* CHECK BOX */

            else if( attributes.get(itemNumber).getFieldType().contains("Checkbox") ) {
                CheckBox chkBox = new CheckBox(getContext());
                questionInstances.add(chkBox);
                chkBox.setTag(attributes.get(itemNumber).getPossibleValues());
                chkBox.setText(attributes.get(itemNumber).getPossibleValues());
                chkBox.setPadding(10,10,10,10);
                // CHECK EXISTING DATA
                if(existingData != null &&existingData.size()>1&& existingData.get(itemNumber).equalsIgnoreCase("Yes")){
                    chkBox.setChecked(true);
                }
                questionViews.addView(chkBox,params);
            }

                    /* TEXT BOX (COMMENT) */

            else if( attributes.get(itemNumber).getFieldType().contains("String")
                    && attributes.get(itemNumber).getQuestion().contains("Comment")) {
                EditText txtBox = new EditText(getContext());
                questionInstances.add(txtBox);
                txtBox.setGravity( Gravity.LEFT | Gravity.TOP );
                txtBox.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                txtBox.setLines(6);
                txtBox.setMaxLines(8);
                txtBox.setMinLines(4);
                txtBox.setVerticalScrollBarEnabled(true);
                txtBox.setSingleLine(false);
                txtBox.setPadding(10,10,10,10);
                if(existingData != null && existingData.size()>1){
                    txtBox.setText(existingData.get(itemNumber));
                }
                questionViews.addView(txtBox,params);
            }

                    /* TEXT BOX (INTEGER OR STRING) */

            else if( attributes.get(itemNumber).getFieldType().contains("Integer")
                    || attributes.get(itemNumber).getFieldType().contains("String") ) {
                EditText txtInp = new EditText(getContext());
                questionInstances.add(txtInp);
                txtInp.setSingleLine(true);
                if(existingData != null&& existingData.size() >1){
                    txtInp.setText(existingData.get(itemNumber));
                }
                questionViews.addView(txtInp,params);
            }
        }
        // DIVIDER
//        View divider = new View(getContext());
//        divider.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 1));
//        divider.setBackgroundColor(Color.parseColor("#444444"));
//        titleInstances.add(divider);
//        questionViews.addView(divider);
    }
}

