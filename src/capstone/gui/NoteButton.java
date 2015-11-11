package capstone.gui;

import javax.swing.JButton;

public class NoteButton extends JButton {
	private int track;
	private int beat;
	
	public NoteButton(int track, int beat){
		super();
		
		this.track = track;
		this.beat = beat;
	}
	
	public int getTrack(){
		return track;
	}
	
	public int getBeat(){
		return beat;
	}
}
