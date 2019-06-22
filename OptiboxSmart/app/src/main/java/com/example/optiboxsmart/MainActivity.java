package com.example.optiboxsmart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.optiboxsmart.clp_solver.Box;
import com.example.optiboxsmart.clp_solver.Container;
import com.example.optiboxsmart.clp_solver.Loader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnStart;
    private static final String TAG = "MainActivity";
    public static  Box[] boxTypes = new Box[]{
            new Box(4, 10, 10,new byte[]{1, 1, 1}),
            new Box(10, 5, 3, new byte[]{1, 1, 1}),
            new Box(20, 15, 5, new byte[]{1, 1, 1}),
            new Box(8, 4, 15,new byte[]{1, 1, 1}),
            new Box(7, 15, 7, new byte[]{1, 1, 1}),
            new Box(6, 2, 3, new byte[]{1, 1, 1}),
    };
    public static double[] containerDim = new double[]{60, 23, 23};


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
                Intent toAjouterCartonsActivity = new Intent(this, AjouterCartonsActivity.class);
                startActivity(toAjouterCartonsActivity);
        }
    }
}
