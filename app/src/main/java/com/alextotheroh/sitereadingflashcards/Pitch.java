package com.alextotheroh.sitereadingflashcards;

public class Pitch {
    float frequency;
    String note;

    public Pitch(String note, String modifier, int number, float frequency) {
        this.frequency = frequency;
        this.note = note;
    }
}
