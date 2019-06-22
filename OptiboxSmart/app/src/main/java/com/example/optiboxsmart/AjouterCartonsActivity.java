package com.example.optiboxsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjouterCartonsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AjouterCartonsActivity";

    private ArrayList<Integer> listeCartons = new ArrayList<>(Arrays.asList(0,0,0,0,0,0));

    static private final String sepQR = "CartonType="; // Ex de string in the qr code : 'CartonType=2'

    private Map<Integer, Integer> mapCartons;
    private TextView tvNbType1;
    private TextView tvNbType2;
    private TextView tvNbType3;
    private TextView tvNbType4;
    private TextView tvNbType5;
    private TextView tvNbType6;
    private Button btnAjoutType1;
    private Button btnAjoutType2;
    private Button btnAjoutType3;
    private Button btnAjoutType4;
    private Button btnAjoutType5;
    private Button btnAjoutType6;
    private Button btnRetraitType1;
    private Button btnRetraitType2;
    private Button btnRetraitType3;
    private Button btnRetraitType4;
    private Button btnRetraitType5;
    private Button btnRetraitType6;
    private Button btnAjoutQrCode;
    private List<TextView> listNbCartons;
    private List<Button> listBtnAjout;
    private List<Button> listBtnRetrait;

    private Button btnValiderCartons;

    private static String CAT = "PMR";
    // Fonction alerter()
    private void alerter(String s){
        Toast toastAlert = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toastAlert.show();
        Log.i(CAT, s);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_cartons);
        mapCartons = new HashMap<>(); // key : id carton, value : nb cartons
    }

    @Override
    protected void onStart() {
        super.onStart();
        btnAjoutType1 = findViewById(R.id.btnAjoutType1);
        btnAjoutType2 = findViewById(R.id.btnAjoutType2);
        btnAjoutType3 = findViewById(R.id.btnAjoutType3);
        btnAjoutType4 = findViewById(R.id.btnAjoutType4);
        btnAjoutType5 = findViewById(R.id.btnAjoutType5);
        btnAjoutType6 = findViewById(R.id.btnAjoutType6);
        btnRetraitType1 = findViewById(R.id.btnRetraitType1);
        btnRetraitType2 = findViewById(R.id.btnRetraitType2);
        btnRetraitType3 = findViewById(R.id.btnRetraitType3);
        btnRetraitType4 = findViewById(R.id.btnRetraitType4);
        btnRetraitType5 = findViewById(R.id.btnRetraitType5);
        btnRetraitType6 = findViewById(R.id.btnRetraitType6);
        btnAjoutQrCode = findViewById(R.id.btnAjoutQrCode);
        tvNbType1 = findViewById(R.id.tvType1);
        tvNbType2 = findViewById(R.id.tvType2);
        tvNbType3 = findViewById(R.id.tvType3);
        tvNbType4 = findViewById(R.id.tvType4);
        tvNbType5 = findViewById(R.id.tvType5);
        tvNbType6 = findViewById(R.id.tvType6);

        btnValiderCartons = findViewById(R.id.btnValiderCartons);

        listNbCartons = new ArrayList<>(Arrays.asList(tvNbType1, tvNbType2, tvNbType3,
                tvNbType4, tvNbType5, tvNbType6));
        listBtnAjout =  new ArrayList<>(Arrays.asList(btnAjoutType1, btnAjoutType2, btnAjoutType3,
                btnAjoutType4, btnAjoutType5, btnAjoutType6));
        listBtnRetrait = new ArrayList<>(Arrays.asList(btnRetraitType1, btnRetraitType2, btnRetraitType3,
                btnRetraitType4, btnRetraitType5, btnRetraitType6));

        // ########### Mise en place d'un gestionnaire de clics ############
        for (int i = 0; i < 6; i++) {
            listBtnRetrait.get(i).setOnClickListener(this);
            listBtnAjout.get(i).setOnClickListener(this);
        }
        btnValiderCartons.setOnClickListener(this);
        btnAjoutQrCode.setOnClickListener(this);
    }

    // ########### Fonctions gestion des cartons #########

    /**
     *
     * @param typeCarton
     * @return true si le carton a été ajouté
     */
    public boolean ajoutCarton(int typeCarton){
        boolean succes = false;
        if (typeCarton > 6 || typeCarton < 1) return false;
        else{
            int nbCartons = listeCartons.get(typeCarton - 1);
            nbCartons++;

            listeCartons.set(typeCarton - 1, nbCartons);
            alerter(String.valueOf(listeCartons.get(typeCarton - 1)));
            listNbCartons.get(typeCarton-1).setText(String.valueOf(nbCartons));
            return true;
        }
    }

    /**
     *
     * @param typeCarton
     * @return true si le carton a été retiré
     */
    public boolean retraitCarton(int typeCarton){
        boolean succes = false;
        if (typeCarton > 6 || typeCarton < 1) return false;
        else{
            int nbCartons = listeCartons.get(typeCarton - 1);
            nbCartons--;
            nbCartons = Math.max(nbCartons, 0);
            listeCartons.set(typeCarton - 1, nbCartons);
            alerter(String.valueOf(listeCartons.get(typeCarton - 1)));
            listNbCartons.get(typeCarton-1).setText(String.valueOf(nbCartons));
            return true;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.btnAjoutType1:
                ajoutCarton(1);
                break;
            case R.id.btnRetraitType1:
                retraitCarton(1);
                break;

            case R.id.btnAjoutType2:
                ajoutCarton(2);
                break;
            case R.id.btnRetraitType2:
                retraitCarton(2);
                break;
            case R.id.btnAjoutType3:
                ajoutCarton(3);
                break;
            case R.id.btnRetraitType3:
                retraitCarton(3);
                break;
            case R.id.btnAjoutType4:
                ajoutCarton(4);
                break;
            case R.id.btnRetraitType4:
                retraitCarton(4);
                break;
            case R.id.btnAjoutType5:
                ajoutCarton(5);
                break;
            case R.id.btnRetraitType5:
                retraitCarton(5);
                break;
            case R.id.btnAjoutType6:
                ajoutCarton(6);
                break;
            case R.id.btnRetraitType6:
                retraitCarton(6);
                break;
            case R.id.btnValiderCartons:
                Bundle myBdl = new Bundle();
                myBdl.putSerializable("listeCartons", listeCartons);
                Intent toEnvoiBluetooth = new Intent(this, EnvoiBluetooth.class);
                toEnvoiBluetooth.putExtras(myBdl);
                startActivity(toEnvoiBluetooth);
                break;
            case R.id.btnAjoutQrCode:
                IntentIntegrator intent = new IntentIntegrator(this);
                intent.setCaptureActivity(AnyOrientationCaptureActivity.class);
                intent.setOrientationLocked(false);
                intent.initiateScan();
                break;
        }
    }

    // ########### Fonction lecture du QR code #########
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
//                Log.d(TAG, "onActivityResult: cancelled.");
                Toast.makeText(this, "QR scan cancelled", Toast.LENGTH_LONG).show();
            } else {
//                Log.d(TAG, "onActivityResult: scanned = "+result.getContents());
//                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                int iCarton = Integer.parseInt(result.getContents().split(sepQR)[1]);
                ajoutCarton(iCarton);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
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
