package com.example.arduino_control;

import android.app.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BtControll extends Activity {

    public SeekBar controller_01;
    public SeekBar controller_02;
    public SeekBar controller_03;
    public BluetoothDevice mDevice;
    public ConnectingToBT c;
    public ManageConnection manager;

    public UUID mDeviceUUID;

    String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controling_activity);

        controller_01 = findViewById(R.id.controller_01);
        controller_02 = findViewById(R.id.controller_02);
        controller_03 = findViewById(R.id.controller_03);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        mDevice = b.getParcelable(MainActivity.BLUETOOTH_DEVICE);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));

        startConnecting();

        sendData();
    }

    public void startConnecting(){
        c = new ConnectingToBT(mDevice);
        c.run();
    }

    public void sendData(){
        controller_01.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                data = controller_01.getProgress() + "\n";
                manager.write(data.getBytes());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        controller_02.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                data = controller_02.getProgress() + "\n";
                manager.write(data.getBytes());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        controller_03.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                data = controller_03.getProgress() + "\n";
                manager.write(data.getBytes());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private class ConnectingToBT extends Thread {

        private final BluetoothSocket mnSocket;
        private final BluetoothDevice mnDevice;

        public ConnectingToBT(BluetoothDevice device) {

            BluetoothSocket tmp = null;
            this.mnDevice = device;

            try {
                tmp = mnDevice.createRfcommSocketToServiceRecord(mDeviceUUID);
            } catch (IOException e){
                Log.e("T", "Could not create client socket.");
            }

            mnSocket = tmp;
        }

        public void run(){
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try{
                mnSocket.connect();
            } catch (IOException e){
                Log.e("T", "Could not connect client socket.", e);
                Toast.makeText(BtControll.this,"Could not connect to client.", Toast.LENGTH_LONG).show();
                try{
                    mnSocket.close();
                    finish();
                }catch (IOException ee){
                    Log.e("T", "Could not close the client socket.", ee);
                }
            }
            manager= new ManageConnection(mnSocket);
        }

        public void test(){
            if (!mnSocket.isConnected()){
                c.cancel();
                startConnecting();
            }
        }

        public void cancel() {
            try {
                mnSocket.close();
                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            } catch (IOException e) {
                Log.e("T", "Could not close the client socket.", e);
            }
        }
    }

    private class ManageConnection extends Thread {
        private final BluetoothSocket mmSocket;
        private final OutputStream mmOutputStream;

        public ManageConnection (BluetoothSocket socket){
            this.mmSocket = socket;
            OutputStream tmpOut = null;

            try {
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e){
                Log.e("T","Could not get input stream.");
            }

            mmOutputStream = tmpOut;
        }


        public void write(byte[] bytes){
            try{
                mmOutputStream.write(bytes);
                Log.d("S","send: " + data);

            }catch (Exception e){
                Log.e("T","Could not send data to device." + data);
                if (mmSocket.isConnected()){
                    c.cancel();
                    startConnecting();
                }
                else{
                    startConnecting();
                }
            }
        }

        public void cancel(){
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("T","Could not close the connect socket");
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        c.test();
    }

    @Override
    protected void onPause(){
        super.onPause();
        c.cancel();
    }

    @Override
    protected void onDestroy(){
        c.cancel();
        super.onDestroy();
    }
}
