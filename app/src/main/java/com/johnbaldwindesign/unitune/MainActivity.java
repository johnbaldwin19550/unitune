package com.johnbaldwindesign.unitune;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import android.view.*;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    int instr=0;
    int pitch=0;
    double calib=440;
    String[] instrumentNames; // parallel
    int[][] pitches; // parallel
    int[] transpose; //parallel
    int[][] harmonics; //parallel
    WaveformGenerator gen=new WaveformGenerator();
    SharedPreferences prefs;
    int volume;
    int mode=0; //0=display common tuning pitches; 1=display all chromatic pitches
    int on=0;
    public void onPause(){
        super.onPause();
        gen.stop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs=getPreferences(Context.MODE_PRIVATE);
        String instring=prefs.getString("instrument", "Guitar");
        volume=prefs.getInt("volume", 50);
        mode=prefs.getInt("mode", 0);
        calib=prefs.getInt("calibration", 440);
        System.out.println(instring);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (on == 0) {
                    button.setText("Stop");
                    on=1;
                    gen.play();
                }else{
                    button.setText("Start");
                    on=0;
                    gen.stop();
                }
            }
        });
        String[] instruments=getResources().getStringArray(R.array.instruments);
        Arrays.sort(instruments);
        instrumentNames=new String[instruments.length];
        transpose=new int[instruments.length];
        harmonics=new int[instruments.length][];
        pitches=new int[instruments.length][];
        for(int i=0; i<instruments.length; i++){
            instrumentNames[i]=instruments[i].split(":")[0];
            if(instrumentNames[i].equals(instring)){
                instr=i;
            }
            String[] instrumentPitches=instruments[i].split(":")[1].split(",");
            pitches[i]=new int[instrumentPitches.length];
            if(instr==i&&pitch>=pitches[i].length){ //if the selected note is out of range, set it to 0
                pitch=0;
            }
            for(int s=0; s<instrumentPitches.length; s++){
                pitches[i][s]=Integer.parseInt(instrumentPitches[s]);
            }
            transpose[i]=Integer.parseInt(instruments[i].split(":")[2]);
            String harms=instruments[i].split(":")[3];
            harmonics[i]=new int[harms.length()];
            for(int s=0; s<harms.length(); s++){
                harmonics[i][s]=Integer.parseInt(harms.charAt(s)+"", 16);
            }
        }
        final Spinner spinner=(Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, instrumentNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(instr);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> av, View v, int i, long l) {
                int item = spinner.getSelectedItemPosition();
                setInstrument(item);
            }

            public void onNothingSelected(AdapterView<?> av) {

            }

        });
        setInstrument(instr);
        SeekBar seekbar=(SeekBar)findViewById(R.id.seekBar);
        seekbar.setProgress(volume);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gen.setVolume((double) Math.pow(2, (double) progress / 100d) - 1d);
                volume = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        final NumberPicker picker=(NumberPicker)findViewById(R.id.numberPicker);
        picker.setMinValue(420);
        picker.setMaxValue(460);
        picker.setValue((int) calib);
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                calib=newVal;
                generateWaveform();
            }
        });
        Spinner spinner2=(Spinner)findViewById(R.id.spinner2);
        spinner2.setSelection(mode);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mode==0&&position==1) { // if changing from tuning pitches mode to all pitches mode
                    pitch=pitches[instr][pitch]; //pitches becomes the MIDI pitch of the current note, so that pitch is retained
                    mode = position;
                }else if(mode==1&&position==0){ //if changing from all pitches mode to tuning pitches mdoe
                    int min=Integer.MAX_VALUE;
                    int which=-1;
                    for(int i=0; i<pitches[instr].length; i++){ // do a sequential search for the nearest tuning pitch
                        if(Math.abs(pitches[instr][i]-pitch)<min){ // if the current tuning pitch is closer than all the previous ones
                            min=Math.abs(pitches[instr][i]-pitch); // select the pitch and reset the minimum distance
                            which=i;
                        }
                    }
                    pitch=which; // assign the index of the nearest match
                    mode=position;
                }
                setInstrument(instr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void setInstrument(int item){
        instr=item;
        final LinearLayout ll=(LinearLayout)findViewById(R.id.pitches);
        ll.removeAllViews();
        if(mode==0){
            if(pitch>=pitches[item].length){
                pitch=pitches[item].length-1;
            }
            for (int i = 0; i < pitches[item].length; i++) {
                final Button thisButton = new Button(this);
                thisButton.setAllCaps(false);
                thisButton.setText(getNoteName(pitches[item][i]));
                if (i == pitch) {
                    thisButton.setPressed(true);
                }
                final int which = i;
                thisButton.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        for (int s = 0; s < ll.getChildCount(); s++) {
                            Button b = (Button) ll.getChildAt(s);
                            b.setPressed(false);
                        }
                        setPitch(which);
                        thisButton.setPressed(true);
                        return true;
                    }
                });
                ll.addView(thisButton);
            }
        }else if(mode==1){
            for(int i=21; i<108; i++){
                final Button thisButton=new Button(this);
                thisButton.setAllCaps(false);
                thisButton.setText(getNoteName(i));
                final int which=i;
                thisButton.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        for(int s=0; s<ll.getChildCount(); s++) {
                            Button b=(Button)ll.getChildAt(s);
                            b.setPressed(false);
                        }
                        setPitch(which);
                        thisButton.setPressed(true);
                        return true;
                    }
                });
                ll.addView(thisButton);
                if(i==pitch){
                    thisButton.setPressed(true);
                }
            }
        }
        generateWaveform();
    }
    private void generateWaveform(){
        CompositeSineWave csw;
        if(mode==0) {
            csw = new CompositeSineWave(midifreq(pitches[instr][pitch]));
        }else{
            csw = new CompositeSineWave(midifreq(pitch));
        }
        for(int i=0; i<harmonics[instr].length; i++){
            csw.setHarmonic((i+1), (double)harmonics[instr][i]/32d);
        }
        gen.setWaveform(csw);
    }
    private void setPitch(int which){ //in mode 0, which is the index of the selected note; in mode 1, it is the midi pitch of the note
        pitch=which;
        generateWaveform();
    }
    private double midifreq(int midi){
        return calib*Math.pow(2, (double)(midi-69)/12d);
    }
    private String getNoteName(int midi){
        midi -=transpose[instr];
        int octave=midi/12-1;
        String[] notenames=getResources().getStringArray(R.array.notenames);
        return notenames[midi%12]+octave;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onStop(){
        SharedPreferences.Editor ed=prefs.edit();
        System.out.println(instrumentNames[instr]);
        ed.putString("instrument", instrumentNames[instr]);
        ed.putInt("volume", volume);
        ed.putInt("calibration", (int)calib);
        ed.putInt("mode", mode);
        ed.commit();
        super.onStop();
    }
}
