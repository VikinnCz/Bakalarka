package com.example.arduino_control;

public class OurDevice {

    private String ourName;
    private String macAddress;
    private int colorClicked;


    public OurDevice(String macAddress, String ourName){
        this.ourName = ourName;
        this.macAddress = macAddress;
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

    public String getOurName() {
        return ourName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public int getColorClicked() {
        return colorClicked;
    }
}
