package com.alextotheroh.sitereadingflashcards;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.alextotheroh.sitereadingflashcards.audio.DetectedPitchesBuffer;
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
    private TextView textPerformedPitch;
    private TextView textPitchesBuffer;
    private TextView textNoteToPlay;

    private ArrayList<Pitch> detectablePitches = Pitch.getPitchArrayFromCSV(this, getAssets());
    private DetectedPitchesBuffer detectedPitchesBuffer = new DetectedPitchesBuffer();
    private PitchFlashcards pitchFlashcards = PitchFlashcards.getPitchFlashcardsFromDetectablePitchesArray(detectablePitches);

    private Pitch closestPitch = new Pitch("C", "n", 4, 261.63);

    // the performed pitch is different than the detected pitch because we require that the user
    // sustain the performed pitch for some period of time.  This is the purpose of the detectedPitchesBuffer.
    private Pitch performedPitch = new Pitch("C", "n", 4, 261.63);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        recorder = new Recorder(callback);
        audioCalculator = new AudioCalculator();
        handler = new Handler(Looper.getMainLooper());

        textAmplitude = findViewById(R.id.textAmplitude);
        textDecibel = findViewById(R.id.textDecibel);
        textFrequency = findViewById(R.id.textFrequency);
        textClosestPitch = findViewById(R.id.textClosestPitch);
        textPerformedPitch = findViewById(R.id.textPerformedPitch);
        textPitchesBuffer = findViewById(R.id.textPitchesBuffer);
        textNoteToPlay = findViewById(R.id.textNoteToPlay);
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
                    closestPitch = Pitch.getClosestPitchForFrequency(frequency, detectablePitches);
                    detectedPitchesBuffer.add(closestPitch);

                    textAmplitude.setText(amp);
                    textDecibel.setText(db);
                    textFrequency.setText(hz);
                    textClosestPitch.setText(closestPitch.toString());
                    textPitchesBuffer.setText(detectedPitchesBuffer.toString());
                    textNoteToPlay.setText("Play note: " + pitchFlashcards.getNextCard().toString());

                    if (detectedPitchesBuffer.pitchWasPerformed()) {
                        performedPitch = closestPitch.copy();
                        textPerformedPitch.setText(performedPitch.toString());

                        // TODO if performed pitch was correct pitch, then play success sound and change flashcard
                    }
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
