package com.example.optiboxsmart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EnvoiBluetooth extends AppCompatActivity implements View.OnClickListener {

    private Button btnConnexionBluetooth;
    private TextView tvResultBluetooth;
    private Map<Integer, Integer> mapCartons;
    private TextView tvMapCartons;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice> pairedDevices;
    private String deviceName;
    private String deviceAddress;
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
        tvResultBluetooth = findViewById(R.id.tvResultBluetooth);
        tvMapCartons = findViewById(R.id.tvMapCartons);

        // Mise en place d'un gestionnaire de clic
        btnConnexionBluetooth.setOnClickListener(this);

        // Récupération de mapCartons
        Bundle myBdl = this.getIntent().getExtras();
        mapCartons = (HashMap) myBdl.getSerializable("mapCartons");
        tvMapCartons.setText(mapCartons.toString());

        // #################### SETUP BLUETOOTH #################################

        // Bluetooth pris en charge ?
        tvResultBluetooth.setText("Connexion Bluetooth ...");
        if(isBluetoothOk(bluetoothAdapter)) tvResultBluetooth.setText("Bluetooth ok !");
        else tvResultBluetooth.setText("Bluetooth pas ok ...");

        // Vérification que Bluetooth est activé, sinon on demande l'autorisation
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1); //request code > 0

            // if success --> l'activité reçoit RESULT_OK (onActivityResult() )
            // else --> l'activité reçoit RESULT_CANCELED

        }

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
        }

        // Scanner les appareils à portée (Discovery)
        // Register for broadcasts when a device is discovered.
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);



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
                tvResultBluetooth.setText("Connexion Bluetooth ...");
                if(isBluetoothOk(bluetoothAdapter)) tvResultBluetooth.setText("Bluetooth ok !");
                else tvResultBluetooth.setText("Bluetooth pas ok ...");
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
}
