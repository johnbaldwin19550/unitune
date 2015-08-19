package com.johnbaldwindesign.unitune;

import android.annotation.TargetApi;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;

/**
 * Created by john on 8/12/15.
 */
public class WaveformGenerator implements Runnable{
    Thread th;
    boolean on=false;
    Waveform waveform;
    int srate=44100;
    double volume=1;
    public void setWaveform(Waveform form){
        waveform=form;
    }
    public void play(){
        if(th==null){
            on=true;
            th=new Thread(this);
            th.start();
        }
    }
    public void setVolume(double val){
        volume=val;
    }
    public void stop(){
        on=false;
        if(th!=null){
            th=null;
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void run(){
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, srate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT, 4096, AudioTrack.MODE_STREAM);
        AudioStreamWriter asw=new AudioStreamWriter();
        while(on) {
            int off=0;
            try {
                int numcycles=20;
                float[] samples = new float[(int) Math.round((double) srate / waveform.getFrequency() * (double)numcycles)];
                //byte[] data = new byte[2 * samples.length];
                for (int i = 0; i < samples.length; i++) {
                    samples[i] = (float) waveform.getValueAtTime((double) i / (double) srate);
                    //int val = (int) Math.round(samples[i] * 32768d);
                    //if (val < 0) {
                    //    val += 65536;
                    //}
                    //data[i * 2] = (byte) ((val) % 256);
                    //data[i * 2 + 1] = (byte) ((val) / 256);
                }
                at.setVolume((float) volume);
                at.play();
                asw.write(at, samples, off, samples.length, AudioTrack.WRITE_BLOCKING);
                off += samples.length;
                //th.sleep((int)(waveform.getPeriod()*1000d));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        at.flush();
    }
}
