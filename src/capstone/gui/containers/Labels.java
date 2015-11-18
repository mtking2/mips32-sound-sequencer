package capstone.gui.containers;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import capstone.gui.Note;
import capstone.gui.utils.SequencerUtils;

public class Labels {
	private JLabel 	pitch, volume, tempo, scale, tSig;
	private JFormattedTextField duration;
	
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
	
	public void modifyPitchLabel(String s){
		pitch.setText(s);
	}
	
	public void modifyVolumeLabel(String s){
		volume.setText(s);
	}
	
	public void modifyDurationLabel(String s){
		duration.setText(s);
	}
	
	public void modifyNoteLabels(String pitch, String volume, String duration){
		this.pitch.setText(pitch);
		this.volume.setText(volume);
		this.duration.setText(duration);
	}
	
	public void modifyTempoLabel(){
		tempo.setText("Tempo: " + SequencerUtils.tempo +" bmp");
	}
	
	public void modifyTimeSignatureLabel(){
		tSig.setText("Time Signature: " + SequencerUtils.tSig.getName());
	}
	
	public JLabel getTempoLabel(){
		return tempo;
	}
	
	public JLabel getScaleLabel(){
		return scale;
	}
	
	public JLabel getPitchLabel(){
		return pitch;
	}
	
	public JLabel getVolumeLabel(){
		return volume;
	}
	
	public JFormattedTextField getDurationLabel(){
		return duration;
	}
	
	public void modifyNoteLabels(Note n){
		modifyNoteLabels(SequencerUtils.intPitchToString(n.getPitch()), 
				"" + n.getVolume(), "" + n.getDuration());
	}
	
	public Component[] getComponents(){
		Component[] all = { tempo, scale, tSig};
		
		return all;
	}
}
