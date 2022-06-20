package com.example.itracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.bluetooth.le.ScanCallback;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.empatica.empalink.ConnectionNotAllowedException;
import com.empatica.empalink.EmpaDeviceManager;
import com.empatica.empalink.EmpaticaDevice;
import com.empatica.empalink.config.EmpaSensorStatus;
import com.empatica.empalink.config.EmpaSensorType;
import com.empatica.empalink.config.EmpaStatus;
import com.empatica.empalink.delegate.EmpaDataDelegate;
import com.empatica.empalink.delegate.EmpaStatusDelegate;
import com.example.itracker.data.iDBHandler;
import com.example.itracker.model.Sensor;


import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements EmpaDataDelegate, EmpaStatusDelegate, SensorEventListener {

    private static final String EMPATICA_API_KEY = "cf1d75d31bb14142a3a2148ef4a81193";
    private EmpaDeviceManager deviceManager = null;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 1;
    ArrayList<String> w_acc_x = new ArrayList<String>();
    ArrayList<String> w_acc_y = new ArrayList<String>();
    ArrayList<String> w_acc_z = new ArrayList<String>();
    ArrayList<String> w_bvp = new ArrayList<String>();
    ArrayList<String> w_ts = new ArrayList<String>();
    ArrayList<String> m_acc_x = new ArrayList<String>();
    ArrayList<String> m_acc_y = new ArrayList<String>();
    ArrayList<String> m_acc_z = new ArrayList<String>();
    ArrayList<String> m_gyro_x = new ArrayList<String>();
    ArrayList<String> m_gyro_y = new ArrayList<String>();
    ArrayList<String> m_gyro_z = new ArrayList<String>();

    private  int init_db_rec = 0;
    private SensorManager msensorManager;
    private android.hardware.Sensor msensorAccelerometer,msensorGyro;
    boolean isStart = false;
    boolean is_Acc_Start = false;
    private String inp_activity;
    private String inp_username;
//    private TextView accel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button start_button = findViewById(R.id.button2);
        Button stop_button = findViewById(R.id.button3);
        EditText edit_text = findViewById(R.id.activity);
        EditText username = findViewById(R.id.username);


        iDBHandler db = new iDBHandler(MainActivity.this);
        msensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        msensorAccelerometer = msensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
        msensorGyro = msensorManager.getDefaultSensor(android.hardware.Sensor.TYPE_GYROSCOPE);
//        accel =findViewById(R.id.textView);




        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] VALUES = new String[]{"Sitting", "Standing", "Walking", "Jogging", "Writing", "Brushing", "Eating", "Sleeping", "Upstairs", "Downstairs", "Drinking", "Testing"};
                inp_activity = edit_text.getText().toString();
                inp_username = username.getText().toString();
                if ((inp_username.trim().equals(""))) {
                    Toast.makeText(MainActivity.this, "User name is empty", Toast.LENGTH_SHORT).show();
                } else if ((inp_username.length()<4)){
                    Toast.makeText(MainActivity.this, "User name cannot be less than 4 characters", Toast.LENGTH_SHORT).show();
                } else{
                if (Arrays.asList(VALUES).contains(inp_activity)) {
                    initEmpaticaDeviceManager();
                    Toast.makeText(MainActivity.this, "Set your Watch Sensor ON!", Toast.LENGTH_SHORT).show();
                    isStart = true;
                    msensorManager.registerListener((SensorEventListener) MainActivity.this, msensorGyro, SensorManager.SENSOR_DELAY_GAME);
                    msensorManager.registerListener((SensorEventListener) MainActivity.this, msensorAccelerometer, SensorManager.SENSOR_DELAY_GAME);
//                init_db_rec = db.getCount();
                } else {
                    Toast.makeText(MainActivity.this, "Input correct activity", Toast.LENGTH_SHORT).show();
                }
            }
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isStart = false;
                is_Acc_Start = false;
                if (deviceManager != null) {
                    deviceManager.disconnect();
                }
                msensorManager.unregisterListener(MainActivity.this);
                Toast.makeText(MainActivity.this, "Stopped Recording!", Toast.LENGTH_SHORT).show();
                Log.i("e4", "Watch BVP Sample Count : "+String.valueOf(w_bvp.size()));
                Log.i("e4", "Watch Acc Sample Count : "+String.valueOf(w_acc_x.size()));
                Log.i("e4", "Mobile Acc Sample Count : "+String.valueOf(m_acc_x.size()));
                Log.i("e4", "Mobile Gyro Sample Count : "+String.valueOf(m_gyro_x.size()));

                init_db_rec = db.getCount();
                if (w_acc_x.size() != 0 && w_bvp.size()!=0 && m_acc_z.size()!=0 && m_gyro_x.size()!=0 && w_acc_x.size()<m_acc_x.size())
                {
                for (int i = 0; i < w_acc_x.size(); i++) {
                    Sensor s = new Sensor();
                    s.setUser_id(inp_username);
                    s.setUser_activity(inp_activity);
                    s.setW_acc_x(w_acc_x.get(i));
                    s.setW_acc_y(w_acc_y.get(i));
                    s.setW_acc_z(w_acc_z.get(i));
                    s.setW_bvp(w_bvp.get((int) (1.5*i)));
                    s.setW_timestamp(w_ts.get((int) (i)));
                    s.setM_acc_x(m_acc_x.get((int) (1.5*i)));
                    s.setM_acc_y(m_acc_y.get((int) (1.5*i)));
                    s.setM_acc_z(m_acc_z.get((int) (1.5*i)));
                    s.setM_gyro_x(m_gyro_x.get((int) (1.5*i)));
                    s.setM_gyro_y(m_gyro_y.get((int) (1.5*i)));
                    s.setM_gyro_z(m_gyro_z.get((int) (1.5*i)));
                    try {
                        db.addSensorData(s);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("e4","Issue in Samples collected . No entry in database");
                        Toast.makeText(MainActivity.this, "Issue in Storing Records.Check Logs", Toast.LENGTH_SHORT).show();
                    }
                }}
                else{
                    Log.i("e4","Issue in Samples collected . No entry in database");
                }

                Log.i("e4_db", "Number of Samples added in Database : " + String.valueOf(db.getCount() - init_db_rec));
                Log.i("e4_db", "Total Records " + db.getCount());
                Toast.makeText(MainActivity.this, "Number of Samples added in Database :" + String.valueOf(db.getCount() - init_db_rec), Toast.LENGTH_SHORT).show();
                w_acc_x.clear();
                w_acc_y.clear();
                w_acc_z.clear();
                w_bvp.clear();
                w_ts.clear();
                m_gyro_x.clear();
                m_gyro_y.clear();
                m_gyro_z.clear();
                m_acc_x.clear();
                m_acc_y.clear();
                m_acc_z.clear();
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, yay!
                    initEmpaticaDeviceManager();
                } else {
                    // Permission denied, boo!
                    final boolean needRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);
                    new AlertDialog.Builder(this)
                            .setTitle("Permission required")
                            .setMessage("Without this permission bluetooth low energy devices cannot be found, allow it in order to connect to the device.")
                            .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // try again
                                    if (needRationale) {
                                        // the "never ask again" flash is not set, try again with permission request
                                        initEmpaticaDeviceManager();
                                    } else {
                                        // the "never ask again" flag is set so the permission requests is disabled, try open app settings to enable the permission
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .setNegativeButton("Exit application", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // without permission exit is the only way
                                    finish();
                                }
                            })
                            .show();
                }
                break;
        }
    }

    private void initEmpaticaDeviceManager(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION);
        } else {

            if (TextUtils.isEmpty(EMPATICA_API_KEY)) {
                new AlertDialog.Builder(this)
                        .setTitle("Warning")
                        .setMessage("Please insert your API KEY")
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // without permission exit is the only way
                                finish();
                            }
                        })
                        .show();
                return;
            }
        // Create a new EmpaDeviceManager. MainActivity is both its data and status delegate.
        deviceManager = new EmpaDeviceManager(getApplicationContext(), this, this);

        // Initialize the Device Manager using your API key. You need to have Internet access at this point.
        deviceManager.authenticateWithAPIKey(EMPATICA_API_KEY);
    }
    }

    @Override
    public void didReceiveGSR(float gsr, double timestamp) {}

    @Override
    public void didReceiveIBI(float ibi, double timestamp) {}

    @Override
    public void didReceiveTemperature(float temp, double timestamp) {}

    @Override
    public void didReceiveBVP(float bvp, double timestamp) {
        if (isStart){
        w_bvp.add(Float.toString(bvp));}
    }

    @Override
    public void didReceiveAcceleration(int x, int y, int z, double timestamp){
        is_Acc_Start = true;
        if (isStart){
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        Log.i("e4_watch","Accelerometer: " + x +" , "+ y + "," + z +"," + ts);
//        accel.setText(Float.toString(x));
        w_acc_x.add(Float.toString(x));
        w_acc_y.add(Float.toString(y));
        w_acc_z.add(Float.toString(z));
        w_ts.add(ts);}
    }

    @Override
    public void didReceiveBatteryLevel(float battery, double timestamp) {}

    @Override
    public void didReceiveTag(double timestamp) {}

    @Override
    public void didUpdateStatus(EmpaStatus status){
        // Update the UI

        // The device manager is ready for use
        if (status == EmpaStatus.READY) {
            // Start scanning
            Log.i("e4","Device manager is ready for use");
            deviceManager.startScanning();
            // The device manager has established a connection
        } else if (status == EmpaStatus.CONNECTED) {
            Log.i("e4","Device is connected");
            // The device manager disconnected from a device
        } else if (status == EmpaStatus.DISCONNECTED) {
            Log.i("e4","Device is disconnected");
        }

    }

    @Override
    public void didEstablishConnection() {
        Log.i("e4","Connection established");
    }

    @Override
    public void didUpdateSensorStatus(@EmpaSensorStatus int status, EmpaSensorType type) {}

    @Override
    public void didDiscoverDevice(EmpaticaDevice bluetoothDevice, String deviceName, int rssi, boolean allowed) {
        Log.i("e4", "didDiscoverDevice" + deviceName + "allowed: " + allowed);

        if (allowed) {
            // Stop scanning. The first allowed device will do.
            deviceManager.stopScanning();
            try {
                // Connect to the device
                deviceManager.connectDevice(bluetoothDevice);
                Log.i("e4", "To: " + deviceName);
            } catch (ConnectionNotAllowedException e) {
                // This should happen only if you try to connect when allowed == false.
                Toast.makeText(MainActivity.this, "Sorry, you can't connect to this device", Toast.LENGTH_SHORT).show();
                Log.e("e4", "didDiscoverDevice" + deviceName + "allowed: " + allowed + " - ConnectionNotAllowedException", e);
            }
        }
    }

    @Override
    public void didFailedScanning(int errorCode){
         /*
         A system error occurred while scanning.
         @see https://developer.android.com/reference/android/bluetooth/le/ScanCallback
        */
        switch (errorCode) {
            case ScanCallback.SCAN_FAILED_ALREADY_STARTED:
                Log.e("e4","Scan failed: a BLE scan with the same settings is already started by the app");
                break;
            case ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                Log.e("e4","Scan failed: app cannot be registered");
                break;
            case ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED:
                Log.e("e4","Scan failed: power optimized scan feature is not supported");
                break;
            case ScanCallback.SCAN_FAILED_INTERNAL_ERROR:
                Log.e("e4","Scan failed: internal error");
                break;
            default:
                Log.e("e4","Scan failed with unknown error (errorCode=" + errorCode + ")");
                break;
        }
    }

    @Override
    public void didRequestEnableBluetooth() {
    }

    @Override
    public void bluetoothStateChanged() {
        // E4link detected a bluetooth adapter change
        // Check bluetooth adapter and update your UI accordingly.
        boolean isBluetoothOn = BluetoothAdapter.getDefaultAdapter().isEnabled();
        Log.i("e4", "Bluetooth State Changed: " + isBluetoothOn);
    }

    @Override
    public void didUpdateOnWristStatus(@EmpaSensorStatus final int status) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // The user chose not to enable Bluetooth
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            // You should deal with this
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (msensorGyro != null) {
            msensorManager.unregisterListener(MainActivity.this);
        }
        if (msensorAccelerometer != null) {
            msensorManager.unregisterListener(MainActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (deviceManager != null) {
            deviceManager.cleanUp();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (deviceManager != null) {
            deviceManager.stopScanning();
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == android.hardware.Sensor.TYPE_GYROSCOPE) {

            if (is_Acc_Start){
                Log.i("e4_mob_gyro", String.valueOf(event.values[0]));
                m_gyro_x.add(String.valueOf(event.values[0]));
                m_gyro_y.add(String.valueOf(event.values[1]));
                m_gyro_z.add(String.valueOf(event.values[2]));
            }
        }
        if (event.sensor.getType() == android.hardware.Sensor.TYPE_ACCELEROMETER){
            float[] gravity = event.values;
            if (is_Acc_Start){
                Log.i("e4_mob_accelerometer",String.valueOf(event.values[0]));
                m_acc_x.add(String.valueOf(event.values[0]));
                m_acc_y.add(String.valueOf(event.values[1]));
                m_acc_z.add(String.valueOf(event.values[2]));
            }
        }
    }
    @Override
    public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

    }


    };


    

