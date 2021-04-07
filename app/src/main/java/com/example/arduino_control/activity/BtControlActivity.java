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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arduino_control.OurDevice;
import com.example.arduino_control.R;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;
import java.util.zip.Inflater;

public class BtControlActivity extends AppCompatActivity {

    private static final String TAG = BtControlActivity.class.getName();
    private static final String KNOB_1_MIN = "0\n";
    private static final String KNOB_2_MIN = "181\n";
    private static final String KNOB_3_MIN = "361\n";

    private SeekBar controller_01;
    private SeekBar controller_02;
    private SeekBar controller_03;
    private OurDevice ourDevice;
    private BluetoothDevice btDevice;
    private ConnectingToBT c;
    private ManageConnection manager;
    Dialog mDialog;

    private UUID mDeviceUUID;

    String data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        ourDevice = (OurDevice) intent.getSerializableExtra(MainActivity.BLUETOOTH_DEVICE);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));

        openDialogConnecting();

        startConnecting();

//      TODO: check if device is set or no
//      TODO: create introduction to set device if is not set
    }

    public void startConnecting() {
        btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(ourDevice.getMacAddress());
        c = new ConnectingToBT(btDevice);
        new Thread(() -> {
            c.run();
        }).start();
        Log.d(TAG, "startConnecting: ");
        new Thread(() -> {
            for (int i = 0; i <= 50; i++) {
                Log.d(TAG, "Connecting: " + i);
                if (c.isConnect()) {
                    runOnUiThread(() -> {
                        mDialog.dismiss();
                        setView();
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
        }).start();
    }

    private void setView() {

//        setContentView(R.layout.activity_bt_control);
//
//        controller_01 = findViewById(R.id.controller_01);
//        controller_02 = findViewById(R.id.controller_02);
//        controller_03 = findViewById(R.id.controller_03);
//
//                    sendData();


        if(ourDevice.isSet) {
            setContentView(R.layout.activity_bt_control);

            controller_01 = findViewById(R.id.controller_01);
            controller_02 = findViewById(R.id.controller_02);
            controller_03 = findViewById(R.id.controller_03);

            sendData();
        } else {
            openDialogSetNewDevice();
    }
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

    private void openDialogSetNewDevice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nejprve je potřeba vše nastavit");
        builder.setMessage("Sundejte zařízení s efektu a okotče knoby na minimul do leva. Následně stiskněte ok.");
        builder.setPositiveButton("Ok", ((dialog, which) -> {
            manager.write(KNOB_1_MIN.getBytes());
            manager.write(KNOB_2_MIN.getBytes());
            manager.write(KNOB_3_MIN.getBytes());
            openDialogPutDevice();
        }));
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();

    }

    private void openDialogPutDevice(){
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nasaďte zařízení na efekt");
        builder.setMessage("Nasaďte serva na knoby");
        builder.setPositiveButton("Ok", ((dialog, which) -> openDialogSetKnob1()));
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();
    }

    private void openDialogSetKnob1(){
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_set_knob_1, null);

        EditText knob1Name = view.findViewById(R.id.nameKnob1);
        SeekBar knob1Max = view.findViewById(R.id.maxKnob1);

        knob1Max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                data = progress + "\n";
                manager.write(data.getBytes());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setView(view);
        builder.setTitle("Nastavte maximálni hodnotu knobu 1");
        builder.setMessage("Pomalu posunujte slidrem z leva do prava než dojedete na maximální úhel otočení vašeho knobu. Následně stiskněte ok.");
        builder.setPositiveButton("Ok", ((dialog, which) -> {
            ourDevice.getMax().set(0,knob1Max.getProgress());
            ourDevice.getNames().set(0,knob1Name.getText().toString());
        }));
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        c.test();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            c.cancel();
            manager.cancel();
        } catch (NullPointerException e) {
            Log.d(TAG, "onDestroy: socket is canceled");
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        try {
            c.cancel();
            manager.cancel();
        } catch (NullPointerException e) {
            Log.d(TAG, "onDestroy: socket is canceled");
        }
        super.onBackPressed();
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
//                runOnUiThread(()->Toast.makeText(BtControlActivity.this, "Could not connect to client.", Toast.LENGTH_LONG).show());
                try {
                    mnSocket.close();
                    return;
                } catch (IOException ee) {
                    Log.e(TAG, "Could not close the client socket.", ee);
                    return;
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
}
