package com.example.edwin.photoarchive;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.photoarchive.Activities.MainActivity;


public class TabFragment5 extends Fragment {
    private GPSTracker gps;

    public String getDays(Intent i){
        String str;
        if(i.getExtras() == null){
            return str = "90";
        }
        else{

            return str = i.getExtras().getString("numDays");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_fragment_5, container, false);




        Button logOutButton = (Button)view.findViewById(R.id.button7);

        TextView deleteDays = (TextView)view.findViewById(R.id.textView8);
        deleteDays.setText("Delete after " + getDays(getActivity().getIntent()) + " " + "days");





        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Log out confirmation")
                        .setMessage("Are you sure you want to log out?")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {

                                Intent i= new Intent(getActivity(), MainActivity.class);
                                startActivity(i);

                                Toast.makeText(getContext(), "You have been logged out", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .create()
                        .show();

            }
        });

        Button editDays = (Button)view.findViewById(R.id.button8);


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

                                getActivity().getIntent().putExtra("numDays", Integer.toString(picker.getValue()));

                                System.out.println(getDays(getActivity().getIntent()));
                                getFragmentManager().beginTransaction().detach(getFragmentManager().getFragments().get(4)).attach(getFragmentManager().getFragments().get(4)).commitAllowingStateLoss();

                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });

        Switch swLocation = (Switch) view.findViewById(R.id.switch2);

        gps= new GPSTracker(getContext());


        if(gps.isGPSEnabled){
            swLocation.setChecked(true);
        }
        else{
            swLocation.setChecked(false);

        }

        return  view;
    }
}