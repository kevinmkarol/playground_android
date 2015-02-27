package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

/**
 * Created by Kevin on 1/26/15.
 *  A simple class for wrapping Information about Shows
 */
public class ShowInformation extends InformationContainer {

    public ShowInformation(String title, String displayTime, String location,
        String locationAbbr, String description, String specialThanks, String audienceWarning,
        String showCreator, String showParticipants, int month, int day, int hour, int minutes){

        super(title, displayTime,location, locationAbbr,description,specialThanks,audienceWarning,
                showCreator, showParticipants, month, day, hour, minutes);

    }
    @Override
    public boolean isInstallation(){return false;}

}
