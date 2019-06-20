package com.example.optiboxsmart;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EnvoiBluetooth extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "Bluetooth";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private Button btnConnexionBluetooth;
    private  Button btnRunServer;
    private TextView tvRunServer;
    private TextView tvConnexionBluetooth;
    private Set<BluetoothDevice> pairedDevices;



    // Fonction alerter()
    private void alerter(String s){
        Toast toastAlert = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toastAlert.show();
        Log.i(TAG, s);
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
        tvConnexionBluetooth = findViewById(R.id.tvConnexionBluetooth);

        btnRunServer = findViewById(R.id.btnRunServer);
        tvRunServer = findViewById(R.id.tvRunServer);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();
        tvConnexionBluetooth.setText(pairedDevices.toString());

        btnConnexionBluetooth.setOnClickListener(this);
        btnRunServer.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK){
            tvConnexionBluetooth.setText("Bluetooth actif");
            alerter("Bluetooth actif !");
        } else {
            alerter("Bluetooth inactif ...");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnConnexionBluetooth:
                if (isBluetoothOk(bluetoothAdapter)){
//                    tvConnexionBluetooth.setText("Bluetooth ok");
                    if(activerBluetooth(bluetoothAdapter)) tvConnexionBluetooth.setText("" +
                            "Bluetooth actif");
                }
                else tvConnexionBluetooth.setText("Bluetooth kaput");
                break;

            case R.id.btnRunServer:
                AcceptThread acceptThread = new AcceptThread();
                acceptThread.run();
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

    public boolean activerBluetooth(BluetoothAdapter bluetoothAdapter){
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
                tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Optibox", UUID.fromString("1da2c6c6-f44c-4cfb-96cd-f6a7ea7db0a1"));
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                    alerter("succès");
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    tvConnexionBluetooth.setText("Connexion acceptée");
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
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }


}
