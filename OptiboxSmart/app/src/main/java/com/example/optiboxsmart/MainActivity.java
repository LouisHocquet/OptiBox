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
        Box i1 = new Box(new double[]{100, 76, 30});
        Box i2 = new Box(110, 43, 25, new byte[]{0, 1, 1});
        Box i3 = new Box(92, 81, 55, new byte[]{1, 1, 1});

        HashMap<Box, Integer> boxes = new HashMap<>();
        boxes.put(i1, 40);
        boxes.put(i2, 33);
        boxes.put(i3, 39);

        Loader clp = new Loader(new double[]{587, 233, 220}, boxes);
        Container container = clp.solve();
        Log.i(TAG, container.toString());
        for (double[] d : container.toArray()){
            Log.i(TAG, Arrays.toString(d) + ",");
        }
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
