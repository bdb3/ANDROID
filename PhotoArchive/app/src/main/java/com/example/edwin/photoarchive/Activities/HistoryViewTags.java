package com.example.edwin.photoarchive.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.photoarchive.R;


public class HistoryViewTags extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view_tags);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tags");

        Bundle extras = getIntent().getExtras();
        final LinearLayout linearLayoutTags = (LinearLayout) findViewById(R.id.linearLayoutHistoryTags);

      for(int i=0;i<3;i++){
            Button b = new Button(this);
            b.setText("Context ");
            b.setClickable(false);
            linearLayoutTags.addView(b);

            TextView q = new TextView(this);
            q.setTextColor(Color.BLACK);
            q.setText("Attribute");
            linearLayoutTags.addView(q);

            EditText a = new EditText(this);
            a.setText("Text");
            a.setEnabled(false);
            linearLayoutTags.addView(a);
        }
    }

}


