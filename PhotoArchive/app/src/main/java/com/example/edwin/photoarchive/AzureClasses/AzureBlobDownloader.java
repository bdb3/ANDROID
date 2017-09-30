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
import java.util.HashMap;
import java.util.List;

public class AzureBlobDownloader extends AzureBlobLoader{
    private Activity act;
    private String userName;
    //TODO SQL server path
    private String urlPath = "http://boephotoarchive-dev.azurewebsites.net";

    public AzureBlobDownloader(Activity act, String userName){
        super();
        this.act = act;
        this.userName = userName;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        ArrayList<String> databasePaths = new ArrayList<>();
        //Object[] results=new Object[3];
        //We create an array list to store the final paths
        ArrayList<String> azurePaths = new ArrayList<>();
        //HashMap<String,ArrayList<String>> icavObjects=new HashMap<>();
        try {
            //get the database paths
            this.setDBClient(
                    new MobileServiceClient(
                            urlPath,
                            this.act.getApplicationContext()
                    )
            );

            this.setImageTable(this.getDBClient().getTable(Image.class));
            this.setIcavTable(this.getDBClient().getTable(ICAV.class));

            Log.d("Azure", "About to execute query");

            //THIS IS THE PROBLEM RIGHT HERE :/
            final List<Image> dbImages = this.getImageTable().execute().get();
            //final List<ICAV> dbICAV= this.getIcavTable().execute().get();
            //add prefix for https and blob storage navigation
            //TODO Blob Storage Full path
            String prefix = "https://boephotostore.blob.core.windows.net/photocontainer/";

            //Grab the final reference to the blob url
            Log.d("Azure",""+dbImages.size());

//            for(ICAV icav: dbICAV){
//                //ADD AN IF STATEMENT IF YOU ARE LOOKING FOR A SPECFIC CONTEXT
//                ArrayList<String> keys=new ArrayList<>();
//                keys.add(icav.getContextID());
//                keys.add(icav.getAttributeID());
//                keys.add(icav.getValue());
//                icavObjects.put(icav.getImageID().replaceFirst("_","/"),keys);
//            }
            for(Image img : dbImages){

                Log.d("Images Loading",prefix + img.getId().replaceFirst("_", "/") +getSas());
                azurePaths.add(prefix + img.getId().replaceFirst("_", "/") + getSas());
            }


        } catch (Exception e) {
            Log.d("Azure", e.toString()); e.printStackTrace();
        }
       // results[0]=azurePaths; // ArrayList<String>
//        results[1]=icavObjects; // HashMap<String,ArrayList<String>>
        return azurePaths;
    }
    // TODO Returns an Array of Context,Attribute, AzurePaths,
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
