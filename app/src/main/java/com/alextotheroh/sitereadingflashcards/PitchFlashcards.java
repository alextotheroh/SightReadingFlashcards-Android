package com.alextotheroh.sitereadingflashcards;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import lombok.Getter;

public class PitchFlashcards {

    private static final Pitch flashcardRangeBegin = new Pitch("D", "s", 4, 311.13);
    private static final Pitch flashcardRangeEnd = new Pitch("F", "s", 5, 739.99);

    private Random random = new Random();

    @Getter
    private ArrayList<Pitch> pitchFlashcards;

    public PitchFlashcards(ArrayList<Pitch> pitches) {
        this.pitchFlashcards = pitches;
    }

    public Pitch getNextCard() {
        return this.pitchFlashcards.get(
            random.nextInt(this.pitchFlashcards.size())
        );
    }

    public static PitchFlashcards getPitchFlashcardsFromDetectablePitchesArray(ArrayList<Pitch> detectablePitches) {
        boolean inRange = false;
        ArrayList<Pitch> pitchFlashcards = new ArrayList<>();

        for (int i = 0; i < detectablePitches.size(); i++) {
            if (inRange) {

                if (detectablePitches.get(i).equals(flashcardRangeEnd)) {
                    pitchFlashcards.add(detectablePitches.get(i));
                    return new PitchFlashcards(pitchFlashcards);
                } else {
                    pitchFlashcards.add(detectablePitches.get(i));
                }

            } else { // not yet in range

                if (detectablePitches.get(i).equals(flashcardRangeBegin)) {
                    inRange = true;
                    pitchFlashcards.add(detectablePitches.get(i));
                }
            }
        }

        Log.e("getPitchFlashcards:", "Made it through entire list of detectable pitches and never found range end of pitch flashcards.  Something has gone wrong.");
        throw new RuntimeException("Made it through entire list of detectable pitches and never found range end of pitch flashcards.  Something has gone wrong.");
    }

}
