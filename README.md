# SiteReadingFlashcards-Android

## Common tasks
### Adding more note flashcards
- to extends flashcard set, add a new png to `src/main/res/drawable` and update range begin/end in `PitchFlashcards.java`
- new flashcard images MUST be referenced from old images. Each note.png is overlayed onto sheet_music_background.png.  This is possible because we assume the size of note pngs.  Note pngs must be 86px wide by 310px tall.
- we only have frequency data for a limited number of pitches.  It is unlikely that anyone would want to create a flashcard image for a note outside of the frequencies we have data for, but to be safe, see `src/main/assets/frequency-to-note-mapping.csv`.  Rows are in the form `note, modifier, number, freq`, so `D, s, 3, 155.56` means "D sharp 3 has a frequency of 155.56hz"

## Sources
### Code has been inspired by the following projects:
- https://github.com/lucns/Android-Audio-Sample
