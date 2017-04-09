package com.example.edwin.photoarchive.AzureClasses;

import android.app.Activity;
import android.content.*;
import android.nfc.Tag;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

import com.example.edwin.photoarchive.Activities.TagsActivity;
import com.example.edwin.photoarchive.Adapters.ImageAdapterDashboard;
import com.example.edwin.photoarchive.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AzureBlobUploader extends AzureBlobLoader {
    private Activity act;
    private String userName;
    private TaggedImageObject img;

    public AzureBlobUploader(Activity act, String userName, TaggedImageObject img) {
        super();
        this.act = act;
        this.userName = userName;
        this.img = img;
    }


    @Override
    protected Object doInBackground(Object[] params) {
        File imageFile = new File(this.img.getImgPath());

        try {
            //-----BLOB CONTAINER----//
            InputStream in = new FileInputStream(imageFile);
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();

            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(buffer);
            fos.close();

            // Define the path to a local file.
            final String filePath = imageFile.getPath();

            // Create or overwrite the blob with contents from the local file.
            String[] imagePathArray = filePath.split("/");
            String imageName = imagePathArray[imagePathArray.length-1];

            System.out.println("Image Name: " + imageName);

            String containerName = userName + "/" + imageName;

            System.out.println("Container Name: " + containerName);

            CloudBlockBlob blob = this.getContainer().getBlockBlobReference(containerName);
            System.out.println("Properties"  + blob.getProperties());

            //UPLOAD!
            blob.upload(new FileInputStream(imageFile), imageFile.length());

            //-----DATABASE-----//
            //create client
            this.setDBClient(
                    new MobileServiceClient(
                            "https://boephotoarchive-dev.azurewebsites.net",
                            this.act.getApplicationContext()
                    )
            );

            this.setImageTable(this.getDBClient().getTable(Image.class));
            this.setIcavTable(this.getDBClient().getTable(ICAV.class));

            //IMG TABLE QUERY
            String validImageID = containerName.replace("/", "_");
            Log.d("Azure", "Valid Image ID: " + validImageID);

            Image img = new Image(validImageID, this.img.getUser(), this.img.getLat(), this.img.getLon());
            this.getImageTable().insert(img);

            for(String context : this.img.getContextAttributeMap().keySet()){
                Map<String,String> attributeValueMap = this.img.getContextAttributeMap().get(context);

                for(String attribute : attributeValueMap.keySet()){
                    String value = attributeValueMap.get(attribute);

                    //ICAV QUERY
                    ICAV icavRow = new ICAV();
                    icavRow.setImageID(validImageID);
                    icavRow.setContextID(context);
                    icavRow.setAttributeID(attribute);
                    icavRow.setValue(value);

                    this.getIcavTable().insert(icavRow);
                }

            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        //access shared preferences and remove the path
        SharedPreferences sp = this.act.getSharedPreferences(TagsActivity.MyTagsPREFERENCES, android.content.Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        String savedArraylist  = sp.getString("listOfImagesWithTags", null);
        Type listType = new TypeToken<ArrayList<TaggedImageObject>>(){}.getType();
        List<TaggedImageObject> taggedImageObjectsList = new Gson().fromJson(savedArraylist, listType);

        int toBeRemoved = -1;

        for(TaggedImageObject t : taggedImageObjectsList){
            if(t.getImgPath().equals(this.img.getImgPath())){
                Log.d("Azure", "MATCH!");
                toBeRemoved = taggedImageObjectsList.indexOf(t);
            }
        }

        //remove the index
        taggedImageObjectsList.remove(toBeRemoved);

        //resave it
        String taggedImageslistAsString = new Gson().toJson(taggedImageObjectsList);
        editor.remove("listOfImagesWithTags");
        editor.apply();
        editor.putString("listOfImagesWithTags", taggedImageslistAsString);
        editor.apply();

        //grab the GridView and update it!
        GridView imageGrid = (GridView)this.act.findViewById(R.id.gridview);
        ImageAdapterDashboard imagesAdapter = (ImageAdapterDashboard)imageGrid.getAdapter();
        imagesAdapter.removePath(this.img.getImgPath());

        //refresh
        imagesAdapter.notifyDataSetChanged();

        TextView photosToBeUploaded = (TextView)this.act.findViewById(R.id.textView20);
        photosToBeUploaded.invalidate();
        photosToBeUploaded.setText(imagesAdapter.getCount() + " image(s) waiting to upload");
    }
}
