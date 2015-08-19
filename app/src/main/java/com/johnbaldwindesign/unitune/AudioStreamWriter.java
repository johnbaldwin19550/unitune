package com.johnbaldwindesign.unitune;

import android.media.AudioTrack;

/**
 * Created by john on 8/13/15.
 */
public class AudioStreamWriter implements Runnable{
    Thread th;
    AudioTrack at;
    float[] data;
    int off;
    int length;
    int mode;
    public void write(AudioTrack at, float[] data, int off, int length, int mode){
        this.at=at;
        this.data=data;
        this.off=off;
        this.length=length;
        this.mode=mode;
        if(th==null){
            th=new Thread(this);
            th.start();
        }
    }
    public void run(){
        at.write(data, off, length, mode);
        if(th!=null){
            th=null;
        }
    }
}
