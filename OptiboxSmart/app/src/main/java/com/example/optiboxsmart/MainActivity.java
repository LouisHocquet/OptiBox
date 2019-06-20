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

        /* Testing CLP */
        Box[] boxTypes = new Box[]{
                new Box(new double[]{100, 76, 30}),
                new Box(110, 43, 25, new byte[]{0, 1, 1}),
                new Box(92, 81, 55, new byte[]{1, 1, 1}),
                new Box(new double[]{200, 26, 18}),
                new Box(50, 143, 14, new byte[]{0, 1, 1}),
                new Box(29, 92, 13, new byte[]{1, 1, 1}),
        };
        double[] containerDim = new double[]{587, 233, 220};
        int[] boxNumber = new int[]{40, 33, 39, 0, 0, 0};

        HashMap<Box, Integer> cargo = new HashMap<>();
        for (int n = 0; n < 6; n++){
            cargo.put(boxTypes[n], boxNumber[n]);
        }


        Loader clp = new Loader(containerDim, cargo);
        List<double[]> boxes = clp.solveToArray();
        for (double[] d : boxes){
            Log.i(TAG, Arrays.toString(d) + ",");
        }
        /* End testing CLP*/
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
