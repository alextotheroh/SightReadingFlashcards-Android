package com.alextotheroh.sightreadingflashcards.audio.core;

public interface Callback {
    void onBufferAvailable(byte[] buffer);
}