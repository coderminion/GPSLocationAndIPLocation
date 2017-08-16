package com.coderminion.gpslocation.utils;

/**
 * Created by vaibhav on 11/8/17 .
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;


import com.coderminion.gpslocation.R;

import org.json.JSONObject;

/**
 * Created by vaibhav on 2/8/17 .
 */
public class ServerResponseHandler {
    //Common Method for all responses by server :TO Show Alerts or Toasts
    public static Boolean jsonDataHandler (Activity activity, JSONObject responseJSON) {
        try {
            String message="",title="";

            String[] excepArray = {
                    "timeout_error",
                    "internet_error",
                    "servererror_error",
                    "contentnotfound_error",
                    "loginfeature_error",
                    "badrequest_error",
                    "internalservererr_error",
                    "connectionerror_error"
            };

            int caseSelect;
            caseSelect = 9;
            for (int i = 0; i < excepArray.length; i++) {
                if (responseJSON.has(excepArray[i])) {
                    caseSelect = i;
                    break;
                }
            }

            switch (caseSelect) {

                case 0:

                    message = activity.getResources().getString(R.string.timeout);
                    title = "Timeout";
                    break;
                case 1:

                    message = activity.getResources().getString(R.string.internet_problem);
                    title = "Internet Connection";

                    break;

                case 2:

                    message = activity.getResources().getString(R.string.connectionerror);
                    title = "Connection Error.";
                    break;

                case 3:

                    message = activity.getResources().getString(R.string.contentnotfound);
                    title = "Page Not Found";

                    break;

                case 4:

                    message = activity.getResources().getString(R.string.unauthorized);
                    title = "Unauthorized";
                    break;

                case 5:

                    message = activity.getResources().getString(R.string.badrequest);
                    title = "Bad Request";

                    break;

                case 6:

                    message = activity.getResources().getString(R.string.serverexception);
                    title = "Server Problem";
                    break;

                case 7: //For coding exception

                    message = activity.getResources().getString(R.string.connectionerror);
                    title = "Connection Error";
                    break;

                default: return true;

            }

            if(!title.equals(""))
            {
                new AlertDialog.Builder(activity)
                        .setTitle(title)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}