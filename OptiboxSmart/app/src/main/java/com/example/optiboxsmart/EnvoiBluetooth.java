package com.example.optiboxsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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

import com.example.optiboxsmart.clp_solver.Box;
import com.example.optiboxsmart.clp_solver.Loader;
import com.google.gson.Gson;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.example.optiboxsmart.MyBluetoothService.MessageConstants.MESSAGE_READ;

public class EnvoiBluetooth extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "Bluetooth";

    private ArrayList<Integer> listeCartons;
    private TextView tvListeCartons;
    private List<double[]> listeDouble = new ArrayList<>();

    // Déclaration BluetoothAdapter et BluetoothDevice
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice bluetoothDevice;


    private Button btnRecupData;
    private TextView tvRecupData;
    private Set<BluetoothDevice> pairedDevices;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    alerter(readMessage);
                    break;

            }
        }
    };



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
        btnRecupData = findViewById(R.id.btnEnvoiData);
        tvRecupData = findViewById(R.id.tvRecupData);
        tvListeCartons = findViewById(R.id.tvListeCartons);

        // Instanciation du BluetoothAdapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = bluetoothAdapter.getBondedDevices();

        btnRecupData.setOnClickListener(this);


        double[] testDouble = new double[]{0.,1.,2.,3.,4.,5.};
        listeDouble.add(testDouble);

        Bundle myBundle = this.getIntent().getExtras();
        listeCartons = (ArrayList<Integer>) myBundle.get("listeCartons");
        tvListeCartons.setText(listeCartons.toString());
    }

/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK){
            tvConnexionBluetooth.setText("Bluetooth actif");
            alerter("Bluetooth actif !");
        } else {
            alerter("Bluetooth inactif ...");
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.btnEnvoiData:
                AcceptThread acceptThread = new AcceptThread();
                acceptThread.start();
        }
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
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(EnvoiBluetooth.this);
            SharedPreferences.Editor editor = settings.edit();

            BluetoothSocket socket = null;
            String mac = settings.getString("MAC", null);
            // Keep listening until exception occurs or a socket is returned.
            while (socket == null) {
                try {
                    socket = mmServerSocket.accept();
//                    alerter("succès");
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    EnvoiBluetooth.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvRecupData.setText("Connexion acceptée");
                        }
                    });
                    BluetoothCom bluetoothCom = new BluetoothCom(handler,socket);
                    bluetoothCom.setCartons(getCardboardsCoordsList());
                    try {
                        bluetoothCom.send();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

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

    private List<int[]> getCardboardsCoordsList() {
        /* Testing CLP */

        Object[] boxNumber = listeCartons.toArray();

        HashMap<Box, Integer> cargo = new HashMap<>();
        for (int n = 0; n < 6; n++){
            cargo.put(MainActivity.boxTypes[n], (int) boxNumber[n]);
        }

        Loader clp = new Loader(MainActivity.containerDim, cargo);
        List<int[]> boxes = clp.solveToArray();
        return boxes;
    };

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
