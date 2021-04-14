package com.example.arduino_control;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for User bluetooth device where is all information about device and list of Presets for device.
 * @author Vikinn
 */
public class OurDevice implements Serializable {

    private String ourName;
    private String macAddress;
    private ArrayList<Integer> max;
    private ArrayList<String> names;
    public ArrayList<Preset> listOfPresets;
    public boolean isSet;
    private int colorClicked;
    private int knobs;

    /**
     * OurDevice constructor
     * @param macAddress Bluetooth MAC address of device
     * @param ourName Selected name of device.
     */
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
        listOfPresets = new ArrayList<>();
    }

    /**
     * Empty constructor due to Serializable object
     * @see Serializable
     */
    public OurDevice(){}

    public void setOurName(String ourName) {
        this.ourName = ourName;
    }

    public void setColorClicked(int colorClicked) {
        this.colorClicked = colorClicked;
    }

    public void setKnobs(int knobs){
        this.knobs = knobs;
    }

    public void setNames(ArrayList<String> names){
        this.names = names;
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

    public ArrayList<Preset> getListOfPresets() {
        return listOfPresets;
    }
}
