package com.example.edwin.photoarchive.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edwin.photoarchive.AzureClasses.ICAV;
import com.example.edwin.photoarchive.R;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.util.List;


public class HistoryViewTags extends AppCompatActivity {
    private MobileServiceClient dbClient;
    private MobileServiceTable<ICAV> icavTable;
    private List<ICAV> listofICAVs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_view_tags);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Tags");

        Bundle extras = getIntent().getExtras();
        final LinearLayout linearLayoutTags = (LinearLayout) findViewById(R.id.linearLayoutHistoryTags);

        try {
            //get the database paths
            MobileServiceClient dbClient = new MobileServiceClient(
                    "http://boephotoarchive-dev.azurewebsites.net",
                    this.getApplicationContext()
            );
        }
        catch(MalformedURLException e){
                e.printStackTrace();
            }

            icavTable= dbClient.getTable(ICAV.class);

        try {
            listofICAVs = icavTable.execute().get();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        for (ICAV icav: listofICAVs){
            Button b = new Button(this);
            b.setText("Context " +icav.getContextID());
            b.setClickable(false);
            linearLayoutTags.addView(b);

            TextView q = new TextView(this);
            q.setTextColor(Color.BLACK);
            q.setText(icav.getAttributeID());
            linearLayoutTags.addView(q);

            EditText a = new EditText(this);
            a.setText(icav.getValue());
            a.setEnabled(false);
            linearLayoutTags.addView(a);
        }
    }

}


