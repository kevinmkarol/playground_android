package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

import java.util.Date;

/**
 * Created by Kevin on 1/26/15.
 *  A simple class for wrapping Information about Shows
 */
public class ShowInformation extends InformationContainer {

    public ShowInformation(String title, String location,
        String description, String specialThanks, String audienceWarning,
        String showParticipants, Date date){

        super(title, location, description,specialThanks,audienceWarning,
                showParticipants, date);

    }
    @Override
    public boolean isInstallation(){return false;}

}
