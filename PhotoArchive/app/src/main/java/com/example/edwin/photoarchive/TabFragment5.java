package com.example.edwin.photoarchive;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONObject;

public class TabFragment5 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_5, container, false);

        Button logOutButton = (Button)view.findViewById(R.id.button7);

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


        return  view;
    }
}