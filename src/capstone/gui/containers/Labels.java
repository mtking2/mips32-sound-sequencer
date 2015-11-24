package capstone.gui.containers;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import capstone.gui.Note;
import capstone.gui.utils.SequencerUtils;

/**
 * Class grouping together the labels present on the sequencer display.
 * 
 * @author Brad Westley
 * @author Michael King
 * @version 12.11.15
 */
public class Labels {
	/** The label displaying the currently selected note's pitch **/
	private JLabel 	pitch;
	
	/** The label displaying the currently selected note's volume **/
	private JLabel 	volume;
	
	/** The label displaying the currently selected note's tempo **/
	private JLabel 	tempo;
	
	/** The label displaying the current scale **/
	private JLabel 	scale;
	
	/** 
	 * The formatted field for modifying the currently 
	 * selected note's duration 
	 */
	private JFormattedTextField duration;
	
	/** The label displaying the current time signature **/
	private JLabel 	tSig;
	
	/**
	 * Creates a new label container and initializes all labels.
	 */
	public Labels(){
		tempo = new JLabel();
		modifyTempoLabel();
		scale = new JLabel("Scale: None");
		pitch = new JLabel();
		volume = new JLabel();
		tSig = new JLabel();
		modifyTimeSignatureLabel();
		NumberFormat format = NumberFormat.getInstance();
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(0);
		formatter.setMaximum(Integer.MAX_VALUE);
		//formatter.setCommitsOnValidEdit(true);
		duration = new JFormattedTextField(formatter);
	}
	
	/**
	 * Set the contents of the pitch label.
	 * 
	 * @param s the new contents of the pitch label
	 */
	public void modifyPitchLabel(String s){
		pitch.setText(s);
	}
	
	/**
	 * Set the contents of the volume label.
	 * 
	 * @param s the new contents of the volume label
	 */
	public void modifyVolumeLabel(String s){
		volume.setText(s);
	}
	
	/**
	 * Set the contents of the duration label.
	 * 
	 * @param s the new contents of the duration label
	 */
	public void modifyDurationLabel(String s){
		duration.setText(s);
	}
	
	/**
	 * Modify the contents of the pitch, volume, and duration labels all 
	 * at once.
	 * 
	 * @param pitch the new contents of the pitch label
	 * @param volume the new contents of the volume label
	 * @param duration the new contents of the duration label
	 */
	public void modifyNoteLabels(String pitch, String volume, String duration){
		this.pitch.setText(pitch);
		this.volume.setText(volume);
		this.duration.setText(duration);
	}
	
	/**
	 * Update the tempo label to the current tempo.
	 */
	public void modifyTempoLabel(){
		tempo.setText("Tempo: " + SequencerUtils.tempo +" bmp");
	}
	
	/**
	 * Update the time signature label to the current time signature.
	 */
	public void modifyTimeSignatureLabel(){
		tSig.setText("Time Signature: " + SequencerUtils.tSig.getName());
	}
	
	/**
	 * @return the label displaying the current tempo
	 */
	public JLabel getTempoLabel(){	return tempo;	}
	
	/**
	 * @return the label displaying the current scale
	 */
	public JLabel getScaleLabel(){	return scale;	}
	
	/**
	 * @return the label displaying the currently selected note's pitch
	 */
	public JLabel getPitchLabel(){	return pitch;	}
	
	/**
	 * @return the label displaying the currently selected note's volume
	 */
	public JLabel getVolumeLabel(){	return volume;	}
	
	/**
	 * @return the formatted field displaying the currently selected note's duration
	 */
	public JFormattedTextField getDurationLabel(){	return duration;	}
	
	public void modifyNoteLabels(Note n){
		modifyNoteLabels(SequencerUtils.intPitchToString(n.getPitch()), 
				"" + n.getVolume(), "" + n.getDuration());
	}
	
	/**
	 * Get the labels that reside in the top panel of the sequencer.
	 * 
	 * @return the labels located in the top panel of the sequencer
	 */
	public Component[] getTopComponents(){
		Component[] all = { tempo, scale, tSig};
		
		return all;
	}
}
