package com.example.edwin.photoarchive.Helpers;

import android.app.Activity;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edwin on 4/16/2017.
 */
public class DeleteAfterXDays {
    private int days;
    private Activity activity;
    private int total;
    private Fragment frag;

    public DeleteAfterXDays(int days, final Activity activity, final Fragment frag){
        this.days = days;
        this.activity = activity;
        this.frag = frag;

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        File inAppImagesPath = new File(Environment.getExternalStorageDirectory(), "PhotoArchive Images");

                        String[] inAppImgList = null;

                        if (inAppImagesPath.exists()) {
                            inAppImgList = inAppImagesPath.list();

                            for (String s: inAppImgList) {
                                if(!s.equals(".nomedia")){
                                    String path = inAppImagesPath + "/" + s;
                                    deleteImages(path);
                                }
                            }

                            if(total > 0){
                                Toast.makeText(activity, total + " image(s) were deleted", Toast.LENGTH_LONG).show();
                                //refresh
                                FragmentManager fm = frag.getActivity().getSupportFragmentManager();
                                Fragment frag2 = fm.getFragments().get(1);

                                fm.beginTransaction().detach(frag2).attach(frag2).commitAllowingStateLoss();

                            }

                        }
                        //

                    }
                });

                return null;
            }
        }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void deleteImages(String s){
        ExifInterface exif = null;

        try {
            exif = new ExifInterface(s);

            SimpleDateFormat format = new SimpleDateFormat("yyyy:mm:dd HH:mm:ss");
            Date dt1 = format.parse(exif.getAttribute(ExifInterface.TAG_DATETIME));
            String date = new SimpleDateFormat("yyyy:mm:dd HH:mm:ss").format(new Date());
            Date dt2 = new SimpleDateFormat("yyyy:mm:dd HH:mm:ss").parse(date);

            long diff = dt2.getTime() - dt1.getTime();
            long dayDiff = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if(dayDiff > days){
                total++;
                File f = new File(s);
                f.delete();
                activity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(s))));
            }


        } catch (Exception e) {
            e.printStackTrace();

        }


    }
}
