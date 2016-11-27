package com.example.edwin.photoarchive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class TabFragment2 extends Fragment {
    private String imageFileLocation="";
    private static final int ACTIVITY_START_CAMERA_APP = 1;
    private Context context = null;
    GPSTracker gps;
    private ListView ctxListView;
    private  Button button = null;
    private  Button buttonUpload = null;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);
        context= this.getContext();

        //camera btn
        button = (Button) view.findViewById(R.id.button);

        //list view

        ctxListView = (ListView) view.findViewById(R.id.listView);
        String[] values = new String[] { "Meeting",
                "Permit",
                "Project",
                "Cell tower"
        };

        final Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();

        for(int i=0; i < values.length; i++){

            ArrayList<String> list = new ArrayList<String>();

            if(i==0){
                list.add("How many people are in the photo?");
                list.add("Where was the photo taken?");
                list.add("What is the topic of the meeting?");

            }
            else if(i==1){
                list.add("Where was the photo taken?");
                list.add("What is the Permit Number?");
            }
            else if(i==2){
                list.add("Where was the photo taken?");
                list.add("What is the Project Number?");
            }
            else if(i==3){
                list.add("Where was the photo taken?");
                list.add("What is the company that owns this tower?");
                list.add("How far does the signal reach?");

            }

            map.put(values[i], list);

        }

        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.list_layout_1, R.id.text1, values);

        ctxListView.setAdapter(adapter);
        final Button btn1 = (Button) view.findViewById(R.id.button5);
        final Button btn2 = (Button) view.findViewById(R.id.button6);
        final TextView tf = (TextView) view.findViewById(R.id.textView2);
        final ScrollView sv = (ScrollView) view.findViewById(R.id.scrollView);
        final LinearLayout linLay = (LinearLayout) view.findViewById(R.id.linearLayout1);

        ctxListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                int itemPosition     = position;
                String  itemValue    = (String) ctxListView.getItemAtPosition(position);
                btn1.setVisibility(View.VISIBLE);
                btn1.setText(itemValue);
                btn2.setVisibility(View.VISIBLE);
                ctxListView.setVisibility(View.INVISIBLE);
                tf.setText("Category:");
                sv.setVisibility(View.VISIBLE);
                linLay.setVisibility(View.VISIBLE);

                ArrayList<String> attList = new ArrayList<String>(map.get(itemValue));

                //questions for context hard coded for now

                for(String s: attList){
                    TextView textView = new TextView(context);
                    textView.setText(s);
                    linLay.addView(textView);
                    EditText editText = new EditText(context);
                    linLay.addView(editText);

                }

                ///


                TextView myTextView;
                for (int i=0; i<linLay.getChildCount();i++) {
                    View view = linLay.getChildAt(i);
                    if (view instanceof TextView){
                        myTextView= (TextView) view;
                        myTextView.setTextColor(Color.BLACK);
                    }
                }


                final Button okBtn = new Button(context);
                okBtn.setText("UPLOAD");
                linLay.addView(okBtn);

                okBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (View view : linLay.getTouchables()){
                            if (view instanceof EditText){
                                EditText editText = (EditText) view;
                                editText.setEnabled(false);
                                editText.setFocusable(false);
                                editText.setFocusableInTouchMode(false);
                            }
                        }
                        okBtn.setEnabled(false);

                 /// add image path to queue list
                        ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pager);

                        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        if(!sharedpreferences.contains("pathList")){
                            Set<String> pathSet = new HashSet<String>();
                            pathSet.add(imageFileLocation);

                            editor.putStringSet("pathList", pathSet);
                            editor.commit();


                        }
                        else{
                            sharedpreferences.getStringSet("pathList", null).add(imageFileLocation);
                            Set<String> pathSet = new HashSet<String>(sharedpreferences.getStringSet("pathList", null));
                            editor.remove("pathList");
                            editor.apply();
                            editor.putStringSet("pathList", pathSet);
                            editor.apply();

                        }



                        getFragmentManager().beginTransaction().detach(getFragmentManager().getFragments().get(1)).attach(getFragmentManager().getFragments().get(1)).commitAllowingStateLoss();
                        getFragmentManager().beginTransaction().detach(getFragmentManager().getFragments().get(0)).attach(getFragmentManager().getFragments().get(0)).commitAllowingStateLoss();
                        viewPager.setCurrentItem(0);
                        Toast.makeText(context, "Upload has started", Toast.LENGTH_LONG).show();






                        ///end of add image path to queue list

                    }
                });


            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn1.setVisibility(View.INVISIBLE);
                btn2.setVisibility(View.INVISIBLE);
                ctxListView.setVisibility(View.VISIBLE);
                tf.setText("Select a category for the image");

                linLay.setVisibility(View.INVISIBLE);
                sv.setVisibility(View.INVISIBLE);

                if(((LinearLayout) linLay).getChildCount() > 0)
                    ((LinearLayout) linLay).removeAllViews();




            }
        });


        //end of list view


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button.getText() == "Take another"){
                    buttonUpload.setEnabled(false);
                    btn2.performClick();
                    tf.setVisibility(View.INVISIBLE);
                    ctxListView.setVisibility(View.INVISIBLE);
                    buttonUpload.setBackgroundColor(Color.parseColor("#adadad"));




                }
                if (hasCamera() ) {
                    launchCamera(null);
                }


            }
        });


        buttonUpload = (Button) view.findViewById(R.id.buttonUpload);

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tf.setVisibility(View.VISIBLE);
                ctxListView.setVisibility(View.VISIBLE);
                buttonUpload.setEnabled(false);



            }
        });





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

        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == Activity.RESULT_OK ) {
            button.setText("Take another");
            buttonUpload.setEnabled(true);
            buttonUpload.setBackgroundColor(Color.GREEN);
            getFragmentManager().beginTransaction().detach(getFragmentManager().getFragments().get(2)).attach(getFragmentManager().getFragments().get(2)).commitAllowingStateLoss();



            //  Bitmap photo = rotateImage(BitmapFactory.decodeFile(imageFileLocation));

            //exif code

            /*
            ExifInterface exif = null;

            try{
                exif= new ExifInterface(imageFileLocation);

                double latitude = gps.getLatitude();
                double longitude = gps.getLongitude();

                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GPS.convert(latitude));
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, GPS.latitudeRef(latitude));
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GPS.convert(longitude));
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, GPS.longitudeRef(longitude));
                exif.saveAttributes();

                System.out.println("lat: " + exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE));
                System.out.println("lat ref: " + exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));
                System.out.println("lon: " + exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                System.out.println("lon ref: " + exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));

            }catch(IOException e){
                e.printStackTrace();

            }

            //
            //resultPhoto.setImageBitmap(photo);

            */

        }
        else if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == Activity.RESULT_CANCELED) {
            File file = new File(imageFileLocation);
            file.delete();

            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imageFileLocation))));
        }
        else{
            File file = new File(imageFileLocation);
            file.delete();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imageFileLocation))));

        }



        }


    File createImageFile() throws IOException{
        String timeStamp= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
        String imageFileName = "IMAGES_"+ timeStamp+ "_";
        File dir = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");
        if(!dir.exists()){
            dir.mkdirs();
            File output = new File(dir, ".nomedia");
            boolean fileCreated = output.createNewFile();
        }
        File image= File.createTempFile(imageFileName, ".jpg", dir);
        imageFileLocation = image.getAbsolutePath();

        return image;
    }



        /*
    private Bitmap rotateImage(Bitmap bitmap){
        ExifInterface exif = null;

        try{
            exif= new ExifInterface(imageFileLocation);

        }catch(IOException e){
            e.printStackTrace();

        }

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix= new Matrix();
        switch(orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }


        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true );
        return rotatedBitmap;
    }

*/

}