package com.example.arduino_control.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.arduino_control.OurDevice;
import com.example.arduino_control.Preset;
import com.example.arduino_control.R;
import com.example.arduino_control.User;
import com.example.arduino_control.ui.main.ListOfPresetsAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class BtControlActivity extends AppCompatActivity {

    private static final String TAG = BtControlActivity.class.getName();
    private static final String KNOB_1_MIN = "0\n";
    private static final String KNOB_2_MIN = "181\n";
    private static final String KNOB_3_MIN = "361\n";

    private SeekBar controller_01;
    private SeekBar controller_02;
    private SeekBar controller_03;
    private Button knob2Add;
    private Button knob3Add;
    private User user;
    private OurDevice ourDevice;
    private ConnectingToBT c;
    private ManageConnection manager;
    private ListOfPresetsAdapter listOfPresetsAdapter;
    Dialog mDialog;

    private UUID mDeviceUUID;

    private String data;
    public int positionInDeviceList;
    private ArrayList<Preset> listOfPresets;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        user = (User) intent.getSerializableExtra(MainActivity.USER);
        positionInDeviceList = b.getInt(MainActivity.POSITION);
        mDeviceUUID = UUID.fromString(b.getString(MainActivity.DEVICE_UUID));
        ourDevice = user.getOurDeviceList().get(positionInDeviceList);
        listOfPresets = ourDevice.getListOfPresets();
        listOfPresetsAdapter = new ListOfPresetsAdapter(getApplicationContext(), R.layout.item_preset, listOfPresets);

        openDialogConnecting();

        startConnecting();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_presets, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.presetsItem) {
            openDialogPresets();
        }
        return super.onOptionsItemSelected(item);
    }

    public void startConnecting() {
        BluetoothDevice btDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(user.getOurDeviceList().get(positionInDeviceList).getMacAddress());
        c = new ConnectingToBT(btDevice);
        c.start(); // TODO: Testnou jestloi je pottřeba po změne s c.run() na c.start() stále spouštět v dalším vlkáknu dřív zaseklo main thread.
        Log.d(TAG, "startConnecting: ");
        new Thread(() -> {
            for (int i = 0; i <= 50; i++) {
                Log.d(TAG, "Connecting: " + i);
                if (c.isConnect()) {
                    runOnUiThread(() -> {
                        mDialog.dismiss();
                        isDeviceSet();
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

    public void isDeviceSet() {
        if (ourDevice.isSet) {
            setView();
        } else {
            openDialogSetNewDevice();
        }
    }

    private void setView() {

        setContentView(R.layout.activity_bt_control);

        TextView knob1NameView = findViewById(R.id.knob1NameView);
        TextView knob2NameView = findViewById(R.id.knob2NameView);
        TextView knob3NameView = findViewById(R.id.knob3NameView);
        controller_01 = findViewById(R.id.controller_01);
        controller_02 = findViewById(R.id.controller_02);
        controller_03 = findViewById(R.id.controller_03);
        knob2Add = findViewById(R.id.addKnob2);
        knob3Add = findViewById(R.id.addKnob3);

        Objects.requireNonNull(getSupportActionBar()).setTitle(ourDevice.getOurName());

        switch (ourDevice.getKnobs()) {
            case 1:
                knob1NameView.setText(ourDevice.getNames().get(0));
                controller_01.setMax(ourDevice.getMax().get(0));

                knob1NameView.setVisibility(View.VISIBLE);
                controller_01.setVisibility(View.VISIBLE);
                controller_01.setClickable(true);

                knob2Add.setVisibility(View.VISIBLE);

                break;
            case 2:
                knob1NameView.setText(ourDevice.getNames().get(0));
                knob2NameView.setText(ourDevice.getNames().get(1));
                controller_01.setMax(ourDevice.getMax().get(0));
                controller_02.setMax(ourDevice.getMax().get(1));

                knob1NameView.setVisibility(View.VISIBLE);
                controller_01.setVisibility(View.VISIBLE);
                controller_01.setClickable(true);

                knob2NameView.setVisibility(View.VISIBLE);
                controller_02.setVisibility(View.VISIBLE);
                controller_02.setClickable(true);

                knob3Add.setVisibility(View.VISIBLE);
                break;
            case 3:
                knob1NameView.setText(ourDevice.getNames().get(0));
                knob2NameView.setText(ourDevice.getNames().get(1));
                knob3NameView.setText(ourDevice.getNames().get(2));
                controller_01.setMax(ourDevice.getMax().get(0));
                controller_02.setMax(ourDevice.getMax().get(1));
                controller_03.setMax(ourDevice.getMax().get(2));

                knob1NameView.setVisibility(View.VISIBLE);
                controller_01.setVisibility(View.VISIBLE);
                controller_01.setClickable(true);

                knob2NameView.setVisibility(View.VISIBLE);
                controller_02.setVisibility(View.VISIBLE);
                controller_02.setClickable(true);

                knob3NameView.setVisibility(View.VISIBLE);
                controller_03.setVisibility(View.VISIBLE);
                controller_03.setClickable(true);
                break;
        }

        takeData();
        addKnobs();
    }

    private void addKnobs() {
        knob2Add.setOnClickListener(v -> {
            manager.write(KNOB_2_MIN.getBytes());
            openDialogSetKnob2();


        });

        knob3Add.setOnClickListener(v -> {
            manager.write(KNOB_3_MIN.getBytes());
            openDialogSetKnob3();
        });
    }

    public void takeData() {
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

    private void openDialogPutDevice() {
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Nasaďte zařízení na efekt");
        builder.setMessage("Nasaďte serva na knoby");
        builder.setPositiveButton("Ok", ((dialog, which) -> openDialogSetKnob1()));
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();
    }

    private void openDialogSetKnob1() {
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
            ourDevice.getMax().set(0, knob1Max.getProgress());
            ourDevice.getNames().set(0, knob1Name.getText().toString());
            openDialogSetKnob2();
        }));
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();
    }

    private void openDialogSetKnob2() {
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_set_knob_2, null);

        EditText knob2Name = view.findViewById(R.id.nameKnob2);
        SeekBar knob2Max = view.findViewById(R.id.maxKnob2);

        knob2Max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        builder.setTitle("Nastavte maximálni hodnotu knobu 2");
        builder.setMessage("Pomalu posunujte slidrem z leva do prava než dojedete na maximální úhel otočení vašeho knobu. Následně stiskněte ok.");
        builder.setPositiveButton("Ok", ((dialog, which) -> {
            ourDevice.getMax().set(1, knob2Max.getProgress());
            ourDevice.getNames().set(1, knob2Name.getText().toString());
            openDialogSetKnob3();
        }));
        builder.setNegativeButton("Skip", ((dialog, which) -> {
            ourDevice.setKnobs(1);
            ourDevice.isSet = true;
            user.getOurDeviceList().set(positionInDeviceList, ourDevice);
            setView();

        }));
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();
    }

    private void openDialogSetKnob3() {
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_set_knob_3, null);

        EditText knob3Name = view.findViewById(R.id.nameKnob3);
        SeekBar knob3Max = view.findViewById(R.id.maxKnob3);

        knob3Max.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
        builder.setTitle("Nastavte maximálni hodnotu knobu 3");
        builder.setMessage("Pomalu posunujte slidrem z leva do prava než dojedete na maximální úhel otočení vašeho knobu. Následně stiskněte ok.");
        builder.setPositiveButton("Ok", ((dialog, which) -> {
            ourDevice.getMax().set(2, knob3Max.getProgress());
            ourDevice.getNames().set(2, knob3Name.getText().toString());
            ourDevice.setKnobs(3);
            ourDevice.isSet = true;
            user.getOurDeviceList().set(positionInDeviceList, ourDevice);
            setView();
        }));
        builder.setNegativeButton("Skip", ((dialog, which) -> {
            ourDevice.setKnobs(2);
            ourDevice.isSet = true;
            user.getOurDeviceList().set(positionInDeviceList, ourDevice);
            setView();
        }));
        builder.setCancelable(false);
        mDialog = builder.create();
        mDialog.show();
    }

    private void openDialogPresets() {
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_presets, null);

        ListView listViewOfPresets = view.findViewById(R.id.listViewPresets);
        listViewOfPresets.setAdapter(listOfPresetsAdapter);


        builder.setView(view);
        builder.setTitle("Presety");
        builder.setPositiveButton("Přidat", (dialog, which) -> openDialogGetPresetName()); // save preset
        builder.setNegativeButton("Odejít", (dialog, which) -> {});
        mDialog = builder.create();
        mDialog.show();

        listViewOfPresets.setOnItemClickListener((parent, view1, position, id) -> {
            // load preset
            Preset preset = ourDevice.listOfPresets.get(position);
            switch (ourDevice.getKnobs()) {
                case 1:
                    data = preset.getValue1() + "\n";
                    manager.write(data.getBytes());
                    controller_01.setProgress(preset.getValue1());
                    break;
                case 2:
                    //TODO: Nestíhá poslat druhou hodnotu
                    data = preset.getValue1() + "\n";
                    manager.write(data.getBytes());
                    controller_01.setProgress(preset.getValue1());

                    data = preset.getValue2() + "\n";
                    manager.write(data.getBytes());
                    controller_02.setProgress(preset.getValue2());
                    break;
                case 3:
                    data = preset.getValue1() + "\n";
                    manager.write(data.getBytes());
                    controller_01.setProgress(preset.getValue1());

                    data = preset.getValue2() + "\n";
                    manager.write(data.getBytes());
                    controller_02.setProgress(preset.getValue2());

                    data = preset.getValue3() + "\n";
                    manager.write(data.getBytes());
                    controller_03.setProgress(preset.getValue3());

                    break;
            }
        });

        listViewOfPresets.setOnItemLongClickListener((parent, view12, position, id) -> {
            openDialogChangePreset(position);
            return true;
        });
    }

    public void openDialogGetPresetName() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_layout, null);

        EditText mOurName = view.findViewById(R.id.ourName);

        builder.setView(view)
                .setTitle("Rename")
                .setNegativeButton("cancel", (dialog, which) -> {

                })
                .setPositiveButton("ok", (dialog, which) -> {
                    // sae preset
                    switch (ourDevice.getKnobs()) {
                        case 1:
                            listOfPresets.add(new Preset(mOurName.getText().toString(), controller_01.getProgress()));
                            listOfPresetsAdapter.notifyDataSetChanged();
                            break;
                        case 2:
                            listOfPresets.add(new Preset(mOurName.getText().toString(), controller_01.getProgress(), controller_02.getProgress()));
                            listOfPresetsAdapter.notifyDataSetChanged();
                            break;
                        case 3:
                            listOfPresets.add(new Preset(mOurName.getText().toString(), controller_01.getProgress(), controller_02.getProgress(), controller_03.getProgress()));
                            listOfPresetsAdapter.notifyDataSetChanged();
                            break;
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openDialogChangePreset(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_layout, null);

        EditText getName = v.findViewById(R.id.ourName);

        builder.setView(v);
        builder.setPositiveButton("Přejmenovat", (dialog, which) -> {
            listOfPresets.get(position).setName(getName.getText().toString());
            listOfPresetsAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Smazat", (dialog, which) -> {
            listOfPresets.remove(position);
            listOfPresetsAdapter.notifyDataSetChanged();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore dataBase = FirebaseFirestore.getInstance();
        dataBase.collection("users").document(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "saveData: Data successfully written"))
                .addOnFailureListener(e -> Log.w(TAG, "saveData: Error", e));
    }

    @Override
    protected void onResume() {
        super.onResume();
        c.test();
    }

    @Override
    protected void onPause() {
        saveData();
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("user", user);
        setResult(RESULT_OK, resultIntent);
        try {
            c.cancel();
            manager.cancel();
        } catch (NullPointerException e) {
            Log.d(TAG, "onDestroy: socket is canceled");
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        saveData();
        try {
            c.cancel();
            manager.cancel();
        } catch (NullPointerException e) {
            Log.d(TAG, "onDestroy: socket is canceled");
        }
        super.onDestroy();
    }

    private class ConnectingToBT extends Thread {

        private final BluetoothSocket mnSocket;

        public ConnectingToBT(BluetoothDevice device) {

            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(mDeviceUUID);
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
        private boolean sending = false;

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
            if (!sending) {
                try {
                    sending = true;
                    mmOutputStream.write(bytes);
                    Log.d(TAG, "send: " + data);
                    new Thread(() -> { //TODO: Stále doje po chvíli k zahlcení řešení může být upravit kód v arduinu aby mazal buffer
                        try {
                            Thread.sleep(20);
                            sending = false;
                        } catch (InterruptedException e) {
                            Log.d(TAG, "write: " + e);
                        }
                    }).start();
                } catch (Exception e) {
                    Log.e(TAG, "Could not send data to device." + data);
                    if (mmSocket.isConnected()) {
                        c.cancel();
                    }
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
