package com.example.arduino_control;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ListView;

import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BtScanFragment extends Fragment {

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<BluetoothDevice> pairedDeviceList = new ArrayList<>();
    BluetoothDevice mDevice;

    private ListView mListView;

    public String ourName;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
            }
        }
    };

    public BtScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bt_scan, container, false);
        mListView = v.findViewById(R.id.deviceList);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        setListView();

        return v;
    }

    protected void setListView(){

        pairedDevices = mBluetoothAdapter.getBondedDevices();
        int size = pairedDeviceList.size();

        if (size > 0) {
            pairedDeviceList.clear();
        }

        if(pairedDevices.size()>0){
            pairedDeviceList.addAll(pairedDevices);
        }
        AddDeviceActivity.PairedDeviceAdapter mAdapter = new AddDeviceActivity.PairedDeviceAdapter(getContext(),R.layout.item_list,R.id.BtName, pairedDeviceList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            mDevice = (BluetoothDevice) (mListView.getAdapter()).getItem(position);
            try {
                openDialog();
            } catch (Exception e) {
                Log.e("T", "Dialog can not open.");
            }
        });
    }

    protected void openDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);

        final EditText mOurName = (EditText) view.findViewById(R.id.ourName);

        builder.setView(view)
                .setTitle("Rename")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ourName = mOurName.getText().toString();
                        cancel();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void cancel(){
        ((AddDeviceActivity)this.getActivity()).cancelActivityWithResult(mDevice,ourName);
    }

}