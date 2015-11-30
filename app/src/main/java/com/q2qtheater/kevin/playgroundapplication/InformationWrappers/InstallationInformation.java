package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

import java.io.Serializable;

/**
 * Created by Kevin on 1/26/15.
 *  A simple class for wrapping Information about Installations
 */
public class InstallationInformation extends InformationContainer implements Serializable {

    public InstallationInformation(String title, String location,
          String description, String specialThanks, String audienceWarning,
          String showParticipants){

        super(title, location,description,specialThanks,audienceWarning,
                showParticipants, null);
    }

    @Override
    public boolean isInstallation(){return true;}
}
