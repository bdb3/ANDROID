package com.example.edwin.photoarchive;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.edwin.photoarchive.Activities.TagsActivity;
import com.example.edwin.photoarchive.Adapters.AzureServiceAdapter;
import com.example.edwin.photoarchive.AzureClasses.AzureBlobDownloader;
import com.google.gson.Gson;

public class TabFragment4 extends Fragment {
    private Context context = null;
    private String username;
    private SharedPreferences sharedPreferences;
    private Spinner categorySpinner;
    private ArrayAdapter<String> adapter;
    private String filter;
    private String[] contextsArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        categorySpinner = (Spinner) view.findViewById(R.id.categorySpinner);
        adapter = null;
        filter = "";
        Gson gson = new Gson();
        contextsArray = null;
        try {
            contextsArray = gson.fromJson(sharedPreferences.getString("contexts", null), String[].class);
            adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, contextsArray);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(adapter);
        } catch (Exception e) {

        }

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filter = categorySpinner.getItemAtPosition(i).toString();
                new AzureBlobDownloader(getActivity(), username, filter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        if (sharedPreferences.contains("loggedInUser")) {
            username = sharedPreferences.getString("loggedInUser", null);

        }

        //Must be Serial or else it hangs!
        new AzureBlobDownloader(this.getActivity(), username, filter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);


        return view;
    }


}