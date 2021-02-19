package com.example.arduino_control_03;

import android.bluetooth.BluetoothDevice;

public class OurDevice {

    private BluetoothDevice device;
    private String ourName;
    private String maddress;
    private int colorClicked;

    public OurDevice(BluetoothDevice device, String ourName){
        this.device = device;
        this.ourName = ourName;
        this.maddress = device.getAddress();
    }

    public OurDevice(String maddress, String ourName){
        this.ourName = ourName;
        this.maddress = maddress;
    }

    public void setOurName(String ourName) {
        this.ourName = ourName;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public void setMaddress(String maddress) {
        this.maddress = maddress;
    }

    public void setColorClicked(int colorClicked) {
        this.colorClicked = colorClicked;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public String getOurName() {
        return ourName;
    }

    public String getMaddress() {
        return maddress;
    }

    public int getColorClicked() {
        return colorClicked;
    }
}
