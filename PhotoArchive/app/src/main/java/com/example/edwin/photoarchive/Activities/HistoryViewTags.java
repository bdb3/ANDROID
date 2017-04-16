package com.example.edwin.photoarchive.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

        final LinearLayout linearLayoutTags = (LinearLayout) findViewById(R.id.linearLayoutHistoryTags);

        for (int i=0; i<3; i++){
            Button b = new Button(this);
            b.setText("Context " +i);
            b.setClickable(false);
            linearLayoutTags.addView(b);

            TextView q = new TextView(this);
            q.setTextColor(Color.BLACK);
            q.setText("question");
            linearLayoutTags.addView(q);

            EditText a = new EditText(this);
            a.setText("answer");
            a.setEnabled(false);
            linearLayoutTags.addView(a);


        }


    }

}
