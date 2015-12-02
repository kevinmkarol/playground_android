package com.q2qtheater.kevin.playgroundapplication;

import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.BreakInformation;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.Credit;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.InstallationImage;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.InstallationInformation;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.ShowInformation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    private static Context applicationContext;
    private static PageViewManager currentActivity;

    //File accessors
    private static final String version_fn = "version";
    private static final String festivalDates_fn = "festivalDates";
    private static final String festivalStaff_fn = "festivalStaff";
    private static final String specialThanks_fn = "specialThanks";
    private static final String thursday_fn = "thursday";
    private static final String friday_fn = "friday";
    private static final String saturday_fn = "saturady";
    private static final String installation_fn = "installationInfo";
    private static final String installationImages_fn = "installationImages";
    private static final String break_fn = "breakInfo";
    private static final String lastUpdate_fn = "lastUpdate";
    private static final String[] allFileNames = {version_fn, festivalDates_fn, festivalStaff_fn, specialThanks_fn
                                                  , thursday_fn, friday_fn, saturday_fn, installation_fn, installationImages_fn
                                                  , break_fn, lastUpdate_fn};

    //Array list caches
    private static ArrayList<Credit> festivalStaff = new ArrayList<Credit>();
    private static ArrayList<Credit> specialThanks = new ArrayList<Credit>();
    private static ArrayList<ShowInformation> thursdayShowList = new ArrayList<ShowInformation>();
    private static ArrayList<ShowInformation> fridayShowList = new ArrayList<ShowInformation>();
    private static ArrayList<ShowInformation> saturdayShowList = new ArrayList<ShowInformation>();
    private static ArrayList<InstallationInformation> installations = new ArrayList<InstallationInformation>();
    private static ArrayList<InstallationImage> installationImages = new ArrayList<InstallationImage>();
    private static ArrayList<BreakInformation> breaks = new ArrayList<BreakInformation>();

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

        GregorianCalendar lastUpdate = getLastUpdateTime();
        boolean successfulUpdate = true;
        WebInterfaceManager webInterface = new WebInterfaceManager();
        GregorianCalendar now = new GregorianCalendar(
                TimeZone.getTimeZone("GMT-5:00"));

        if (lastUpdate == null) {
            //Files have not been created, create and get initial content
            createAllFiles();

            successfulUpdate = webInterface.getProgramInformation(appContext);
            setLastUpdateTime(now);
        } else {
            //Conetent exists - check if it needs to be updated


            //Check if content is from a different day
            if(now.get(Calendar.DATE) != lastUpdate.get(Calendar.DATE)){
                webInterface.getProgramInformation(appContext);
                setLastUpdateTime(now);
            }
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
     * @param informationType enum representing which piece of program information you'd like to recieve
     * @return an array list of all ShowInformation for that day
     */
    public static ArrayList getProgramInformation(InformationType informationType){
        ArrayList information = null;

        switch(informationType) {
            case FESTIVAL_STAFF:
                if(festivalStaff == null || festivalStaff.size() == 0){
                    festivalStaff = retrieveInformationFromFileSystem(festivalStaff_fn);
                }
                information = festivalStaff;
                break;
            case SPECIAL_THANKS:
                if(specialThanks == null || specialThanks.size() == 0){
                    specialThanks = retrieveInformationFromFileSystem(specialThanks_fn);
                }
                information = specialThanks;
                break;
            case THURSDAY_SHOWS:
                if(thursdayShowList == null || thursdayShowList.size() == 0){
                    thursdayShowList = retrieveInformationFromFileSystem(thursday_fn);
                }
                information = thursdayShowList;
                break;
            case FRIDAY_SHOWS:
                if(fridayShowList == null || fridayShowList.size() == 0){
                    fridayShowList = retrieveInformationFromFileSystem(friday_fn);
                }
                information = fridayShowList;
                break;
            case SATURDAY_SHOWS:
                if(saturdayShowList == null || saturdayShowList.size() == 0){
                    saturdayShowList = retrieveInformationFromFileSystem(saturday_fn);
                }
                information = saturdayShowList;
                break;
            case INSTALLATIONS:
                if(installations == null || installations.size() == 0){
                    installations = retrieveInformationFromFileSystem(installation_fn);
                }
                information = installations;
                break;
            case INSTALLATION_IMAGES:
                if(installationImages == null || installationImages.size() == 0){
                    installationImages = retrieveInformationFromFileSystem(installationImages_fn);
                }
                information = installationImages;
                break;
            case BREAKS:
                if(breaks == null || breaks.size() == 0){
                    breaks = retrieveInformationFromFileSystem(break_fn);
                }
                information = breaks;
                break;
        }

        //protect null accessors in fragments
        if(information == null){
            information = new ArrayList();
        }

        return information;
    }
    //endregion


    //Parses the google sheets information into appropriate information storage arrays
    //And writes each to the appropriate file
    public static void parseGoogleSheetResponse(String result) {
        float oldVersion = getCurrentVersion();
        float newVersion = 0.0f;
        ArrayList<GregorianCalendar>  festivalDates = new ArrayList<GregorianCalendar>();
        String currentHeader = "";
        ArrayList currentList = new ArrayList();
        Scanner lineScanner = new Scanner(result);
        lineScanner.useDelimiter("\r\n");
        while (lineScanner.hasNext()) {
            String currentLine = lineScanner.next();
            Log.d("Line parsing", currentLine);
            String[] allFields = currentLine.split(",");

            ///////////
            ///Distinguish between different headers
            ///////////
            if (allFields[0].equals("version")) {
                newVersion = Float.parseFloat(allFields[1]);
                currentList.add(newVersion);
                continue;

            } else if (allFields[0].equals("Festival Dates")) {
                currentHeader = "Festival Dates";
                saveProgramInformationToFile(version_fn, currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("Installation Images")) {
                currentHeader = "Installation Images";
                saveProgramInformationToFile(festivalDates_fn, currentList);
                festivalDates = new ArrayList<GregorianCalendar>(currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("Festival Staff")) {
                currentHeader = "Festival Staff";
                saveProgramInformationToFile(installationImages_fn, currentList);
                installationImages = new ArrayList<InstallationImage>(currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("Special Thanks")) {
                currentHeader = "Special Thanks";
                saveProgramInformationToFile(festivalStaff_fn, currentList);
                festivalStaff = new ArrayList<Credit>(currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("Thursday")) {
                currentHeader = "Thursday";
                saveProgramInformationToFile(specialThanks_fn, currentList);
                specialThanks = new ArrayList<Credit>(currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("Friday")) {
                currentHeader = "Thursday";
                saveProgramInformationToFile(thursday_fn, currentList);
                thursdayShowList = new ArrayList<ShowInformation>(currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("Saturday")) {
                currentHeader = "Saturday";
                saveProgramInformationToFile(friday_fn, currentList);
                fridayShowList = new ArrayList<ShowInformation>(currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("Installations")) {
                currentHeader = "Installations";
                saveProgramInformationToFile(saturday_fn, currentList);
                saturdayShowList = new ArrayList<ShowInformation>(currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("Breaks")) {
                currentHeader = "Breaks";
                saveProgramInformationToFile(installation_fn, currentList);
                installations = new ArrayList<InstallationInformation>(currentList);
                currentList.clear();
                continue;

            } else if (allFields[0].equals("End")) {
                saveProgramInformationToFile(break_fn, currentList);
                breaks = new ArrayList<BreakInformation>(currentList);
                currentList.clear();
                break;
            }

            ///////////
            ///Process each line based on current Header
            ///////////
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy-HH-mm-ss");
            if (currentHeader.equals("version")) {
                continue;
            } else if (currentHeader.equals("Festival Dates")) {
                try {
                    Date thursday = df.parse(allFields[1]);
                    Date friday = df.parse(allFields[2]);
                    Date saturday = df.parse(allFields[3]);
                    currentList.add(thursday);
                    currentList.add(friday);
                    currentList.add(saturday);
                }catch (java.text.ParseException e){
                    Log.e("Playground App", "date parsing error from sheet", e);

                }

            } else if (currentHeader.equals("Installation Images")) {
                InstallationImage imageInfo = new InstallationImage(allFields[1], allFields[2]);
                currentList.add(imageInfo);
            } else if (currentHeader.equals("Festival Staff")) {
                Credit credit = new Credit(allFields[1], allFields[2]);
                currentList.add(credit);
            } else if (currentHeader.equals("Special Thanks")) {
                Credit credit = new Credit(allFields[1]);
                currentList.add(credit);
            } else if (currentHeader.equals("Thursday")
                       || currentHeader.equals("Friday")
                       || currentHeader.equals("Saturday")) {

                Date d = null;
                try {
                    d = df.parse(allFields[3]);
                }catch (java.text.ParseException e){
                    Log.e("Playground App", "date parsing error for show from sheet", e);
                }

                ShowInformation show = new ShowInformation(allFields[1], allFields[2], allFields[4],
                                                           allFields[5], allFields[6], allFields[7], d);
                currentList.add(show);

            } else if (currentHeader.equals("Installations")) {
                InstallationInformation show = new InstallationInformation(allFields[1], allFields[2], allFields[4],
                        allFields[5], allFields[6], allFields[7]);
                currentList.add(show);

            } else if (currentHeader.equals("Breaks")) {
                Date d = null;
                try {
                    d = df.parse(allFields[1]);
                }catch (java.text.ParseException e){
                    Log.e("Playground App", "break date parsing error", e);
                }
                BreakInformation breakInfo = new BreakInformation(d);
            }
        }
        //If the version number has been updated, download new image files
        if(newVersion > oldVersion){
            ArrayList<InstallationImage> imageWrappers = getProgramInformation(InformationType.INSTALLATION_IMAGES);
            for(InstallationImage installationImage : imageWrappers){
                WebInterfaceManager.downloadImage(applicationContext, installationImage);
            }
        }

        //This is a terrible hack to refresh the data
        //should be removed ASAP
        Intent mStartActivity = new Intent(applicationContext, PageViewManager.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(applicationContext, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)applicationContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    private static float getCurrentVersion(){
        ArrayList<Float> version = retrieveInformationFromFileSystem(version_fn);
        float extractedVersion = -1.0f;
        if(version.size() != 0){
            extractedVersion = version.get(0);
        }
        return extractedVersion;
    }

    private static ArrayList retrieveInformationFromFileSystem(String fileName){
        File readFile = new File(applicationContext.getFilesDir(), fileName);
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        ArrayList fileContents = new ArrayList();
        if(readFile.length() != 0) {
            try {
                fin = new FileInputStream(readFile);
                ois = new ObjectInputStream(fin);
                if (ois != null) {
                    fileContents = (ArrayList) ois.readObject();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.e("Playground App", "retrieve information error", e);
            } catch (java.io.IOException e) {
                Log.e("Playground App", "retrieve information error", e);
            } catch (java.lang.ClassNotFoundException e) {
                Log.e("Playground App", "retrieve information error", e);
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                    if (fin != null) {
                        fin.close();
                    }
                } catch (java.io.IOException e) {
                    Log.e("Playground App", "retrieve information error", e);
                }
            }
        }

        return fileContents;
    }

    private static void saveProgramInformationToFile(String fileName, ArrayList information){
        File writeFile = new File(applicationContext.getFilesDir(), fileName);
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream(writeFile);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(information);

        }catch(java.io.IOException e) {
            Log.e("Playground App", "write information error", e);
        }finally{
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }catch(java.io.IOException e){
                Log.e("Playground App", "retrieve information error", e);
            }
        }
    }

    /**
     *
     * Save the image file to the file system for later access
     *
     * @param imageName the name of the image to save
     * @param bitmap the actual bitmap of the image
     * @return the local path to access the image
     */
    public static void saveImageToFile(String imageName, Bitmap bitmap){
        //create a new file with the image name
        File imageFile = new File(applicationContext.getFilesDir(), imageName);
        FileOutputStream out = null;
        try{
            imageFile.createNewFile();
            out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        }catch (Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(out != null){
                    out.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static Bitmap getImageFromFile(String fileName) {
        File imageFile = new File(applicationContext.getFilesDir(), fileName);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        }catch (Exception e){
            e.printStackTrace();
        }

        return bitmap;
    }

    //region manage interaction with file system, and object

    /**
     * Create the files that will persistently store show information
     * between app sessions and when there is no internet connection
     *
     */
    private static void createAllFiles(){
        File toCreate = null;
        FileWriter updateWriter;
        for(String fileName : allFileNames){
            toCreate = new File(applicationContext.getFilesDir(), fileName);
            try{
                toCreate.createNewFile();
            }catch(java.io.IOException e){
                Log.e("Playground App", "create all files error", e);

            }
        }

        //Set the date in the lastUpdate file so that data will be fetched the
        //first time the app is opened
        File updatefile = new File(applicationContext.getFilesDir(), lastUpdate_fn);
        //Set the time to 1970 so that data will be fetched
        int notADate = -1;
        try {
            updateWriter = new FileWriter(updatefile, false);
            updateWriter.write(Integer.toString(notADate));
        }catch (java.io.IOException e){
            Log.e("Playground App", "write initial date error", e);
        }

    }

    private static GregorianCalendar getLastUpdateTime(){
        File lastUpdateFile = new File(applicationContext.getFilesDir(), lastUpdate_fn);
        GregorianCalendar updateDate = null;
        if(lastUpdateFile.exists()) {
            FileInputStream fin = null;
            ObjectInputStream ois = null;
            try {
                fin = new FileInputStream(lastUpdateFile);
                ois = new ObjectInputStream(fin);
                if (ois != null) {
                    updateDate = (GregorianCalendar) ois.readObject();
                }
            } catch (java.io.FileNotFoundException e) {
                Log.e("Playground App", "update file not found", e);
            } catch (java.io.IOException e) {
                Log.e("Playground App", "update file not read properly", e);
            } catch (java.lang.ClassNotFoundException e) {
                Log.e("Playground App", "update file class not found", e);
            } finally {
                try {
                    if (ois != null) {
                        ois.close();
                    }
                    if (fin != null) {
                        fin.close();
                    }
                } catch (java.io.IOException e) {
                    Log.e("Playground App", "retrieve information error", e);
                }
            }
        }
        return updateDate;
    }

    private static void setLastUpdateTime(GregorianCalendar update){
        File writeFile = new File(applicationContext.getFilesDir(), lastUpdate_fn);
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            fout = new FileOutputStream(writeFile);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(update);

        }catch(java.io.IOException e) {
            Log.e("Playground App", "write last update error", e);
        }finally{
            try {
                if (oos != null) {
                    oos.close();
                }
                if (fout != null) {
                    fout.close();
                }
            }catch(java.io.IOException e){
                Log.e("Playground App", "closing IO error last update", e);
            }
        }
    }

    //endregion



}
