package com.example.edwin.photoarchive;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
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
import com.example.edwin.photoarchive.Adapters.AzureServiceAdapter;
import com.example.edwin.photoarchive.Adapters.ImageAdapterDashboard;
import com.example.edwin.photoarchive.AzureClasses.CategoryField;
import com.example.edwin.photoarchive.AzureClasses.Field;
import com.example.edwin.photoarchive.AzureClasses.Category;
import com.example.edwin.photoarchive.AzureClasses.AzureBlobUploader;
import com.example.edwin.photoarchive.AzureClasses.TaggedImageObject;
import com.example.edwin.photoarchive.Helpers.DeleteAfterXDays;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TabFragment1 extends Fragment {
    private android.content.Context context = null;
    private GridView imageGrid;
    private ArrayList<String> imgPathList;
    private SharedPreferences sharedPreferences;
    private Menu menu;
    private TextView photosToBeUploaded;
    private TextView permissionsStatus;
    private TextView tagsStatus;
    private GPSTracker gps;
    private BroadcastReceiver receiver;
    ProgressBar pb;

    private HashMap<Category, ArrayList<Field>> categoryFieldMap = new LinkedHashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getContext();
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        permissionsStatus = (TextView) view.findViewById(R.id.textView6);

        tagsStatus = (TextView) view.findViewById(R.id.textView5);

        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, android.content.Context.MODE_PRIVATE);
        pb = (ProgressBar) view.findViewById(R.id.progressBar);
        pb.getProgressDrawable().setColorFilter(Color.rgb(37, 126, 11), PorterDuff.Mode.SRC_IN);
        pb.setScaleY(3f);

        //show enable gps alert
        if (!sharedPreferences.contains("locationAlertShown")) {
            gps = new GPSTracker(context);

            if (!gps.isGPSEnabled) {
                gps.showSettingsAlert();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("locationAlertShown", true);
                editor.apply();
            }

        }
        Fragment histFragment = getFragmentManager().getFragments().get(3);

        //delete app images after x days, replace 2 with value from sharedprefs
        new DeleteAfterXDays(sharedPreferences.getInt("numDays", 90), getActivity(), histFragment);

        //IMPORTANT! PULL INFORMATION FROM THE DB
        pullAzureData();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(android.content.Context context, Intent intent) {
                if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);


                    if (netInfo.getState() == NetworkInfo.State.CONNECTED) {

                        permissionsStatus.invalidate();
                        permissionsStatus.setText("Required Permissions: all granted");

                        permissionsStatus.setTextColor(Color.BLACK);

                        Log.d("connectionStatus", "now connected");

                    }

                    if (netInfo.getState() == NetworkInfo.State.DISCONNECTED) {
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

        if (!isConnectedViaWifi()) {
            permissionsStatus.invalidate();
            permissionsStatus.setText("No wifi connection");
            permissionsStatus.setTextColor(Color.RED);
        } else {
            Log.d("connectionStatus", "connected to wifi");

        }

        imageGrid = (GridView) view.findViewById(R.id.gridview);
        photosToBeUploaded = (TextView) view.findViewById(R.id.textView20);
        imgPathList = new ArrayList<String>();

        if (sharedPreferences.contains("listOfImagesWithTags")) {
            String savedArraylist = sharedPreferences.getString("listOfImagesWithTags", null);
            Type listType = new TypeToken<ArrayList<TaggedImageObject>>() {
            }.getType();
            List<TaggedImageObject> taggedImageObjectsList = new Gson().fromJson(savedArraylist, listType);


            for (TaggedImageObject t : taggedImageObjectsList) {

                if (pb.getVisibility() == View.INVISIBLE) {
                    pb.setVisibility(View.VISIBLE);

                }
                imgPathList.add(t.getImgPath());

                new AzureBlobUploader(histFragment, this.getActivity(), t.getUser(), t).execute();
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
        if (v.getId() == R.id.gridview) {
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
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

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
            }
        });

        if (imgPathList.size() == 0) {
            hideOption(0);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.viewTags:

                Intent i = new Intent(getActivity(), ViewTags.class);
                i.putExtra("imagePath", imgPathList.get(info.position));

                startActivity(i);

                return true;
            case R.id.delete:

                deleteImage(imgPathList.get(info.position));
                return true;

            case R.id.viewInfo:
                Intent j = new Intent(getActivity(), ViewInfo.class);
                j.putExtra("imagePath", imgPathList.get(info.position));
                startActivity(j);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private boolean isConnectedViaWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
        }

        return false;
    }

    private void deleteImage(String s) {
        if (sharedPreferences.contains("listOfImagesWithTags")) {
            String savedArraylist = sharedPreferences.getString("listOfImagesWithTags", null);
            Type listType = new TypeToken<ArrayList<TaggedImageObject>>() {
            }.getType();
            List<TaggedImageObject> taggedImageObjectsList = new Gson().fromJson(savedArraylist, listType);

            for (TaggedImageObject t : taggedImageObjectsList) {
                if (t.getImgPath().equals(s)) {
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
    @SuppressLint("StaticFieldLeak")
    private void pullAzureData() {
        //initialize client connection
        //TODO Categories are loaded here
        MobileServiceClient mClient = AzureServiceAdapter.getInstance().getClient();

        //create table references
        final MobileServiceTable<Category> categoryTable = mClient.getTable("Category",Category.class);
        final MobileServiceTable<Field> fieldTable = mClient.getTable("Field",Field.class);
        final MobileServiceTable<CategoryField> catFieldTable = mClient.getTable("Context_Attribute",CategoryField.class);

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    //pull data into lists
                    final List<Category> categories = categoryTable.execute().get();
                    final List<Field> fields = fieldTable.execute().get();
                    //for(Field a: fields) Log.i("F1A", a.getFieldType() + " Q:" + a.getQuestion());
                    final List<CategoryField> catField = catFieldTable.execute().get();
                    final ArrayList<String> listOfCategory = new ArrayList<>();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (Category current : categories) { // Current Category
                                listOfCategory.add(current.getId()); // Add All Category IDs to listOfCategory

                                ArrayList<Field> currentFields = new ArrayList<>(); // Empty list of Fields for Current Category
                                if (!categoryFieldMap.containsKey(current)) { // If this Category is not in the Map already
                                    ArrayList<CategoryField> cfList = new ArrayList<>();
                                    for (CategoryField cf : catField) {  // Find all the CategoryField relations
                                        if (cf.getContextID().equals(current.getId())) cfList.add(cf); // Add all CategoryField relations
                                    }
                                    Collections.sort(cfList); // SORT all CategoryField relations based on SortNumber
                                    // This way, the Fields are SORTED by SortNumber
                                    for (int index = 0; index < cfList.size(); index++ ) { // Counting for loop just to be safe
                                        String fieldID = cfList.get(index).getAttributeID();
                                        for (Field a : fields) {
                                            if (a.getId().equals(fieldID)) {
                                                currentFields.add(a);
                                                break;
                                            }
                                        }
                                    }
                                }
                                //push the data into the map
                                categoryFieldMap.put(current, currentFields);
                            }

                            Collections.sort(listOfCategory);
                            Gson gson = new Gson();
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            if (sharedPreferences.contains("contexts")) {
                                editor.remove("contexts");
                                editor.apply();
                            }
                            editor.putString("contexts", gson.toJson(listOfCategory.toArray(new String[listOfCategory.size()])));
                            editor.apply();

                            tagsStatus.setTextColor(Color.GREEN);
                            tagsStatus.setText("Tags status: up to date!");

                            //store contextsAndAttributes into extras
                            getActivity().getIntent().putExtra("azure", categoryFieldMap);
                            Fragment tabFrag4 = null;
                            for (Fragment frag : getFragmentManager().getFragments()) {
                                if (frag instanceof TabFragment4) {
                                    tabFrag4 = frag;
                                }
                            }
                            try {
                                tabFrag4.getFragmentManager().beginTransaction().detach(tabFrag4).attach(tabFrag4).commit();
                            } catch (Exception e) {
                            }

                            Fragment tabFrag2 = null;
                            for (Fragment frag : getFragmentManager().getFragments()) {
                                if (frag instanceof TabFragment2) {
                                    tabFrag2 = frag;
                                }
                            }
                            try {
                                tabFrag2.getFragmentManager().beginTransaction().detach(tabFrag2).attach(tabFrag2).commit();
                            } catch (Exception e) {
                            }
                        }
                    });
                } catch (Exception exception) {
                    Log.d("Azure", "Field error! \n");
                    exception.printStackTrace();
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }
}

