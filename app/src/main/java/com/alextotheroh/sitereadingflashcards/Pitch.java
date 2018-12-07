package com.alextotheroh.sitereadingflashcards;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
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
                // note,   modifier,   number,   frequency
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

    public int getDrawableResourceId(Context context) {
        String filePathNoteModifier;

        switch (this.getModifier()) {
            case "n":
                filePathNoteModifier = "";
                break;
            case "s":
                filePathNoteModifier = "sharp";
                break;
            case "f":
                filePathNoteModifier = "flat";
                break;
            default:
                filePathNoteModifier = "INVALID";
        }

        if (filePathNoteModifier.equals("INVALID")) {
            throw new RuntimeException("failed to get image file path modifier for pitch- unexpected value for pitch.getModifier for pitch: " + this.toString());
        }

        String drawableName =  this.getNote().toLowerCase() + filePathNoteModifier + this.getNumber();

        int drawableId = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());

        if (drawableId == 0) { // doesn't exist
            throw new RuntimeException("couldn't find file for pitch: " + this.toString() + "\nBest guess was: " + drawableName);
        }

        return drawableId;
    }

    public Pitch copy() {
        return new Pitch(this.note, this.modifier, this.number, this.frequency);
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (getClass() != other.getClass())
            return false;

        return (
            this.modifier.equals(((Pitch)other).modifier) &&
            this.note.equals(((Pitch)other).note) &&
            this.number == ((Pitch)other).number
        );
    }

    @Override
    public int hashCode() {
        return (
            this.note.hashCode() +
            this.modifier.hashCode() +
            this.number
        );
    }

}
