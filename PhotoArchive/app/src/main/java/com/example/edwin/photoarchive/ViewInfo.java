package com.example.edwin.photoarchive;

import android.graphics.Color;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

public class ViewInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_info);

        final LinearLayout linearLayoutInfo = (LinearLayout) findViewById(R.id.linearLayoutInfo);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final String path = extras.getString("imagePath");

            ExifInterface exif = null;

            try {
                exif = new ExifInterface(path);

                if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null &&
                        exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) !=null ) {

                    String lat = String.valueOf(convertRationalLatLonToFloat(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),
                            exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)));

                    TextView tv = new TextView(this);
                    tv.setTextColor(Color.BLACK);
                    tv.setText("Lat: " + lat);
                    linearLayoutInfo.addView(tv);

                    String lon = String.valueOf(convertRationalLatLonToFloat(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),
                            exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)));

                    TextView tv2 = new TextView(this);
                    tv2.setText("Lon: " + lon);
                    tv2.setTextColor(Color.BLACK);
                    linearLayoutInfo.addView(tv2);
                }
                else{
                    TextView tv3 = new TextView(this);
                    tv3.setText("No EXIF data available");
                    tv3.setTextColor(Color.BLACK);
                    linearLayoutInfo.addView(tv3);

                }



            } catch (IOException e) {
                e.printStackTrace();

            }

        }
    }

    private float convertRationalLatLonToFloat(String rationalString, String ref) {
        try {
            String [] parts = rationalString.split(",");
            String [] pair;
            pair = parts[0].split("/");
            double degrees = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());
            pair = parts[1].split("/");
            double minutes = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());
            pair = parts[2].split("/");
            double seconds = Double.parseDouble(pair[0].trim())
                    / Double.parseDouble(pair[1].trim());
            double result = degrees + (minutes / 60.0) + (seconds / 3600.0);
            if ((ref.equals("S") || ref.equals("W"))) {
                return (float) -result;
            }
            return (float) result;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException();
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException();
        }
    }
}
