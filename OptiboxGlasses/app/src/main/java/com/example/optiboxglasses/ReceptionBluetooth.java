package com.example.optiboxglasses;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.example.optiboxglasses.MyBluetoothService.MessageConstants.MESSAGE_READ;

public class ReceptionBluetooth extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Integer> listeCartons;
    private TextView tvCartons;


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothCom bluetoothCom;

    private ConnectThread connectThread;

    private Button btnRecupData;
    private TextView tvRecupData;

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.v("BLUETOOTH_COM",readMessage);
                    if(bluetoothCom!=null)
                        bluetoothCom.setDataReceived(true);
                    break;

            }
        }
    };

    private List<double[]> listeDouble = new ArrayList<>();

//    private SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
//    private SharedPreferences.Editor editor = settings.edit();

    private String CAT = "Bluetooth";
    private String phoneMAC;
    private SharedPreferences.Editor editor;

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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ReceptionBluetooth.this);
        editor = sharedPref.edit();
        phoneMAC = sharedPref.getString("phone_MAC_adress","none");

    }

    @Override
    protected void onStart() {
        super.onStart();
        tvCartons = findViewById(R.id.tvListeCartons);
        btnRecupData = findViewById(R.id.btnRecupData);
        tvRecupData = findViewById(R.id.tvRecupData);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();


        // Gestionnaire de clic
        btnRecupData.setOnClickListener(this);

        tvRecupData.setText(pairedDevices.toString());
    }



    @Override
    public void onClick(View v) {
        switch(v.getId()){


            case R.id.btnRecupData:
                // ################## FIND DEVICES #####################################
                // Retrouver les appareils déjà apairés
                pairedDevices = bluetoothAdapter.getBondedDevices();
                tvRecupData.setText(pairedDevices.toString());

                if (pairedDevices.size() > 0) {

                    // There are paired devices. Get the name and address of each paired device.
                    boolean preferedServerFound =false;
                    for (BluetoothDevice device : pairedDevices) {
                       String deviceName = device.getName();
                       String deviceHardwareAddress = device.getAddress(); // MAC address
                       if(phoneMAC.equals(deviceHardwareAddress)){
                           preferedServerFound = true;
                           alerter("Hôte préféré trouvé, connexion...");
                           (new ConnectThread(device)).start();
                           break;
                       }
                    }
                    if(!preferedServerFound){
                        for (BluetoothDevice device : pairedDevices)
                            (new ConnectThread(device)).start();
                    }
                }
                break;


        }
    }

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
                tmp = mmDevice.createRfcommSocketToServiceRecord(UUID.fromString("1da2c6c6-f44c-4cfb-96cd-f6a7ea7db0a1"));
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
                ReceptionBluetooth.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvRecupData.setText("succès");
                    }
                });
                bluetoothDevice=mmDevice;
                phoneMAC = bluetoothDevice.getAddress();
                editor.putString("phone_MAC_adress",phoneMAC);
                editor.commit();

                bluetoothCom = new BluetoothCom(handler,mmSocket);
                bluetoothCom.run();
                while(bluetoothCom.getResult()==""){
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                final String result = bluetoothCom.getResult();
                ReceptionBluetooth.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        alerter(result);
                        Intent intentOpenGL = new Intent(ReceptionBluetooth.this,OpenGLES20Activity.class);
                        intentOpenGL.putExtra("jsonCardboards",result);
                        startActivity(intentOpenGL);
                    }
                });


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
