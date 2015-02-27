package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

/**
 * Created by Kevin on 1/26/15.
 *  A simple class for wrapping Information about Installations
 */
public class InstallationInformation extends InformationContainer {

    public InstallationInformation(String title, String location,
          String description, String specialThanks, String audienceWarning,
          String showCreator, String showParticipants){
        super(title, "",location, "",description,specialThanks,audienceWarning,
                showCreator, showParticipants, 0, 0, 0, 0);
    }

    @Override
    public boolean isInstallation(){return true;}
}
