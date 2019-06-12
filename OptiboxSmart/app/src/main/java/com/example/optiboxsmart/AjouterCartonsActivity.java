package com.example.optiboxsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class AjouterCartonsActivity extends AppCompatActivity {

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
        tvNbType1 = findViewById(R.id.tvNbType1);
        tvNbType2 = findViewById(R.id.tvNbType2);
        tvNbType3 = findViewById(R.id.tvType3);
        tvNbType4 = findViewById(R.id.tvType4);
        tvNbType5 = findViewById(R.id.tvType5);
        tvNbType6 = findViewById(R.id.tvType6);
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
            mapCartons.put(typeCarton, nbCartons++);
            actualisationNbCartons();
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
            mapCartons.put(typeCarton, Math.max(nbCartons, 0));
            actualisationNbCartons();
            return true;
        }
    }

    /**
     *
     * @return true 
     */
    public boolean actualisationNbCartons(){
        for (int i = 1; i < 6; i++) {
            int nbCartons = mapCartons.get(i);
                tvNbType1.setText(nbCartons);
        }
        return true;
    }
}
