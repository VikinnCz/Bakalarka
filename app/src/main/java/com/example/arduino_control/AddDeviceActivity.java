package com.example.arduino_control;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class AddDeviceActivity extends Activity {

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<BluetoothDevice> pairedDeviceList = new ArrayList<>();
    ListView mListView;
    BluetoothDevice mDevice;

    public String ourName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        mListView = findViewById(R.id.deviceList);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        setListView();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDevice = (BluetoothDevice) (mListView.getAdapter()).getItem(position);
                try {
                    openDialog();
                } catch (Exception e) {
                    Log.e("T", "Dialog can not open.");
                }
            }
        });
    }

    protected void cancelActivityWithResult(BluetoothDevice mDevice, String ourName){

        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(pairedDeviceList);
        editor.putString("devices list", json);
        editor.apply();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("macAddress",mDevice.getAddress());
        resultIntent.putExtra("name",ourName);
        setResult(RESULT_OK,resultIntent);
        mBluetoothAdapter.cancelDiscovery();
        finish();
    }

    protected void openDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

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
                        cancelActivityWithResult(mDevice, ourName);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
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
        PairedDeviceAdapter mAdapter = new PairedDeviceAdapter(getApplicationContext(),R.layout.item_list,R.id.BtName, pairedDeviceList);
        mListView.setAdapter(mAdapter);

    }

    private class PairedDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

        Context context;
        List<BluetoothDevice> myList;

        public PairedDeviceAdapter(Context context, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            myList = objects;
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public BluetoothDevice getItem(int position) {
            return myList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            PairedDeviceAdapter.ViewHolder holder;
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.item_list, null);
                holder = new PairedDeviceAdapter.ViewHolder();

                holder.name = (TextView) v.findViewById(R.id.BtName);

                v.setTag(holder);
            } else {
                holder = (PairedDeviceAdapter.ViewHolder) v.getTag();
            }

            BluetoothDevice device = myList.get(position);
            holder.name.setText(device.getName() + "\n " + device.getAddress());

            return v;
        }

        private class ViewHolder{
            TextView name;
        }
    }
}
