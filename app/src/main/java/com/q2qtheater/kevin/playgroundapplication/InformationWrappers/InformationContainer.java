package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kevin on 1/28/15.
 *
 * A simple abstract class for wrapping information about different performances
 */
public abstract class InformationContainer implements Serializable {
    private String title;
    private String location;
    private String description;
    private String specialThanks;
    private String audienceWarning;
    private String showParticipants;
    private Date date;


    public InformationContainer(String title, String location,
                           String description, String specialThanks, String audienceWarning,
                           String showParticipants, Date date){
        this.title = title;
        this.location = location;
        this.description = description;
        this.specialThanks = specialThanks;
        this.audienceWarning = audienceWarning;
        this.showParticipants = showParticipants;
        this.date = date;
    }

    @Override
    public String toString(){
        return this.title;
    }

    public abstract boolean isInstallation();

    public String getTitle(){
        return title;
    }

    public Date getDate(){
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getSpecialThanks() {
        return specialThanks;
    }

    public String getAudienceWarning() {
        return audienceWarning;
    }

    public String getShowParticipants() {
        return showParticipants;
    }

}
