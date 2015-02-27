package com.q2qtheater.kevin.playgroundapplication;

import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.BreakInformation;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.InstallationInformation;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.ShowInformation;

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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.*;

/**
 * Created by Kevin on 1/26/15.
 *
 * This class manages all content for the application, including web connections
 * to check for updates to the internal show and installation lists
 *
 */
public class ContentManager {
    private static final String jsonWebsite = "http://kevinmkarol.com/playgroundapp";
    private static Context applicationContext;
    private static PageViewManager currentActivity;

    private static final String showStorage = "showInfo";
    private static final String installationStorage = "installationInfo";
    private static final String breakStorage = "breakInfo";
    private static final String lastUpdateStorage = "lastUpdate";


    private static ArrayList<ShowInformation> thursdayShows = null;
    private static ArrayList<ShowInformation> fridayShows = null;
    private static ArrayList<ShowInformation> saturdayShows = null;

    private static ArrayList<BreakInformation> breaks = null;
    private static ArrayList<InstallationInformation> installations = null;

    //Getter for Thursday Show List
    public static ArrayList<ShowInformation> getThursdayShows(){
        ArrayList<ShowInformation> shows;
        if(thursdayShows != null){
            shows = thursdayShows;
        }else{
            shows = new ArrayList<ShowInformation>();
        }
        return shows;
    }

    //Getter for Friday Show List
    public static ArrayList<ShowInformation> getFridayShows(){
        ArrayList<ShowInformation> shows;
        if(fridayShows != null){
            shows = fridayShows;
        }else{
            shows = new ArrayList<ShowInformation>();
        }
        return shows;
    }

    //Getter for Saturday Show List
    public static ArrayList<ShowInformation> getSaturdayShows(){
        ArrayList<ShowInformation> shows;
        if(saturdayShows != null){
            shows = saturdayShows;
        }else{
            shows = new ArrayList<ShowInformation>();
        }
        return shows;
    }

    //Getter for the Break List
    public static ArrayList<BreakInformation> getBreakInformation(){
        ArrayList<BreakInformation> breakList;
        if(breaks != null){
            breakList = breaks;
        }else{
            breakList = new ArrayList<BreakInformation>();
        }
        return breakList;
    }

    //Getter for the Installation List
    public static ArrayList<InstallationInformation> getInstallations(){
        ArrayList<InstallationInformation> install;
        if(installations != null){
            install = installations;
        }else{
            install = new ArrayList<InstallationInformation>();
        }
        return install;
    }


    /**
     * Called when the main view launches, this function checks the last time the content
     * of the application was updated, and if it was greater than 24 hours ago, pulls the
     * data from the web.
     *
     * @param appContext the context for the activity so that the app can access the file
     *                   directory
     */
    public static void initializeContent(Context appContext, PageViewManager activity){
        applicationContext = appContext;
        currentActivity = activity;

        File lastUpdateFile = new File(appContext.getFilesDir(), lastUpdateStorage);
        boolean successfulUpdate = true;
        try {
            if (!lastUpdateFile.exists()) {
                //Files have not been created, create and get initial content
                createAllFiles(appContext);
                successfulUpdate = getContentFromWeb();
            } else {
                //Conetent exists - check if it needs to be updated
                //TODO: Not an object, use a scanner
                StringBuilder updateTimeSB = new StringBuilder();
                Scanner showScanner = new Scanner(lastUpdateFile);
                while(showScanner.hasNextLine()){
                    updateTimeSB.append(showScanner.nextLine());
                }
                int lastUpdated = Integer.parseInt(updateTimeSB.toString());

                GregorianCalendar now = new GregorianCalendar(
                                            TimeZone.getTimeZone("GMT-5:00"));

                //Check if content is from a different day
                if(now.get(Calendar.DATE) != lastUpdated){
                    getContentFromWeb();
                }else{
                    setContentFields();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        if(!successfulUpdate) {
            //Send toast to connect to internet
            currentActivity.sendToast("Internet Required to Update Data");
        }
    }

    //region getter methods

    /**
     * Retrieve the Show List for a given day
     *
     * @param day integer representing which day of playground (0 indexed to thursday)
     * @return an array list of all ShowInformation for that day
     */
    public static ArrayList<ShowInformation> getShowList(int day){
        ArrayList<ShowInformation> shows;

        switch(day) {
            case 0:
                shows = thursdayShows;
                break;
            case 1:
                shows = fridayShows;
                break;
            case 2:
                shows = saturdayShows;
                break;
            default:
                shows = null;
                break;
        }

        return shows;
    }

    public static ArrayList<InstallationInformation> getInstallationList(){
        return installations;
    }

    public static ArrayList<BreakInformation> getBreakList(){
        return breaks;
    }

    //endregion

    //region manage interaction between web, file system, and object

    /**
     * Create the files that will persistently store show information
     * between app sessions and when there is no internet connection
     *
     * @param appContext the app context to allow writing to the file system
     */
    private static void createAllFiles(Context appContext){
        File updatefile = new File(appContext.getFilesDir(), lastUpdateStorage);
        File showfile = new File(appContext.getFilesDir(), showStorage);
        File breakfile = new File(appContext.getFilesDir(), breakStorage);
        File installationfile = new File(appContext.getFilesDir(), installationStorage);
        FileWriter updateWriter;
        try {
            updatefile.createNewFile();
            showfile.createNewFile();
            breakfile.createNewFile();
            installationfile.createNewFile();

            //Set the time to 1970 so that data will be fetched
            int notADate = -1;
            updateWriter = new FileWriter(updatefile, false);
            updateWriter.write(Integer.toString(notADate));

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Retrieves JSON information from the playground website, and stores it in android file system
     */
    private static boolean getContentFromWeb(){
        ConnectivityManager connMgr =
                (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo == null || !networkInfo.isConnected()){
            return false;
        }
        new FetchJSON().execute(jsonWebsite);

        return true;
    }

    /**
     * Sets the static content lists from file directory
     */
    private static void setContentFields(){
        if(thursdayShows == null){
            File showfile = new File(applicationContext.getFilesDir(), showStorage);
            File breakfile = new File(applicationContext.getFilesDir(), breakStorage);
            File installationfile = new File(applicationContext.getFilesDir(), installationStorage);
            JSONObject allShows = null;
            JSONArray allBreaks = null;
            JSONArray allInstallations = null;

            try {
                StringBuilder showSB = new StringBuilder();
                Scanner showScanner = new Scanner(showfile);
                while(showScanner.hasNextLine()){
                     showSB.append(showScanner.nextLine());
                }
                allShows = new JSONObject(showSB.toString());

                StringBuilder breakSB = new StringBuilder();
                Scanner breakScanner = new Scanner(breakfile);
                while(breakScanner.hasNextLine()){
                    breakSB.append(breakScanner.nextLine());
                }
                allBreaks = new JSONArray(breakSB.toString());


                StringBuilder installSB = new StringBuilder();
                Scanner installationScanner = new Scanner(installationfile);
                while(installationScanner.hasNextLine()){
                    installSB.append(installationScanner.nextLine());
                }
                allInstallations = new JSONArray(installSB.toString());

                JSONArray thursday = allShows.getJSONArray("thursday");
                JSONArray friday = allShows.getJSONArray("friday");
                JSONArray saturday = allShows.getJSONArray("saturday");


                //Construct objects from JSON and add to master lists
                breaks = new ArrayList<BreakInformation>();
                installations = new ArrayList<InstallationInformation>();
                thursdayShows = new ArrayList<ShowInformation>();
                fridayShows = new ArrayList<ShowInformation>();
                saturdayShows = new ArrayList<ShowInformation>();

                for(int i = 0; i < allBreaks.length(); i++){
                    JSONObject current = allBreaks.getJSONObject(i);
                    String displayTime = current.getString("time");
                    int month = current.getInt("month");
                    int day = current.getInt("day");
                    int hour = current.getInt("hour");
                    int minute = current.getInt("minute");
                    BreakInformation currentBreak =
                            new BreakInformation(displayTime, month, day, hour, minute);
                    breaks.add(currentBreak);
                }

                for(int i = 0; i < allInstallations.length(); i++){
                    JSONObject current = allInstallations.getJSONObject(i);
                    String title = current.getString("title");
                    String location = current.getString("location");
                    String description = current.getString("description");
                    String specialThanks = current.getString("specialThanks");
                    String audiencWarning = current.getString("audienceWarning");
                    String showCreator = current.getString("showCreator");
                    String showParticipants = current.getString("showParticipants");

                    InstallationInformation install =
                            new InstallationInformation(title, location, description,
                                    specialThanks, audiencWarning, showCreator, showParticipants);
                    installations.add(install);
                }

                ////////
                ///////
                //Duplicate code for these 3 functions
                ///////
                //////
                for(int i = 0; i < thursday.length(); i++){
                    JSONObject current = thursday.getJSONObject(i);
                    String title = current.getString("title");
                    String time = current.getString("time");
                    String location = current.getString("location");
                    String locationAbbr = current.getString("locationAbbr");
                    String description = current.getString("description");
                    String specialThanks = current.getString("specialThanks");
                    String audienceWarning = current.getString("audienceWarning");
                    String showCreator = current.getString("showCreator");
                    String showParticipants = current.getString("showParticipants");

                    int month = current.getInt("month");
                    int day = current.getInt("day");
                    int hour = current.getInt("hour");
                    int minutes = current.getInt("minutes");

                    ShowInformation info = new ShowInformation(title, time, location, locationAbbr,
                            description, specialThanks, audienceWarning, showCreator, showParticipants,
                            month, day, hour, minutes);
                    thursdayShows.add(info);


                }

                for(int i = 0; i < friday.length(); i++){
                    JSONObject current = friday.getJSONObject(i);
                    String title = current.getString("title");
                    String time = current.getString("time");
                    String location = current.getString("location");
                    String locationAbbr = current.getString("locationAbbr");
                    String description = current.getString("description");
                    String specialThanks = current.getString("specialThanks");
                    String audienceWarning = current.getString("audienceWarning");
                    String showCreator = "";
                    String showParticipants = "";
                    if(current.has("showCreator")) {
                        showCreator = current.getString("showCreator");
                    }
                    if(current.has("showParticipants")) {
                        showParticipants = current.getString("showParticipants");
                    }
                    int month = current.getInt("month");
                    int day = current.getInt("day");
                    int hour = current.getInt("hour");
                    int minutes = current.getInt("minutes");

                    ShowInformation info = new ShowInformation(title, time, location, locationAbbr,
                            description, specialThanks, audienceWarning, showCreator, showParticipants,
                            month, day, hour, minutes);
                    fridayShows.add(info);
                }

                for(int i = 0; i < saturday.length(); i++){
                    JSONObject current = saturday.getJSONObject(i);
                    String title = current.getString("title");
                    String time = current.getString("time");
                    String location = current.getString("location");
                    String locationAbbr = current.getString("locationAbbr");
                    String description = current.getString("description");
                    String specialThanks = current.getString("specialThanks");
                    String audienceWarning = current.getString("audienceWarning");
                    String showCreator = current.getString("showCreator");
                    String showParticipants = current.getString("showParticipants");

                    int month = current.getInt("month");
                    int day = current.getInt("day");
                    int hour = current.getInt("hour");
                    int minutes = current.getInt("minutes");

                    ShowInformation info = new ShowInformation(title, time, location, locationAbbr,
                            description, specialThanks, audienceWarning, showCreator, showParticipants,
                            month, day, hour, minutes);
                    saturdayShows.add(info);
                }

            }catch (IOException e){
                e.printStackTrace();
            }catch (JSONException e){
                e.printStackTrace();
            }

            currentActivity.updateScreen();
        }
    }
    //endregion

    //AsyncTask creates a new task thread.
    private static class FetchJSON extends AsyncTask<String, Void, String> {
                  @Override
                  protected String doInBackground(String... urls){
                      String resultString;
                      try {
                          resultString = downloadUrl(urls[0]);
                      }catch (IOException e){
                          resultString = "Unable to retrieve web page.";
                      }
                      return resultString;
                  }

        /**
         * Process the JSON returned from the web
         *
         * @param result the string representation of the download
         */
                  @Override
                  protected void onPostExecute(String result){
                      Log.d("full response", result);

                      //Set all fields to null so that setContentFields updates
                      thursdayShows = null;
                      fridayShows = null;
                      saturdayShows = null;
                      breaks = null;
                      installations = null;

                      //after update, trigger live update to update content
                      FileWriter updateWriter = null;
                      FileWriter showWriter = null;
                      FileWriter breakWriter = null;
                      FileWriter installationWriter = null;
                      try {
                          JSONObject pInfo = new JSONObject(result);

                          //Extract the individual fields from the web JSON
                          JSONArray thursday = pInfo.getJSONArray("thursday");
                          JSONArray friday = pInfo.getJSONArray("friday");
                          JSONArray saturday = pInfo.getJSONArray("saturday");
                          JSONArray installations = pInfo.getJSONArray("installation");
                          JSONArray breakTimes = pInfo.getJSONArray("breakTimes");

                          //Place all the shows into a single object for writing
                          JSONObject allShows = new JSONObject();
                          allShows.put("thursday", thursday);
                          allShows.put("friday", friday);
                          allShows.put("saturday", saturday);

                          //Convert the JSONs to strings for write operations
                          String showSTR = allShows.toString();
                          String installationSTR = installations.toString();
                          String breakSTR = breakTimes.toString();

                          //Create FileWriters to write to the File System
                          File updatefile = new File(applicationContext.getFilesDir(), lastUpdateStorage);
                          File showfile = new File(applicationContext.getFilesDir(), showStorage);
                          File breakfile = new File(applicationContext.getFilesDir(), breakStorage);
                          File installationfile = new File(applicationContext.getFilesDir(), installationStorage);

                          updateWriter = new FileWriter(updatefile, false);
                          showWriter = new FileWriter(showfile, false);
                          breakWriter = new FileWriter(breakfile, false);
                          installationWriter = new FileWriter(installationfile, false);

                          showWriter.write(showSTR);
                          breakWriter.write(breakSTR);
                          installationWriter.write(installationSTR);

                          //Construct the current date for the updateWriter
                          GregorianCalendar now = new GregorianCalendar(
                                  TimeZone.getTimeZone("GMT-5:00"));
                          updateWriter.write(Integer.toString(now.get(Calendar.DATE)));

                      }catch (JSONException e){
                          Log.d("Async Exception","JSON Parser Exception");
                      }catch (IOException e){
                          Log.d("Web Result","Write Failed");
                      }finally{
                          try {
                              if (updateWriter != null) {
                                  updateWriter.close();
                              }
                              if (showWriter != null) {
                                  showWriter.close();
                              }
                              if (breakWriter != null) {
                                  breakWriter.close();
                              }
                              if (installationWriter != null) {
                                  installationWriter.close();
                              }
                          }catch(Exception e){
                              Log.d("Closing files", "Exception thrown");
                              e.printStackTrace();
                          }
                      }
                      setContentFields();
                  }

                  // Given a URL, establishes an HttpUrlConnection and retrieves
                  // the web page content as a InputStream, which it returns as
                  // a string.
                  private String downloadUrl(String myurl) throws IOException {
                      InputStream is = null;
                      // Only display the first 500 characters of the retrieved
                      // web page content.
                      int len = 500;
                      String contentAsString;
                      try {
                          URL url = new URL(myurl);
                          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                          conn.setReadTimeout(10000 /* milliseconds */);
                          conn.setConnectTimeout(15000 /* milliseconds */);
                          conn.setRequestMethod("GET");
                          conn.setDoInput(true);
                          // Starts the query
                          conn.connect();
                          int response = conn.getResponseCode();
                          Log.d("Debug response", "The response is: " + response);
                          is = conn.getInputStream();

                          // Convert the InputStream into a string
                          contentAsString = readIt(is, len);

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
                  public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
                      Reader reader = null;
                      StringBuilder sb = new StringBuilder();
                      reader = new InputStreamReader(stream, "UTF-8");
                      char[] buffer = new char[len];
                      int bits = 1;
                      while(bits > 0) {
                          bits = reader.read(buffer);
                          sb.append(buffer);
                      }
                      return sb.toString();
                  }
              }

}
