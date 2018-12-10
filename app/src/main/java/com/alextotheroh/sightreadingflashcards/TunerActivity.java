package com.alextotheroh.sightreadingflashcards;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

public class TunerActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar appBar;
    Spinner notesSpinner;
    ImageButton togglePlayButton;

    private String selectedNote;
    private boolean noteIsPlaying;

    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuner);

        appBar = findViewById(R.id.app_toolbar);
        setSupportActionBar(MainActivity.customizeAppBar(appBar));
        notesSpinner = findViewById(R.id.notes_spinner);
        togglePlayButton = findViewById(R.id.toggle_play_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tuner_spinner_values, R.layout.spinner_item);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notesSpinner.setAdapter(adapter);
        notesSpinner.setOnItemSelectedListener(this);

        togglePlayButton.setImageResource(R.drawable.play);

        togglePlayButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!noteIsPlaying) {
                    playNote();
                } else {
                    stopPlayingNote();
                }
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
       selectedNote = ( (String) parent.getItemAtPosition(pos) ).toLowerCase();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void playNote() {
        int noteMp3Id;

        switch (this.selectedNote) {
            case "a":
                noteMp3Id = R.raw.a;
                break;
            case "b":
                noteMp3Id = R.raw.b;
                break;
            case "c":
                noteMp3Id = R.raw.c;
                break;
            case "d":
                noteMp3Id = R.raw.d;
                break;
            case "e":
                noteMp3Id = R.raw.e;
                break;
            case "f":
                noteMp3Id = R.raw.f;
                break;
            case "g":
                noteMp3Id = R.raw.g;
                break;
            default:
                noteMp3Id = R.raw.a;
        }

        mp = MediaPlayer.create(this, noteMp3Id);
        mp.setLooping(true);
        mp.start();
        noteIsPlaying = true;
        togglePlayButton.setImageResource(R.drawable.pause);
    }

    private void stopPlayingNote() {
        mp.stop();
        mp.release();
        mp = null;
        noteIsPlaying = false;
        togglePlayButton.setImageResource(R.drawable.play);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tuner_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_flashcards:
                // User chose the "Flashcards" item, start main activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
