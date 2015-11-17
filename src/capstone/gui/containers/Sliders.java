package capstone.gui.containers;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import capstone.gui.Note;

public class Sliders {
	private JSlider pitch, volume, duration;
	
	public Sliders(Note initialNote){
		pitch = new JSlider(0, 127, initialNote.getPitch());
		volume = new JSlider(0, 100, initialNote.getVolume());
		duration = new JSlider(0, 2000, initialNote.getDuration());
	}
	
	public void setToNote(Note n){
		pitch.setValue(n.getPitch());
		volume.setValue(n.getVolume());
		duration.setValue(n.getDuration());
	}
	
	public boolean isPitchAdjusting(){
		return pitch.getValueIsAdjusting();
	}
	
	public boolean isVolumeAdjusting(){
		return volume.getValueIsAdjusting();
	}
	
	public boolean isDurationAdjusting(){
		return duration.getValueIsAdjusting();
	}
	
	public int getPitchValue(){
		return pitch.getValue();
	}
	
	public int getVolumeValue(){
		return volume.getValue();
	}
	
	public int getDurationValue(){
		return duration.getValue();
	}
	
	public void setPitchValue(int value){
		pitch.setValue(value);
	}
	
	public void setVolumeValue(int value){
		volume.setValue(value);
	}
	
	public void setDurationValue(int value){
		duration.setValue(value);
	}
	
	public JSlider getPitchSlider(){
		return pitch;
	}
	
	public JSlider getVolumeSlider(){
		return volume;
	}
	
	public JSlider getDurationSlider(){
		return duration;
	}

	
	public void addListener(ChangeListener listener){
		pitch.addChangeListener(listener);
		volume.addChangeListener(listener);
		duration.addChangeListener(listener);
	}
}
