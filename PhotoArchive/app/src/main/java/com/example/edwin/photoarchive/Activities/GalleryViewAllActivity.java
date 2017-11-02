package com.example.edwin.photoarchive.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.edwin.photoarchive.Adapters.ImageAdapterForGallery;
import com.example.edwin.photoarchive.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class GalleryViewAllActivity extends AppCompatActivity {
    private GridView imageGrid;
    private ArrayList<String> imgPathList;
    private Menu menu;
    private boolean isSelectEnabled = false;
    private HashSet<String> imagePathSet = new LinkedHashSet<String>();
    private HashSet<ImageView> imageViewSet = new HashSet<ImageView>();
    private Button selectButton;
    private Button clearButton;
    private Button doneButton;

    // DONE Long press to select images

    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data;
        // int column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        // column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);
            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_view_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        imageGrid = (GridView) findViewById(R.id.gridView2);
        imgPathList = new ArrayList<String>(getImagesPath(this));
        setTitle("Gallery (" + imgPathList.size()+")" );

        Collections.reverse(imgPathList);

        imageGrid.setAdapter(new ImageAdapterForGallery(GalleryViewAllActivity.this, imgPathList, this));


        // TODO Buttons do not work, find fix.
        // Button to clear image selection
        clearButton = (Button) findViewById(R.id.android_gallery_clear);
        clearButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // Verify Dialog, defined in function below
                AlertDialog clearCheck = verifyClear();
                clearCheck.show();
            }
        });

        // Button to submit images
        doneButton = (Button) findViewById(R.id.android_gallery_done);
        doneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(GalleryViewAllActivity.this, Activity2.class);
                i.putExtra("viewpager_position", 1);

                //add images passed from tab 2 to imagePathSet

                Bundle extras = getIntent().getExtras();
                if(extras != null ){
                    if(extras.containsKey("selectedImages")){
                        HashSet<String> passedImagesPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImages"));
                        for(String s: passedImagesPathSet){
                            imagePathSet.add(s);
                        }
                    }
                }

                if(imagePathSet.size()>0){
                    i.putExtra("selectedImagesFromGallery", imagePathSet);
                }

                startActivity(i);
                finish();
            }
        });

        /*
        selectButton = (Button)findViewById(R.id.button14);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectButton.setEnabled(false);
                isSelectEnabled = true;
                showOption(0);
                showOption(1);
            }
        });
        */
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    public boolean imagePathSetContains(String s){
        return imagePathSet.contains(s);

    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu= menu;

        menu.add(Menu.NONE, 0, Menu.NONE, "Cancel").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, 1, Menu.NONE, "Done").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        MenuItem item = menu.findItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deselectAllImages();
                return true;
            }
        });

        MenuItem item2 = menu.findItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent i = new Intent(GalleryViewAllActivity.this, Activity2.class);
                i.putExtra("viewpager_position", 1);

                //add images passed from tab 2 to imagePathSet

                Bundle extras = getIntent().getExtras();

                if(extras != null ){
                    if(extras.containsKey("selectedImages")){

                        HashSet<String> passedImagesPathSet = new LinkedHashSet<String>((LinkedHashSet) extras.get("selectedImages"));

                        for(String s: passedImagesPathSet){
                            imagePathSet.add(s);

                        }
                    }

                }

                if(imagePathSet.size()>0){
                    i.putExtra("selectedImagesFromGallery", imagePathSet);
                }

                startActivity(i);
                finish();


                return true;
            }
        });

        hideOption(0);
        hideOption(1);

        super.onCreateOptionsMenu(menu);
        return true;
    }
    */

    public Menu getMenu(){
        return this.menu;
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void deselectAllImages(){
        setTitle("Gallery (" + imgPathList.size()+")" );
        hideOption(0);
        hideOption(1);
        selectButton.setEnabled(true);
        isSelectEnabled = false;
        imagePathSet.clear();

        for(ImageView iv: imageViewSet){
            iv.clearColorFilter();

        }
        imageViewSet.clear();
    }

    public void addToImagePathSet(String s){
        this.imagePathSet.add(s);
    }

    public void removeFromImagePathSet(String s){
        this.imagePathSet.remove(s);
    }

    public void addToImageViewSet(ImageView iv){
        this.imageViewSet.add(iv);
    }

    public void removeFromImageViewSet(ImageView iv){
        this.imageViewSet.remove(iv);
    }

    public int getImagePathSetSize(){
        return imagePathSet.size();
    }

    // Verify Clearing Selection to prevent frustrating fatfinger errors
    private AlertDialog verifyClear(){
        AlertDialog clearDialog = new AlertDialog.Builder(this)
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
