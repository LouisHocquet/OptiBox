package com.example.optiboxglasses;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

<<<<<<< HEAD
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
=======
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
>>>>>>> Bluetooth avec BluetoothCom, not working
    private Button btnStart;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();
        btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                Intent toReceptionBluetooth = new Intent(this, ReceptionBluetooth.class);
                startActivity(toReceptionBluetooth);
        }
    }
}
