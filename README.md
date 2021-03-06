# SightReadingFlashcards-Android

## Common tasks
### Adding more note flashcards
- to extend flashcard set, add a new png to `src/main/res/drawable` and update range begin/end in `PitchFlashcards.java`
- new flashcard images MUST be referenced from old images. Each note.png is overlayed onto sheet_music_background.png.  This is possible because we assume the size of note pngs.  Note pngs must be 86px wide by 310px tall.
- we only have frequency data for a limited number of pitches.  It is unlikely that anyone would want to create a flashcard image for a note outside of the frequencies we have data for, but to be safe, see `src/main/assets/frequency-to-note-mapping.csv`.  Rows are in the form `note, modifier, number, freq`, so `D, s, 3, 155.56` means "D sharp 3 has a frequency of 155.56hz"

## Roadmap
- All 15 key signatures
- 2 stages for each key signature: one stage without accidentals, one stage with.
- Top down or left to right scrolling "Guitar Hero" style
- some of the sharp pitches in `src/main/assets/frequency-to-note-mapping.csv` do not have their corresponding flat in the file.  The pitches in the treble clef in C do, but we should fix the file when we expand the set of flashcards shown. 

## Sources
### Code has been inspired by the following projects:
- https://github.com/lucns/Android-Audio-Sample
