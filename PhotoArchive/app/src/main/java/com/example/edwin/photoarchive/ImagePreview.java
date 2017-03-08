package com.example.edwin.photoarchive;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ImagePreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        ImageView img = (ImageView) findViewById(R.id.imageViewPreview);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            String imagePath = extras.getString("imagePath");
            Glide.with(this).load(imagePath).into(img);

        }
    }
}
