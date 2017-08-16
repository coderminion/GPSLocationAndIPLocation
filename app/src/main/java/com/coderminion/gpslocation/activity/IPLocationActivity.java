package com.coderminion.gpslocation.activity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.coderminion.gpslocation.R;
import com.coderminion.gpslocation.utils.ServerResponseHandler;
import com.coderminion.gpslocation.utils.Webservice;

import org.json.JSONException;
import org.json.JSONObject;

public class IPLocationActivity extends AppCompatActivity {

    Button btn_getlocation;
    TextView latTextView, lonTextView,address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iplocation);


        latTextView = (TextView) findViewById(R.id.lat);
        lonTextView = (TextView) findViewById(R.id.lon);
        address = (TextView) findViewById(R.id.address);

        btn_getlocation = (Button)findViewById(R.id.btn_getlocation);

        btn_getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new GetLocation().execute();
            }
        });

    }

    private class GetLocation extends AsyncTask<JSONObject,JSONObject,JSONObject>
    {

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(IPLocationActivity.this);
            progressDialog.setTitle("Please wait..");
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(JSONObject... params) {
            JSONObject object = Webservice.getLocation("/json");  //http://ip-api.com/json will be our whole URL
            return object;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            if(ServerResponseHandler.jsonDataHandler(IPLocationActivity.this,jsonObject))
            {
                try {


                    String city = jsonObject.getString("city");
                    String regionName = jsonObject.getString("regionName");
                    String country = jsonObject.getString("country");
                    String zip = jsonObject.getString("zip");

                    String lat = jsonObject.getString("lat");
                    String lon = jsonObject.getString("lon");

                    String saperator = ", ";

                    latTextView.setText("Latitude : "+lat);
                    lonTextView.setText("Longitude : "+lon);

                    address.setText("Address : "+city+saperator+regionName+saperator+country+saperator+"ZIP :"+zip);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
