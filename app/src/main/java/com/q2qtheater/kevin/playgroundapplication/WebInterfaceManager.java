package com.q2qtheater.kevin.playgroundapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.ShowInformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Kevin on 11/29/15.
 */
public class WebInterfaceManager {
    private static final String programInfoURL =
            "https://docs.google.com/spreadsheets/d/1cWkRhxul9vv7amo6ASlC2PyRal57hYwJdW09f7FE0Sw/pub?gid=0&single=true&output=csv";
    /**
     * Retrieves program information from the web, and stores it in android file system
     */
    public static boolean getProgramInformation(Context applicationContext){
        ConnectivityManager connMgr =
                (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected()){
            return false;
        }
        new FetchProgramInformation().execute(programInfoURL);

        return true;
    }

    //AsyncTask creates a new task thread.
    private static class FetchProgramInformation extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls){
            String resultString = "";
            try {
                resultString = downloadUrl(urls[0]);
            }catch (IOException e){
                resultString = "Unable to retrieve web page.";
            }
            return resultString;
        }

        /**
         * Send the response to the Content Manager
         *
         * @param result the string representation of the download
         */
        @Override
        protected void onPostExecute(String result){
            Log.d("full response", result);
            ContentManager.parseGoogleSheetResponse(result);
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private String downloadUrl(String myurl) throws IOException {
            InputStream is = null;
            // Only display the first 500 characters of the retrieved
            // web page content.
            String contentAsString;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                //expect HTTP 200 OK
                if(conn.getResponseCode() != HttpURLConnection.HTTP_OK){
                    return "";
                }

                is = conn.getInputStream();

                // Convert the InputStream into a string
                contentAsString = readIt(is);

                // Makes sure that the InputStream is closed after the app is
                // finished using it.
            } finally {
                if (is != null) {
                    is.close();
                }
            }
            return contentAsString;

        }

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            StringBuilder sb = new StringBuilder();
            reader = new InputStreamReader(stream, "UTF-8");
            int bits = 1;
            char[] buffer = new char[500];
            while(bits > 0) {
                bits = reader.read(buffer);
                if(bits == 500) {
                    sb.append(buffer);
                }else{
                    for(int i = 0; i < bits; i++){
                        sb.append(buffer[i]);
                    }
                }
            }
            return sb.toString();
        }
    }
}

