package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kevin on 1/26/15.
 *
 * A simple class for wrapping Break Information
 */
public class BreakInformation implements Serializable {
    private Date date;

    public BreakInformation(Date date){
        this.date = date;
    }

    public Date getDate() {
        return date;
    }
}
