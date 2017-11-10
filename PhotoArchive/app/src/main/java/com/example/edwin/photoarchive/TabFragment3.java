package com.example.edwin.photoarchive;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edwin.photoarchive.Activities.GalleryViewAllActivity;
import com.example.edwin.photoarchive.Activities.TagsActivity;
import com.example.edwin.photoarchive.AzureClasses.Attribute;
import com.example.edwin.photoarchive.AzureClasses.TaggedImageObject;
import com.example.edwin.photoarchive.Helpers.ExtractLatLong;
import com.example.edwin.photoarchive.Helpers.GPS;
import com.example.edwin.photoarchive.Helpers.PopulateAppImages;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;


public class TabFragment3 extends Fragment {
    private String imageFileLocation = "";
    private static final int ACTIVITY_START_CAMERA_APP = 1;
    private Context context = null;
    GPSTracker gps;
    private Button button = null;
    private HashSet<String> imgPathSet = new LinkedHashSet<String>();
    private SharedPreferences sharedPreferences;
    private LinearLayout inAppPictures;

    //remove for now -ph [tags]
//    private LinearLayout tagsContainer;
//    private Button clearTags;

    private ArrayList<TaggedImageObject> taggedImagesList = new ArrayList<TaggedImageObject>();
    private String username;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_3, container, false);
        context = this.getContext();
        sharedPreferences = getActivity().getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);


        // TODO: ORGANIZE CODE CAUSE IT'S A MESS. GROUP STUFF TOGETHER AS MUCH AS POSSIBLE -PH
        // TODO: GET IMAGES SCROLLER WORKING AND REMOVE ALL TAG FUNCTIONS -PH
        // TODO: ADD TAG CHECK TO xml -PH

        // Copied old function from Tab2 and modified
        // This opens Android Gallery in a new Activity
        Button androidGalleryButton = (Button) view.findViewById(R.id.gallery_button);
        androidGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GalleryViewAllActivity.class);
                i.putExtra("selectedImages", imgPathSet);
                startActivity(i);
            }
        });

        if (sharedPreferences.contains("loggedInUser")) {
            username = sharedPreferences.getString("loggedInUser", null);
        }

        //camera btn
        button = (Button) view.findViewById(R.id.button);
        final TextView taken = (TextView) view.findViewById(R.id.textViewTaken);
        final Button clearTaken = (Button) view.findViewById(R.id.clearTaken);

        inAppPictures = (LinearLayout) view.findViewById(R.id.inapp_picture_container);
        new PopulateAppImages(inAppPictures, context, getActivity());

        // remove this for now -ph  [tags]
        //Button addTags = (Button) view.findViewById(R.id.buttonAddTags);
        //clearTags = (Button) view.findViewById(R.id.buttonClearTags);


        final Button uploadBtn = (Button) view.findViewById(R.id.buttonUpload);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (hasCamera()) {
                    launchCamera(null);
                }

            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get tags from shared preferences

                uploadBtn.setEnabled(false);

                String mapString = sharedPreferences.getString("cameraTags", null);

                Map<String, Map<String, String>> outputMap = new LinkedHashMap<String, Map<String, String>>();
                try {
                    JSONObject jsonObject2 = new JSONObject(mapString);
                    Iterator<String> keysItr = jsonObject2.keys();

                    while (keysItr.hasNext()) {
                        String key = keysItr.next();

                        Map<String, String> valueMap = new LinkedHashMap<String, String>();
                        Iterator<String> keysItr2 = ((JSONObject) jsonObject2.get(key)).keys();

                        while (keysItr2.hasNext()) {
                            String key2 = keysItr2.next();
                            String value = (String) ((JSONObject) jsonObject2.get(key)).get(key2);

                            valueMap.put(key2, value);
                        }

                        outputMap.put(key, valueMap);
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }
                ;

                for (String s : imgPathSet) {

                    ExtractLatLong ell = new ExtractLatLong(s);
                    TaggedImageObject tagImgObj = new TaggedImageObject(s, ell.getLat(), ell.getLon(), username, outputMap);
                    taggedImagesList.add(tagImgObj);

                }


                // put imagesTagsMap in shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();


                Gson gson = new Gson();

                if (!sharedPreferences.contains("listOfImagesWithTags")) {

                    String taggedImageslistAsString = gson.toJson(taggedImagesList);
                    editor.putString("listOfImagesWithTags", taggedImageslistAsString);
                    editor.apply();
                    clearTaken.performClick();
                    clearTags2();
                    refreshDash();
                    Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();

                } else {
                    String savedArraylist = sharedPreferences.getString("listOfImagesWithTags", null);
                    Type type = new TypeToken<ArrayList<TaggedImageObject>>() {
                    }.getType();
                    ArrayList<TaggedImageObject> taggedImageObjectsList = gson.fromJson(savedArraylist, type);


                    for (String s : imgPathSet) {
                        ExtractLatLong ell = new ExtractLatLong(s);
                        TaggedImageObject tagImgObj = new TaggedImageObject(s, ell.getLat(), ell.getLon(), username, outputMap);
                        taggedImageObjectsList.add(tagImgObj);

                    }
                    String taggedImageslistAsString = gson.toJson(taggedImageObjectsList);
                    editor.remove("listOfImagesWithTags");
                    editor.apply();
                    editor.putString("listOfImagesWithTags", taggedImageslistAsString);
                    editor.apply();
                    clearTaken.performClick();
                    clearTags2();
                    refreshDash();
                    Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();


                }


                //END OF UPLOAD CODE
            }
        });

        /* remove for now -ph [tags]
        clearTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadBtn.setEnabled(false);
                clearTags();

            }
        });
        */

        clearTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPathSet.clear();
                taken.invalidate();
                taken.setText("Taken: " + imgPathSet.size());
                clearTaken.setEnabled(false);
                uploadBtn.setEnabled(false);
                button.setText("Take Picture");

            }
        });

        Bundle extras = getActivity().getIntent().getExtras();

        if (extras != null) {
            if (extras.containsKey("cameraImages")) {
                HashSet<String> cameraPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("cameraImages"));
                for (String s : cameraPathSet) {
                    imgPathSet.add(s);
                }
            }
        }

        taken.setText("Taken: " + imgPathSet.size());

        /* Remove this for now -ph [tags]
        addTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent i = new Intent(getActivity(), TagsActivity.class);

                    Bundle azureDB = getActivity().getIntent().getExtras();
                    //grab ContextsAndAttributes from extras
                    HashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>> caa = (LinkedHashMap<com.example.edwin.photoarchive.AzureClasses.Context, ArrayList<Attribute>>) azureDB.get("azure");

                    if (caa != null) {
                        i.putExtra("azure", caa);
                        i.putExtra("cameraTab", 1);
                        i.putExtra("cameraImages", imgPathSet);
                        startActivity(i);
                    } else {
                        Toast.makeText(context, "Please wait until contexts finish syncing", Toast.LENGTH_LONG).show();
                    }
                } catch (NullPointerException e) {
                    Toast.makeText(context, "Please wait until contexts finish syncing", Toast.LENGTH_LONG).show();

                }

            }
        })
        */

        ArrayList<String> tagNames = new ArrayList<String>();

        if (sharedPreferences.contains("cameraTags")) {
            String mapString = sharedPreferences.getString("cameraTags", null);

            try {
                JSONObject jsonObject2 = new JSONObject(mapString);
                Iterator<String> keysItr = jsonObject2.keys();

                while (keysItr.hasNext()) {

                    tagNames.add(keysItr.next());
                }


            } catch (Exception e) {
                e.printStackTrace();

            }
            ;

        }


        if (tagNames.size() > 0 && imgPathSet.size() > 0) {
            uploadBtn.setEnabled(true);

        }

        /* remove for now -ph [tags]
        if (tagNames.size() > 0) {
            clearTags.setEnabled(true);

        }*/

        if (imgPathSet.size() > 0) {
            clearTaken.setEnabled(true);
            button.setText("Take another");

        }

        /* remove this for now -ph  [tags]
        tagsContainer = (LinearLayout) view.findViewById(R.id.tagContainerT3);

        for (String s : tagNames) {
            final Button tag1 = new Button(context);
            tag1.setText(s);
            tagsContainer.addView(tag1);
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) tag1.getLayoutParams();
            marginParams.setMargins(0, 0, 10, 0);


        }
        */

        return view;
    }


    public boolean hasCamera() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    public void launchCamera(View v) {
        gps = new GPSTracker(context);
        Intent i = new Intent();
        i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);


        File photoFile = null;
        try {
            photoFile = createImageFile();

        } catch (IOException e) {
            e.printStackTrace();

        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        startActivityForResult(i, ACTIVITY_START_CAMERA_APP);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Photo saved in In-app images", Toast.LENGTH_LONG).show();


            //exif code
            gps = new GPSTracker(context);

            if (gps.isGPSEnabled) {


                ExifInterface exif = null;

                try {
                    exif = new ExifInterface(imageFileLocation);

                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();

                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(latitude));
                    exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(latitude));
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(longitude));
                    exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(longitude));
                    exif.saveAttributes();


                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            imgPathSet.add(imageFileLocation);


            getActivity().getIntent().putExtra("viewpager_position", 2);
            getActivity().getIntent().putExtra("cameraImages", imgPathSet);
            getActivity().recreate();

        } else if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == Activity.RESULT_CANCELED) {
            File file = new File(imageFileLocation);
            file.delete();

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imageFileLocation))));
        } else {
            File file = new File(imageFileLocation);
            file.delete();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imageFileLocation))));

        }


    }


    File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
        String imageFileName = "IMAGES_" + timeStamp + "_";
        File dir = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images/" + username);
        if (!dir.exists()) {
            dir.mkdirs();
            File output = new File(dir, ".nomedia");
            boolean fileCreated = output.createNewFile();
        }
        File image = File.createTempFile(imageFileName, ".jpg", dir);
        imageFileLocation = image.getAbsolutePath();

        return image;
    }

    private void clearTags() {
        new AlertDialog.Builder(context)
                .setTitle("Clear confirmation")
                .setMessage("Are you sure you want to clear all the tags?")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearTags2();
                        Toast.makeText(context, "All tags cleared", Toast.LENGTH_SHORT).show();
                    }
                })
                .create()
                .show();
    }


    private void clearTags2() {
        // [tags]
        // tagsContainer.removeAllViews();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("cameraTags");
        editor.apply();
        // clearTags.setEnabled(false);
    }

    private void refreshDash() {
        getActivity().getIntent().replaceExtras(new Bundle());
        getActivity().getIntent().setAction("");
        getActivity().getIntent().setData(null);
        getActivity().getIntent().setFlags(0);
        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);
        viewPager.setCurrentItem(0);
        getActivity().getSupportFragmentManager().popBackStack();
        getActivity().recreate();
    }
}