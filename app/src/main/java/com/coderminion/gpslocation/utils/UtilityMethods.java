package com.coderminion.gpslocation.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.widget.Toast;

import java.util.List;

/**
 * Created by vaibhav on 9/8/17 .
 */

public class UtilityMethods {
    public static void buildAlertMessageNoGps(final Activity activity, final int request_code) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), request_code);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Toast.makeText(activity.getApplicationContext(), "GPS is off", Toast.LENGTH_SHORT).show();
                        //DO nothing
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //GET last known location :
    public static Location getLastKnownLocation(LocationManager manager) throws SecurityException {
        List<String> providers = manager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {

            Location l = manager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null
                    || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        if (bestLocation == null) {
            return null;
        }
        return bestLocation;
    }


}
