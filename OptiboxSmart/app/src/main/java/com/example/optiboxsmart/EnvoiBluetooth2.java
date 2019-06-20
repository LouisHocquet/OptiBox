package com.example.optiboxsmart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnvoiBluetooth2 extends AppCompatActivity implements View.OnClickListener {

    private Button btnConnexionBluetooth;
    private Button btnFindDevices;
    private Button btnDiscoverable;
    private TextView tvDiscoverable;
    private TextView tvResultBluetooth;
    private TextView tvFindDevices;
    private Map<Integer, Integer> mapCartons;
    private TextView tvMapCartons;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> pairedDevices;

    private BluetoothDevice bluetoothDevice;
    private String deviceName;
    private String deviceAddress;

    private Button btnRunServer;
    private Button btnStopServer;
    private TextView tvRunServer;
    private AcceptThread acceptThread;

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
        setContentView(R.layout.activity_envoi_bluetooth);
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnConnexionBluetooth = findViewById(R.id.btnConnexionBluetooth);
        tvResultBluetooth = findViewById(R.id.tvConnexionBluetooth);
        tvMapCartons = findViewById(R.id.tvMapCartons);
        btnFindDevices = findViewById(R.id.btnFindDevices);
        tvFindDevices = findViewById(R.id.tvFindDevices);
        btnDiscoverable = findViewById(R.id.btnDiscoverable);
        tvDiscoverable = findViewById(R.id.tvDiscoverable);

        btnRunServer = findViewById(R.id.btnRunServer);
        btnStopServer = findViewById(R.id.btnStopServer);
        tvRunServer = findViewById(R.id.tvRunServer);
        pairedDevices = new HashSet<>();

        // Mise en place d'un gestionnaire de clic
        btnConnexionBluetooth.setOnClickListener(this);
        btnFindDevices.setOnClickListener(this);

        // Récupération de mapCartons
        Bundle myBdl = this.getIntent().getExtras();
        mapCartons = (HashMap) myBdl.getSerializable("mapCartons");
        tvMapCartons.setText(mapCartons.toString());

        // #################### SETUP BLUETOOTH #################################

        // Bluetooth pris en charge ?
        tvResultBluetooth.setText("Connexion Bluetooth ...");
        if(isBluetoothOk(bluetoothAdapter)) tvResultBluetooth.setText("Bluetooth ok !");
        else tvResultBluetooth.setText("Bluetooth pas ok ...");




        // ################## FIND DEVICES #####################################

        // Retrouver les appareils déjà apairés
/*        pairedDevices = bluetoothAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
//            for (BluetoothDevice device : pairedDevices) {
//                String deviceName = device.getName();
//                String deviceHardwareAddress = device.getAddress(); // MAC address

            alerter("il y a des appareils apairés");
            } else {
            alerter(" 0 appareil apairé");
        }

        // Scanner les appareils à portée (Discovery)
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK){
            alerter("Bluetooth actif !");
        } else {
            alerter("Bluetooth inactif ...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnConnexionBluetooth:
                // Vérification que Bluetooth est activé, sinon on demande l'autorisation
                tvResultBluetooth.setText("Bluetooth actif ? ...");
                if(isBluetoothActive(bluetoothAdapter)) tvResultBluetooth.setText("Bluetooth actif !");
                else tvResultBluetooth.setText("Bluetooth pas actif ...");
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

            case R.id.btnRunServer:
                acceptThread = new AcceptThread();
                acceptThread.run();
                alerter("Server lancé");
                break;

            case R.id.btnStopServer:
                acceptThread.cancel();
                alerter("Server coupé");
                break;
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

    public boolean isBluetoothActive(BluetoothAdapter bluetoothAdapter){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1); //request code > 0

            // if success --> l'activité reçoit RESULT_OK (onActivityResult() )
            // else --> l'activité reçoit RESULT_CANCELED
            alerter("activation");
            return false;

        } else {
            alerter("déjà actif");
            return true;
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

    // ############## Connect as a server ###############################################
    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("lunettes", UUID.fromString("25155ded-c368-4623-bfe5-cf07fa75cbad"));
                alerter("mmServerSocket créé !");
            } catch (IOException e) {
                Log.e(CAT, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;

        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(CAT, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
//                    manageMyConnectedSocket(socket);
                    tvFindDevices.setText("connexion acceptée !");
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(CAT, "Could not close the connect socket", e);
            }
        }
    }

}
