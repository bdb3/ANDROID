package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import com.example.edwin.photoarchive.AzureClasses.Field;
import com.example.edwin.photoarchive.AzureClasses.Category;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

public class TabFragment2 extends Fragment {
    private HashSet<String> imgPathSet = new LinkedHashSet<String>();
    private SharedPreferences sharedPreferences;
    private Spinner catSpinner;
    private LinearLayout questionViews;
    private Button saveFieldsBtn;
    private ArrayList<Field> fields = null;
    private ArrayList<String> existingData = null;
    private HashMap<String, HashMap<String, String>> storedDataMap; // This is a map of all the stored data
    private HashMap<String,String> innerDataMap; // This is the map that is stored for each Category in the Stored Data Map
    private HashMap<String,String> innerDataMapRetrieved=null; // This is the map that is retrieved to populate the fields
    private Category globalTargetCategory = null;
    private List<View> questionInstances = new ArrayList<View>();
    private List<View> titleInstances = new ArrayList<View>();
    private String currentlySelectedContext=""; // The currently selected Category is A Permits (first attribute) By default
    private HashMap<Category, ArrayList<Field>> categoryFieldMap;
    SendFields fieldData;

    // TODO https://mobikul.com/how-to-get-data-from-dynamically-created-views-android/
    // Make it when the data is updated, the tag information is saved.
    // TODO Make categories persist

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
            String fetchGlobalData = sharedPreferences.getString("globalData", null); // Global Data Being Stored
            if(fetchGlobalContext != null) globalTargetCategory = gson.fromJson(fetchGlobalContext, Category.class);

            String fetchStoredDataMap = sharedPreferences.getString("storedDataMap",null);
            if(fetchStoredDataMap==null) {
                storedDataMap=new HashMap<String, HashMap<String, String>>();
                //Log.d("TabFrag2DataMap",storedDataMap.toString());
            }
            else{
                Type dataType = new TypeToken<HashMap<String,HashMap<String,String>>>() {}.getType();
                storedDataMap=gson.fromJson(sharedPreferences.getString("storedDataMap",null),dataType);
            }

            if(fetchGlobalData != null) {
                Type type = new TypeToken<ArrayList<String>>() {}.getType(); // I have no idea what this does specifically but it is needed GSON Convert the String
                existingData = gson.fromJson(fetchGlobalData, type);
            }
        } catch (Exception e) { Log.d("TabFragment2","CRITICAL ERROR! JSON PARSE EXCEPTION"); }
        // GET Azure Data
        try {
            categoryFieldMap = (HashMap<Category, ArrayList<Field>>) getActivity().getIntent().getExtras().get("azure");
        } catch (Exception e) {
            Log.d("TabFragment2","CRITICAL ERROR! HASHMAP CANNOT BE ASSIGNED");
            categoryFieldMap = new LinkedHashMap<>();
            ArrayList<Field> a = new ArrayList<>();
            a.add(new Field("432","It Broke?","RadioButton","false","truefalse"));
            categoryFieldMap.put(new Category("01","test"),a);
        }

        /** END GET SHAREDPREFERENCES DATA */


        /** BEG VIEW CONTENT CODE */

        if(contextsArray != null) {
            ArrayAdapter<String> catAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, contextsArray);
            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            catSpinner.setAdapter(catAdapter);
            // if data exists
            if(globalTargetCategory != null && existingData != null){
                int index = 0;
                for (int i = 0; i < catSpinner.getCount() ; i++)
                    if (catSpinner.getItemAtPosition(i).equals(globalTargetCategory.getId()))
                        index = i;
                catSpinner.setSelection(index);
                generateCatView();
            }
        }

        /** END VIEW CONTENT CODE */


        /** BEG EVENTLISTENERS */
        // TODO ---------- CATEGORY SPINNER IS LOCATED HERE
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(questionInstances.size()>0){ // If Question Instances Exists
                    for(View v:questionInstances)
                        ((ViewGroup) v.getParent()).removeView(v);
                    questionInstances = new ArrayList<>();
                }
                if(titleInstances.size()>0){ // If Title Instances Exists
                    for(View v:titleInstances)
                        ((ViewGroup) v.getParent()).removeView(v);
                    titleInstances = new ArrayList<>();
                }

                fields = null;

                /** GET CATEGORY FIELDS */
                Category targetCategory = null;
                if(categoryFieldMap != null) {
                    for (Category c : categoryFieldMap.keySet()) {
                        if (c.getId().equals(catSpinner.getItemAtPosition(i).toString())) {
                            targetCategory = c;

                            currentlySelectedContext=(targetCategory.getId());

                            if(globalTargetCategory != null && !targetCategory.getId().equalsIgnoreCase(globalTargetCategory.getId())) {
                                existingData = null;
                                globalTargetCategory = c;
                            }
                        }
                    }
                    fields = categoryFieldMap.get(targetCategory);
                    // target the Object in the map
                }

                if(fields != null) {
                    for (Field a : fields)
                        Log.i("F2ATRS", a.getFieldType() + " " + a.getPossibleValues() + " " + a.getRequired());
                } else {Log.i ("F2ATRS", "Attr NULL");}

                generateCatView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do Nothing
            }
        });

        // TODO -----SAVE FIELDS BUTTON IS LOCATED HERE
        saveFieldsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> data = new ArrayList<>();
                for(int i = 0; i < fields.size(); i++){

                    /* RADIO BUTTON */

                    if( fields.get(i).getFieldType().contains("String") &&
                            fields.get(i).getPossibleValues() != null ) {
                        RadioGroup rg = (RadioGroup) questionInstances.get(i);
                        RadioButton selRB = (RadioButton) questionViews.findViewById(rg.getCheckedRadioButtonId());
                        data.add(selRB.getTag().toString());
                    }

                    /* CHECK BOX */

                    else if( fields.get(i).getFieldType().contains("Checkbox") ) {
                        CheckBox cb = (CheckBox) questionInstances.get(i);
                        if(cb.isChecked()) data.add("Yes"); // TODO change when checkbox is finalized db side
                        else data.add("No");
                    }

                    /* TEXT BOX (COMMENT) */

                    else if( fields.get(i).getFieldType().contains("String")
                            && fields.get(i).getQuestion().contains("Comment")){
                        EditText et = (EditText) questionInstances.get(i);
                        data.add(et.getText().toString());
                    }

                    /* TEXT BOX (INTEGER OR STRING) */

                    else if( fields.get(i).getFieldType().contains("Integer")
                            || fields.get(i).getFieldType().contains("String") ) {
                        EditText et = (EditText) questionInstances.get(i);
                        data.add(et.getText().toString());
                    }
                }

                sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

                // Store data, convert to String
                Gson gson = new Gson();
                String contextString = gson.toJson(globalTargetCategory);
                String attributesString = gson.toJson(fields);
                String dataString = gson.toJson(data);

                // TODO Store
                // Processing Arrays

                SharedPreferences.Editor editor = sharedPreferences.edit();
                // Creating a new hashmap to store the array of info
                // This process creates a new hashmap to be stored based on the Category key
                innerDataMap=new HashMap<String, String>();
                for(int i = 0; i< fields.size(); i++){
                    innerDataMap.put(fields.get(i).getId(),data.get(i));
                }
                storedDataMap.put(currentlySelectedContext, innerDataMap);
                // End of this process

                editor.putString("storedDataMap",gson.toJson(storedDataMap));

                Log.d("TabFrag2DataMapSave",storedDataMap.toString());
                editor.putString("globalContext", contextString);
                editor.putString("globalAttributes", attributesString);
                editor.putString("globalData", dataString);
                editor.putString("currentlySelectedContext",currentlySelectedContext);
                editor.commit();

                Log.d("Cont",contextString);
                Log.d("Attr",attributesString);
                Log.d("Data",dataString);

                fieldData.sendData(globalTargetCategory, fields,data);
            }
        });

        /** END EVENT LISTENERS */

        return view;

    }

    // for transporting data between Fragments - ph
    public interface SendFields{
        void sendData(Category targetCat, ArrayList<Field> fields, ArrayList<String> values);
    }

    // make sure you get the fieldData object from MainActivity
    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try{
            fieldData = (SendFields) getActivity();
        }catch(Exception e){
            throw new ClassCastException("Cannot get data");
        }
    }

    private void generateCatView(){

        for(int itemNumber = 0; fields != null && itemNumber < fields.size() ; itemNumber++ ){
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    /* Json will contain option type which will use if/elseif to separate */

            // TODO Logic of the Attributes Being Pulled and Stored

            // QUESTION TITLE
            TextView questionName = new TextView(getContext());
            titleInstances.add(questionName);
            String questionText = fields.get(itemNumber).getQuestion();
            if(fields.get(itemNumber).getRequired().contains("Y"))   // Sets an asterisk next to the question if the field is required
                questionText += " *";
            questionName.setText(questionText);
            questionName.setTextSize(18);
            questionName.setPadding(10,15,10,5);
            questionViews.addView(questionName,params);

                    /* RADIO BUTTON */

            if( fields.get(itemNumber).getFieldType().contains("String") &&
                    fields.get(itemNumber).getPossibleValues() != null ) {
                String[] options = fields.get(itemNumber).getPossibleValues().split(",");
                RadioGroup radioGroup = new RadioGroup(getContext());
                questionInstances.add(radioGroup);
                for( int radioNumber = 0 ; radioNumber < options.length ; radioNumber++ ) {
                    RadioButton rb = new RadioButton(getContext());
                    radioGroup.addView(rb,params);

                    if (innerDataMapRetrieved == null && radioNumber == 0) { // CHECK EXISTING DATA
                        rb.setChecked(true); // First Option Selected, Can Be Changed
                    } else if (storedDataMap.containsKey(currentlySelectedContext)) {
                        if(options[radioNumber].equalsIgnoreCase(storedDataMap.get(currentlySelectedContext).get(fields.get(itemNumber).getId()))) {

                            rb.setChecked(true);
                        }
                    }

                    rb.setTag(options[radioNumber]);
                    rb.setText(options[radioNumber]);
                }
                radioGroup.setPadding(10,10,10,10);

                questionViews.addView(radioGroup,params);
            }

                    /* CHECK BOX */

            else if( fields.get(itemNumber).getFieldType().contains("Checkbox") ) {
                CheckBox chkBox = new CheckBox(getContext());
                questionInstances.add(chkBox);
                chkBox.setTag(fields.get(itemNumber).getPossibleValues());
                chkBox.setText(fields.get(itemNumber).getPossibleValues());
                chkBox.setPadding(10,10,10,10);
                if(storedDataMap.containsKey(currentlySelectedContext)){
                    if(storedDataMap.get(currentlySelectedContext).get(fields.get(itemNumber).getId()).equalsIgnoreCase("Yes")){
                        chkBox.setChecked(true);
                    }
                }


                questionViews.addView(chkBox,params);
            }

                    /* TEXT BOX (COMMENT) */

            else if( fields.get(itemNumber).getFieldType().contains("String")
                    && fields.get(itemNumber).getQuestion().contains("Comment")) {
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

                if(storedDataMap.containsKey(currentlySelectedContext)){
                    txtBox.setText(storedDataMap.get(currentlySelectedContext).get(fields.get(itemNumber).getId()));
                }
                else{
                    txtBox.setText("");
                }

                questionViews.addView(txtBox,params);
            }

                    /* TEXT BOX (INTEGER OR STRING) */

            else if( fields.get(itemNumber).getFieldType().contains("Integer")
                    || fields.get(itemNumber).getFieldType().contains("String") ) {
                EditText txtInp = new EditText(getContext());
                questionInstances.add(txtInp);
                txtInp.setSingleLine(true);

                if(storedDataMap.containsKey(currentlySelectedContext)){
                        txtInp.setText(storedDataMap.get(currentlySelectedContext).get(fields.get(itemNumber).getId()));
                }
                else{
                    txtInp.setText("");
                }

                questionViews.addView(txtInp,params);
            }
        }
    }
}

