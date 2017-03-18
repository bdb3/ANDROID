package com.example.edwin.photoarchive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;

import java.util.Iterator;


 public class TabFragment1 extends Fragment {
    private Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;

    private ArrayList<String> pathSet = null;
    private SharedPreferences sharedPreferences;
    private Menu menu;
    private TextView photosToBeUploaded;
    private TextView permissionsStatus;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context= this.getContext();
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

         permissionsStatus = (TextView) view.findViewById(R.id.textView6);
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                    NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if(netInfo.isConnected()) {
                        permissionsStatus.invalidate();
                        permissionsStatus.setText("Required Permissions: all granted");
                        permissionsStatus.setTextColor(Color.GREEN);

                        if(Activity2.getInstance()!=null)
                            Activity2.getInstance().updateUI();
                    }

                    if(netInfo.getState() == NetworkInfo.State.DISCONNECTED){
                        permissionsStatus.invalidate();
                        permissionsStatus.setText("No wifi connection");
                        permissionsStatus.setTextColor(Color.RED);

                        if(imgPathList.size()>0)
                            Toast.makeText(context, "Please connect to Wifi. "+ imgPathList.size() + " image(s) waiting to upload.", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(receiver, intentFilter);

        if(!isConnectedViaWifi()) {
            permissionsStatus.invalidate();
            permissionsStatus.setText("No wifi connection");
            permissionsStatus.setTextColor(Color.RED);


        }


        imageGrid = (GridView) view.findViewById(R.id.gridview);
        photosToBeUploaded = (TextView) view.findViewById(R.id.textView20);
        imgPathList = new ArrayList<String>();

        if(sharedPreferences.contains("listOfImagesWithTags")) {
            String mapString = sharedPreferences.getString("listOfImagesWithTags", null);

            try {
                JSONObject jsonObject2 = new JSONObject(mapString);
                Iterator<String> keysItr = jsonObject2.keys();

                while(keysItr.hasNext()) {

                    imgPathList.add(keysItr.next());
                }


            }catch(Exception e){
                e.printStackTrace();

            };


        }
        photosToBeUploaded.invalidate();
        photosToBeUploaded.setText(imgPathList.size() + " images waiting to upload");


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
               // i.putExtra("selectedImages", imgPathSet);

                startActivity(i);

                return true;
            case R.id.delete:
                //
                return true;
            case R.id.viewInfo:
                //
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

    public void updateStatus(String s){
        permissionsStatus.invalidate();
        permissionsStatus.setText(s);

    }



}

