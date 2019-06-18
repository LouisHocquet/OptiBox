package com.example.optiboxglasses;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Map;

public class ReceptionBluetooth extends AppCompatActivity implements View.OnClickListener{
    private Button btnConnexionBluetooth;
    private TextView tvResultBluetooth;
    private Map<Integer, Integer> mapCartons;
    private TextView tvMapCartons;
    private BluetoothAdapter bluetoothAdapter;
    private Button btnDiscoverable;

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

        // Gestionnaire de clic
        btnDiscoverable.setOnClickListener(this);
        btnConnexionBluetooth.setOnClickListener(this);

        // #################### SETUP BLUETOOTH #################################
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Bluetooth pris en charge ?
        tvResultBluetooth.setText("Connexion Bluetooth ...");
        if(isBluetoothOk(bluetoothAdapter)) tvResultBluetooth.setText("Bluetooth ok !");
        else tvResultBluetooth.setText("Bluetooth pas ok ...");

        // Vérification que Bluetooth est activé, sinon on demande l'autorisation
        activationBluetooth(bluetoothAdapter);
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

    public void activationBluetooth(BluetoothAdapter bluetoothAdapter){
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1); //request code > 0

            // if success --> l'activité reçoit RESULT_OK (onActivityResult() )
            // else --> l'activité reçoit RESULT_CANCELED

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
                tvResultBluetooth.setText("Connexion Bluetooth ...");
                if(isBluetoothOk(bluetoothAdapter)) tvResultBluetooth.setText("Bluetooth ok !");
                else tvResultBluetooth.setText("Bluetooth pas ok ...");
                break;
            case R.id.btnDiscoverable:
                // l'appareil est discoverable pendant 300 secondes
                Intent discoverableIntent =
                        new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                startActivity(discoverableIntent);
                alerter("l'appareil est détectable pendant 5 minutes");

        }
    }
}
