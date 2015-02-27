package com.q2qtheater.kevin.playgroundapplication;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;

import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.InformationContainer;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.InstallationInformation;
import com.q2qtheater.kevin.playgroundapplication.InformationWrappers.ShowInformation;

import java.util.ArrayList;

/**
 * An activity which displays the information for a specific show
 *
 */

public class PieceInformation extends Activity {
    //Refernecse to the layout file's fields
    private InformationContainer info;
    private TextView title;
    private TextView creator;
    private TextView location;
    private TextView description;
    private TextView participants;
    private TextView audienceWarnings;
    private TextView specialThanks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_piece_information);
             Intent intent = getIntent();
             String day = intent.getStringExtra(PageViewManager.SHOW_DAY);
             String index = intent.getStringExtra(PageViewManager.SHOW_INDEX);

             ArrayList<InformationContainer> allForDay = new ArrayList<InformationContainer>();

             //Check the day of the week and assign the appropriate show list
             //to allForDay
             if(day.equals("thursday")){
                 ArrayList<ShowInformation> temp = ContentManager.getThursdayShows();
                 for(ShowInformation show: temp){
                     allForDay.add(show);
                 }
             }else if(day.equals("friday")){
                 ArrayList<ShowInformation> temp = ContentManager.getFridayShows();
                 for(ShowInformation show: temp){
                     allForDay.add(show);
                 }
             }else if(day.equals("saturday")){
                 ArrayList<ShowInformation> temp = ContentManager.getSaturdayShows();
                 for(ShowInformation show: temp){
                     allForDay.add(show);
                 }
             }else{
                 ArrayList<InstallationInformation> temp = ContentManager.getInstallations();
                 for(InstallationInformation install: temp){
                     allForDay.add(install);
                 }
             }
             //Grab the appropriate show given the day
             info = allForDay.get(new Integer(index));
             View view = this.findViewById(android.R.id.content);

             //Locate and link the layout file's fields
             //global reference to decrease uses of findViewById
             title = (TextView) view.findViewById(R.id.title);
             creator = (TextView) view.findViewById(R.id.creator);
             location = (TextView) view.findViewById(R.id.location);
             description = (TextView) view.findViewById(R.id.description);
             participants = (TextView) view.findViewById(R.id.performers);
             audienceWarnings = (TextView) view.findViewById(R.id.warnings);
             specialThanks = (TextView) view.findViewById(R.id.specialthanks);

             //Display the text fields from the show taken from the show array
             title.setText(info.getTitle());
             creator.setText(info.getShowCreator());
             location.setText(info.getLocation() + " " + info.getDisplayTime());
             description.setText(info.getDescription());
             participants.setText(info.getShowParticipants());
             audienceWarnings.setText(info.getAudienceWarning());
             specialThanks.setText(info.getSpecialThanks());
         }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.piece_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
