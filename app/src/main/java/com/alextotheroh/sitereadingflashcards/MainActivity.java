package com.alextotheroh.sitereadingflashcards;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.widget.TextView;

import com.alextotheroh.sitereadingflashcards.audio.Pitch;
import com.alextotheroh.sitereadingflashcards.audio.calculators.AudioCalculator;
import com.alextotheroh.sitereadingflashcards.audio.core.Callback;
import com.alextotheroh.sitereadingflashcards.audio.core.Recorder;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Recorder recorder;
    private AudioCalculator audioCalculator;
    private Handler handler;

    private TextView textAmplitude;
    private TextView textDecibel;
    private TextView textFrequency;
    private TextView textClosestPitch;

    private ArrayList<Pitch> pitches;
    private Pitch closestPitch = new Pitch("C", "n", 4, 261.63);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        recorder = new Recorder(callback);
        audioCalculator = new AudioCalculator();
        handler = new Handler(Looper.getMainLooper());

        textAmplitude = (TextView) findViewById(R.id.textAmplitude);
        textDecibel = (TextView) findViewById(R.id.textDecibel);
        textFrequency = (TextView) findViewById(R.id.textFrequency);
        textClosestPitch = (TextView) findViewById(R.id.textClosestPitch);

        pitches = Pitch.getPitchArrayFromCSV(this, getAssets());
    }

    private Callback callback = new Callback() {

        @Override
        public void onBufferAvailable(byte[] buffer) {
            audioCalculator.setBytes(buffer);
            int amplitude = audioCalculator.getAmplitude();
            double decibel = audioCalculator.getDecibel();
            final double frequency = audioCalculator.getFrequency();

            final String amp = String.valueOf(amplitude + " Amp");
            final String db = String.valueOf(decibel + " db");
            final String hz = String.valueOf(frequency + " Hz");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    closestPitch = Pitch.getClosestPitchForFrequency(frequency, pitches);

                    textAmplitude.setText(amp);
                    textDecibel.setText(db);
                    textFrequency.setText(hz);
                    textClosestPitch.setText(closestPitch.toString());
                }
            });
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        recorder.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        recorder.stop();
    }
}
