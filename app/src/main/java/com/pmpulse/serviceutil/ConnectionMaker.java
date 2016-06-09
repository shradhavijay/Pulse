package com.pmpulse.serviceutil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.pmpulse.data.KeyValues;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class ConnectionMaker {


    public static final ConnectionMaker conMaker = new ConnectionMaker();
    public static final String METHOD_POST = "POST";
    public static final String METHOD_GET = "GET";

    // To check internet connectivity in device
    public boolean isConnected(Context context) {
        final ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) return true;
        else return false;
    }

    public String service(String url, String methodType) {
        if(KeyValues.isDebug)
       System.out.println("url "+url);
        HttpURLConnection con = null;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        try {
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod(methodType);
            //con.setDoInput(false);
            con.setDoOutput(false);
            con.setRequestProperty("Content-Type", "application/json");

            con.setConnectTimeout(KeyValues.TIMEOUT);
            con.connect(); // Let's read the response

            try {
                is = con.getInputStream();
                outStream = new ByteArrayOutputStream();

                byte[] returnData = new byte[1024];
                int size = -1;

                while ((size = is.read(returnData)) != -1) {
                    outStream.write(returnData, 0, size);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            byte[] responseByte = outStream.toByteArray();
            String response = new String(responseByte);
            con.disconnect();
            return response;
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Throwable t) {
            }
            try {
                con.disconnect();
            } catch (Throwable t) {
            }
        }
        return null;
    }
}
