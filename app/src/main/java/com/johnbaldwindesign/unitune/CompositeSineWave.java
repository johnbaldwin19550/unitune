package com.johnbaldwindesign.unitune;

/**
 * Created by john on 8/12/15.
 */
public class CompositeSineWave implements Waveform {
    int[] harmonics;
    double[] amplitudes;
    double basefreq;
    public CompositeSineWave(double f) {
        basefreq=f;
        amplitudes=new double[64];
    }
    public void setHarmonic(int which, double amp){
        amplitudes[which]=amp;
    }
    public double getPeriod(){
        return 1d/basefreq;
    }
    public double getFrequency(){
        return basefreq;
    }
    public double getValueAt(double pos){
        double val=0;
        for(int i=0; i<amplitudes.length; i++){
            if(amplitudes[i]!=0) {
                val += Math.sin(pos * Math.PI * 2d*i) * (amplitudes[i]/(double)i);
            }
        }
        return val;
    }
    public double getValueAtTime(double sec){
        return getValueAt(sec*basefreq);
    }
}
