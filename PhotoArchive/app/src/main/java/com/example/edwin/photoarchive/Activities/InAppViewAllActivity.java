package com.example.edwin.photoarchive.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
    private Menu menu;
    private boolean isSelectEnabled = false;
    private HashSet<String> imagePathSet = new LinkedHashSet<String>();
    private HashSet<ImageView> imageViewSet = new HashSet<ImageView>();
    private Button selectButton;
    private Button deleteButton;
    private String username;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_view_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences(TagsActivity.MyTagsPREFERENCES, Context.MODE_PRIVATE);

        if(sharedPreferences.contains("loggedInUser")) {
            username = sharedPreferences.getString("loggedInUser",null);

        }


        imageGrid = (GridView) findViewById(R.id.gridView3);
        imgPathList = new ArrayList<String>();

        File path2 = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images/"+username);
        String[] fileNames2 = null;

        if (path2.exists()) {
            fileNames2 = path2.list();


            for (int i = fileNames2.length-1; i>=0; i--) {
                if(!fileNames2[i].equals(".nomedia")){
                    imgPathList.add(path2 + "/" + fileNames2[i]);
                }
            }

        }

        setTitle("In App ("+ imgPathList.size()+")");
        imageGrid.setAdapter(new ImageAdapterForAppImages(InAppViewAllActivity.this, imgPathList, this));

        
        selectButton = (Button)findViewById(R.id.button15);
        deleteButton = (Button)findViewById(R.id.buttonDelete);


        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectButton.setEnabled(false);
                deleteButton.setEnabled(false);
                isSelectEnabled = true;
                showOption(0);
                showOption(1);


            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectButton.setEnabled(false);
                deleteButton.setEnabled(false);
                isSelectEnabled = true;
                showOption(0);
                showOption(2);
                showOption(3);

            }
        });

    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    public boolean getIsSelectEnabled(){
        return isSelectEnabled;
    }
    public boolean imagePathSetContains(String s){
        return imagePathSet.contains(s);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu= menu;

        menu.add(Menu.NONE, 0, Menu.NONE, "Cancel")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, 1, Menu.NONE, "Done")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.add(Menu.NONE, 2, Menu.NONE, "Select All")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        menu.add(Menu.NONE, 3, Menu.NONE, "")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        menu.findItem(3).setIcon(R.drawable.delete_icon);
        menu.findItem(3).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);


        MenuItem item = menu.findItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deselectAllImages();
                return true;
            }});

        MenuItem itemSelectAll = menu.findItem(2).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                selectAllImages();
                return true;
            }});

        MenuItem itemDelete = menu.findItem(3).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deleteImage();
                return true;
            }});

        MenuItem item2 = menu.findItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Intent i= new Intent(InAppViewAllActivity.this, Activity2.class);
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
                    i.putExtra("selectedImagesFromApp", imagePathSet);


                }
                startActivity(i);
                finish();


                return true;
            }});


        hideOption(0);
        hideOption(1);
        hideOption(2);
        hideOption(3);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    public Menu getMenu(){
        return this.menu;

    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void deselectAllImages(){
        setTitle("In App ("+ imgPathList.size()+")");
        hideOption(0);
        hideOption(1);
        hideOption(2);
        hideOption(3);
        selectButton.setEnabled(true);
        deleteButton.setEnabled(true);
        isSelectEnabled = false;
        imagePathSet.clear();

        for(ImageView iv: imageViewSet){
            iv.clearColorFilter();

        }
        for(int i = 0; i < imageGrid.getChildCount(); i++ ){
            ImageView img = (ImageView) imageGrid.getChildAt(i);
            img.clearColorFilter();

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

    private void selectAllImages(){

        for(String s: imgPathList ){
            imagePathSet.add(s);

        }

        for(int i = 0; i < imageGrid.getChildCount(); i++ ){
            ImageView img = (ImageView) imageGrid.getChildAt(i);
            img.setColorFilter(Color.argb(110, 20, 197, 215));

        }

        setTitle("Selected: "+ imgPathList.size());


    }

}
