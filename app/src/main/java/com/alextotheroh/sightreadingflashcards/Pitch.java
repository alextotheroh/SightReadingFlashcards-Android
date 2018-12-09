package com.alextotheroh.sightreadingflashcards;

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

    public Pitch(String note, String modifier, int number, double frequency) {
        this.note = note.toLowerCase();
        this.modifier = modifier.toLowerCase();
        this.number = number;
        this.frequency = frequency;
    }

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

    public boolean isNaturalNote() {
        return this.modifier.equals("n");
    }

    public boolean isSharpNote() {
        return this.modifier.equals("s");
    }

    public boolean isFlatNote() {
        return this.modifier.equals("f");
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

        return isExactSameNote( (Pitch)other ) ||
            isSameNoteWithDifferentName( (Pitch)other );
    }

    public boolean isExactSameNote(Pitch other) {
        return (
            this.modifier.equals(other.modifier) &&
            this.note.equals(other.note) &&
            this.number == (other).number);
    }

    private boolean isSameNoteWithDifferentName(Pitch other) {
        Pitch p1Equivalent = this.getEquivalentNoteIfExists();
        return (p1Equivalent != null && p1Equivalent.isExactSameNote(other));
    }

    /* Ex: "G sharp" is the same note as "A flat" frequency wise */
    // ASSUME that we will never use double, triple, etc. flat/sharps.  Only single modifiers like "G sharp", not "G sharp sharp".
    // returns null if nothing following above definition of "equivalent note" exists
    public Pitch getEquivalentNoteIfExists() {
        if (this.isNaturalNote()) {
            return null;
        } else if (this.isSharpNote()) {
            return new Pitch(this.getNoteAbove(), "f", this.getNumberOfNoteAbove(), frequency);
        } else { // is flat note
            return new Pitch(this.getNoteBelow(), "s", this.getNumberOfNoteBelow(), frequency);
        }
    }

    private String getNoteAbove() {
        switch (this.note) {
            case "a":
                return "b";
            case "b":
                return "c";
            case "c":
                return "d";
            case "d":
                return "e";
            case "e":
                return "f";
            case "f":
                return "g";
            case "g":
                return "a";
            default:
                throw new RuntimeException("The note for this pitch is not a valid value: " + this.toString());
        }
    }

    private String getNoteBelow() {
        switch (this.note) {
            case "a":
                return "g";
            case "b":
                return "a";
            case "c":
                return "b";
            case "d":
                return "c";
            case "e":
                return "d";
            case "f":
                return "e";
            case "g":
                return "f";
            default:
                throw new RuntimeException("The note for this pitch is not a valid value: " + this.toString());
        }
    }

    private int getNumberOfNoteAbove() {
        if (this.note.equals("b")) {
            return this.number + 1;
        }

        return this.number;
    }

    private int getNumberOfNoteBelow() {
        if (this.note.equals("c")) {
            return this.number - 1;
        }

        return this.number;
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
