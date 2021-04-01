package com.example.arduino_control;

import java.util.ArrayList;

public class User {

    private ArrayList<OurDevice> ourDeviceList;

    public User (){
        this.ourDeviceList = new ArrayList<>();
    }

    public User(ArrayList<OurDevice> ourDeviceList){
        this.ourDeviceList = ourDeviceList;
    }

    public ArrayList<OurDevice> getOurDeviceList(){
        return ourDeviceList;
    }

    public void setOurDeviceList(ArrayList<OurDevice> ourDeviceList){
        this.ourDeviceList = ourDeviceList;
    }
}
