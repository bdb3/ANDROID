package com.example.edwin.photoarchive.AzureClasses;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AzureBlobDownloader extends AzureBlobLoader{
    private Activity act;
    private String userName;
    private ArrayList<String> databasePaths;
    private ArrayList<ImageView> imageContainers;

    public AzureBlobDownloader(Activity act, String userName){
        super();
        this.act = act;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        Log.d("Azure", "Downloading...");

        //We create an array list to store the final paths
        ArrayList<String> azurePaths = new ArrayList<>();

        try {
            //get the database paths
            this.setDBClient(
                    new MobileServiceClient(
                            "https://boephotoarchive-dev.azurewebsites.net",
                            this.act.getApplicationContext()
                    )
            );

            this.setImageTable(this.getDBClient().getTable(Image.class));
            this.setIcavTable(this.getDBClient().getTable(ICAV.class));

            Log.d("Azure", "About to execute query");

            final MobileServiceList<Image> dbImages = this.getImageTable().execute().get();

            for(Image img : dbImages){
                Log.d("Azure", "Path:" + img.getId());
            }

//            for(String path : this.databasePaths){
//                Log.d("Azure", "Looping with path: " + path);
//
//                CloudBlob imageBlob = this.getContainer().getBlockBlobReference(path);
//
//                /*MOST IMPORTANT STEP! WITHOUT THE SAS THE FILE WILL NOT BE DOWNLOADABLE
//                * WE MUST INCLUDE THE SAS AT THE END OF THE URI PROVIDED BY THE BLOB*/
//                String finalPath = imageBlob.getUri().toString()+this.getSas();
//                Log.d("Azure", "Final Path: " + finalPath);
//
//                azurePaths.add(finalPath);
//            }
        } catch (Exception e) {
            Log.d("Azure", e.toString());
        }

        Log.d("Azure", "Done getting paths");

        return azurePaths;
    }

    //Don't worry about this for now...
    protected void onProgressUpdate() {
        //maybe implement later on to show a progress bar?
    }

    /*This is where the magic happens!
    * The first record of the array is used by Picasso to
    * fill the ImageView! If we did it right then the blank
    * space should be filled with an image :D*/

    @Override
    protected void onPostExecute(Object o) {
//        ArrayList<String> results = (ArrayList<String>)o;
//
//        int indexCount = 0;
//        Log.d("Azure", "Executing On Post...");
//
//        for(ImageView imageContainer: imageContainers){
//            String azurePath = results.get(indexCount);
//            Log.d("Azure", "Azure Path For Picasso: " + azurePath);
//
//            //Log the path
//            Log.d("Azure", azurePath);
//
//            //Params: Context -> Path -> View
//            //Pretty straightforward
//            Picasso.with(this.act.getApplicationContext()).load(azurePath).into(imageContainer);
//
//            //move to next image and container
//            indexCount++;
//        }

    }
}
