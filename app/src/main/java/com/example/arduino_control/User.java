package com.example.arduino_control;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class User is main object which contains all users devices.
 * @author Vikinn
 */
public class User implements Serializable {

    private ArrayList<OurDevice> ourDeviceList;

    /**
     * Empty constructor due to Serializable object.
     * @see Serializable
     */
    public User (){
        this.ourDeviceList = new ArrayList<>();
    }

    /**
     * Constructor for User.
     * @param ourDeviceList List of our devices
     * @see OurDevice
     */
    public User(ArrayList<OurDevice> ourDeviceList){
        this.ourDeviceList = ourDeviceList;
    }
    public void setOurDeviceList(ArrayList<OurDevice> ourDeviceList){
        this.ourDeviceList = ourDeviceList;
    }

    public ArrayList<OurDevice> getOurDeviceList(){
        return ourDeviceList;
    }
}
