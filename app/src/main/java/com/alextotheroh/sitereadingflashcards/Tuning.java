package com.alextotheroh.sitereadingflashcards;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.List;

public class Tuning {

    private static final String FREQUENCY_TO_NOTE_MAPPING_FILE = "frequency-to-note-mapping.csv";
    private static final int NUMBER_OF_PITCHES = 108;

    String name;
    ArrayList<Pitch> pitches;

    public Tuning(String name, ArrayList<Pitch> pitches) {
        this.name = name;
        this.pitches = pitches;
    }

    public Pitch closestPitch(float freq) {
        Pitch closest = null;
        float dist = Float.MAX_VALUE;
        for (Pitch pitch : pitches) {
            float d = Math.abs(freq - pitch.frequency);
            if (d < dist) {
                closest = pitch;
                dist = d;
            }
        }
        return closest;
    }

    public int closestPitchIndex(float freq) {
        int index = -1;
        float dist = Float.MAX_VALUE;
        for (int i = 0; i < pitches.size(); i++) {
            Pitch pitch = pitches.get(i);
            float d = Math.abs(freq - pitch.frequency);
            if (d < dist) {
                index = i;
                dist = d;
            }
        }
        return index;
    }

    public static Tuning getTuning(Context context, AssetManager assetManager) {
        return new Tuning("A440", getPitchArrayFromCSV(context, assetManager));
    }

    private static ArrayList<Pitch> getPitchArrayFromCSV(Context context, AssetManager assetManager) {
        ArrayList<Pitch> pitches = new ArrayList<>();
        try {
            InputStream csvInputStream = assetManager.open(FREQUENCY_TO_NOTE_MAPPING_FILE);
            CSVReader reader = new CSVReader(new InputStreamReader(csvInputStream), ',');

            List<String[]> allCsvRows = reader.readAll();

            Log.e("~~~~~~~allRowsCount", String.valueOf(allCsvRows.size()));

            for (String[] row : allCsvRows) {
                // nextLine[] is an array of values from the line
                // note   modifier   number   frequency
                Log.e("current csv line:", row[0] + " " + row[1] + " " + row[2] + " " + row[3]);
                pitches.add(
                    new Pitch(
                        row[0].trim(),
                        row[1].trim(),
                        Integer.parseInt( row[2].trim() ),
                        Float.parseFloat( row[3].trim() ))
                );
            }
        } catch(Exception e){
            Log.e("~~~~caught an exception", e.getMessage());
            e.printStackTrace();
            Toast.makeText(context, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }

        Log.e("~~~~~~~~~~pitcheslength", String.valueOf(pitches.size()));
        return pitches;
    }


}
