package com.example.edwin.photoarchive.Adapters;

/**
 * Created by Redfish on 10/3/2017.
 */

import android.content.Context;
import android.util.Log;

import com.microsoft.windowsazure.mobileservices.*;


public class AzureServiceAdapter {
    private String mMobileBackendUrl = "http://boephotoarchive-dev.azurewebsites.net";
    private Context mContext;
    private MobileServiceClient mClient;
    private static AzureServiceAdapter mInstance = null;

    private AzureServiceAdapter(Context context) {
        mContext = context;
        try {
            mClient = new MobileServiceClient(mMobileBackendUrl, mContext);
        }
        catch(Exception e){
            Log.d("AzureServiceAdapter","Failure Loading Azure Services");
        }
    }

    public static void Initialize(Context context) {
        if (mInstance == null) {
            mInstance = new AzureServiceAdapter(context);
        } else {
            throw new IllegalStateException("AzureServiceAdapter is already initialized");
        }
    }

    public static AzureServiceAdapter getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException("AzureServiceAdapter is not initialized");
        }
        return mInstance;
    }

    public MobileServiceClient getClient() {
        return mClient;
    }

    // Place any public methods that operate on mClient here.
}