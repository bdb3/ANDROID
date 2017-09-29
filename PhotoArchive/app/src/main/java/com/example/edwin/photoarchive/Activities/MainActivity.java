package com.example.edwin.photoarchive.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.photoarchive.GPSTracker;
import com.example.edwin.photoarchive.R;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

        // URI Intent With Parameters
        try {
            Uri data = getIntent().getData();
            List<String> params = data.getPathSegments();
            for(String s:params) {
                Log.d("URI Passing", s);
            }
            String user = params.get(0);
            String repairTaskID=params.get(1);
            // TODO Potential Security Risk
            SharedPreferences.Editor editor =sharedPreferences.edit();
            editor.putString("loggedInUser",user.trim());
            editor.putString("repairTaskID",repairTaskID);
            editor.commit();

            String misc=params.get(2);

        }
        catch(Exception e){}
        if(sharedPreferences.contains("loggedInUser")) {
            Intent i= new Intent(MainActivity.this, Activity2.class);
            startActivity(i);
            finish();

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void signIn(View v){
        EditText username =  (EditText) findViewById(R.id.editText);

        if(username.getText().toString().trim().length()>3){

            if(!sharedPreferences.contains("loggedInUser")) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("loggedInUser",username.getText().toString().trim());
                    editor.commit();

                System.out.println(sharedPreferences.getString("loggedInUser",null));
                Intent i= new Intent(MainActivity.this, Activity2.class);
                startActivity(i);
                finish();

            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Username must be at least 4 characters long", Toast.LENGTH_SHORT).show();
        }

    }
}
