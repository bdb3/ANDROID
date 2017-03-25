package com.example.edwin.photoarchive.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.edwin.photoarchive.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void signIn(View v){
       Intent i= new Intent(MainActivity.this, Activity2.class);
        startActivity(i);
        finish();

    }
}
