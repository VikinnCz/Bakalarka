package com.example.arduino_control.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.arduino_control.R;
import com.example.arduino_control.ui.main.AddDevicePagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

/**
 * This activity have two fragments one BtScanFragment which show all bluetooth paired devices and BtControlActivity where user can scan bluetooth macAddress by QR code.
 * @author Vikinn
 */
public class AddDeviceActivity extends AppCompatActivity {

    BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);

        AddDevicePagerAdapter sectionsPagerAdapter = new AddDevicePagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.startDiscovery();

    }

    /**
     * This function send attributes of new bluetooth device to MainActivity.
     * @param mDevice is selected new bluetooth device.
     * @param ourName is selected name of new bluetooth device.
     */
    public void cancelActivityWithResult(BluetoothDevice mDevice, String ourName){

        Intent resultIntent = new Intent();
        resultIntent.putExtra("macAddress",mDevice.getAddress());
        resultIntent.putExtra("name",ourName);
        setResult(RESULT_OK,resultIntent);
        mBluetoothAdapter.cancelDiscovery();
        finish();
    }

    public static class PairedDeviceAdapter extends ArrayAdapter<BluetoothDevice> {

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
            AddDeviceActivity.PairedDeviceAdapter.ViewHolder holder;
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.item_list, parent);
                holder = new ViewHolder();

                holder.name = (TextView) v.findViewById(R.id.BtName);

                v.setTag(holder);
            } else {
                holder = (AddDeviceActivity.PairedDeviceAdapter.ViewHolder) v.getTag();
            }

            BluetoothDevice device = myList.get(position);
            String deviceDescription = device.getName() + "\n " + device.getAddress();
            holder.name.setText(deviceDescription);

            return v;
        }

        private static class ViewHolder{
            TextView name;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
