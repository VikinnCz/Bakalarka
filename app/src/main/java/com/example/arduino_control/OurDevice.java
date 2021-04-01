package com.example.arduino_control;

import android.bluetooth.BluetoothDevice;

public class OurDevice {

//    private BluetoothDevice device;
    private String ourName;
    private String macAddress;
    private int colorClicked;

//    public OurDevice(BluetoothDevice device, String ourName){
//        this.device = device;
//        this.ourName = ourName;
//        this.macAddress = device.getAddress();
//    }

    public OurDevice(String macAddress, String ourName){
        this.ourName = ourName;
        this.macAddress = macAddress;
    }

    public OurDevice(){

    }

    public void setOurName(String ourName) {
        this.ourName = ourName;
    }

//    public void setDevice(BluetoothDevice device) {
//        this.device = device;
//    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setColorClicked(int colorClicked) {
        this.colorClicked = colorClicked;
    }

//    public BluetoothDevice getDevice() {
//        return device;
//    }

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
