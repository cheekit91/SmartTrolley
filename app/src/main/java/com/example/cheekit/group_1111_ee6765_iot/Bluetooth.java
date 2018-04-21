package com.example.cheekit.group_1111_ee6765_iot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static com.google.android.gms.internal.zzagz.runOnUiThread;

/**
 * Created by cheekit on 11/16/2017.
 */

public class Bluetooth extends Fragment{

    EditText bluetoothSend;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    private static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String TAG = "BluetoothDemo";

    private OutputStream mmOutStream;
    private InputStream mmInStream;

    private BluetoothSocket mmSocket;
    private byte[] mmBuffer; // mmBuffer store for the stream
    private TextView textView;

    private Handler mHandler; // handler that gets info from Bluetooth service

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view=inflater.inflate(R.layout.bluetooth, container,false);
        bluetoothSend = (EditText) view.findViewById(R.id.bluetooth_word);

        textView = (TextView) view.findViewById(R.id.textView2);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmSocket = null;

        on();
        connector();
        th.start();

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Bluetooth");
    }


    // This is to turn on the bluetooth adapter if it is not already on
    public void on(){
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getActivity().getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }


    // Call this to turn off the bluetooth adapter (not used)
    public void off(View v){
        bluetoothAdapter.disable();
        Toast.makeText(getActivity().getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }



    // If connection is not established on app startup (onCreate) try again with this method
    public void connect(View v){


        try{
            String name = "CONNECTED";
            byte[] bytes = name.getBytes();
            mmOutStream.write(bytes);
        }catch (IOException e){
            Toast.makeText(getActivity().getApplicationContext(), "Connecting..." ,Toast.LENGTH_LONG).show();
            connector();


        }
    }


    public void connector(){

        OutputStream tmpOut = null;
        InputStream tmpIn = null;

        // Get list of paired devices

        BluetoothSocket tmp = null;

        Toast.makeText(getActivity().getApplicationContext(), "Connecting", Toast.LENGTH_LONG).show();
        String dname;


        pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice device = null;
        if(pairedDevices.size() >0) {
            for (BluetoothDevice bt : pairedDevices) {
                Log.d("TAG", bt.getName());
                dname = bt.getName();
                if (dname.equals("HC-05")) {
                    device = bt;
                    Log.d("TAG", "HC-05 PARED!!!");
                    Toast.makeText(getActivity().getApplicationContext(), device.getName(), Toast.LENGTH_LONG).show();


                } else {
                    Log.d("TAG", "Not HC-05");
                }

            }

            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                Log.d("TAG", "Socket's listen() method failed", e);
                Toast.makeText(getActivity().getApplicationContext(), "Error 1" ,Toast.LENGTH_LONG).show();
            }
            mmSocket = tmp;


            bluetoothAdapter.cancelDiscovery();



            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();


                Log.d("TAG", "Socket connected!!!!!");
                Toast.makeText(getActivity().getApplicationContext(), "Connected" ,Toast.LENGTH_LONG).show();
            } catch (IOException connectException) {}



            try {
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }


            try {

                tmpOut = mmSocket.getOutputStream();


            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
                Toast.makeText(getActivity().getApplicationContext(), "Error 2" ,Toast.LENGTH_LONG).show();
            }

            mmOutStream = tmpOut;
            mmInStream = tmpIn;



        }else{
            Log.d("TAG", "No devices");
            Toast.makeText(getActivity().getApplicationContext(), "HC-05 is not pared", Toast.LENGTH_LONG).show();
        }




    }


    // thread to listen to the input data from HC05 (not perfect)
    Thread th = new Thread(new Runnable() {
        public void run() {


            mmBuffer = new byte[4096];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    if(mmInStream.available()>2) {
                        Log.d("TAG","mmInStream.available()>2");

                        // Read from the InputStream.
                        numBytes = mmInStream.read(mmBuffer);



                        final String readMessage = new String(mmBuffer, 0, numBytes);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(readMessage);
                                Toast.makeText(getActivity().getApplicationContext(), readMessage, Toast.LENGTH_LONG).show();
                            }
                        });



                        Log.d("TAG", readMessage);
                    }else{
                        SystemClock.sleep(100);
                        Log.d("TAG", "No Data");
                    }





                } catch (IOException e) {
                    Log.d("TAG", "Input stream was disconnected", e);
                    break;
                }
            }


        }
    });

}
