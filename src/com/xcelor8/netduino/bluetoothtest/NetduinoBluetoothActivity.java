package com.xcelor8.netduino.bluetoothtest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class NetduinoBluetoothActivity extends Activity {
	private static final String TAG = "THINBTCLIENT";
	private static final boolean D = false;
	//static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outStream = null;
	
	private static String address = "00:06:66:45:B6:96";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        if (D)
            Log.e(TAG, "+++ ON CREATE +++");

	    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    if (mBluetoothAdapter == null) {
	            Toast.makeText(this,
	                    "Bluetooth is not available.",
	                    Toast.LENGTH_LONG).show();
	            finish();
	            return;
	    }
	
	    if (!mBluetoothAdapter.isEnabled()) {
	            Toast.makeText(this,
	                    "Please enable your BT and re-run this program.",
	                    Toast.LENGTH_LONG).show();
	            finish();
	            return;
	    }
	
	    if (D)
	            Log.e(TAG, "+++ DONE IN ON CREATE, GOT LOCAL BT ADAPTER +++");
	    
	    Button mLedOn = (Button)findViewById(R.id.uxLedOn);
	    Button mLedOff = (Button)findViewById(R.id.uxLedOff);
	    
	    mLedOn.setOnClickListener(mLedOnListener);
	    mLedOff.setOnClickListener(mLedOffListener);
	}
    
    @Override
    public void onStart() {
            super.onStart();
            if (D)
                    Log.e(TAG, "++ ON START ++");
    }
    
    @Override
    public void onResume() {
            super.onResume();

            if (D) {
                    Log.e(TAG, "+ ON RESUME +");
                    Log.e(TAG, "+ ABOUT TO ATTEMPT CLIENT CONNECT +");
            }

            // When this returns, it will 'know' about the server,
            // via it's MAC address.
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);

            // We need two things before we can successfully connect
            // (authentication issues aside): a MAC address, which we
            // already have, and an RFCOMM channel.
            // Because RFCOMM channels (aka ports) are limited in
            // number, Android doesn't allow you to use them directly;
            // instead you request a RFCOMM mapping based on a service
            // ID. In our case, we will use the well-known SPP Service
            // ID. This ID is in UUID (GUID to you Microsofties)
            // format. Given the UUID, Android will handle the
            // mapping for you. Generally, this will return RFCOMM 1,
            // but not always; it depends what other BlueTooth services
            // are in use on your Android device.
            try {
                    btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Socket creation failed.", e);
            }

            // Discovery may be going on, e.g., if you're running a
            // 'scan for devices' search from your handset's Bluetooth
            // settings, so we call cancelDiscovery(). It doesn't hurt
            // to call it, but it might hurt not to... discovery is a
            // heavyweight process; you don't want it in progress when
            // a connection attempt is made.
            mBluetoothAdapter.cancelDiscovery();

            // Blocking connect, for a simple client nothing else can
            // happen until a successful connection is made, so we
            // don't care if it blocks.
            try {
                    btSocket.connect();
                    Log.e(TAG, "ON RESUME: BT connection established, data transfer link open.");
            } catch (IOException e) {
                    try {
                            btSocket.close();
                    } catch (IOException e2) {
                            Log.e(TAG,
                                    "ON RESUME: Unable to close socket during connection failure", e2);
                    }
            }

            // Create a data stream so we can talk to server.
            if (D)
                    Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");

            try {
                    outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Output stream creation failed.", e);
            }

            String message = "Hello message from client to server.\r\n";
            byte[] msgBuffer = message.getBytes();
            try {
                    outStream.write(msgBuffer);
            } catch (IOException e) {
                    Log.e(TAG, "ON RESUME: Exception during write.", e);
            }
    }
    
    @Override
    public void onPause() {
            super.onPause();

            if (D)
                    Log.e(TAG, "- ON PAUSE -");

            if (outStream != null) {
                    try {
                            outStream.flush();
                    } catch (IOException e) {
                            Log.e(TAG, "ON PAUSE: Couldn't flush output stream.", e);
                    }
            }

            try     {
                    btSocket.close();
            } catch (IOException e2) {
                    Log.e(TAG, "ON PAUSE: Unable to close socket.", e2);
            }
    }
    
    @Override
    public void onStop() {
            super.onStop();
            if (D)
                    Log.e(TAG, "-- ON STOP --");
    }
    
    @Override
    public void onDestroy() {
            super.onDestroy();
            if (D)
                    Log.e(TAG, "--- ON DESTROY ---");
    }
    
    private OnClickListener mLedOnListener = new OnClickListener()
    {
		public void onClick(View v) {
			// Create a data stream so we can talk to server.
            if (D)
                    Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");

            try {
                    outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                    Log.e(TAG, "LED ON LISTENER: Output stream creation failed.", e);
            }

            String message = "GPIO13_on\r\n";
            byte[] msgBuffer = message.getBytes();
            try {
                    outStream.write(msgBuffer);
            } catch (IOException e) {
                    Log.e(TAG, "LED ON LISTENER: Exception during write.", e);
            }
		}
    };
    
    private OnClickListener mLedOffListener = new OnClickListener()
    {
		public void onClick(View v) {
			// Create a data stream so we can talk to server.
            if (D)
                    Log.e(TAG, "+ ABOUT TO SAY SOMETHING TO SERVER +");

            try {
                    outStream = btSocket.getOutputStream();
            } catch (IOException e) {
                    Log.e(TAG, "LED ON LISTENER: Output stream creation failed.", e);
            }

            String message = "GPIO13_off\r\n";
            byte[] msgBuffer = message.getBytes();
            try {
                    outStream.write(msgBuffer);
            } catch (IOException e) {
                    Log.e(TAG, "LED ON LISTENER: Exception during write.", e);
            }
		}
    };
}