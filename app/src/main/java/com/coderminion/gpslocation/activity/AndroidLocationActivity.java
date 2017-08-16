package com.coderminion.gpslocation.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.coderminion.gpslocation.data.Constants;
import com.coderminion.gpslocation.R;
import com.coderminion.gpslocation.services.FetchAddressIntentService;

import static com.coderminion.gpslocation.utils.UtilityMethods.buildAlertMessageNoGps;

public class AndroidLocationActivity extends AppCompatActivity {

    private final static int SINGLE_LOCATION = 1010;
    private final static int REQUEST_PERMISSION = 1212;
    private final static int REQUEST_PERMISSION_SETTING = 1211;

    LocationManager manager = null;
    LocationListener locationListener;
    Button btn_getlocation;
    TextView latTextView, lonTextView,address;
    private AddressResultReceiver mResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_androids_location);

        latTextView = (TextView) findViewById(R.id.lat);
        lonTextView = (TextView) findViewById(R.id.lon);
        address = (TextView) findViewById(R.id.address);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        btn_getlocation = (Button) findViewById(R.id.btn_getlocation);
        btn_getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_getlocation.getText().toString().equals("Remove Updates"))
                {
                    if (locationListener != null)
                        if (ActivityCompat.checkSelfPermission(AndroidLocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
                            manager.removeUpdates(locationListener);
                            btn_getlocation.setText("Get Location Updates");
                        }
                }
                else {
                    displayLocation(SINGLE_LOCATION);
                }
            }
        });
        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    /**
     * Method to display the location on UI.
     */
    private void displayLocation(int singleLocation) {

        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(AndroidLocationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION);
        }
        else {
            if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                buildAlertMessageNoGps(this, singleLocation);
            }
            try {

                if (manager != null) {

                    //TODO: Location location = UtilityMethods.getLastKnownLocation(manager);
                    // For getting location at once
                    // Define a listener that responds to location updates
                     locationListener = new LocationListener() {
                        public void onLocationChanged(Location location) {
                            // Called when a new location is found by the network location provider.
                            if(location!=null)
                                makeUseOfNewLocation(location);
                        }

                         public void onStatusChanged(String provider, int status, Bundle extras) {}

                        public void onProviderEnabled(String provider) {}

                        public void onProviderDisabled(String provider) {}
                    };

                    // Register the listener with the Location Manager to receive location updates
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    btn_getlocation.setText("Remove Updates");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void makeUseOfNewLocation(Location location) {
        latTextView.setText(String.valueOf(location.getLatitude()));
        lonTextView.setText(String.valueOf(location.getLongitude()));
        startIntentService(location.getLatitude(),location.getLongitude());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SINGLE_LOCATION) {
            displayLocation(SINGLE_LOCATION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // for each permission check if the user granted/denied them
            // you may want to group the rationale in a single dialog,
            // this is just an example

                String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                    if(ActivityCompat.checkSelfPermission(getApplicationContext(),permission) == PackageManager.PERMISSION_GRANTED)
                    {
                        //ALL OKAY
                        displayLocation(SINGLE_LOCATION);
                    }
                    else
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(),permission) == PackageManager.PERMISSION_DENIED) {
                    // user rejected the permission
                    boolean showRationale = shouldShowRequestPermissionRationale( permission );
                    if (! showRationale) {
                        // user also CHECKED "never ask again"
                        //Take user to settings screen
                        Toast.makeText(getApplicationContext(),"Location Permission is required to complete the task",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);

                    } else {
                        // user did NOT check "never ask again"
                        Toast.makeText(getApplicationContext(),"Location Permission is required to complete the task",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(AndroidLocationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION);
                    }
                }
            }
    }


    private void startIntentService(double lat,double lon) {

        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA,location);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent);
    }



    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            String mAddressOutput = resultData.getString(Constants.RESULT_DATA_KEY);

            Toast.makeText(getApplicationContext(),mAddressOutput,Toast.LENGTH_SHORT).show();
            address.setText("ADDRESS :"+mAddressOutput);
            // Show a toast message if an address was found.
            if (resultCode == Constants.SUCCESS_RESULT) {
                Log.i("Success","true");
            }

        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }



}
