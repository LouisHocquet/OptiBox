package com.example.optiboxglasses;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class OpenGLES20Activity extends AppCompatActivity {

    private static final String TAG = "OPENGL_ACTIVITY";
    private CustomGLSurfaceView gLView;
    private SpeechRecognizer speechRecognizer;
    private TextView textOutput;
    private Button btnUpdate;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String cardboardsJson = getIntent().getStringExtra("jsonCardboards");


        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gLView = new CustomGLSurfaceView(this,cardboardsJson);

        setContentView(gLView);

        textOutput = new TextView(this);
        textOutput.setText("");
        textOutput.setTextColor(Color.WHITE);
        textOutput.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;

        btnUpdate = new Button(this);
        btnUpdate.setText("MAJ");
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gLView.updateCardboardsList();
            }
        });
        FrameLayout.LayoutParams btnParams = new FrameLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        btnParams.gravity = Gravity.BOTTOM|Gravity.END;


        addContentView(textOutput, params);
        addContentView(btnUpdate, btnParams);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new OpenGLES20Activity.SpeechListener());
        startRecognition();
    }

    public void startRecognition(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        speechRecognizer.startListening(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(speechRecognizer!=null)
            speechRecognizer.destroy();
    }

    public class SpeechListener implements RecognitionListener {
        public void onReadyForSpeech(Bundle params)
        {
            textOutput.setText("A l'écoute");
        }

        @Override
        public void onBeginningOfSpeech() {

        }
        @Override
        public void onRmsChanged(float rmsdB) {

        }
        @Override
        public void onBufferReceived(byte[] buffer) {

        }
        @Override
        public void onPartialResults(Bundle partialResults) {
        }
        @Override
        public void onEvent(int eventType, Bundle params) {}

        public void onEndOfSpeech()
        {
            Log.d(TAG, "onEndofSpeech");
        }
        public void onError(int error)
        {
            Log.d(TAG,  "error " +  error);
            textOutput.setText("error " + error);
            startRecognition();
        }
        public void onResults(Bundle results)
        {
            String str = new String();
            Log.d(TAG, "onResults " + results);
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++)
            {
                Log.d(TAG, "result " + data.get(i));
                str += data.get(i)+" ";
            }
            if (str.contains("next")){
                textOutput.setText("Nouveau carton");
                gLView.updateCardboardsList();
            }
            else{
                textOutput.setText("Commande non reconnue");
            }
            startRecognition();
        }



    }
}
