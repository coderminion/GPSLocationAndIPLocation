package com.coderminion.gpslocation.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import static com.coderminion.gpslocation.utils.UtilityMethods.buildAlertMessageNoGps;

public class GoogleLocationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "GOOGLE_LOCATION";
    private GoogleApiClient mGoogleApiClient;
    private final static int REQUEST_PERMISSION = 1212;
    private final static int REQUEST_PERMISSION_SETTING = 1211;
    private final static int PLAY_LOCATION = 1223;
    LocationManager manager;
    private AddressResultReceiver mResultReceiver;

    Button btn_getlocation;

    TextView latTextView,lonTextView,address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_location);

        if (isGooglePlayServicesAvailable(GoogleLocationActivity.this)) {
                buildGoogleApiClient();
        }
         manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        latTextView = (TextView) findViewById(R.id.lat);
        lonTextView = (TextView) findViewById(R.id.lon);
        address = (TextView) findViewById(R.id.address);
        btn_getlocation = (Button) findViewById(R.id.btn_getlocation);

        btn_getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayLocation(PLAY_LOCATION);
            }
        });

        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    private void displayLocation(int PLAY_LOCATION) throws SecurityException {

        if(ActivityCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(GoogleLocationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION);
        }
        else
        {
            if (!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            buildAlertMessageNoGps(this, PLAY_LOCATION);
        }
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                Log.e("Latitude " + latitude, "Longitude " + longitude);
                latTextView.setText("Latitude " + latitude);
                lonTextView.setText("Longitude " + longitude);
                startIntentService(latitude,longitude);
            }
            else {
                if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
                Toast.makeText(getApplicationContext(),"Can not get the location try again",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Creating google api client object to use google play services
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        Log.v(TAG, "buildGoogleApiClient");
    }

    @Override
    protected void onStart() {

        Log.v("Tag_OnStart", "true");
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "onConnected !!! :D");
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Log.v(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }


    /**
     * Method to verify google play services on the device
     */
    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
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
                displayLocation(PLAY_LOCATION);
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
                    ActivityCompat.requestPermissions(GoogleLocationActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_PERMISSION);
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_LOCATION) {
            displayLocation(PLAY_LOCATION);
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
