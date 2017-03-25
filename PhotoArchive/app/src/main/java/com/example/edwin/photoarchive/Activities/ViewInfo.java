package com.example.edwin.photoarchive.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.photoarchive.Helpers.ExtractLatLong;
import com.example.edwin.photoarchive.R;

public class ViewInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info);

        final LinearLayout linearLayoutInfo = (LinearLayout) findViewById(R.id.linearLayoutInfo);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String path = extras.getString("imagePath");
            ExtractLatLong ell = new ExtractLatLong(path);

            float lat = (float )ell.getLat();
            float lon = (float) ell.getLon();


            if ( lat == 0 && lon == 0 ) {

                TextView tv3 = new TextView(this);
                tv3.setText("No EXIF data available");
                tv3.setTextColor(Color.BLACK);
                linearLayoutInfo.addView(tv3);


            } else {

                TextView tv = new TextView(this);
                tv.setTextColor(Color.BLACK);
                tv.setText("Lat: " + lat);
                linearLayoutInfo.addView(tv);

                TextView tv2 = new TextView(this);
                tv2.setText("Lon: " + lon);
                tv2.setTextColor(Color.BLACK);
                linearLayoutInfo.addView(tv2);

            }
        }


    }


}
