# unitune
UniTune: Chromatic oscillating tuner designed to accommodate as many instruments as possible.

With UniTune, you can select an instrument and hear reference pitches in a matching waveform, calibrated to a custom A4 frequency.  UniTune refers to a resource file called "instruments.xml", which stores the name of each instrument, the comon tuning reference pitches (in MIDI pitch notation), the transposition of the instrument, and the waveform to be emulated.  The waveforms are stored as a list of amplitudes of harmonics (whole number multiples of the funamental pitch) in ascending order.  For more details about how this file is notated, take a look at the file itself.  It has a large comment that'll tell you all you need to know.  I encourage editing instruments.xml above all else, because that file alone is the key to this app's versatility.
