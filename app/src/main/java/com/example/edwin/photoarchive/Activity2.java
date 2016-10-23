package com.example.edwin.photoarchive;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;



public class Activity2 extends AppCompatActivity {
    public static final int REQUEST_CAPTURE = 1;
    ImageView resultPhoto;
    private String imageFileLocation="";
    private static final int ACTIVITY_START_CAMERA_APP = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        //TextView name=(TextView)findViewById(R.id.textView4);
        //name.setText("Hello "+getIntent().getExtras().getString("userName"));

        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setActiveColor("#2D67F7");
        bottomNavigationBar.setInActiveColor("#000000");
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);

        bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.ic_home, "Dash"))
                .addItem(new BottomNavigationItem(R.drawable.ic_camera, "Cam"))
                .addItem(new BottomNavigationItem(R.drawable.ic_upload, "Upld"))
                .addItem(new BottomNavigationItem(R.drawable.ic_history, "Hist"))
                .addItem(new BottomNavigationItem(R.drawable.ic_action_settings, "Stngs"))
                .initialise();

        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                if (position == 1) {
                    if (hasCamera()) {
                        launchCamera(null);
                    }

                }
            }

            @Override
            public void onTabUnselected(int position) {
            }

            @Override
            public void onTabReselected(int position) {
            }
        });

        // camera code
        resultPhoto = (ImageView) findViewById(R.id.imageView2);


    }

    public boolean hasCamera() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void launchCamera(View v) {
        Intent i = new Intent();
        i.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

         File photoFile = null;
        try{
            photoFile = createImageFile();

        }catch(IOException e){
            e.printStackTrace();

        }

        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));



        startActivityForResult(i, ACTIVITY_START_CAMERA_APP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Toast.makeText(this, "Photo has been saved ", Toast.LENGTH_SHORT).show();
           // Bundle extras = data.getExtras();
           // Bitmap photo = (Bitmap) extras.get("data");
            Bitmap photo = rotateImage(BitmapFactory.decodeFile(imageFileLocation));
            resultPhoto.setImageBitmap(photo);

        }
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_CANCELED) {
            File file = new File(imageFileLocation);
            file.delete();

            this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(imageFileLocation))));
        }

    }

    File createImageFile() throws IOException{
        String timeStamp= new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(Calendar.getInstance().getTime());
        String imageFileName = "IMAGES_"+ timeStamp+ "_";
        File storageDirectory = Environment.getExternalStorageDirectory();
        File dir = new File(storageDirectory.getAbsolutePath()+ "/Photo Archive");
        if(!dir.exists()){
            dir.mkdirs();
        }
        File image= File.createTempFile(imageFileName, ".jpg", dir);
        imageFileLocation = image.getAbsolutePath();
        checkFolderCreation(image);
        return image;
    }
    private void checkFolderCreation(File file) {
        MediaScannerConnection.scanFile(this,
                new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("ExternalStorage", "Scanned " + path + ":");
                        Log.e("ExternalStorage", "-> uri=" + uri);

                    }
                });

    }

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



}
