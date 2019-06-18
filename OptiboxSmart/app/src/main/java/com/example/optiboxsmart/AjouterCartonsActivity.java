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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjouterCartonsActivity extends AppCompatActivity implements View.OnClickListener {

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
        mapCartons = new HashMap<>();
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
        tvNbType1 = findViewById(R.id.tvType1);
        tvNbType2 = findViewById(R.id.tvType2);
        tvNbType3 = findViewById(R.id.tvType3);
        tvNbType4 = findViewById(R.id.tvType4);
        tvNbType5 = findViewById(R.id.tvType5);
        tvNbType6 = findViewById(R.id.tvType6);

        btnValiderCartons = findViewById(R.id.btnValiderCartons);

        for (int i = 0; i < 6; i++) {
            mapCartons.put(i+1, 0);
        }

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
            int nbCartons = mapCartons.get(typeCarton);
            nbCartons++;

            mapCartons.put(typeCarton, nbCartons);
//            alerter(String.valueOf(mapCartons.get(typeCarton)));
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
            int nbCartons = mapCartons.get(typeCarton);
            nbCartons--;
            nbCartons = Math.max(nbCartons, 0);
            mapCartons.put(typeCarton, nbCartons);
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
                myBdl.putSerializable("mapCartons", (HashMap) mapCartons);
                Intent toEnvoiBluetooth = new Intent(this, EnvoiBluetooth.class);
                toEnvoiBluetooth.putExtras(myBdl);
                startActivity(toEnvoiBluetooth);

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
