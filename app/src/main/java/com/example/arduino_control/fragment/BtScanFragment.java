package com.example.arduino_control.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.arduino_control.R;
import com.example.arduino_control.activity.AddDeviceActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Fragment which search all paired bluetooth device and show to user for select ona.
 * @author Vikinn
 */
public class BtScanFragment extends Fragment {

    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    List<BluetoothDevice> pairedDeviceList = new ArrayList<>();
    BluetoothDevice mDevice;

    private ListView mListView;

    public String ourName;

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
       PairedDeviceAdapter mAdapter = new PairedDeviceAdapter(getContext(), R.layout.item_list, R.id.BtName, pairedDeviceList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener((parent, view, position, id) -> {
            mDevice = (BluetoothDevice) (mListView.getAdapter()).getItem(position);
            try {
                openDialogSetDeviceName();
            } catch (Exception e) {
                Log.e("T", "Dialog can not open.");
            }
        });
    }

    /**
     * Show dialog for write name for selected device a then cancel activity.
     */
    protected void openDialogSetDeviceName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);

        final EditText mOurName = view.findViewById(R.id.ourName);

        builder.setView(view)
                .setTitle("Rename")
                .setNegativeButton("cancel", (dialog, which) -> {})
                .setPositiveButton("ok", (dialog, which) -> {
                    ourName = mOurName.getText().toString();
                    cancel();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Call function cancelActivityWithResult from AddDevice Activity which send selected device with name to MainActivity.
     */
    public void cancel(){
        ((AddDeviceActivity) this.requireActivity()).cancelActivityWithResult(mDevice,ourName);
    }

    public static class PairedDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
        Context context;
        List<BluetoothDevice> myList;

        public PairedDeviceAdapter(Context context, int resource, int textViewResourceId, List<BluetoothDevice> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            this.myList = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View v = convertView;
            PairedDeviceAdapter.ViewHolder holder;
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.item_list, null);
                holder = new PairedDeviceAdapter.ViewHolder();

                holder.name = v.findViewById(R.id.BtName);

                v.setTag(holder);
            } else {
                holder = (PairedDeviceAdapter.ViewHolder) v.getTag();
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

}