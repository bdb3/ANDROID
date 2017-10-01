package com.example.edwin.photoarchive.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.edwin.photoarchive.R;


public class ImagePreview extends AppCompatActivity {
    private ImageView img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        img = (ImageView) findViewById(R.id.imageViewPreview);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imagePath = extras.getString("imagePath");
            Glide.with(this).load(imagePath).into(img);

        }

    }


}
