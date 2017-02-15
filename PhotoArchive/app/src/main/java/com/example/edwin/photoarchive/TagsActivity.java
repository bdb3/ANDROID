package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
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
    private TextView tv9;
    private Button done;
    private LinearLayout attrList;
    private Button ok;
    private Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
    private Map<String, ArrayList<String>> tagList = new HashMap<String, ArrayList<String>>();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tags);

        ctxListView = (ListView) findViewById(R.id.listView);
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

        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.list_layout_1, R.id.text1, values);

        ctxListView.setAdapter(adapter);


        tv9 = (TextView) findViewById(R.id.textView9);
        attrList = (LinearLayout) findViewById(R.id.linearLayout1);
        ok = (Button) findViewById(R.id.button9);
        done = (Button) findViewById(R.id.button10);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> listOfAttributes = new ArrayList<String>();


                for (int i=0; i<attrList.getChildCount(); i++) {
                    View view = attrList.getChildAt(i);
                    String value = "";

                    if (view instanceof TextView){
                        value = ((TextView) view).getText().toString();

                    }
                    if(view instanceof EditText){
                        value = ((EditText) view).getText().toString();

                    }

                    listOfAttributes.add(value);

                }

                tagList.put(tv9.getText().toString(), listOfAttributes);


                System.out.println(tagList.toString());



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

    public void showForm(View v){

        tv9.setText(((TextView)v).getText());
        ctxListView.setVisibility(View.INVISIBLE);
        done.setVisibility(View.INVISIBLE);
        attrList.setVisibility(View.VISIBLE);
        ok.setVisibility(View.VISIBLE);

        ArrayList<String> attList = new ArrayList<String>(map.get(((TextView)v).getText()));

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


}
