package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

/**
 * Created by Kevin on 1/26/15.
 *
 * A simple class for wrapping Break Information
 */
public class BreakInformation {
    private String displayTime;
    private int month;
    private int day;
    private int hour;
    private int minute;

    public BreakInformation(String displayTime, int month, int day, int hour, int minute){
        this.displayTime = displayTime;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public String getDisplayTime() {
        return displayTime;
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

    public int getMinute() {
        return minute;
    }
}
