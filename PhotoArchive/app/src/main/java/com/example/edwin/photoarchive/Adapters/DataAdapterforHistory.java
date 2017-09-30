package com.example.edwin.photoarchive.Adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.example.edwin.photoarchive.AzureClasses.ICAV;

/**
 * Created by Redfish on 9/30/2017.
 */

public class DataAdapterforHistory extends ArrayAdapter<ICAV> {
    Context mContext;

    /**
     * Adapter View layout
     */
    int mLayoutResourceId;


    public DataAdapterforHistory(Context context, int layoutResourceId) {
        super(context, layoutResourceId);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

}
