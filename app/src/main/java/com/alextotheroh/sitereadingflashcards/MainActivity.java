package com.alextotheroh.sitereadingflashcards;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.alextotheroh.sitereadingflashcards.audio.DetectedPitchesBuffer;
import com.alextotheroh.sitereadingflashcards.audio.calculators.AudioCalculator;
import com.alextotheroh.sitereadingflashcards.audio.core.Callback;
import com.alextotheroh.sitereadingflashcards.audio.core.Recorder;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Recorder recorder;
    private AudioCalculator audioCalculator;
    private Handler handler;

    private ImageView noteImgView;
    private ImageView sheetMusicImageView;

    private ArrayList<Pitch> detectablePitches;
    private DetectedPitchesBuffer detectedPitchesBuffer = new DetectedPitchesBuffer();
    private PitchFlashcards pitchFlashcards;

    private Pitch closestPitch = new Pitch("C", "n", 4, 261.63);

    // the performed pitch is different than the detected pitch because we require that the user
    // sustain the performed pitch for some period of time.  This is the purpose of the detectedPitchesBuffer.
    private Pitch performedPitch = new Pitch("C", "n", 4, 261.63);
    private Pitch pitchToPerform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        recorder = new Recorder(callback);
        audioCalculator = new AudioCalculator();
        handler = new Handler(Looper.getMainLooper());

        noteImgView = findViewById(R.id.noteImg);
        sheetMusicImageView = findViewById(R.id.sheetMusicBackground);

        sheetMusicImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sheetMusicImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                setNoteImageViewSize(sheetMusicImageView.getMeasuredWidth(), sheetMusicImageView.getMeasuredHeight());
            }
        });

        detectablePitches = Pitch.getPitchArrayFromCSV(this, getAssets());
        pitchFlashcards = PitchFlashcards.getPitchFlashcardsFromDetectablePitchesArray(detectablePitches);
        pitchToPerform = pitchFlashcards.getNextCard();
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

                    if (detectedPitchesBuffer.pitchWasPerformed()) {
                        performedPitch = closestPitch.copy();

                        // TODO if performed pitch was correct pitch, then play success sound and change flashcard
                        if (performedPitch.equals(pitchToPerform)) {
                            correctPitchWasPerformed();
                        }
                    }
                }
            });
        }
    };

    private void correctPitchWasPerformed() {
        Toast successToast = Toast.makeText(this, "Correct note was played!", Toast.LENGTH_SHORT);
        successToast.show();
        this.pitchToPerform = pitchFlashcards.getNextCard();
    }

    private void setNoteImageViewSize(int sheetMusicImgWidth, int sheetMusicImgHeight) {
        final double sheetMusicWidthToNoteImgWidthRatio = 58d/604d;
        int noteImgWidth = (int)Math.round((double)sheetMusicImgWidth * sheetMusicWidthToNoteImgWidthRatio);

        ConstraintSet constraintSet = new ConstraintSet();
        int noteImgViewId = R.id.noteImg;
        int sheetMusicImgId = R.id.sheetMusicBackground;

        // reconnect starts and ends to try and center horizontally again, resizing seems to break it
        constraintSet.constrainWidth(noteImgViewId, noteImgWidth);
        constraintSet.constrainHeight(noteImgViewId, sheetMusicImgHeight);
        constraintSet.connect(noteImgViewId, ConstraintSet.BOTTOM, sheetMusicImgId, ConstraintSet.BOTTOM);
        constraintSet.connect(noteImgViewId, ConstraintSet.START, sheetMusicImgId, ConstraintSet.START);
        constraintSet.connect(noteImgViewId, ConstraintSet.END, sheetMusicImgId, ConstraintSet.END);

        ConstraintLayout rootLayout = findViewById(R.id.root);
        constraintSet.applyTo(rootLayout);
    }

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
