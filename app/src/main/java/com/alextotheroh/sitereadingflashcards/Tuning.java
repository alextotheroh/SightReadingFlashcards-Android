package com.alextotheroh.sitereadingflashcards;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.lang.ref.PhantomReference;
import java.util.ArrayList;

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

    public static Tuning getTuning(Context context) {
        return new Tuning("A440", getPitchArrayFromCSV(context));
    }

    private static ArrayList<Pitch> getPitchArrayFromCSV(Context context) {
        ArrayList<Pitch> pitches = new ArrayList<>();
        try {
            String csvFilePath = context.getApplicationInfo().dataDir + File.separatorChar + FREQUENCY_TO_NOTE_MAPPING_FILE;
            File f = new File(csvFilePath);

            CSVReader reader = new CSVReader(new FileReader(f.getAbsolutePath()));
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                // note   modifier   number   frequency
                Log.e("current csv line:", nextLine[0] + nextLine[1] + nextLine[2] + nextLine[3]);
                pitches.add(
                    new Pitch(
                        nextLine[0],
                        nextLine[1],
                        Integer.parseInt(nextLine[2]),
                        Float.parseFloat(nextLine[3]))
                );
            }
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(context, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }

        return pitches;
    }


}
