package com.example.edwin.photoarchive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signIn(View v){
       Intent i= new Intent(MainActivity.this, Activity2.class);
        //EditText et =(EditText)findViewById(R.id.editText);
        //i.putExtra("userName", et.getText().toString());
        startActivity(i);






    }
}
