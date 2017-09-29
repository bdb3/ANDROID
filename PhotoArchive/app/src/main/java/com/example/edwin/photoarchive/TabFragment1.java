package com.example.edwin.photoarchive;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.photoarchive.Activities.TagsActivity;
import com.example.edwin.photoarchive.Activities.ViewInfo;
import com.example.edwin.photoarchive.Activities.ViewTags;
import com.example.edwin.photoarchive.Adapters.ImageAdapterDashboard;
import com.example.edwin.photoarchive.AzureClasses.Attribute;
import com.example.edwin.photoarchive.AzureClasses.AzureBlobUploader;
import com.example.edwin.photoarchive.AzureClasses.Context_Attribute;
import com.example.edwin.photoarchive.AzureClasses.TaggedImageObject;
import com.example.edwin.photoarchive.Helpers.DeleteAfterXDays;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import java.net.MalformedURLException;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;


public class TabFragment1 extends Fragment {
    private Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;

    private ArrayList<String> pathSet = null;
    private SharedPreferences sharedPreferences;
    private Menu menu;
    private TextView photosToBeUploaded;
    private TextView permissionsStatus;

    private  TextView tagsStatus;
    private GPSTracker gps;
    private BroadcastReceiver receiver;
    ProgressBar pb;

    private HashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>> contextsAndAttributes = new LinkedHashMap<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context= this.getContext();
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

         permissionsStatus = (TextView) view.findViewById(R.id.textView6);

         tagsStatus = (TextView) view.findViewById(R.id.textView5);

        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        pb = (ProgressBar)view.findViewById(R.id.progressBar);
        pb.getProgressDrawable().setColorFilter(Color.rgb(37,126,11), PorterDuff.Mode.SRC_IN);
        pb.setScaleY(3f);

        //show enable gps alert

        if(!sharedPreferences.contains("locationAlertShown")) {
            gps = new GPSTracker(context);

            if (!gps.isGPSEnabled) {
                gps.showSettingsAlert();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("locationAlertShown", true);
                editor.commit();

            }

        }
        Fragment histFragment = getFragmentManager().getFragments().get(3);

        //delete app images after x days, replace 2 with value from sharedprefs
        new DeleteAfterXDays(sharedPreferences.getInt("numDays", 90), getActivity(), histFragment);

        //IMPORTANT! PULL INFORMATION FROM THE DB
        pullContextsAndAttributes();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                    NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);


                    if(netInfo.getState() == NetworkInfo.State.CONNECTED) {

                        permissionsStatus.invalidate();
                        permissionsStatus.setText("Required Permissions: all granted \nRepair Task ID:"+sharedPreferences.getString("repairTaskID",null));

                        permissionsStatus.setTextColor(Color.BLACK);

                        Log.d("connectionStatus", "now connected");

                    }

                    if(netInfo.getState() == NetworkInfo.State.DISCONNECTED){
                        permissionsStatus.invalidate();

                        permissionsStatus.setText("No wifi connection");
                        permissionsStatus.setTextColor(Color.RED);

                        Log.d("connectionStatus", "lost wifi connection");

                    }

                }

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(receiver, intentFilter);

        if(!isConnectedViaWifi()) {
            permissionsStatus.invalidate();
            permissionsStatus.setText("No wifi connection");
            permissionsStatus.setTextColor(Color.RED);
        }
        else{
            Log.d("connectionStatus", "connected to wifi");

        }


        imageGrid = (GridView) view.findViewById(R.id.gridview);
        photosToBeUploaded = (TextView) view.findViewById(R.id.textView20);
        imgPathList = new ArrayList<String>();

        if(sharedPreferences.contains("listOfImagesWithTags")) {
            String savedArraylist  = sharedPreferences.getString("listOfImagesWithTags", null);
            Type listType = new TypeToken<ArrayList<TaggedImageObject>>(){}.getType();
            List<TaggedImageObject> taggedImageObjectsList = new Gson().fromJson(savedArraylist, listType);


            for(TaggedImageObject t: taggedImageObjectsList){
                if(pb.getVisibility() == View.INVISIBLE){
                    pb.setVisibility(View.VISIBLE);

                }
                imgPathList.add(t.getImgPath());


                new AzureBlobUploader(histFragment,this.getActivity(), t.getUser(), t).execute();
            }
        }
        photosToBeUploaded.invalidate();
        photosToBeUploaded.setText(imgPathList.size() + " image(s) waiting to upload");


        imageGrid.setAdapter(new ImageAdapterDashboard(context, imgPathList));

        registerForContextMenu(imageGrid);
        imageGrid.setOnCreateContextMenuListener(this);


        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.gridview) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_list, menu);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        menu.add(Menu.NONE, 0, Menu.NONE, "Clear Queue")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        MenuItem item = menu.findItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //clear queue

                new AlertDialog.Builder(context)
                        .setTitle("Clear confirmation")
                        .setMessage("Are you sure you want to clear the upload queue?")
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override public void onClick(DialogInterface dialog, int which) {

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("listOfImagesWithTags");
                                editor.apply();
                                hideOption(0);
                                imgPathList.clear();

                                getActivity().recreate();
                                ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
                                viewPager.setCurrentItem(0);

                                Toast.makeText(context, "Queue successfully cleared", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .create()
                        .show();



                return true;
            }});



        if(imgPathList.size() == 0){
            hideOption(0);


        }


        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }


    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.viewTags:

                Intent i= new Intent(getActivity(), ViewTags.class);
                i.putExtra("imagePath", imgPathList.get(info.position));

                startActivity(i);

                return true;
            case R.id.delete:

                deleteImage(imgPathList.get(info.position));
                return true;

            case R.id.viewInfo:
                Intent j= new Intent(getActivity(), ViewInfo.class);
                j.putExtra("imagePath", imgPathList.get(info.position));
                startActivity(j);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private boolean isConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if(info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }


        return false;
    }

 private void deleteImage(String s){

     if(sharedPreferences.contains("listOfImagesWithTags")) {
         String savedArraylist  = sharedPreferences.getString("listOfImagesWithTags", null);
         Type listType = new TypeToken<ArrayList<TaggedImageObject>>(){}.getType();
         List<TaggedImageObject> taggedImageObjectsList = new Gson().fromJson(savedArraylist, listType);

         for(TaggedImageObject t: taggedImageObjectsList){
             if (t.getImgPath().equals(s)){
                 taggedImageObjectsList.remove(t);
                 break;
             }

         }

             SharedPreferences.Editor editor = sharedPreferences.edit();
             editor.remove("listOfImagesWithTags");
             editor.apply();
             editor.putString("listOfImagesWithTags", new Gson().toJson(taggedImageObjectsList));
             editor.apply();



         getActivity().recreate();
         ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
         viewPager.setCurrentItem(0);

     }

 }

    //PULL DATA FROM DB
    private void pullContextsAndAttributes(){
        try {
            //initialize client connection
            //TODO Contexts are loaded here
            MobileServiceClient mClient = new MobileServiceClient(
                    "http://boephotoarchive-dev.azurewebsites.net",
                    context);

            //create table references
            final MobileServiceTable<com.example.edwin.photoarchive.AzureClasses.Context> contextTable = mClient.getTable(com.example.edwin.photoarchive.AzureClasses.Context.class);
            final MobileServiceTable<Attribute> attributeTable = mClient.getTable(Attribute.class);
            final MobileServiceTable<Context_Attribute> caTable = mClient.getTable(Context_Attribute.class);

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    try {

                        //pull data into lists
                        final List<com.example.edwin.photoarchive.AzureClasses.Context> contexts = contextTable.execute().get();
                        final List<Attribute> attributes = attributeTable.execute().get();
                        final List<Context_Attribute> context_attributes = caTable.execute().get();

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for(com.example.edwin.photoarchive.AzureClasses.Context current : contexts){

                                    //make a new list of possible attributes
                                    ArrayList<Attribute> currentAttributes = new ArrayList<>();

                                    if(!contextsAndAttributes.containsKey(current)){

                                        //generate a list of all of its attributes
                                        for(Context_Attribute ca : context_attributes){


                                            if(ca.getContextID().equals(current.getId())){

                                                //get attribute
                                                String attributeID = ca.getAttributeID();

                                                for(Attribute a : attributes){

                                                    if(a.getId().equals(attributeID)){

                                                        currentAttributes.add(a);

                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    Collections.sort(currentAttributes);
                                    //push the data into the map
                                    contextsAndAttributes.put(current, currentAttributes);
                                }


                                tagsStatus.setTextColor(Color.GREEN);
                                tagsStatus.setText("Tags status: up to date!");

                                //store contextsAndAttributes into extras
                                getActivity().getIntent().putExtra("azure", contextsAndAttributes);
                            }
                        });
                    } catch (Exception exception) {
                        Log.d("Azure", "Attribute error!");
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        }catch (MalformedURLException m) {
            Log.d("Azure", "Error! Invalid URL");
        }
    }




}

