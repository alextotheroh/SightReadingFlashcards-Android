package com.alextotheroh.sitereadingflashcards.audio.core;

public interface Callback {
    void onBufferAvailable(byte[] buffer);
}