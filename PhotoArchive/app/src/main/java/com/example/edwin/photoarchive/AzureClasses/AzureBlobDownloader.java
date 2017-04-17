package com.example.edwin.photoarchive.AzureClasses;

import android.app.Activity;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.edwin.photoarchive.Adapters.ImageAdapterHistory;
import com.example.edwin.photoarchive.R;
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

    public AzureBlobDownloader(Activity act, String userName){
        super();
        this.act = act;
        this.userName = userName;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        ArrayList<String> databasePaths = new ArrayList<>();

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

            //THIS IS THE PROBLEM RIGHT HERE :/
            final List<Image> dbImages = this.getImageTable().execute().get();

            //add prefix for https and blob storage navigation
            String prefix = "https://boephotostore.blob.core.windows.net/photocontainer/";

            //Grab the final reference to the blob url

            for(Image img : dbImages){
                azurePaths.add(prefix + img.getId().replaceFirst("_", "/") + getSas());
            }


        } catch (Exception e) {
            Log.d("Azure", e.toString());
        }

        return azurePaths;
    }

    /*The Paths of the images are then added to the Adapter
    * And if we did it right, then the images should fill the
    * screen*/

    @Override
    protected void onPostExecute(Object o) {
        ArrayList<String> results = (ArrayList<String>)o;

        GridView imageGrid = (GridView) this.act.findViewById(R.id.gridView4);

        try {
            imageGrid.setAdapter(new ImageAdapterHistory(this.act.getApplicationContext(), results));
        }catch(NullPointerException e){
            e.printStackTrace();

        }

    }
}
