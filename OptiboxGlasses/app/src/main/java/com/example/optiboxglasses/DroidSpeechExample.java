package com.example.optiboxglasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vikramezhil.droidspeech.DroidSpeech;
import com.vikramezhil.droidspeech.OnDSListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DroidSpeechExample extends AppCompatActivity implements View.OnClickListener, OnDSListener {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE = 10;
    private DroidSpeech droidSpeech;
    private TextView textOutput;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textOutput= findViewById(R.id.textOutput);
        btnStart = findViewById(R.id.startDictation);

        btnStart.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startDictation :
                droidSpeech = new DroidSpeech(this, null);
                droidSpeech.setOnDroidSpeechListener(this);
                droidSpeech.startDroidSpeechRecognition();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        droidSpeech.closeDroidSpeechOperations();
    }

    @Override
    public void onDroidSpeechSupportedLanguages(String currentSpeechLanguage, List<String> supportedSpeechLanguages) {
        Log.d(TAG, "onDroidSpeechSupportedLanguages: "+currentSpeechLanguage);
    }

    @Override
    public void onDroidSpeechRmsChanged(float rmsChangedValue) {
        Log.d(TAG, "onDroidSpeechRmsChanged: "+rmsChangedValue);
    }

    @Override
    public void onDroidSpeechLiveResult(String liveSpeechResult) {
        Log.d(TAG, "onDroidSpeechLiveResult: "+liveSpeechResult);
    }

    @Override
    public void onDroidSpeechFinalResult(String finalSpeechResult) {
        Log.d(TAG, "onDroidSpeechFinalResult: "+finalSpeechResult);
    }

    @Override
    public void onDroidSpeechClosedByUser() {
        Log.d(TAG, "onDroidSpeechClosedByUser.");
    }

    @Override
    public void onDroidSpeechError(String errorMsg) {
        Log.d(TAG, "onDroidSpeechError: "+errorMsg);
    }
}
