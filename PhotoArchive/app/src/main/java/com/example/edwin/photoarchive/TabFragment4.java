package com.example.edwin.photoarchive;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.os.AsyncTaskCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.example.edwin.photoarchive.Adapters.ImageAdapterHistory;
import com.example.edwin.photoarchive.AzureClasses.AzureBlobDownloader;

import java.io.File;
import java.util.ArrayList;

public class TabFragment4 extends Fragment {
    private Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context= this.getContext();
        View view = inflater.inflate(R.layout.tab_fragment_4, container, false);

        /*Hard coded for now, same with upload*/
        String userName = "user";

        //Must be Serial or else it hangs!
        new AzureBlobDownloader(this.getActivity(), userName).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);


        return view;
    }


}