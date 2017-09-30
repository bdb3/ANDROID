package com.example.edwin.photoarchive.Activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.edwin.photoarchive.Adapters.DataAdapterforHistory;
import com.example.edwin.photoarchive.AzureClasses.ICAV;
import com.example.edwin.photoarchive.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class HistoryViewTags extends AppCompatActivity {
    private MobileServiceTable<ICAV> icavTable;
    private DataAdapterforHistory mAdapter;
    private EditText mTextNewToDo;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view_tags);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tags");

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            MobileServiceClient mClient = new MobileServiceClient(
                    "http://boephotoarchive-dev.azurewebsites.net",
                    this);

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            icavTable = mClient.getTable(ICAV.class);

        } catch (MalformedURLException e) {
            Log.d("Historytab", "There was an error creating the Mobile Service. Verify the URL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bundle extras = getIntent().getExtras();
        imagePath = extras.getString("imagePath");
        // TODO SECURITY RISK
        imagePath=imagePath.replace("?sv=2017-04-17&ss=bfqt&srt=sco&sp=rwdlacup&se=2018-11-10T03:26:58Z&st=2017-09-27T19:26:58Z&spr=https&sig=YKozpcGRTl3LZSMUJgBCmmmLNSteOSeAZPX8dNsARLA%3D","");
        imagePath=imagePath.replace("https://boephotostore.blob.core.windows.net/photocontainer/","");
        imagePath=imagePath.replaceFirst("/","_");
        Log.d("HistoryView", imagePath);
        mAdapter = new DataAdapterforHistory(this, R.layout.activity_history_view_tags);
        ListView listViewToDo = (ListView) findViewById(R.id.mAdapterListView);
        listViewToDo.setAdapter(mAdapter);
        refreshItemsFromTable();
//        final LinearLayout linearLayoutTags = (LinearLayout) findViewById(R.id.linearLayoutHistoryTags);
//
//        for (int i=0; i<3; i++){
//            Button b = new Button(this);
//            b.setText("Context " +i);
//            b.setClickable(false);
//            linearLayoutTags.addView(b);
//
//            TextView q = new TextView(this);
//            q.setTextColor(Color.BLACK);
//            q.setText("question");
//            linearLayoutTags.addView(q);
//
//            EditText a = new EditText(this);
//            a.setText("answer");
//            a.setEnabled(false);
//            linearLayoutTags.addView(a);
//        }
    }

    private void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<ICAV> results = refreshItemsFromMobileServiceTable();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (ICAV item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        };

        task.execute();
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<ICAV> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        try {
            return icavTable.where().startsWith("ImageID",imagePath).execute().get();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}


