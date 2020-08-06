package com.example.meld.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.net.ContentHandler;
import java.net.HttpURLConnection;
import java.net.URL;

public class DependencyValidation {

    private static final String CONNECTION_LOG_TAG = "NO CONNECTION";

    private static boolean isDeviceOnline() {
        int response;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
            httpURLConnection.setRequestProperty("User-Agent", "Test");
            // no need to keep connection alive
            httpURLConnection.setRequestProperty("Connection", "close");
            httpURLConnection.setConnectTimeout(4000);
            httpURLConnection.connect();

            response = httpURLConnection.getResponseCode();

        } catch (Exception e) {
            response = 0;
            Log.e(CONNECTION_LOG_TAG, "Error checking internet connection", e);
        }
        return response == 200;
    }



    /**
     * Check that Google Play services APK is installed and up to date.
     * @return true if Google Play Services is available and up to
     *     date on this device; false otherwise.
     */
    private static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    /**
     * Attempt to resolve a missing, out-of-date, invalid or disabled Google
     * Play Services installation via a user dialog, if possible.
     */
    private static void acquireGooglePlayServices(Context context) {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(context);
//        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
//            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
//        }
    }


}
