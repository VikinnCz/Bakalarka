package com.example.arduino_control.activity;

import android.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.arduino_control.OurDevice;
import com.example.arduino_control.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BtControlActivity extends Activity {

    private static final String TAG = BtControlActivity.class.getName();

    private SeekBar controller_01;
    private SeekBar controller_02;
    private SeekBar controller_03;
    private OurDevice ourDevice;
    private BluetoothDevice btDevice;
    private ConnectingToBT c;
    private ManageConnection manager;
    private Dialog mDialog;

    private UUID mDeviceUUID;

    String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        openDialogConnecting();

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        ourDevice = (OurDevice) intent.getSerializableExtra(MainActivity.BLUETOOTH_DEVICE);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));

        startConnecting();

//      TODO: predelat na async with Thread waiting for connection.
//      TODO: then check if device is set or no
//      TODO: create introduction to set device if is not set
    }

    public void startConnecting() {
        btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ourDevice.getMacAddress());
        c = new ConnectingToBT(btDevice);
        c.run();
        Log.d(TAG, "startConnecting: ");
            for (int i = 0; i <= 50; i++) {
                Log.d(TAG, "Connecting: " + i);
                if (c.isConnect()) {
                    runOnUiThread(() -> {
                        setView();
                        mDialog.dismiss();
                    });
                    return;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            runOnUiThread(() -> {
                mDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("\"Oj\"");
                builder.setMessage("Nepodařilo se připojit k Bluetooth zařízení, prosím zkontrolujte jestli je zapnuté.");
                builder.setPositiveButton("Ok", ((dialog, which) -> finish()));
                builder.setOnCancelListener((dialog) -> finish());
                mDialog = builder.create();
                mDialog.show();
            });
    }

    private void setView(){
        setContentView(R.layout.activity_bt_control);

        controller_01 = findViewById(R.id.controller_01);
        controller_02 = findViewById(R.id.controller_02);
        controller_03 = findViewById(R.id.controller_03);

        sendData();
    }

    public void sendData() {
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

    protected void openDialogConnecting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(R.layout.dialog_connecting);
        builder.setCancelable(false);

        mDialog = builder.create();
        mDialog.show();
    }

    private class ConnectingToBT extends Thread {

        private final BluetoothSocket mnSocket;
        private final BluetoothDevice mnDevice;

        public ConnectingToBT(BluetoothDevice device) {

            BluetoothSocket tmp = null;
            this.mnDevice = device;

            try {
                tmp = mnDevice.createRfcommSocketToServiceRecord(mDeviceUUID);
            } catch (IOException e) {
                Log.e("T", "Could not create client socket.");
            }

            mnSocket = tmp;
        }

        public void run() {
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            try {
                mnSocket.connect();
                Log.d(TAG, "run: Connected");
            } catch (IOException e) {
                Log.e(TAG, "Could not connect client socket.", e);
                Toast.makeText(BtControlActivity.this, "Could not connect to client.", Toast.LENGTH_LONG).show();
                try {
                    mnSocket.close();
                    finish();
                } catch (IOException ee) {
                    Log.e(TAG, "Could not close the client socket.", ee);
                }
            }
            manager = new ManageConnection(mnSocket);
        }

        public boolean isConnect() {
            return mnSocket.isConnected();
        }

        public void test() {
            if (!mnSocket.isConnected()) {
                c.cancel();
                startConnecting();
            }
        }

        public void cancel() {
            try {
                mnSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket.", e);
            }
        }
    }

    private class ManageConnection extends Thread {
        private final BluetoothSocket mmSocket;
        private final OutputStream mmOutputStream;

        public ManageConnection(BluetoothSocket socket) {
            this.mmSocket = socket;
            OutputStream tmpOut = null;

            try {
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Could not get input stream.");
            }

            mmOutputStream = tmpOut;
        }


        public void write(byte[] bytes) {
            try {
                mmOutputStream.write(bytes);
                Log.d(TAG, "send: " + data);

            } catch (Exception e) {
                Log.e(TAG, "Could not send data to device." + data);
                if (mmSocket.isConnected()) {
                    c.cancel();
                    startConnecting();
                } else {
                    startConnecting();
                }
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket");
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        c.test();
    }

    @Override
    protected void onPause() {
        super.onPause();
        c.cancel();
        manager.cancel();
    }

    @Override
    protected void onDestroy() {
        c.cancel();
        manager.cancel();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        c.cancel();
        manager.cancel();
        super.onBackPressed();
    }
}
