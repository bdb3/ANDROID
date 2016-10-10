package com.example.edwin.photoarchive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Activity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        TextView name=(TextView)findViewById(R.id.textView4);
        //name.setText("Hello "+getIntent().getExtras().getString("userName") );

    }
}
