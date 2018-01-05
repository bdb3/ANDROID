package com.example.edwin.photoarchive.Activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.example.edwin.photoarchive.R;


public class ImagePreview extends AppCompatActivity {
    private ImageView img;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        img = (ImageView) findViewById(R.id.imageViewPreview);
        progressBar=(ProgressBar)findViewById(R.id.image_preview_ProgressBar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String imagePath = extras.getString("imagePath");
            Glide.with(this).load(imagePath).dontTransform().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(new GlideDrawableImageViewTarget(img){
                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    Log.d("Image Preview","Error loading Image");
                    //Handle error
                }

                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                    progressBar.setVisibility(View.GONE);
                    super.onResourceReady(resource,animation);
                    //Hide loading, show image
                }
            });

        }

    }


}
