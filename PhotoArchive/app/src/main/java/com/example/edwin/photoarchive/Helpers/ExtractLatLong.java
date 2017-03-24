package com.example.edwin.photoarchive.Helpers;

import android.graphics.Color;
import android.media.ExifInterface;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Edwin on 3/23/2017.
 */
public class ExtractLatLong {
    private  double lat;
    private double lon;

    public double getLon() {
        return lon;
    }

    public double getLat() {

        return lat;
    }

    public ExtractLatLong(String path){

            ExifInterface exif = null;

            try {
                exif = new ExifInterface(path);

                if(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null &&
                        exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE) !=null ) {

                    this.lat = convertRationalLatLonToFloat(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE),
                            exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF));


                    this.lon = convertRationalLatLonToFloat(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE),
                            exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF));

                }
                else{
                    this.lat = 0;
                    this.lon = 0;

                }



            } catch (IOException e) {
                e.printStackTrace();

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
