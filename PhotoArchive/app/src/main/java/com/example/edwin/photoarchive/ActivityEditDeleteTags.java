package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class ActivityEditDeleteTags extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_edit_delete_tags);

        final LinearLayout linearLayoutEditTags = (LinearLayout) findViewById(R.id.linearLayoutEditTags);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            final String name = extras.getString("tag_name");
            TextView title = (TextView) findViewById(R.id.textView10);
            title.setText(name);

            SharedPreferences sharedPreferences = getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = sharedPreferences.edit();

            if(sharedPreferences.contains("listOfTags")) {

                String mapString = sharedPreferences.getString("listOfTags", null);

                final Map<String, Map<String, String>> outputMap = new HashMap<String, Map<String, String>>();
                try {
                    JSONObject jsonObject2 = new JSONObject(mapString);
                    Iterator<String> keysItr = jsonObject2.keys();

                    while (keysItr.hasNext()) {
                        String key = keysItr.next();

                        Map<String, String> valueMap = new HashMap<String, String>();
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

                };


                //find attributes and values in map using key=name

                Map<String, String> valueMap = new HashMap<String, String>(outputMap.get(name));

                for (Map.Entry<String, String> entry : valueMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    TextView t = new TextView(this);
                    t.setText(key);
                    t.setTextColor(Color.BLACK);
                    linearLayoutEditTags.addView(t);

                    EditText et = new EditText(this);
                    et.setText(value);
                    et.setEnabled(false);
                    linearLayoutEditTags.addView(et);

                }

                final ImageButton deleteBtn = (ImageButton) findViewById(R.id.button13);
                final ImageButton editBtn = (ImageButton) findViewById(R.id.button12);

                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new AlertDialog.Builder(ActivityEditDeleteTags.this)
                                .setTitle("Delete confirmation")
                                .setMessage("Are you sure you want to delete this tag?")
                                .setNegativeButton(android.R.string.cancel, null)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override public void onClick(DialogInterface dialog, int which) {


                                        // delete tag
                                        outputMap.remove(name);

                                        //update shared preferences and redirect to tab 2
                                        JSONObject finalJsonObject = new JSONObject(outputMap);

                                        editor.remove("listOfTags");
                                        editor.apply();
                                        editor.putString("listOfTags", finalJsonObject.toString() );
                                        editor.apply();

                                        Intent i= new Intent(ActivityEditDeleteTags.this, Activity2.class);
                                        i.putExtra("viewpager_position", 1);

                                        // get images sent  from tab 2 and send them back

                                        Bundle extras = getIntent().getExtras();

                                        if(extras != null ){
                                            if(extras.containsKey("selectedImages")){

                                                HashSet<String> passedImagesPathSet = new HashSet<String>((HashSet) extras.get("selectedImages"));

                                                i.putExtra("selectedImagesFromGallery", passedImagesPathSet);
                                            }

                                        }

                                        startActivity(i);

                                        Toast.makeText(getApplicationContext(), "Tag deleted", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .create()
                                .show();

                    }
                });

                editBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteBtn.setEnabled(false);
                        deleteBtn.setColorFilter(Color.GRAY);
                        editBtn.setEnabled(false);
                        editBtn.setColorFilter(Color.GRAY);

                        //enable EditTexts

                        for(int i=0; i < linearLayoutEditTags.getChildCount(); i++){
                            View view = linearLayoutEditTags.getChildAt(i);

                            if(view instanceof EditText){
                                ((EditText)view).setEnabled(true);

                            }
                        }

                        // add cancel and ok btns

                        LinearLayout okCancelBtnsContainer = new LinearLayout(getApplicationContext());
                        okCancelBtnsContainer.setOrientation(LinearLayout.HORIZONTAL);
                        okCancelBtnsContainer.setGravity(Gravity.CENTER);

                        Button ok = new Button(getApplicationContext());
                        ok.setText("OK");

                        okCancelBtnsContainer.addView(ok);

                        Button cancel = new Button(getApplicationContext());
                        cancel.setText("Cancel");

                        // cancel click evt

                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                recreate();
                            }
                        });

                        ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String key = name;
                                Map<String, String> value = new HashMap<String, String>();

                                for (int i=0; i<linearLayoutEditTags.getChildCount()-1; i+=2) {
                                    View view = linearLayoutEditTags.getChildAt(i);
                                    View view2 = linearLayoutEditTags.getChildAt(i+1);

                                    value.put(((TextView) view).getText().toString(), ((EditText) view2).getText().toString() );

                                }

                                outputMap.remove(name);

                                outputMap.put(key, value);

                                //update shared prefs
                                JSONObject finalJsonObject = new JSONObject(outputMap);

                                editor.remove("listOfTags");
                                editor.apply();
                                editor.putString("listOfTags", finalJsonObject.toString() );
                                editor.apply();

                                recreate();
                            }
                        });


                        okCancelBtnsContainer.addView(cancel);

                        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams1.setMargins(0,25,0,0);

                        linearLayoutEditTags.addView(okCancelBtnsContainer, layoutParams1);


                    }
                });



            }


        }


    }


    public void backToTab2(View v){
        Intent i= new Intent(ActivityEditDeleteTags.this, Activity2.class);
        i.putExtra("viewpager_position", 1);

        // get images sent  from tab 2 and send them back

        Bundle extras = getIntent().getExtras();

        if(extras != null ){
            if(extras.containsKey("selectedImages")){

                HashSet<String> passedImagesPathSet = new HashSet<String>((HashSet) extras.get("selectedImages"));

                i.putExtra("selectedImagesFromGallery", passedImagesPathSet);
            }

        }

        startActivity(i);
    }
}
