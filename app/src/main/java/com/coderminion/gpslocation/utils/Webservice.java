package com.coderminion.gpslocation.utils;

/**
 * Created by vaibhav on 11/8/17 .
 */

import android.util.Log;


import com.coderminion.gpslocation.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.HashMap;


/**
 * Created by vaibhav on 2/8/17 .
 */

public class Webservice {

    private static final String GET_REQUEST = "GET";
    private static final String POST_REQUEST = "POST";


    //TODO Assign LOCAL or REMOTE URL in single shot
    private static final String BASE_URL = "http://ip-api.com";

    private static String CommonExceptionHandler(Exception e1) {
        String temp;
        temp = "";
        if (e1 instanceof SocketTimeoutException) {
            temp = "{timeout_error:true}";
        } else if (e1 instanceof UnknownHostException) {
            temp = "{internet_error:false}";
        } else if (e1 instanceof java.io.FileNotFoundException) {
            temp = "{FileNotFound_error: true}";
        } else if (e1 instanceof java.net.ConnectException) {
            temp = "{connectionerror_error:true}";
        }
        return temp;
    }

    //You can set as per your needs
    private static final String USERAGENT = "Android " + BuildConfig.FLAVOR + " Version " + BuildConfig.VERSION_NAME + "VersionCode " + BuildConfig.VERSION_CODE;

    /* ************************************************************** HttpURLConnection Non-Depricated ****************************************************************************************** */
    //Common method calling to server..
    private static JSONObject sendServer(HashMap<String, String> params, String serviceURL, String methodOfRequest) {

        String COOKIE_HEADERS = "";

        String charset = "UTF-8";
        HttpURLConnection conn = null;
        DataOutputStream wr;
        StringBuilder result;
        URL urlObj;
        StringBuilder sbParams;
        String paramsString;
        int httpResponseCode = 0;
        serviceURL = BASE_URL + serviceURL;

        sbParams = new StringBuilder();
        try {
            int i = 0;
            for (String key : params.keySet()) {

                if (i != 0) {
                    sbParams.append("&");
                }
                try {
                    String param = params.get(key);
                    if (param == null)
                        param = "";

                    sbParams.append(key).append("=").append(URLEncoder.encode(param, charset));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                i++;
            }

            if (methodOfRequest.equals(POST_REQUEST)) {
                //REQUESTED METHOD IS POST
                urlObj = new URL(serviceURL);
                Log.v("Test URL POST", serviceURL + sbParams);
                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setRequestProperty("Accept", charset);
                conn.setRequestProperty("User-Agent", USERAGENT);//You can set as per your needs
                conn.setRequestProperty("Cookie", COOKIE_HEADERS);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                Log.i("URL:", serviceURL);

                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();

                paramsString = sbParams.toString();

                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();
            } else if (methodOfRequest.equals(GET_REQUEST)) {

                if (sbParams.length() != 0) {
                    serviceURL += "?" + sbParams.toString();
                }
                urlObj = new URL(serviceURL);

                conn = (HttpURLConnection) urlObj.openConnection();
                Log.v("Test URL POST", serviceURL);
                conn.setRequestProperty("Accept", charset);
                conn.setRequestProperty("User-Agent", USERAGENT);
                conn.setRequestProperty("Cookie", COOKIE_HEADERS);
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.setDoOutput(false);
                conn.setRequestMethod("GET");
                conn.connect();
            }

            Log.i("URL:", serviceURL);

            //Check responsecode
            httpResponseCode = conn.getResponseCode();
            String httpResonseString = conn.getResponseMessage();
            Log.v("Response: Code,String :", httpResponseCode + "  " + httpResonseString);
            Log.v("Method Req  ", methodOfRequest);


            System.out.println("" + COOKIE_HEADERS);

            if (httpResponseCode != HttpURLConnection.HTTP_OK) {
                Log.e("Error Stream", conn.getErrorStream().toString());
                String temp = "";
                if (httpResponseCode == 404)
                    temp = "{contentnotfound_error:true}";
                else if (httpResponseCode == 401)
                    temp = "{loginfeature_error:true}";
                else if (httpResponseCode == 500)
                    temp = "{internalservererr_error:true}";
                else if (httpResponseCode == 400)
                    temp = "{badrequest_error:true}";

                return new JSONObject(temp);
            }

            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            System.out.print(""+result);
            conn.disconnect();

            JSONObject object = new JSONObject(result.toString());
            Log.i("Response ",result.toString());
            return object;

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            String temp = "";

            if (temp.length() == 0) {
                temp = CommonExceptionHandler(e1);
            }
            try {
                Log.v("message", temp);
                return new JSONObject(temp);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static JSONObject getLocation(String serviceUrl)
    {
        HashMap<String,String> hashMap = new HashMap<>();

        JSONObject object = sendServer(hashMap,serviceUrl ,GET_REQUEST);

        return object;
    }

}