package com.example.edwin.photoarchive.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.edwin.photoarchive.Adapters.ImageAdapterForAppImages;
import com.example.edwin.photoarchive.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class InAppViewAllActivity extends AppCompatActivity {
    private GridView imageGrid;
    private ArrayList<String> imgPathList;
    private HashSet<String> imagePathSet = new LinkedHashSet<String>();
    private HashSet<ImageView> imageViewSet = new HashSet<ImageView>();
    private Button clearButton;
    private Button doneButton;
    private String username;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_view_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);
        username = sharedPreferences.getString("loggedInUser",null);

        imageGrid = (GridView) findViewById(R.id.gridView3);
        imgPathList = new ArrayList<>();

        File path2 = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images/"+username);
        String[] fileNames2 = null;

        if (path2.exists()) {
            fileNames2 = path2.list();
            for (int i = fileNames2.length-1; i>=0; i--)
                if(!fileNames2[i].equals(".nomedia"))
                    imgPathList.add(path2 + "/" + fileNames2[i]);
        }

        setTitle("In App ("+ imgPathList.size()+")");
        imageGrid.setAdapter(new ImageAdapterForAppImages(InAppViewAllActivity.this, imgPathList, this));

        // Button to clear image selection
        clearButton = (Button) findViewById(R.id.android_inapp_clear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify Dialog, defined in function below
                android.app.AlertDialog clearCheck = verifyClear();
                clearCheck.show();
            }
        });

        // Button to submit images
        doneButton = (Button) findViewById(R.id.android_inapp_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent i = new Intent(InAppViewAllActivity.this, MainActivity.class);
            i.putExtra("viewpager_position", 2);

            //add images passed from tab 2 to imagePathSet

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (extras.containsKey("selectedImages")) {
                    HashSet<String> passedImagesPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImages"));
                    for (String s : passedImagesPathSet) {
                        imagePathSet.add(s);
                    }
                }
            }

            if (imagePathSet.size() > 0)
                i.putExtra("selectedImagesFromGallery", imagePathSet);

            startActivity(i);
            finish();
            }
        });
    }


    public boolean imagePathSetContains(String s){ return imagePathSet.contains(s); }


    private void deselectAllImages(){
        setTitle("In App ("+ imgPathList.size()+")");
        imagePathSet.clear();

        for(ImageView iv: imageViewSet){
            iv.clearColorFilter();
        }

        imageViewSet.clear();
    }

    public void addToImagePathSet(String s){this.imagePathSet.add(s);}

    public void removeFromImagePathSet(String s){this.imagePathSet.remove(s);}

    public void addToImageViewSet(ImageView iv){this.imageViewSet.add(iv);}

    public void removeFromImageViewSet(ImageView iv){this.imageViewSet.remove(iv);}

    public int getImagePathSetSize(){return imagePathSet.size();}

    private void deleteImage(){
        new AlertDialog.Builder(InAppViewAllActivity.this)
            .setTitle("Delete confirmation")
            .setMessage("Are you sure you want to delete these image(s)?")
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {

                for(String s: imagePathSet){
                    File file = new File(s);
                    file.delete();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(s))));
                }

                Toast.makeText(InAppViewAllActivity.this, imagePathSet.size() + " image(s) deleted", Toast.LENGTH_SHORT).show();
                recreate();

                }
            })
            .create()
            .show();
    }

    private android.app.AlertDialog verifyClear() {
        android.app.AlertDialog clearDialog = new android.app.AlertDialog.Builder(this)
            //set message, title, and icon
            .setTitle("Clear Selection")
            .setMessage("Are you sure you want to clear your image selection?")
            .setIcon(R.drawable.delete_icon)
            .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                    deselectAllImages();
                    dialog.dismiss();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int i) {
                    dialog.dismiss();
                }
            })
            .create();
        return clearDialog;
    }
}
