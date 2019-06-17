package com.example.optiboxsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class EnvoiBluetooth extends AppCompatActivity implements View.OnClickListener {

    private Button btnConnexionBluetooth;
    private TextView tvResultBluetooth;
    private Map<Integer, Integer> mapCartons;
    private TextView tvMapCartons;

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
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnConnexionBluetooth:
                tvResultBluetooth.setText("Connexion Bluetooth ...");
                break;
        }
    }
}
