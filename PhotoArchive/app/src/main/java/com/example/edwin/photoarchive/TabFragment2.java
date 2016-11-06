package com.example.edwin.photoarchive;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TabFragment2 extends Fragment {
    ImageView resultPhoto;
    private String imageFileLocation="";
    private static final int ACTIVITY_START_CAMERA_APP = 1;
    private Context context = null;
    GPSTracker gps;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tab_fragment_2, container, false);
        context= this.getContext();

        resultPhoto = (ImageView) view.findViewById(R.id.imageView2);

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCamera()) {
                    launchCamera(null);
                }

            }
        });

        return view;

    }


    public boolean hasCamera() {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    public void launchCamera(View v) {
        gps = new GPSTracker(context);
        if(gps.canGetLocation()) {

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
        else{
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == Activity.RESULT_OK ) {
            Toast.makeText(context, "Photo has been saved", Toast.LENGTH_LONG).show();

             












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