package com.example.arduino_control;

import java.io.Serializable;
import java.util.ArrayList;

public class OurDevice implements Serializable {

    private String ourName;
    private String macAddress;
    private ArrayList<Integer> max;
    private ArrayList<String> names;
    public boolean isSet;
    private int colorClicked;
    private int knobs;


    public OurDevice(String macAddress, String ourName){
        this.ourName = ourName;
        this.macAddress = macAddress;
        this.isSet = false;
        max = new ArrayList<>(3);
        max.add(1);
        max.add(1);
        max.add(1);
        names = new ArrayList<>(3);
        names.add("knob 1");
        names.add("knob 2");
        names.add("knob 3");
    }

    public OurDevice(){

    }

    public void setOurName(String ourName) {
        this.ourName = ourName;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setColorClicked(int colorClicked) {
        this.colorClicked = colorClicked;
    }

    public void setKnobs(int knobs){
        this.knobs = knobs;
    }

    public String getOurName() {
        return ourName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getColorClicked() {
        return colorClicked;
    }

    public ArrayList<Integer> getMax(){
        return max;
    }

    public ArrayList<String> getNames(){
        return names;
    }

    public int getKnobs(){
        return knobs;
    }
}
