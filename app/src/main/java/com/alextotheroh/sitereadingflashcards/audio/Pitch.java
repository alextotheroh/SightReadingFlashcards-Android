package com.alextotheroh.sitereadingflashcards.audio;

import android.content.Context;
import android.content.res.AssetManager;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
public class Pitch {

    @Getter
    private String note;
    @Getter
    private String modifier;
    @Getter
    private int number;
    @Getter
    private double frequency;

    private static final String FREQUENCY_TO_NOTE_MAPPING_FILE = "frequency-to-note-mapping.csv";

    public static Pitch getClosestPitchForFrequency(double frequency, ArrayList<Pitch> pitches) {
        double currentDistance = Integer.MAX_VALUE;
        int indexOfClosestPitch = 0;

        for (int i = 0; i < pitches.size(); i++) {
            double distance = Math.abs(pitches.get(i).frequency - frequency);
            if (distance < currentDistance) {
                currentDistance = distance;
                indexOfClosestPitch = i;
            }
        }

        return pitches.get(indexOfClosestPitch);
    }

    public static ArrayList<Pitch> getPitchArrayFromCSV(Context context, AssetManager assetManager) {
        ArrayList<Pitch> pitches = new ArrayList<>();
        try {
            InputStream csvInputStream = assetManager.open(FREQUENCY_TO_NOTE_MAPPING_FILE);
            CSVReader reader = new CSVReader(new InputStreamReader(csvInputStream));

            List<String[]> allCsvRows = reader.readAll();

            for (String[] row : allCsvRows) {
                // nextLine[] is an array of values from the line
                // note   modifier   number   frequency
                pitches.add(
                        new Pitch(
                                row[0].trim(),
                                row[1].trim(),
                                Integer.parseInt( row[2].trim() ),
                                Float.parseFloat( row[3].trim() ))
                );
            }
        } catch(Exception e){
            e.printStackTrace();
            Toast.makeText(context, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }

        return pitches;
    }

}
