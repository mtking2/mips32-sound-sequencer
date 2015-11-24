package capstone.gui.containers;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import capstone.gui.Note;

/**
 * Class grouping the sliders that select note values.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class Sliders {
	/** The slider that selects the pitch of the note **/
	private JSlider pitch;
	
	/** The slider that selects the volume of the note **/
	private JSlider volume;

	/** The slider that selects the duration of the note **/
	private JSlider duration;
	
	/**
	 * Makes a new slider container object and initializes all sliders.
	 * 
	 * @param initialNote the note to use for initial values
	 */
	public Sliders(Note initialNote){
		pitch = new JSlider(0, 127, initialNote.getPitch());
		volume = new JSlider(0, 100, initialNote.getVolume());
		duration = new JSlider(0, 2000, initialNote.getDuration());
	}
	
	/**
	 * Set the sliders to match a note.
	 * 
	 * @param n the note to match slider values to
	 */
	public void setToNote(Note n){
		pitch.setValue(n.getPitch());
		volume.setValue(n.getVolume());
		duration.setValue(n.getDuration());
	}
	
	/**
	 * @return if the pitch slider is adjusting
	 */
	public boolean isPitchAdjusting(){
		return pitch.getValueIsAdjusting();
	}
	
	/**
	 * @return if the volume slider is adjusting
	 */
	public boolean isVolumeAdjusting(){
		return volume.getValueIsAdjusting();
	}
	
	/**
	 * @return if the duration slider is adjusting
	 */
	public boolean isDurationAdjusting(){
		return duration.getValueIsAdjusting();
	}
	
	/**
	 * @return the value on the pitch slider
	 */
	public int getPitchValue(){
		return pitch.getValue();
	}
	
	/**
	 * @return the value on the volume slider
	 */
	public int getVolumeValue(){
		return volume.getValue();
	}
	
	/**
	 * @return the value on the duration slider
	 */
	public int getDurationValue(){
		return duration.getValue();
	}
	
	/**
	 * Set the value on the pitch slider
	 * 
	 * @param value the new value for the pitch slider
	 */
	public void setPitchValue(int value){
		pitch.setValue(value);
	}
	
	/**
	 * Set the value on the volume slider
	 * 
	 * @param value the new value for the volume slider
	 */
	public void setVolumeValue(int value){
		volume.setValue(value);
	}
	
	/**
	 * Set the value on the duration slider
	 * 
	 * @param value the new value for the duration slider
	 */
	public void setDurationValue(int value){
		duration.setValue(value);
	}
	
	/**
	 * @return the pitch slider
	 */
	public JSlider getPitchSlider(){	return pitch;	}
	
	/**
	 * @return the volume slider
	 */
	public JSlider getVolumeSlider(){	return volume;	}
	
	/**
	 * @return the duration slider
	 */
	public JSlider getDurationSlider(){	return duration;	}

	/**
	 * Adds an {@link ActionListener} to each slider.
	 * 
	 * @see ActionListener
	 * @param listener the listener to add
	 */
	public void addListener(ChangeListener listener){
		pitch.addChangeListener(listener);
		volume.addChangeListener(listener);
		duration.addChangeListener(listener);
	}
}
