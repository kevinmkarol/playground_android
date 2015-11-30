package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

import java.io.Serializable;

/**
 * Created by Kevin on 11/29/15.
 */
public class Credit implements Serializable {
    private String name;
    private String title;

    public Credit(String name){
        this.name = name;
    }

    public Credit(String name, String title){
        this.name = name;
        this.title = title;
    }
}
