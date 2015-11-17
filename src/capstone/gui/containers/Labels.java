package capstone.gui.containers;

import java.awt.Component;

import javax.swing.JLabel;

import capstone.gui.Note;
import capstone.gui.utils.SequencerUtils;

public class Labels {
	private JLabel 	pitch, volume, duration, instrument, tempo, scale, tSig;
	
	public Labels(){
		tempo = new JLabel();
		modifyTempoLabel();
		tSig = new JLabel();
		modifyTimeSignatureLabel();
		scale = new JLabel("Scale: None");
		pitch = new JLabel();
		volume = new JLabel();
		duration = new JLabel();
		instrument = new JLabel();
	}
	
	public void resetNoteLabels(){
		pitch = new JLabel();
		volume = new JLabel();
		duration = new JLabel();
		instrument = new JLabel();
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
	
	public void modifyInstrumentLabel(String s){
		instrument.setText(s);
	}
	
	public void modifyNoteLabels(String pitch, String volume
			, String duration, String instrument){
		this.pitch.setText(pitch);
		this.volume.setText(volume);
		this.duration.setText(duration);
		this.instrument.setText(instrument);
	}
	
	public void modifyTempoLabel(){
		tempo.setText("Tempo: " + SequencerUtils.tempo);
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
	
	public JLabel getDurationLabel(){
		return duration;
	}
	
	public JLabel getInstrumentLabel(){
		return instrument;
	}
	
	public void modifyNoteLabels(Note n){
		modifyNoteLabels(SequencerUtils.intPitchToString(n.getPitch()), 
				"" + n.getVolume(), "" + n.getDuration(), 
				"" + n.getInstrument());
	}
	
	public Component[] getNorthComponents(){
		Component[] all = { tempo, scale, tSig };
		
		return all;
	}
	
	public void modifyTimeSignatureLabel(){
		tSig.setText("Time Signature: " + SequencerUtils.tSig.getName());
	}
}
