package de.androidcrypto.bluetooth02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 201;
    TextView textView;
    Button btn01SetBluetoothAdapter, btn02EnableBluetooth, btn03QueryPairedDevices;

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv);
        btn01SetBluetoothAdapter = findViewById(R.id.btn01SetBluetoothAdapter);
        btn02EnableBluetooth = findViewById(R.id.btn02EnableBluetooth);
        btn03QueryPairedDevices = findViewById(R.id.btn03QueryPairedDevices);

        btn01SetBluetoothAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // minimum Android 23
                bluetoothManager = getSystemService(BluetoothManager.class);
                bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter == null) {
                    // Device doesn't support Bluetooth
                    textView.setText("Device does not support Bluetooth");
                } else {
                    textView.setText("Device does support Bluetooth");
                }
            }
        });

        btn02EnableBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!bluetoothAdapter.isEnabled()) {
                    textView.setText("need to enable Bluetooth");
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    textView.setText("Bluetooth is enabled");
                }
            }
        });

        btn03QueryPairedDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
                    int deviceNr = 1;
                    String output = "paired devices:\n";
                    for (BluetoothDevice device : pairedDevices) {
                        if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        String deviceName = device.getName();
                        String deviceHardwareAddress = device.getAddress(); // MAC address
                        output += "deviceName: " + deviceName + " MAC: " + deviceHardwareAddress + "\n";
                    }
                    textView.setText(output);
                } else {
                    textView.setText("no paired devices");
                }
            }
        });

    }
}