package de.androidcrypto.bluetooth02;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {

    // example from https://stackoverflow.com/questions/45140098/how-to-send-receive-messages-via-bluetooth-android-studio

    /**
     * This is a running example of a minimal bluetooth connection with connecting of 2 devices
     * and sending text messages
     * Source: https://stackoverflow.com/questions/45140098/how-to-send-receive-messages-via-bluetooth-android-studio
     *
     * Original note:
     * NOTE: Make sure both the devices are paired and the device that you are trying to connect to
     * is at the top of the list(or just remove all the paired devices from both the devices and
     * only pair the devices that you want to connect). Also, you must start the AcceptThread before
     * ConnectThread
     *
     * My note: I'm using a special PairDevice method for connecting to a known device with it's known
     * MAC address
     */

    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private static final int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothDevice mmDevice;
    private UUID deviceUUID;
    ConnectedThread mConnectedThread;
    private Handler handler;

    InputStream inStream;
    OutputStream outputStream;

    String TAG = "MainActivity";
    EditText send_data;
    TextView view_data;
    StringBuilder messages;

    Button pairDevice, pairDeviceGalaxyS5, startServer, send;
    EditText textToSend;
    TextView textView;

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        pairDevice = findViewById(R.id.btnPairDevice);
        pairDeviceGalaxyS5 = findViewById(R.id.btnPairDeviceGalaxyS5);
        startServer = findViewById(R.id.btnStartServer);
        textToSend = findViewById(R.id.etSend);
        textView = findViewById(R.id.textView);
        send = findViewById(R.id.btnSend);

        send_data = (EditText) findViewById(R.id.etSend);
        view_data = (TextView) findViewById(R.id.textView);

        mContext = getApplicationContext();

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        pairDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pairDevice();
            }

        });

        pairDeviceGalaxyS5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pairDeviceGalaxyS5();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage(view);
            }
        });

        startServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start_Server(view);
            }
        });
    }

    /**
     * This method uses the known MAC address of a specific device to connect with
     */
    private void pairDeviceGalaxyS5() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        /*
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Log.e("MAinActivity", "" + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            Object[] devices = pairedDevices.toArray();
            BluetoothDevice device = (BluetoothDevice) devices[0];
            //ParcelUuid[] uuid = device.getUuids();
            Log.e("MAinActivity", "" + device);
            //Log.e("MAinActivity", "" + uuid)
            ConnectThread connect = new ConnectThread(device, MY_UUID_INSECURE);
            connect.start();
        */
        String macGalaxyS5 = "90:06:28:A6:0C:5C";
        //System.out.println("7 ConnectToGalaxyS4, MAC: " + macGalaxyS4);
        System.out.println("7 ConnectToGalaxyS5, MAC: " + macGalaxyS5);
        // Get the BluetoothDevice object
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macGalaxyS5);
        Log.e("MainActivity2", "device: " + device);
        ConnectThread connect = new ConnectThread(device, MY_UUID_INSECURE);
        connect.start();
    }

    /**
     * Important: both devices need to be coupled before AND both device are No 1 on both devices
     */

    public void pairDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        Log.e("MAinActivity", "" + pairedDevices.size());
        if (pairedDevices.size() > 0) {
            Object[] devices = pairedDevices.toArray();
            BluetoothDevice device = (BluetoothDevice) devices[0];
            //ParcelUuid[] uuid = device.getUuids();
            Log.e("MAinActivity", "" + device);
            //Log.e("MAinActivity", "" + uuid)
            ConnectThread connect = new ConnectThread(device, MY_UUID_INSECURE);
            connect.start();

        }
    }

    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            deviceUUID = uuid;
        }

        @SuppressLint("MissingPermission")
        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create InsecureRfcommSocket using UUID: "
                        +MY_UUID_INSECURE );
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;

            // Make a connection to the BluetoothSocket

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();

            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID_INSECURE );
            }

            //will talk about this in the 3rd video
            connected(mmSocket);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    private void connected(BluetoothSocket mmSocket) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;



            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                // Read from the InputStream
                try {
                    bytes = mmInStream.read(buffer);
                    final String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            view_data.setText(incomingMessage);
                        }
                    });


                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                    break;
                }
            }
        }


        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void SendMessage(View v) {
        byte[] bytes = send_data.getText().toString().getBytes(Charset.defaultCharset());
        mConnectedThread.write(bytes);
    }

    public void Start_Server(View view) {
        AcceptThread accept = new AcceptThread();
        accept.start();
    }

    private class AcceptThread extends Thread {

        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        @SuppressLint("MissingPermission")
        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                //tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("appname", MY_UUID_INSECURE);
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("appname", MY_UUID_INSECURE);

                Log.d(TAG, "AcceptThread: Setting up Server using: " + MY_UUID_INSECURE);
            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            mmServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread Running.");

            BluetoothSocket socket = null;

            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                Log.d(TAG, "run: RFCOM server socket start.....");

                socket = mmServerSocket.accept();

                Log.d(TAG, "run: RFCOM server socket accepted connection.");

            } catch (IOException e) {
                Log.e(TAG, "AcceptThread: IOException: " + e.getMessage());
            }

            //talk about this is in the 3rd
            if (socket != null) {
                connected(socket);
            }

            Log.i(TAG, "END mAcceptThread ");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Canceling AcceptThread.");
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: Close of AcceptThread ServerSocket failed. " + e.getMessage());
            }
        }
    }

}