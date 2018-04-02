package com.example.edwin.photoarchive;

import android.app.Activity;
import android.content.Context;
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
import com.example.edwin.photoarchive.Activities.InAppViewAllActivity;
import com.example.edwin.photoarchive.Activities.TagsActivity;
import com.example.edwin.photoarchive.AzureClasses.Field;
import com.example.edwin.photoarchive.AzureClasses.Category;
import com.example.edwin.photoarchive.AzureClasses.TaggedImageObject;
import com.example.edwin.photoarchive.Helpers.ExtractLatLong;
import com.example.edwin.photoarchive.Helpers.GPS;
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
    private HashSet<String> imgPathSet = new LinkedHashSet<String>();
    private SharedPreferences sharedPreferences;
    private LinearLayout inAppPictures;
    private HashMap<String, HashMap<String, String>> storedDataMap;
    private int selectedSize = 0;
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

        final String fetchStoredDataMap = sharedPreferences.getString("storedDataMap", null);

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
                    } catch (Exception e) { }

                    Map<String, Map<String, String>> outputMap = new LinkedHashMap<String, Map<String, String>>();
                    Map<String, String> fieldMap = new LinkedHashMap<>();

                    outputMap.put(fetchCurrentlySelectedContext, storedDataMap.get(fetchCurrentlySelectedContext));
                    Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();

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

        taken.setText("Selected: " + selectedSize);

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
        }

        if (imgPathSet.size() + selectedSize > 0) {
            uploadBtn.setEnabled(true);
        }

        if (imgPathSet.size() > 0) {
            clearTaken.setEnabled(true);
        }

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

            Log.d("CAMERA", imageFileLocation);

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
            getActivity().getIntent().putExtra("selectedImagesFromGallery", imgPathSet);
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

    private void clearTags2() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("cameraTags");
        editor.apply();
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

    public void recData(Category category, ArrayList<Field> fields, ArrayList<String> data) {
        if (category != null && fields != null && data != null) {
            uploadBtn.setEnabled(true);
        }
    }
}