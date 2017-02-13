package com.example.edwin.photoarchive;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TagsActivity extends AppCompatActivity {
    private ListView ctxListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        ctxListView = (ListView) findViewById(R.id.listView);
        String[] values = new String[] { "Meeting",
                "Permit",
                "Project",
                "Cell tower"

        };

        final Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

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

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_layout_1, R.id.text1, values);
        ctxListView.setAdapter(adapter);

        final TextView tv9 = (TextView) findViewById(R.id.textView9);
        final LinearLayout attrList = (LinearLayout) findViewById(R.id.linearLayout1);
        final Button ok = (Button) findViewById(R.id.button9);
        final Button done = (Button) findViewById(R.id.button10);

        ctxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                int itemPosition     = position;
                String  itemValue    = (String) ctxListView.getItemAtPosition(position);


                tv9.setText(itemValue);
                ctxListView.setVisibility(View.INVISIBLE);
                done.setVisibility(View.INVISIBLE);
                attrList.setVisibility(View.VISIBLE);
                ok.setVisibility(View.VISIBLE);

                ArrayList<String> attList = new ArrayList<String>(map.get(itemValue));



                for(String s: attList){
                    TextView textView = new TextView(getApplicationContext());
                    textView.setText(s);
                    attrList.addView(textView);
                    EditText editText = new EditText(getApplicationContext());
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
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Intent i= new Intent(TagsActivity.this, Activity2.class);
               // i.putExtra("viewpager_position", 1);
                //startActivity(i);
                attrList.setVisibility(View.INVISIBLE);
                ok.setVisibility(View.INVISIBLE);

                if(((LinearLayout) attrList).getChildCount() > 0)
                    ((LinearLayout) attrList).removeAllViews();

                ctxListView.setVisibility(View.VISIBLE);
                tv9.setText("Select a Category");
                done.setVisibility(View.VISIBLE);


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

    public void TestClick(View v){
        System.out.println("clicked");


    }
}
