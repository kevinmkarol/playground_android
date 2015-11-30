package com.q2qtheater.kevin.playgroundapplication.InformationWrappers;

import java.io.Serializable;

/**
 * Created by Kevin on 11/29/15.
 */
public class InstallationImage implements Serializable{
    private String imageName;
    private String webURL;
    private String localURL;

    public InstallationImage(String imageName, String webURL){
        this.imageName = imageName;
        this.webURL = webURL;
    }

}
