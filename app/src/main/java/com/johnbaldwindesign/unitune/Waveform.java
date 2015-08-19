package com.johnbaldwindesign.unitune;

/**
 * Created by john on 8/12/15.
 */
public interface Waveform {
    public double getValueAt(double input); //returns the sample at a positon (from 0-1) in the waveform cycle
    public double getValueAtTime(double sec); //returns the sample at a point in time (seconds)
    public double getPeriod(); //returns the period duration (seconds) of a cycle
    public double getFrequency(); //returns the number of cycles per second
}
