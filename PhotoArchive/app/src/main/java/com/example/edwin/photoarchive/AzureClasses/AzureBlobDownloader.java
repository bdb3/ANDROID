package com.example.edwin.photoarchive.AzureClasses;

import android.app.Activity;
import android.util.Log;
import android.widget.GridView;

import com.bumptech.glide.Glide;
import com.example.edwin.photoarchive.Adapters.AzureServiceAdapter;
import com.example.edwin.photoarchive.Adapters.ImageAdapterHistory;
import com.example.edwin.photoarchive.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Set;

public class AzureBlobDownloader extends AzureBlobLoader{
    private Activity act;
    private String userName;
    private String filter;
    //TODO SQL server path
    private String urlPath = "http://boephotoarchive-dev.azurewebsites.net";

    public AzureBlobDownloader(Activity act, String userName,String filter){
        super();
        this.act = act;
        this.userName = userName;
        this.filter=filter;
    }
    @Override
    protected Object doInBackground(Object[] params) {

        //We create an array list to store the final paths
        ArrayList<String> azurePaths = new ArrayList<>();
        try {
            //get the database paths
            this.setDBClient(AzureServiceAdapter.getInstance().getClient()
            );

            this.setImageTable(this.getDBClient().getTable(Image.class));
            this.setIcavTable(this.getDBClient().getTable(ICAV.class));

            Log.d("Azure", "About to execute query");

            //THIS IS THE PROBLEM RIGHT HERE :/

            List<ICAV> dbImages = this.getIcavTable().where().field("contextID").eq(filter).execute().get();


            //add prefix for https and blob storage navigation
            //TODO Blob Storage Full path
            String prefix = "https://boephotostore.blob.core.windows.net/photocontainer/";

            //Grab the final reference to the blob url
            Log.d("Azure",""+dbImages.size());

            for(ICAV img : dbImages){

                //Log.d("Images Loading",prefix + img.getImageID().replaceFirst("_", "/") +getSas());
                azurePaths.add(prefix + img.getImageID().replaceFirst("_", "/") + getSas());
            }

            Set<String> convertedPaths=new LinkedHashSet<>(azurePaths);
            azurePaths=new ArrayList<String>(convertedPaths);
        } catch (Exception e) {
            Log.d("Azure", e.toString()); e.printStackTrace();
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
            ImageAdapterHistory imageAdapter=new ImageAdapterHistory(this.act.getApplicationContext(), results);
            imageGrid.setAdapter(imageAdapter);
            imageAdapter.notifyDataSetChanged();
        }catch(NullPointerException e){
            e.printStackTrace();

        }

    }
}
