package com.example.edwin.photoarchive;

import android.content.*;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class ViewTags extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_tags);

        final LinearLayout linearLayoutTags = (LinearLayout) findViewById(R.id.linearLayoutTags);

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            final String path = extras.getString("imagePath");

            SharedPreferences sharedPreferences = getSharedPreferences(TagsActivity.MyTagsPREFERENCES, android.content.Context.MODE_PRIVATE);

            if(sharedPreferences.contains("listOfImagesWithTags")) {
                String mapString = sharedPreferences.getString("listOfImagesWithTags", null);

                final Map<String, Map<String, String>> outputMap = new LinkedHashMap<String, Map<String, String>>();

                try {
                    JSONObject jsonObject2 = new JSONObject(mapString);
                    JSONObject jsonObject3 = (JSONObject)jsonObject2.get(path);

                    Iterator<String> keysItr = jsonObject3.keys();

                    while (keysItr.hasNext()) {
                        String key = keysItr.next();

                        Button ctx = new Button(this);
                        ctx.setText(key);
                        ctx.setClickable(false);
                        linearLayoutTags.addView(ctx);

                        Map<String, String> valueMap = new LinkedHashMap<String, String>();
                        Iterator<String> keysItr2 = ((JSONObject) jsonObject3.get(key)).keys();

                        while (keysItr2.hasNext()) {
                            String key2 = keysItr2.next();

                            TextView q = new TextView(this);
                            q.setTextColor(Color.BLACK);
                            q.setText(key2);
                            linearLayoutTags.addView(q);


                            String value = (String) ((JSONObject) jsonObject3.get(key)).get(key2);

                            EditText a = new EditText(this);
                            a.setText(value);
                            a.setEnabled(false);
                            linearLayoutTags.addView(a);

                            valueMap.put(key2, value);
                        }

                        outputMap.put(key, valueMap);
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                };


            }

        }
    }
}
