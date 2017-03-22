package com.example.edwin.photoarchive;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_view_all);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("App Images");

        imageGrid = (GridView) findViewById(R.id.gridView3);
        imgPathList = new ArrayList<String>();

        File path2 = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");
        String[] fileNames2 = null;

        if (path2.exists()) {
            fileNames2 = path2.list();


            for (int i = fileNames2.length-1; i>=0; i--) {
                if(!fileNames2[i].equals(".nomedia")){
                    imgPathList.add(path2 + "/" + fileNames2[i]);
                }
            }

        }

        imageGrid.setAdapter(new ImageAdapterForAppImages(InAppViewAllActivity.this, imgPathList, this));

        selectButton = (Button)findViewById(R.id.button15);

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectButton.setEnabled(false);
                isSelectEnabled = true;
                showOption(0);
                showOption(1);


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

        menu.add(Menu.NONE, 1, Menu.NONE, "DONE")
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


        MenuItem item = menu.findItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                deselectAllImages();
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
        setTitle("App Images");
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

}