package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

/**
 * Created by Kevin on 1/28/15.
 *
 * A simple abstract class for wrapping information about different performances
 */
public abstract class InformationContainer {
    private String title;
    private String displayTime;
    private String location;
    private String locationAbbr;
    private String description;
    private String specialThanks;
    private String audienceWarning;
    private String showCreator;
    private String showParticipants;
    private int month;
    private int day;
    private int hour;
    private int minutes;


    public InformationContainer(String title, String displayTime, String location,
                           String locationAbbr, String description, String specialThanks, String audienceWarning,
                           String showCreator, String showParticipants, int month, int day, int hour, int minutes){
        this.title = title;
        this.displayTime = displayTime;
        this.location = location;
        this.locationAbbr = locationAbbr;
        this.description = description;
        this.specialThanks = specialThanks;
        this.audienceWarning = audienceWarning;
        this.showCreator = showCreator;
        this.showParticipants = showParticipants;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minutes = minutes;
    }

    @Override
    public String toString(){
        return this.title;
    }

    public abstract boolean isInstallation();

    public String getTitle(){
        return title;
    }

    public String getDisplayTime(){
        return displayTime;
    }

    public String getLocation() {
        return location;
    }

    public String getLocationAbbr() {
        return locationAbbr;
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

    public String getShowCreator() {
        return showCreator;
    }

    public String getShowParticipants() {
        return showParticipants;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }
}
