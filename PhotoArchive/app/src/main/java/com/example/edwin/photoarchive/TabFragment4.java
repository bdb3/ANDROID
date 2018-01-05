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
import com.example.edwin.photoarchive.AzureClasses.AzureBlobDownloader;
import com.google.gson.Gson;

public class TabFragment4 extends Fragment {
    private Context context = null;
    private String username;
    private SharedPreferences sharedPreferences;
    private Spinner categorySearchSpinner;
    private String filter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);

        /** BEG DECLARE VIEW OBJECTS */

        categorySearchSpinner = (Spinner) view.findViewById(R.id.categorySpinner);

        /** END DECLARE VIEW OBJECTS */


        /** BEG GET SHAREDPREFERENCES DATA */

        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        String[] contextsArray = null;
        Gson gson = new Gson();
        try{
            contextsArray = gson.fromJson(sharedPreferences.getString("contexts", null), String[].class);
        } catch (Exception e) { Log.d("TabFragment2","CRITICAL ERROR! JSON PARSE EXCEPTION"); }
        username = sharedPreferences.getString("loggedInUser", null);

        /** END GET SHAREDPREFERENCES DATA */


        /** BEG VIEW CONTENT CODE */

        if(contextsArray != null) {
            ArrayAdapter<String> categorySearchAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_item, contextsArray);
            categorySearchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySearchSpinner.setAdapter(categorySearchAdapter);
        }

        /** END VIEW CONTENT CODE */


        /** BEG EVENTLISTENERS */

        categorySearchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filter = categorySearchSpinner.getItemAtPosition(i).toString();
                /* ASYNC */
                new AzureBlobDownloader(getActivity(), username, filter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do Nothing
            }
        });

        /** END EVENT LISTENERS */


        /* ASYNC */
        // Must be Serial or else it hangs!
        new AzureBlobDownloader(this.getActivity(), username, filter).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);

        return view;
    }
}