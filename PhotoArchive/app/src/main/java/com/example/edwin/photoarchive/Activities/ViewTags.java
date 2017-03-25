package com.example.edwin.photoarchive.Activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.photoarchive.AzureClasses.TaggedImageObject;
import com.example.edwin.photoarchive.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
                String savedArraylist  = sharedPreferences.getString("listOfImagesWithTags", null);
                Type listType = new TypeToken<ArrayList<TaggedImageObject>>(){}.getType();
                List<TaggedImageObject> taggedImageObjectsList = new Gson().fromJson(savedArraylist, listType);

                TaggedImageObject t = null;

                for(TaggedImageObject f: taggedImageObjectsList){
                    if(f.getImgPath().equals(path)){
                        t = f;
                        break;

                    }

            }
                    try {
                        JSONObject jsonObject3 = new JSONObject(t.getContextAttributeMap());
                        Iterator<String> keysItr = jsonObject3.keys();

                        while (keysItr.hasNext()) {
                            String key = keysItr.next();

                            Button ctx = new Button(this);
                            ctx.setText(key);
                            ctx.setClickable(false);
                            linearLayoutTags.addView(ctx);

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

                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();

                    };


            }

        }
    }
}
