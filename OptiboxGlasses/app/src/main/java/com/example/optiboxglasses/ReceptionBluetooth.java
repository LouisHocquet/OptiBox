package com.example.optiboxglasses;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ReceptionBluetooth extends AppCompatActivity implements View.OnClickListener{
    private Button btnConnexionBluetooth;
    private TextView tvResultBluetooth;
    private Map<Integer, Integer> mapCartons;
    private TextView tvMapCartons;
    private BluetoothAdapter bluetoothAdapter;
    private Button btnDiscoverable;
    private TextView tvDiscoverable;
    private Set<BluetoothDevice> pairedDevices;
    private String deviceName;
    private String deviceAddress;

    private Button btnRunClient;
    private Button btnStopClient;
    private TextView tvRunClient;
    private ConnectThread connectThread;
    private BluetoothDevice bluetoothDevice;
    private Button btnFindDevices;
    private TextView tvFindDevices;

    private String CAT = "Bluetooth";

    // Fonction alerter()
    private void alerter(String s){
        Toast toastAlert = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toastAlert.show();
        Log.i(CAT, s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception_bluetooth);
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnConnexionBluetooth = findViewById(R.id.btnConnexionBluetooth);
        tvResultBluetooth = findViewById(R.id.tvReceptionBluetooth);
        tvMapCartons = findViewById(R.id.tvMapCartons);
        btnDiscoverable = findViewById(R.id.btnDiscoverable);
        tvDiscoverable = findViewById(R.id.tvDiscoverable);
        btnFindDevices = findViewById(R.id.btnFindDevices);
        tvFindDevices = findViewById(R.id.tvFindDevices);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();

        btnRunClient = findViewById(R.id.btnRunClient);
        btnStopClient = findViewById(R.id.btnStopClient);
        tvRunClient = findViewById(R.id.tvRunClient);

        // Gestionnaire de clic
        btnDiscoverable.setOnClickListener(this);
        btnConnexionBluetooth.setOnClickListener(this);
        btnFindDevices.setOnClickListener(this);

        btnRunClient.setOnClickListener(this);
        btnStopClient.setOnClickListener(this);

        // #################### SETUP BLUETOOTH #################################
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Bluetooth pris en charge ?
        tvResultBluetooth.setText("Connexion Bluetooth ...");
        if(isBluetoothOk(bluetoothAdapter)) tvResultBluetooth.setText("Bluetooth ok !");
        else tvResultBluetooth.setText("Bluetooth pas ok ...");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CANCELED){
            alerter("Appareil non détectable");
        } else {
            alerter("Appareil détectable pendant " + requestCode + " s");
        }
    }

    /**
     *
     * @return true si l'appareil supporte Bluetooth
     */
    public boolean isBluetoothOk(BluetoothAdapter bluetoothAdapter){
        if (bluetoothAdapter == null){
            alerter("Bluetooth non supporté");
            return false;
        } else {
            alerter("Bluetooth supporté");
            return true;
        }
    }

    // Vérification que Bluetooth est activé, sinon on demande l'autorisation
    public void activationBluetooth(BluetoothAdapter bluetoothAdapter){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1); //request code > 0
            tvResultBluetooth.setText("Bluetooth activé");


            // if success --> l'activité reçoit RESULT_OK (onActivityResult() )
            // else --> l'activité reçoit RESULT_CANCELED

        } else {
            tvResultBluetooth.setText("Bluetooth déjà activé");
        }
    }

    // ############### Sérialisation/ Désérialisation ###################################
    public Map<Integer, Integer> deserialisation(String jsonMapCartons){
        Gson gson = new Gson();
        Map<Integer, Integer> mapCartons = gson.fromJson(jsonMapCartons, Map.class);
        return mapCartons;
    }


    public String serialisation(Map<Integer, Integer> mapCartons){
        Gson gson = new Gson();
        return gson.toJson(mapCartons);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnConnexionBluetooth:
                activationBluetooth(bluetoothAdapter);
                break;
            case R.id.btnDiscoverable:
                // l'appareil est discoverable pendant 300 secondes
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                alerter("l'appareil est détectable pendant 5 minutes");
                tvDiscoverable.setText("discoverable pendant 300 secondes");
                break;

            case R.id.btnFindDevices:
                // ################## FIND DEVICES #####################################
                // Retrouver les appareils déjà apairés
                pairedDevices = bluetoothAdapter.getBondedDevices();
                tvFindDevices.setText(pairedDevices.toString());

                if (pairedDevices.size() > 0) {
                    // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address

                    alerter("il y a des appareils apairés");
                } else {
                    alerter(" 0 appareil apairé");

                    // Scanner les appareils à portée (Discovery)
                    // Register for broadcasts when a device is discovered.
                    IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(receiver, filter);
                }
                break;

            case R.id.btnRunClient:
                if (bluetoothDevice != null) {
                    connectThread = new ConnectThread(bluetoothDevice);
                    connectThread.run();
                    break;
                }

        }
    }

    // ######################## BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceName = device.getName();
                deviceAddress = device.getAddress(); // MAC address
                pairedDevices.add(device);
                bluetoothDevice = device;
                alerter("device name = " + deviceName + ", deviceAddress = " + deviceAddress);
            }
        }
    };

    // ############## Connect as a client ################################################
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("1da2c6c6-f44c-4cfb-96cd-f6a7ea7db0a1"));
            } catch (IOException e) {
                Log.e(CAT, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                tvRunClient.setText("succès");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(CAT, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
//            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(CAT, "Could not close the client socket", e);
            }
        }
    }

}
