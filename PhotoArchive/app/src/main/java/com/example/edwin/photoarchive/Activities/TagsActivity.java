package com.example.edwin.photoarchive.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.photoarchive.AzureClasses.Attribute;
import com.example.edwin.photoarchive.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeMap;


public class TagsActivity extends AppCompatActivity {
    private LinearLayout linearLayoutContextContainer;
    private TextView tv9;
    private Button done;
    private LinearLayout attrList;
    private Button ok;
    private Button cancelBtn;
    private Map<String, Map<String, String>> tagList = new LinkedHashMap<String, Map<String, String>>();
    SharedPreferences sharedPreferences;
    public static final String MyTagsPREFERENCES = "Preferences" ;
    private String referrer = "";
    private String prefsKey = "";

    private TreeMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>> contextsAndAttributes;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);


        Bundle azureDB = getIntent().getExtras();

        contextsAndAttributes =
        new TreeMap<>((HashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>>) azureDB.get("azure"));


        linearLayoutContextContainer = (LinearLayout) findViewById(R.id.linearLayoutContextContainer);

        if(azureDB.containsKey("cameraTab")){
            referrer = "cameraTab";
            prefsKey = "cameraTags";

        }
        else{
            prefsKey = "listOfTags";
        }


       // generate context vertical list

        for(Map.Entry<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>> entry : contextsAndAttributes.entrySet()){

            final com.example.edwin.photoarchive.AzureClasses.Context key = entry.getKey();

            LinearLayout contextRow = new LinearLayout(this);
            contextRow.setOrientation(LinearLayout.HORIZONTAL);


            GradientDrawable border = new GradientDrawable();
            border.setStroke(1, 0xD3D3D3D3);
            border.setGradientType(GradientDrawable.RECTANGLE);

            Drawable[] layers = {border};
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            layerDrawable.setLayerInset(0, -2, -2,-2,1);
            contextRow.setBackground(layerDrawable);


            contextRow.setWeightSum(4);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams1.setMargins(0,15,0,0);

            final TextView tv = new TextView(this);
            tv.setText(key.getId());
            tv.setTextColor(Color.BLACK);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 4.0f);

            tv.setLayoutParams(param);

            contextRow.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showForm(key);
                }
            });



            ImageButton btn2 = new ImageButton(this);
            btn2.setImageResource(R.drawable.ic_check);
            btn2.setBackgroundColor(Color.TRANSPARENT);
            btn2.setVisibility(View.INVISIBLE);

            contextRow.addView(btn2);

            linearLayoutContextContainer.addView(contextRow, layoutParams1);

        }

        sharedPreferences = getSharedPreferences(MyTagsPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(sharedPreferences.contains(prefsKey)){
            String mapString  = sharedPreferences.getString(prefsKey, null);

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

            tagList =  new LinkedHashMap<String, Map<String, String>>(outputMap);


        }

        grayOutSelectedCategories();

        tv9 = (TextView) findViewById(R.id.textView9);
        attrList = (LinearLayout) findViewById(R.id.linearLayout1);
        ok = (Button) findViewById(R.id.button9);
        done = (Button) findViewById(R.id.button10);
        cancelBtn = (Button) findViewById(R.id.button11);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, String> attributesMap = new LinkedHashMap<String, String>();


                for (int i=0; i<attrList.getChildCount(); i+=2) {
                    View view = attrList.getChildAt(i);
                    View view2 = attrList.getChildAt(i+1);

                    attributesMap.put(((TextView) view).getTag().toString(), ((EditText) view2).getText().toString() );

                }

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if(!sharedPreferences.contains(prefsKey)) {
                    tagList.put(tv9.getText().toString(), attributesMap);

                    JSONObject tagListMapAsJSON = new JSONObject(tagList);
                    String tagListMapAsJSONString   = tagListMapAsJSON.toString();

                    editor.putString(prefsKey, tagListMapAsJSONString);
                    editor.apply();


                }

                else{
                    String mapString  = sharedPreferences.getString(prefsKey, null);

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

                    tagList =  new LinkedHashMap<String, Map<String, String>>(outputMap);
                    tagList.put(tv9.getText().toString(), attributesMap);

                    JSONObject finalJsonObject = new JSONObject(tagList);

                    editor.remove(prefsKey);
                    editor.apply();
                    editor.putString(prefsKey, finalJsonObject.toString() );
                    editor.apply();


                }

                attrList.setVisibility(View.GONE);
                ok.setVisibility(View.GONE);
                ok.setEnabled(false);
                cancelBtn.setVisibility(View.GONE);

                if(((LinearLayout) attrList).getChildCount() > 0)
                    ((LinearLayout) attrList).removeAllViews();

                // gray out selected category

                grayOutSelectedCategories();

                linearLayoutContextContainer.setVisibility(View.VISIBLE);
                tv9.setText("Select a Category");
                done.setVisibility(View.VISIBLE);


            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recreate();
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(TagsActivity.this, Activity2.class);
                Bundle extras = getIntent().getExtras();

                if(referrer.equals("cameraTab")){
                    i.putExtra("viewpager_position", 2);
                    if (extras != null) {
                        if (extras.containsKey("cameraImages")) {

                            HashSet<String> passedImagesPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("cameraImages"));

                            i.putExtra("cameraImages", passedImagesPathSet);
                        }

                    }

                }
                else {

                    i.putExtra("viewpager_position", 1);

                    if (extras != null) {
                        if (extras.containsKey("selectedImages")) {

                            HashSet<String> passedImagesPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImages"));

                            i.putExtra("selectedImagesFromGallery", passedImagesPathSet);
                        }

                    }
                }


                startActivity(i);
                finish();
            }
        });

    }

    public void showForm(com.example.edwin.photoarchive.AzureClasses.Context choice){

        tv9.setText(choice.getId());
        linearLayoutContextContainer.setVisibility(View.GONE);
        done.setVisibility(View.GONE);
        attrList.setVisibility(View.VISIBLE);
        ok.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);

        ArrayList<Attribute> attList = new ArrayList<Attribute>(contextsAndAttributes.get(choice));

        for(Attribute s: attList){
            TextView textView = new TextView(getApplicationContext());
            textView.setText(s.getQuestion());
            textView.setTag(s.getId());
            textView.setTextColor(Color.BLACK);
            attrList.addView(textView);
            final EditText editText = new EditText(getApplicationContext());


            //TODO STicky forms based on filled info
            //EditText event listener, all fields are required before btn is enabled

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable arg0) {
                    if(editText.getText().toString().trim().length()>0 && allAreFilledOut() ){
                        ok.setEnabled(true);
                    }
                    else{
                        ok.setEnabled(false);

                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });


            editText.setBackgroundColor(Color.LTGRAY);
            editText.setTextColor(Color.BLACK);
            attrList.addView(editText);

        }


    }



    public boolean allAreFilledOut(){
        for (int i=0; i<attrList.getChildCount(); i++) {
            View view = attrList.getChildAt(i);

            if(view instanceof EditText){
                String value = ((EditText) view).getText().toString();

                if(value.trim().length()<1){
                    return false;
                }
            }
        }
        return true;
    }


    public void grayOutSelectedCategories(){
        for(int i=0; i<linearLayoutContextContainer.getChildCount(); i++){
            LinearLayout row = (LinearLayout) linearLayoutContextContainer.getChildAt(i);
            TextView category =  (TextView) row.getChildAt(0);

            if(tagList.get(category.getText().toString()) != null){
                row.setBackgroundColor(Color.LTGRAY);

                ((ImageButton)row.getChildAt(1)).setVisibility(View.VISIBLE);

                category.setEnabled(false);


            }

        }

    }


}
