package com.example.edwin.photoarchive;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.edwin.photoarchive.Activities.GalleryViewAllActivity;
import com.example.edwin.photoarchive.Activities.ImagePreview;
import com.example.edwin.photoarchive.Activities.InAppViewAllActivity;
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
    private Button uploadBtn = null;
    private com.example.edwin.photoarchive.AzureClasses.Context globalContext;
    private ArrayList<Attribute> globalAttribute;
    private ArrayList<String> globalData;
    private HashSet<String> imgPathSet = new LinkedHashSet<String>();
    private SharedPreferences sharedPreferences;
    private LinearLayout inAppPictures;
    private HashMap<String, HashMap<String, String>> storedDataMap;
    private String currentlySelectedContext;
    private int selectedSize = 0;

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

        Button inAppGalleryButton = (Button) view.findViewById(R.id.inappgallery_button);
        inAppGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), InAppViewAllActivity.class);
                i.putExtra("selectedImages", imgPathSet);
                startActivity(i);
            }
        });

        username = sharedPreferences.getString("loggedInUser", null);

        //camera btn
        button = (Button) view.findViewById(R.id.button);
        final TextView taken = (TextView) view.findViewById(R.id.textViewTaken);
        final Button clearTaken = (Button) view.findViewById(R.id.clearTaken);

        inAppPictures = (LinearLayout) view.findViewById(R.id.inapp_picture_container);
        uploadBtn = (Button) view.findViewById(R.id.buttonUpload);

        // FETCH
        Gson gson = new Gson();

        String fetchGlobalContext = sharedPreferences.getString("globalContext", null);
        String fetchGlobalAttributes = sharedPreferences.getString("globalAttributes", null);
        String fetchGlobalData = sharedPreferences.getString("globalData", null);
        if (fetchGlobalContext != null) {
            Log.d("TabFrag3", "fetchGlobalContext Not Null");
            globalContext = gson.fromJson(fetchGlobalContext, com.example.edwin.photoarchive.AzureClasses.Context.class);
        }
        if (fetchGlobalAttributes != null) {
            Log.d("TabFrag3", "fetchGlobalAttributes Not Null");
            Type typea = new com.google.common.reflect.TypeToken<ArrayList<Attribute>>() {
            }.getType(); // I have no idea what this does specifically but it is needed GSON Convert the String
            globalAttribute = gson.fromJson(fetchGlobalAttributes, typea);
        }
        if (fetchGlobalData != null) {
            Log.d("TabFrag3", "fetchGlobalData Not Null");
            Type type = new com.google.common.reflect.TypeToken<ArrayList<String>>() {
            }.getType(); // I have no idea what this does specifically but it is needed GSON Convert the String
            globalData = gson.fromJson(fetchGlobalData, type);
        }

        final String fetchStoredDataMap = sharedPreferences.getString("storedDataMap", null);
//        final String fetchCurrentlySelectedContext = sharedPreferences.getString("currentlySelectedContext", null);
//        final int fetchImagePathSize = sharedPreferences.getInt("imgPathSize", 0);

        if (fetchStoredDataMap != null) {
            Type dataType = new com.google.common.reflect.TypeToken<HashMap<String, HashMap<String, String>>>() {
            }.getType();
            storedDataMap = gson.fromJson(sharedPreferences.getString("storedDataMap", null), dataType);

        }

        Bundle extras = getActivity().getIntent().getExtras();

        if (getActivity().getIntent().hasExtra("selectedImagesFromGallery")) {
            Log.d("TabFrag3ImgPt", "Extras not null");
            imgPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImagesFromGallery"));
            selectedSize = imgPathSet.size();
            for (String imagePath : imgPathSet) {
                Log.d("TabFrag3ImgPt", imagePath);
                final ImageView imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getActivity().getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getActivity().getResources().getDisplayMetrics())));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Glide.with(context).load(imagePath).into(imageView);

                inAppPictures.addView(imageView);

                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                marginParams.setMargins(0, 0, 10, 0);
            }
        }

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
                String fetchCurrentlySelectedContext = sharedPreferences.getString("currentlySelectedContext", null);
                if (storedDataMap != null && storedDataMap.containsKey(fetchCurrentlySelectedContext)) {
                    Gson gson = new Gson();
                    //get tags from shared preferences
                    uploadBtn.setEnabled(false);

                    Bundle extras = getActivity().getIntent().getExtras();
                    try {
                        if (getActivity().getIntent().hasExtra("selectedImagesFromGallery"))
                            imgPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImagesFromGallery"));
                        else
                            return;
                    } catch (Exception e) {

                    }

                    //                String mapString = sharedPreferences.getString("cameraTags", null);
                    //
                    //                Map<String, Map<String, String>> outputMap = new LinkedHashMap<String, Map<String, String>>();
                    //                try {
                    //                    JSONObject jsonObject2 = new JSONObject(mapString);
                    //                    Iterator<String> keysItr = jsonObject2.keys();
                    //
                    //                    while (keysItr.hasNext()) {
                    //                        String key = keysItr.next();
                    //
                    //                        Map<String, String> valueMap = new LinkedHashMap<String, String>();
                    //                        Iterator<String> keysItr2 = ((JSONObject) jsonObject2.get(key)).keys();
                    //
                    //                        while (keysItr2.hasNext()) {
                    //                            String key2 = keysItr2.next();
                    //                            String value = (String) ((JSONObject) jsonObject2.get(key)).get(key2);
                    //
                    //                            valueMap.put(key2, value);
                    //                        }
                    //
                    //                        outputMap.put(key, valueMap);
                    //                    }
                    //
                    //
                    //                } catch (Exception e) {
                    //                    e.printStackTrace();
                    //
                    //                }
                    Map<String, Map<String, String>> outputMap = new LinkedHashMap<String, Map<String, String>>();
                    Map<String, String> fieldMap = new LinkedHashMap<>();


//                for(int i = 0; globalAttribute != null && i < globalAttribute.size(); i++){
//                    fieldMap.put(globalAttribute.get(i).getId(), globalData.get(i));
//                }

                    outputMap.put(fetchCurrentlySelectedContext, storedDataMap.get(fetchCurrentlySelectedContext));
                    Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();

                    //outputMap.put(globalContext.getId(),fieldMap);

                    for (String s : imgPathSet) {

                        ExtractLatLong ell = new ExtractLatLong(s);
                        TaggedImageObject tagImgObj = new TaggedImageObject(s, ell.getLat(), ell.getLon(), username, outputMap);
                        taggedImagesList.add(tagImgObj);

                    }

                    // put imagesTagsMap in shared preferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();


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
                            Log.d("TabFrag3", tagImgObj.toString());
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
                else{
                    Toast.makeText(context, "Please fill out the category page first", Toast.LENGTH_LONG).show();
                }
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
                selectedSize = imgPathSet.size();
                taken.invalidate();
                taken.setText("Selected: " + selectedSize);
                clearTaken.setEnabled(false);
                uploadBtn.setEnabled(false);
            }
        });

//       Bundle extras = getActivity().getIntent().getExtras();

//        if (extras != null) {
//            if (extras.containsKey("cameraImages")) {
//                HashSet<String> cameraPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("cameraImages"));
//                for (String s : cameraPathSet) {
//                    imgPathSet.add(s);
//                }
//            }
//        }

        taken.setText("Selected: " + selectedSize);

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


        if (imgPathSet.size() + selectedSize > 0) {
            uploadBtn.setEnabled(true);

        }

        /* remove for now -ph [tags]
        if (tagNames.size() > 0) {
            clearTags.setEnabled(true);

        }*/

        if (imgPathSet.size() > 0) {
            clearTaken.setEnabled(true);
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

    public void recData(com.example.edwin.photoarchive.AzureClasses.Context context, ArrayList<Attribute> attributes, ArrayList<String> data) {
        if (context != null && attributes != null && data != null) {
            globalContext = context;
            globalAttribute = attributes;
            globalData = data;
            uploadBtn.setEnabled(true);
        }
    }
}