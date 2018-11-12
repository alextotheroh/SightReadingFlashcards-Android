package com.alextotheroh.sitereadingflashcards.audio;

import com.alextotheroh.sitereadingflashcards.Pitch;

import java.util.ArrayList;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DetectedPitchesBuffer {

    private static final int MAX_BUFFER_SIZE = 12;

    private ArrayList<Pitch> buffer = new ArrayList<>();

    public void add(Pitch p) {
        if (buffer.size() < MAX_BUFFER_SIZE) {
            buffer.add(p);
        } else if (buffer.size() >= MAX_BUFFER_SIZE) {
            buffer.remove(0);
            buffer.add(p);
        }
    }

    /*
      A pitch is considered performed if the entire buffer is filled with the same pitch.
      This means that the user has sustained the pitch through the configured number of loop iterations.

      TODO loop iterations per unit time is almost certainly a pretty large difference depending on device hardware.
        This theory hasn't been tested, but the code base should be refactored to do buffer and 'performed note' calculations based
        on time instead of loop iterations.  If that's not feasible, then the existing logic should be tested on a wide range of devices,
        especially those with limited cpu resources.
     */
    public boolean pitchWasPerformed() {
        final Pitch pitchToCompareBufferAgainst = this.buffer.get(this.buffer.size() - 1); // most recent Pitch in buffer

        for (Pitch p : this.buffer) {
            if (!p.equals(pitchToCompareBufferAgainst)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        String result = "";
        for (Pitch p : this.buffer) {
            result += p.getNote() + p.getModifier() + p.getNumber() + " - ";
        }
        return result;
    }

}
