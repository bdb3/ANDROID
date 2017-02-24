package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TagsActivity extends AppCompatActivity {
    private LinearLayout linearLayoutContextContainer;
    private TextView tv9;
    private Button done;
    private LinearLayout attrList;
    private Button ok;
    private Button cancelBtn;
    private Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
    private Map<String, Map<String, String>> tagList = new HashMap<String, Map<String, String>>();
    SharedPreferences sharedPreferences;
    public static final String MyTagsPREFERENCES = "Preferences" ;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        linearLayoutContextContainer = (LinearLayout) findViewById(R.id.linearLayoutContextContainer);
        String[] values = new String[] { "Meeting",
                "Permit",
                "Project",
                "Cell tower"
        };

        for(int i=0; i < values.length; i++){

            ArrayList<String> list = new ArrayList<String>();

            if(i==0){
                list.add("How many people are in the photo?");
                list.add("Where was the photo taken?");
                list.add("What is the topic of the meeting?");

            }
            else if(i==1){
                list.add("Where was the photo taken?");
                list.add("What is the Permit Number?");
            }
            else if(i==2){
                list.add("Where was the photo taken?");
                list.add("What is the Project Number?");
            }
            else if(i==3){
                list.add("Where was the photo taken?");
                list.add("What is the company that owns this tower?");
                list.add("How far does the signal reach?");

            }

            map.put(values[i], list);

        }

       // generate context vertical list

        for(int i = 0; i<values.length; i++){
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
            tv.setText(values[i]);
            tv.setTextColor(Color.BLACK);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 4.0f);

            tv.setLayoutParams(param);

            contextRow.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showForm(tv.getText().toString());
                }
            });


            ImageButton btn1 = new ImageButton(this);
            btn1.setImageResource(R.drawable.edit);
            btn1.setBackgroundColor(Color.TRANSPARENT);
            btn1.setVisibility(View.INVISIBLE);

            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            layoutParams2.setMargins(0,0,18,0);

            contextRow.addView(btn1, layoutParams2);

            ImageButton btn2 = new ImageButton(this);
            btn2.setImageResource(R.drawable.delete_icon);
            btn2.setBackgroundColor(Color.TRANSPARENT);
            btn2.setVisibility(View.INVISIBLE);

            contextRow.addView(btn2);

            linearLayoutContextContainer.addView(contextRow, layoutParams1);

        }

        sharedPreferences = getSharedPreferences(MyTagsPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if(sharedPreferences.contains("listOfTags")){
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

            tagList =  new HashMap<String, Map<String, String>>(outputMap);


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

                Map<String, String> attributesMap = new HashMap<String, String>();

                String myKey = "";
                String myValue = "";


                for (int i=0; i<attrList.getChildCount(); i+=2) {
                    View view = attrList.getChildAt(i);
                    View view2 = attrList.getChildAt(i+1);

                    attributesMap.put(((TextView) view).getText().toString(), ((EditText) view2).getText().toString() );

                }

                SharedPreferences.Editor editor = sharedPreferences.edit();

                if(!sharedPreferences.contains("listOfTags")) {
                    tagList.put(tv9.getText().toString(), attributesMap);

                    JSONObject tagListMapAsJSON = new JSONObject(tagList);
                    String tagListMapAsJSONString   = tagListMapAsJSON.toString();

                    editor.putString("listOfTags", tagListMapAsJSONString);
                    editor.commit();

                    //System.out.println(sharedPreferences.getString("listOfTags", null));
                }

                else{
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

                    tagList =  new HashMap<String, Map<String, String>>(outputMap);
                    tagList.put(tv9.getText().toString(), attributesMap);

                    JSONObject finalJsonObject = new JSONObject(tagList);

                    editor.remove("listOfTags");
                    editor.apply();
                    editor.putString("listOfTags", finalJsonObject.toString() );
                    editor.apply();

                  //  System.out.println(sharedPreferences.getString("listOfTags", null));


                }

                attrList.setVisibility(View.INVISIBLE);
                ok.setVisibility(View.INVISIBLE);
                ok.setEnabled(false);
                cancelBtn.setVisibility(View.INVISIBLE);

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
                i.putExtra("viewpager_position", 1);
                startActivity(i);
            }
        });

    }

    public void showForm(String choice){

        tv9.setText(choice);
        linearLayoutContextContainer.setVisibility(View.INVISIBLE);
        done.setVisibility(View.INVISIBLE);
        attrList.setVisibility(View.VISIBLE);
        ok.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);

        ArrayList<String> attList = new ArrayList<String>(map.get(choice));

        for(String s: attList){
            TextView textView = new TextView(getApplicationContext());
            textView.setText(s);
            attrList.addView(textView);
            final EditText editText = new EditText(getApplicationContext());

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
            attrList.addView(editText);

        }

        TextView myTextView;
        for (int i=0; i<attrList.getChildCount();i++) {
            View view = attrList.getChildAt(i);
            if (view instanceof TextView){
                myTextView= (TextView) view;
                myTextView.setTextColor(Color.BLACK);
            }
        }

    }

    public void editAttributes(View v){

        ViewParent linearLayout = v.getParent();

        TextView textView = (TextView) ((View) linearLayout).findViewById(R.id.text1);
        String text = textView.getText().toString();

        System.out.println("edit: " + text);

    }

    public void deleteCurrentTag(View v){
        ViewParent linearLayout = v.getParent();

        TextView textView = (TextView) ((View) linearLayout).findViewById(R.id.text1);
        String text = textView.getText().toString();

        System.out.println("delete: " + text);

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
                ((ImageButton)row.getChildAt(2)).setVisibility(View.VISIBLE);
                category.setEnabled(false);


            }

        }

    }


}
