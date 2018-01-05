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

import com.example.edwin.photoarchive.Adapters.AzureServiceAdapter;
import com.example.edwin.photoarchive.R;

import java.util.List;
import android.provider.Settings.Secure;
import android.provider.Settings;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        // Use ANDROID_ID as unique identifier
        android_id = Settings.System.getString(this.getContentResolver(), Secure.ANDROID_ID);

        // DONE INITIALIZE AZURE SERVICES
        AzureServiceAdapter.Initialize(this);
        // CODE TO READ URLS WITH PARAMETERS ////////////////////////////
        // URI Intent With Parameters
        try {
            Uri data = getIntent().getData();
            List<String> params = data.getPathSegments();
            for(String s:params) {
                Log.d("URI Passing", s);
            }

            SharedPreferences.Editor editor = sharedPreferences.edit();

            String username = params.get(0).trim();
            String repairID = params.get(2);
            if(params.get(1).equals("repairtask")){
                editor.putString("repairTaskID",repairID);
            }
            // TODO Potential Security Risk

            editor.putString("loggedInUser", username);
            editor.apply();

            String misc = params.get(2);
        } catch(Exception e) {}
        // CODE TO READ URLS WITH PARAMETERS ////////////////////////////

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(sharedPreferences.contains("loggedInUser") && sharedPreferences.contains("androidID")) {
            Intent i= new Intent(MainActivity.this, Activity2.class);
            startActivity(i);
            finish();
        }

        // Display ANDROID_ID on 'login'
        TextView androidIdBox = (TextView) findViewById(R.id.android_id_box);
        androidIdBox.setText(android_id);

        // STORE ANDROID_ID into sharedPreferences
        // TODO CONSIDER Change storage of username and ANDROID_ID
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("androidID",android_id);
        editor.apply();
    }

    public void signIn(View v){
        EditText username = (EditText) findViewById(R.id.editText);

        if(username.getText().toString().trim().length() > 3){
            if(!sharedPreferences.contains("loggedInUser")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("loggedInUser",username.getText().toString().trim());
                editor.apply();

                System.out.println(sharedPreferences.getString("loggedInUser",null));
                Intent i = new Intent(MainActivity.this, Activity2.class);
                startActivity(i);
                finish();
            }
        }else{
            Toast.makeText(getApplicationContext(), "Username must be at least 4 characters long", Toast.LENGTH_SHORT).show();
        }
    }
}
