package com.example.arduino_control;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private UUID mDeviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String BLUETOOTH_DEVICE = "BtDevice";
    public static final String DEVICE_UUID = "DeviceUUID";

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ADD_DEVICE = 2;

    private BluetoothAdapter mBluetoothAdapter;
    private User user;
    private ArrayList<OurDevice> ourDeviceList = new ArrayList<>();
    private OurDeviceListAdapter mAdapter;
    private ListView mListView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        dataBase = FirebaseFirestore.getInstance();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        isBluetoothEnable();
        getUser();
        loadData();
    }

    private void getUser() {
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        currentUser = b.getParcelable("user");
    }

    public void isBluetoothEnable() {
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            try {
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Bluetooth failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add01:

                Intent intent1 = new Intent(getApplicationContext(), AddDeviceActivity.class);
                startActivityForResult(intent1, REQUEST_ADD_DEVICE);

                break;
            case R.id.logOut:
                mAuth.signOut();
                Intent intent2 = new Intent(this, LogInActivity.class);
                startActivity(intent2);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:

                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Bluetooth on", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_ADD_DEVICE:

                if (resultCode == RESULT_OK) {
                    String macAddress = null;
                    String ourName = null;

                    try {
                        ourName = data.getStringExtra("name");
                        macAddress = data.getStringExtra("macAddress");
                    } catch (NullPointerException e) {
                        Log.e("T", "Cant import btDevice from Intent.");
                    }

                    ourDeviceList.add(new OurDevice(macAddress, ourName));
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    protected void buildOurDeviceListView() {

        mListView = findViewById(R.id.ourDeviceList);

        mAdapter = new OurDeviceListAdapter(getApplicationContext(), R.layout.item_device, R.id.BtName, ourDeviceList);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < ourDeviceList.size(); i++) {
                    ourDeviceList.get(i).setColorClicked(getResources().getColor(R.color.cardview_light_background));
                }
                ourDeviceList.get(position).setColorClicked(getResources().getColor(R.color.colorClicked));
                mAdapter.notifyDataSetChanged();

                openDialog();

                Intent intent = new Intent(getApplicationContext(), BtControlActivity.class);
                intent.putExtra(BLUETOOTH_DEVICE, mBluetoothAdapter.getRemoteDevice(ourDeviceList.get(position).getMacAddress()));
                intent.putExtra(DEVICE_UUID, mDeviceUUID.toString());
                mBluetoothAdapter.cancelDiscovery();
                startActivity(intent);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Delete: " + ourDeviceList.get(position).getOurName())
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ourDeviceList.remove(position);
                                mAdapter.notifyDataSetChanged();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });
    }

    private class OurDeviceListAdapter extends ArrayAdapter<OurDevice> {

        Context context;

        List<OurDevice> myList;

        public OurDeviceListAdapter(Context context, int resource, int textViewResourceId, List<OurDevice> objects) {
            super(context, resource, textViewResourceId, objects);
            this.context = context;
            myList = objects;
        }

        @Override
        public int getCount() {
            return myList.size();
        }

        @Override
        public OurDevice getItem(int position) {
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
            OurDeviceListAdapter.ViewHolder holder;
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.item_device, null);
                holder = new OurDeviceListAdapter.ViewHolder();

                holder.name = (TextView) v.findViewById(R.id.ourDeviceName);
                holder.item = (RelativeLayout) v.findViewById(R.id.deviceItem);
                holder.item.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));

                v.setTag(holder);
            } else {
                holder = (OurDeviceListAdapter.ViewHolder) v.getTag();
            }
            OurDevice device = myList.get(position);
            holder.name.setText(device.getOurName());
            holder.item.setBackgroundColor(myList.get(position).getColorClicked());

            return v;
        }

        private class ViewHolder {
            TextView name;
            RelativeLayout item;
        }
    }

    public void saveData() {
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();

//        Gson gson = new Gson();
//        String json = gson.toJson(ourDeviceList);

        //save to local
//        editor.putString("device list", json);
//        editor.apply();

        //save to firebase
        User user = new User(ourDeviceList);
        dataBase.collection("users").document(currentUser.getUid()).set(user).addOnSuccessListener(aVoid -> {
            Log.d(TAG, "saveData: Data successfully written");
        }).addOnFailureListener(e -> {
            Log.w(TAG, "saveData: Error", e);
        });

        // Kdzyž nemám internet, tak se ani nelognu.
        //TODO: Save to local and firebase. Need it?
    }

    private void loadData() {
//        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);

//        Gson gson = new Gson();
//        String json = sharedPreferences.getString("device list", null);
        Type type = new TypeToken<ArrayList<OurDevice>>() {
        }.getType();

        dataBase.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(documentSnapshot -> {
            user  = documentSnapshot.toObject(User.class);
            ourDeviceList = user.getOurDeviceList();
            buildOurDeviceListView();

        }).addOnFailureListener(e->{
            user = new User(new ArrayList<OurDevice>());
            ourDeviceList = new ArrayList<OurDevice>();
            buildOurDeviceListView();
        });

        // Kdzyž nemám internet, tak se ani nelognu.
        //TODO: Load from Local if internet not available else load from firebase. Need it?
    }

    protected void openDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Loading");

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }
}



