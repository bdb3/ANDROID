package com.example.edwin.photoarchive.Activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.edwin.photoarchive.Adapters.AzureServiceAdapter;
import com.example.edwin.photoarchive.AzureClasses.ICAV;
import com.example.edwin.photoarchive.AzureClasses.Image;
import com.example.edwin.photoarchive.Helpers.AsyncTaskLoaderEx;
import com.microsoft.windowsazure.mobileservices.*;
import com.example.edwin.photoarchive.R;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.List;


public class HistoryViewTags extends AppCompatActivity {
    private MobileServiceClient mClient;
    private List<ICAV> icavList;
    private List<Image> imageList;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view_tags);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tags");

        final Bundle extras = getIntent().getExtras();
        final LinearLayout linearLayoutTags = (LinearLayout) findViewById(R.id.linearLayoutHistoryTags);

        // AsyncTaskLoader to Load the Data from the ICAV table
        class DataLoadingTask extends AsyncTaskLoaderEx<List<ICAV>> {

            public DataLoadingTask(Context context) {
                super(context);
            }

            @Override
            public List<ICAV> loadInBackground() {
                //Trim Imagepath String USES SAS PATTERN
                key = extras.getString("imagePath");
                key = key.replace("https://boephotostore.blob.core.windows.net/photocontainer/", "");
                key = key.replaceFirst("/", "_");
                key = key.substring(0, key.indexOf("?sv="));
                Log.d("Key", key);
                // Start MobileServiceClient
                try {
                    mClient = AzureServiceAdapter.getInstance().getClient();
                } catch (Exception e) {
                    Log.d("HistoryViewTags", "Mobile Service Client Failure");
                }

                MobileServiceTable<ICAV> icavMobileServiceTable = mClient.getTable(ICAV.class);
                MobileServiceTable<Image> imageMobileServiceTable=mClient.getTable(Image.class);
                try {
                    icavList = mClient.getTable(ICAV.class).where().startsWith("ImageID",key).execute().get();
                    return icavList;
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("HistoryViewTags", "ICAV List Failure");
                }
                return null;
            }
        }
        getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<List<ICAV>>() {
            private Context context=getApplicationContext();
            @Override
            public Loader<List<ICAV>> onCreateLoader(final int id, final Bundle args) {
                return new DataLoadingTask(HistoryViewTags.this);
            }

            @Override
            public void onLoadFinished(final Loader<List<ICAV>> loader, final List<ICAV> result) {
                if (result == null)
                    return;
                ICAV first=result.get(0);
                // Should define via an adapter but didn't
                for (ICAV icav:result) {
                    Button b = new Button(context);
                    b.setText(icav.getContextID());
                    b.setClickable(false);
                    linearLayoutTags.addView(b);

                    TextView q = new TextView(context);
                    q.setTextColor(Color.BLACK);
                    q.setText(icav.getAttributeID());
                    linearLayoutTags.addView(q);

                    EditText a = new EditText(context);
                    a.setTextColor(Color.BLACK);
                    a.setText(icav.getValue());
                    a.setEnabled(false);
                    linearLayoutTags.addView(a);
                }
                TextView q = new TextView(context);
                q.setTextColor(Color.BLACK);
                q.setText("\nDate Created: \n"+first.getCreatedAt());
                linearLayoutTags.addView(q);
            }

            @Override
            public void onLoaderReset(final Loader<List<ICAV>> loader) {
            }
        });

    }

}


