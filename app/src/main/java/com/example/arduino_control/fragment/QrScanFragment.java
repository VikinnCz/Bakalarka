package com.example.arduino_control.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.example.arduino_control.R;
import com.example.arduino_control.activity.AddDeviceActivity;

/**
 * Fragment for add bluetooth device by scan QR code with bluetooth MAC address. This Fragment use library code-scanner. Require CAMERA permission.
 * @see com.budiyev.android.codescanner.CodeScanner
 * @author Vikinn
 */
public class QrScanFragment extends Fragment {

    private CodeScanner mCodeScanner;
    private CodeScannerView scannerView;
    private BluetoothDevice mDevice;
    private Activity activity;

    public String ourName;

    public QrScanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_qr_scan, container, false);
        scannerView = v.findViewById(R.id.scanner_view);
        scanner();
        return v;
    }

    /**
     * Operate with scanView and read QR code to String.
     */
    private void scanner() {
        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(result -> activity.runOnUiThread(() -> {
            if(BluetoothAdapter.checkBluetoothAddress(result.getText())){
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(result.getText());
                try {
                    openDialogSerDeviceName();
                } catch (Exception e) {
                    Log.e("T", "Dialog can not open.");
                }
            } else {
                Toast.makeText(activity, "Invalid MAC address",Toast.LENGTH_SHORT).show();
            }
        }));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
    }

    /**
     * Show dialog for write name for selected device a then cancel activity.
     */
    protected void openDialogSerDeviceName(){
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

    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
}