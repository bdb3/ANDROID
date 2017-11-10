package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.photoarchive.Activities.MainActivity;
import com.example.edwin.photoarchive.Activities.TagsActivity;


public class TabFragment5 extends Fragment {
    private GPSTracker gps;
    private Menu menu;
    private String username;
    private SharedPreferences sharedPreferences;
    private String android_id;

    public String getDays(Intent i) {
        String str;
        if (i.getExtras() == null) {
            return str = "90";
        } else {

            return str = i.getExtras().getString("numDays");
        }
    }

    public String getDays() {
        String str;
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("numDays")) {
            return str = "90";
        } else {
            return str = Integer.toString(sharedPreferences.getInt("numDays", 90));
        }
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
        if (sharedPreferences.contains("loggedInUser"))
            username = sharedPreferences.getString("loggedInUser", null);

        /** END GET SHAREDPREFERENCES DATA */


        /** BEG VIEW CONTENT CODE */

        // ANDROID_ID Text View
        androidIdView.setText(android_id);

        // Username Text View
        androidUserView.setText(username);

        // Delete Days Spinner
        ArrayAdapter<CharSequence> delDayAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.del_img_day_array, android.R.layout.simple_spinner_item);
        delDayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delDaySpinner.setAdapter(delDayAdapter);

        // GPS Switch state
        gps = new GPSTracker(getContext());
        if (gps.isGPSEnabled) swLocation.setChecked(true);
        else swLocation.setChecked(false);

        /** END VIEW CONTENT CODE */


        /** BEG EVENTLISTENERS */

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new AlertDialog.Builder(getActivity())
                .setTitle("Log out confirmation")
                .setMessage("Are you sure you want to log out?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("loggedInUser");
                        editor.apply();

                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);

                        Toast.makeText(getContext(), "You have been logged out", Toast.LENGTH_SHORT).show();

                    }
                })
                .create()
                .show();
            }
        });

        // String Resource Array to create Simple Spinner -ph

        // TODO: ADD SPINNER LOGIC TO SET DAYS IN SHARED PREFERENCES -ph

        /*  Remove old edit button and methods -ph
        // Button editDays = (Button) view.findViewById(R.id.button8);
        editDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final NumberPicker picker = new NumberPicker(getActivity());
                picker.setMinValue(1);
                picker.setMaxValue(90);
                FrameLayout layout = new FrameLayout(getActivity());

                layout.addView(picker, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER));

                new AlertDialog.Builder(getActivity())
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                if (!sharedPreferences.contains("numDays")) {
                                    editor.putInt("numDays", picker.getValue());
                                    editor.apply();

                                } else {
                                    editor.remove("numDays");
                                    editor.apply();
                                    editor.putInt("numDays", picker.getValue());
                                    editor.apply();

                                }

                                getFragmentManager().beginTransaction().detach(getFragmentManager().getFragments().get(4)).attach(getFragmentManager().getFragments().get(4)).commitAllowingStateLoss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        }); */

        /** END EVENT LISTENERS */


        return view;
    }

    /* User Menu will become superfluous or can be re-added to `APP SETTINGS` fragment
       at a later date. -ph
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;

        menu.add(Menu.NONE, 0, Menu.NONE, "")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.findItem(0).setIcon(R.drawable.ic_user).setEnabled(false);
        menu.findItem(0).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);

        menu.add(Menu.NONE, 1, Menu.NONE, username + "   ")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.findItem(1).setEnabled(false);


        super.onCreateOptionsMenu(menu, inflater);
    }
    */
}