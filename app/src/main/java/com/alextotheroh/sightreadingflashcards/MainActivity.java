package com.alextotheroh.sightreadingflashcards;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.alextotheroh.sightreadingflashcards.audio.DetectedPitchesBuffer;
import com.alextotheroh.sightreadingflashcards.audio.calculators.AudioCalculator;
import com.alextotheroh.sightreadingflashcards.audio.core.Callback;
import com.alextotheroh.sightreadingflashcards.audio.core.Recorder;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    public final String APP_LOG_TAG = "sightreadingflashcards";

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

        noteImgView = findViewById(R.id.noteImg);
        sheetMusicImageView = findViewById(R.id.sheetMusicBackground);

        detectablePitches = Pitch.getPitchArrayFromCSV(this, getAssets());
        pitchFlashcards = PitchFlashcards.getPitchFlashcardsFromDetectablePitchesArray(detectablePitches);
        pitchToPerform = pitchFlashcards.getNextCard();
        Log.i(APP_LOG_TAG, "pitch to perform is: " + pitchToPerform.toString());

        sheetMusicImageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                sheetMusicImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setNoteImageViewSize(sheetMusicImageView.getMeasuredWidth(), sheetMusicImageView.getMeasuredHeight(), pitchToPerform);
            }
        });

        updateNoteImage();
    }

    private Callback callback = new Callback() {

        @Override
        public void onBufferAvailable(byte[] buffer) {
            audioCalculator.setBytes(buffer);
            int amplitude = audioCalculator.getAmplitude();
            double decibel = audioCalculator.getDecibel();
            final double frequency = audioCalculator.getFrequency();

            final String amp = String.valueOf(amplitude) + " Amp";
            final String db = String.valueOf(decibel) + " db";
            final String hz = String.valueOf(frequency) + " Hz";

            handler.post(new Runnable() {
                @Override
                public void run() {
                    closestPitch = Pitch.getClosestPitchForFrequency(frequency, detectablePitches);
                    detectedPitchesBuffer.add(closestPitch);
                    Log.i(APP_LOG_TAG, "pitch detected: " + closestPitch.toString());

                    if (detectedPitchesBuffer.pitchWasPerformed()) {
                        performedPitch = closestPitch.copy();

                        // TODO if performed pitch was correct pitch, then play success sound and change flashcard
                        if (performedPitch.equals(pitchToPerform)) {
                            correctPitchWasPerformed();
                        }
                        //TODO if wrong pitch was performed, play buzzer sound, display if flat or sharp
                    }
                }
            });
        }
    };

    private void correctPitchWasPerformed() {
        Toast successToast = Toast.makeText(this, "Correct note was played!", Toast.LENGTH_SHORT);
        successToast.show();
        this.pitchToPerform = pitchFlashcards.getNextCard();
        updateNoteImage();
    }

    private void setNoteImageViewSize(int sheetMusicImgWidth, int sheetMusicImgHeight, Pitch pitchToPerform) {
        float sheetMusicWidthToNoteImgWidthRatio; // if is a natural note
        if (pitchToPerform.isNaturalNote()) {
            sheetMusicWidthToNoteImgWidthRatio = 58f/604f;
        } else {
            sheetMusicWidthToNoteImgWidthRatio = 86f/604f;
        }
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

    private void updateNoteImage() {
        noteImgView.setImageResource(
            pitchToPerform.getDrawableResourceId(this.getApplicationContext())
        );
        setNoteImageViewSize(sheetMusicImageView.getMeasuredWidth(), sheetMusicImageView.getMeasuredHeight(), pitchToPerform);
        Log.i(TAG, "pitch to perform is: " + pitchToPerform.toString());
    }

    private void requestRecordAudioPermissionAndStartRecording() {
        //check API version, do nothing if API version < 23!
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > android.os.Build.VERSION_CODES.LOLLIPOP){

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                    // todo
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1); // references onPermissionsResult callback
                }
            } else {
                initAndStartRecordingAudio();
            }
        } else {
            initAndStartRecordingAudio();
        }
    }

    private void initAndStartRecordingAudio() {
        if (recorder == null) {
            recorder = new Recorder(callback);
        }
        if (audioCalculator == null) {
            audioCalculator = new AudioCalculator();
        }
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        recorder.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d("Activity", "Granted!");
                    initAndStartRecordingAudio();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("Activity", "Denied!");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestRecordAudioPermissionAndStartRecording();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recorder != null) {
            recorder.stop();
        }
    }
}
