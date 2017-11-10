package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.edwin.photoarchive.Activities.MainActivity;
import com.example.edwin.photoarchive.Activities.TagsActivity;

public class TabFragment5 extends Fragment {
    private GPSTracker gps;
    private String username;
    private SharedPreferences sharedPreferences;
    private String android_id;

    public String getDays(Intent i) {
        return i.getExtras().getString("numDays", "90");
    }

    public String getDays() {
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        return Integer.toString(sharedPreferences.getInt("numDays", 90));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.tab_fragment_5, container, false);

        /** BEG DECLARE VIEW OBJECTS */

        Button logOutButton = (Button) view.findViewById(R.id.button7);
        Switch swLocation = (Switch) view.findViewById(R.id.switch2);
        // DELETE DAYS SPINNER defined here -ph
        Spinner delDaySpinner = (Spinner) view.findViewById(R.id.del_img_day_count);
        TextView androidIdView = (TextView) view.findViewById(R.id.settings_android_id_view);
        TextView androidUserView = (TextView) view.findViewById(R.id.settings_android_user_view);

        /** END DECLARE VIEW OBJECTS */


        /** BEG GET SHAREDPREFERENCES DATA */

        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        // Get ANDROID_ID
        android_id = sharedPreferences.getString("androidID",null);
        // Get Username
        username = sharedPreferences.getString("loggedInUser", null);

        /** END GET SHAREDPREFERENCES DATA */


        /** BEG VIEW CONTENT CODE */

        // ANDROID_ID Text View
        androidIdView.setText(android_id);

        // Username Text View
        androidUserView.setText(username);

        // Delete Days Spinner
        //     String Resource Array to create Simple Spinner -ph
        ArrayAdapter<CharSequence> delDayAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.del_img_day_array, android.R.layout.simple_spinner_item);
        delDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delDaySpinner.setAdapter(delDayAdapter);
        //     Check previous value in numDays
        int daysNum = sharedPreferences.getInt("numDays",90);
        if(daysNum < 0) {
            delDaySpinner.setSelection(0);
        } else {
            // Find it programmatically
            int delDayIndex = 1;
            for(int i = 1; i < delDaySpinner.getCount(); i++)
                if(delDaySpinner.getItemAtPosition(i).equals("" + daysNum + " days"))
                    delDayIndex = i;
            delDaySpinner.setSelection(delDayIndex);
        }

        // GPS Switch state
        gps = new GPSTracker(getContext());
        if (gps.isGPSEnabled) swLocation.setChecked(true);
        else swLocation.setChecked(false);

        /** END VIEW CONTENT CODE */


        /** BEG EVENTLISTENERS */

        // Username Change Button FORMERLY Logout Button
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new AlertDialog.Builder(getActivity())
                .setTitle("Username Change Confirmation")
                .setMessage("Are you sure you want to change your username?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("loggedInUser");
                        editor.apply();

                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);

                        Toast.makeText(getContext(), "Your username has been reset.", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
            }
        });

        // DONE: ADD SPINNER LOGIC TO SET DAYS IN SHARED PREFERENCES -ph
        // Number of Days before auto-deleting inApp images Spinner
        delDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                String daysStr = adapterView.getItemAtPosition(pos).toString();
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Using string parsing so that String literals in string.xml
                // define the days and they are NOT hardcoded by position

                Log.d("numDays",daysStr);

                if(daysStr.equalsIgnoreCase("never")){
                    if(sharedPreferences.contains("numDays")){
                        editor.remove("numDays");
                        editor.apply();
                    }
                    editor.putInt("numDays", -1);
                    editor.apply();
                    Log.d("numDays","SHRD:-1 (NEVER)");
                    return;
                }

                int daysNum;
                try{
                    daysNum = Integer.parseInt(daysStr.split(" ")[0]);
                } catch (Exception e) {return;}

                if(sharedPreferences.contains("numDays")){
                    editor.remove("numDays");
                    editor.apply();
                }
                editor.putInt("numDays", daysNum);
                editor.apply();
                Log.d("numDays", "SHRD:" + sharedPreferences.getInt("numDays",-10));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });

        /** END EVENT LISTENERS */


        return view;
    }
}